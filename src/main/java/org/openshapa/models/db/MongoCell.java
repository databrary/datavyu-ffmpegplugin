/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.models.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.bson.types.ObjectId;
import java.lang.Math;
import java.util.*;


public class MongoCell extends BasicDBObject implements Cell {
    DBCollection matrix_value_collection = MongoDatastore.getDB().getCollection("matrix_values");
    DBCollection nominal_value_collection = MongoDatastore.getDB().getCollection("nominal_values");
    DBCollection text_value_collection = MongoDatastore.getDB().getCollection("text_values");

    static Map<ObjectId, List<CellListener>> allListeners = new
                                        HashMap<ObjectId, List<CellListener>>();

    /**
     * @param cellId The ID of the variable we want the listeners for.
     *
     * @return The list of listeners for the specified cellId.
     */
    private static List<CellListener> getListeners(ObjectId cellId) {
        List<CellListener> result = allListeners.get(cellId);

        if (result == null) {
            result = new ArrayList<CellListener>();
            allListeners.put(cellId, result);
        }

        return result;
    }

    public MongoCell() { }

    public MongoCell(ObjectId variable_id, Argument type) {
        this.put("variable_id", variable_id);
        this.put("onset", 0L);
        this.put("offset", 0L);
        this.put("type", type.type.ordinal());
        this.put("selected", true);
        this.put("highlighted", true);



        // Necessary to be given an _id by Mongo
        this.save();

        // Build argument list from the argument given

        if (type.type == Argument.Type.NOMINAL) {
            nominal_value_collection.save(new MongoNominalValue((ObjectId)this.get("_id")));
        } else if (type.type == Argument.Type.TEXT) {
            text_value_collection.save(new MongoTextValue((ObjectId)this.get("_id")));
        } else {
            matrix_value_collection.save(new MongoMatrixValue((ObjectId)this.get("_id"), type));
        }
    }


    public void save() {
        MongoDatastore.getCellCollection().save(this);
    }

    private MongoCell getLatest() {
        return (MongoCell) MongoDatastore.getCellCollection().findOne((ObjectId) this.get("_id"));
    }

    public ObjectId getVariableID() {
        return (ObjectId) getLatest().get("variable_id");
    }

    private String convertMStoTimestamp(long time) {
        long hours = Math.round(Math.floor((time/1000.0/60.0/60.0)));
        long minutes = Math.round(Math.floor(time/1000.0/60.0 - (hours * 60)));
        long seconds = Math.round(Math.floor(time/1000.0 - (hours*60*60) - (minutes * 60)));
        long mseconds = Math.round(Math.floor(time - (hours *60*60*1000) - (minutes*60*1000) - (seconds*1000)));

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, mseconds);
    }

    private long convertTimestampToMS(String timestamp) {

        String[] s = timestamp.split(":");
        long hours = Long.valueOf(s[0]) * 60 * 60 * 1000;
        long minutes = Long.valueOf(s[1]) * 60 * 1000;
        long seconds = Long.valueOf(s[2]) * 1000;
        long mseconds = Long.valueOf(s[3]);

        return hours + minutes + seconds + mseconds;
    }


    @Override
    public String getOffsetString() {
        return convertMStoTimestamp((Long) getLatest().get("offset"));
    }

    @Override
    public long getOffset() {
        return (Long) getLatest().get("offset");
    }

    public void setVariableID(int variable_id) {
        this.put("variable_id", variable_id);
    }

    @Override
    public void setOffset(final long newOffset) {
        this.put("offset", newOffset);
        this.save();

        for(CellListener cl : getListeners(getID())) {
            cl.offsetChanged(newOffset);
        }
    }

    @Override
    public void setOffset(final String newOffset) {
        this.put("offset", convertTimestampToMS(newOffset));
        this.save();

        for(CellListener cl : getListeners(getID())) {
            cl.offsetChanged(convertTimestampToMS(newOffset));
        }
    }

    @Override
    public long getOnset() {
        return (Long) getLatest().get("onset");
    }

    @Override
    public String getOnsetString() {
        return convertMStoTimestamp((Long) getLatest().get("onset"));
    }

    @Override
    public void setOnset(final String newOnset) {
        this.put("onset", convertTimestampToMS(newOnset));
        this.save();

        for(CellListener cl : getListeners(getID())) {
            cl.onsetChanged(convertTimestampToMS(newOnset));
        }

        this.save();
    }

    @Override
    public void setOnset(final long newOnset) {
        this.put("onset", newOnset);
        this.save();

        for(CellListener cl : getListeners(getID()) ) {
            cl.onsetChanged(newOnset);
        }
    }

    @Override
    public String getValueAsString() {
        return getValue().toString();
    }

    @Override
    public Value getValue() {
        Value value = null;
        BasicDBObject query = new BasicDBObject();
        query.put("parent_id", getLatest().get("_id"));

        if((Integer)this.get("type") == Argument.Type.MATRIX.ordinal()) {
            DBCursor cur = matrix_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoMatrixValue)cur.next();
            }
        } else if((Integer) getLatest().get("type") == Argument.Type.NOMINAL.ordinal()) {
            DBCursor cur = nominal_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoNominalValue)cur.next();
            }
        } else if((Integer) getLatest().get("type") == Argument.Type.TEXT.ordinal()) {
            DBCursor cur = text_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoTextValue)cur.next();
            }
        }

        return value;
    }

    @Override
    public boolean isSelected() {
        MongoCell dbCell = (MongoCell) MongoDatastore.getCellCollection().findOne((ObjectId) this.get("_id"));
        return (Boolean) dbCell.get("selected");
    }

    @Override
    public void setSelected(final boolean selected) {
        this.put("selected", selected);

        // If a cell is deselected, it must also not be highlighted.
        if (!selected) {
            this.put("highlighted", selected);
        }
        this.save();

        for(CellListener cl : getListeners(getID()) ) {
            cl.selectionChange(selected);
        }
    }

    @Override
    public boolean isHighlighted() {
        MongoCell dbCell = (MongoCell) MongoDatastore.getCellCollection().findOne((ObjectId) this.get("_id"));
        return (Boolean) dbCell.get("highlighted");
    }

    @Override
    public void setHighlighted(final boolean highlighted) {
        this.put("highlighted", highlighted);

        // If the cell is highlighted, it must also be selected.
        if (highlighted) {
            this.put("selected", highlighted);
        }
        this.save();

        for(CellListener cl : getListeners(getID()) ) {
            cl.highlightingChange(highlighted);
        }
    }
    
    @Override
    public void addMatrixValue(final Argument.Type type) {
        MongoMatrixValue val = (MongoMatrixValue)getValue();
        val.createArgument(type);
        val.save();
    }
    
    @Override
    public void moveMatrixValue(final int old_index, int new_index){
        MongoMatrixValue val = (MongoMatrixValue)getValue();
        List<Value> values = val.getArguments();
        Value v = values.get(old_index);
        
        values.remove(old_index);
        values.add(new_index, v);
        
        for(int i = 0; i < values.size(); i++) {
            ((MongoValue)values.get(i)).setIndex(i);
        }
        val.save();
    }
    
    @Override
    public void removeMatrixValue(final int index) {
        ((MongoMatrixValue)getValue()).removeArgument(index);
    }
    
    @Override
    public void setMatrixValue(final int index, final String v) {
        MongoMatrixValue val = (MongoMatrixValue)getValue();
        List<Value> values = val.getArguments();
        values.get(index).set(v);
        val.save();
    }
    
    @Override
    public Value getMatrixValue(final int index) {
        return ((MongoMatrixValue)getValue()).getArguments().get(index);
    }
    
    @Override
    public void clearMatrixValue(final int index) {
        MongoMatrixValue val = (MongoMatrixValue)getValue();
        List<Value> values = val.getArguments();
        values.get(index).clear();
        val.save();
    }

    @Override
    public void addListener(final CellListener listener) {
        getListeners(getID()).add(listener);
    }

    @Override
    public void removeListener(final CellListener listener) {
        getListeners(getID()).remove(listener);
    }

    public ObjectId getID() {
        return ((ObjectId) getLatest().get("_id"));
    }

    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MongoCell)) {
            return false;
        }
        MongoCell otherC = (MongoCell) other;

        if(otherC.getID().toString().equals(this.getID().toString())) {
            return true;
        } else {
            return false;
        }
    }
}

