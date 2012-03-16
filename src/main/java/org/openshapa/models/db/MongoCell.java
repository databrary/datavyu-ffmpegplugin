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
import java.util.Vector;
import org.bson.types.ObjectId;
import java.lang.Math;


public class MongoCell extends BasicDBObject implements Cell {
    
    DBCollection matrix_value_collection = MongoDatastore.getDB().getCollection("matrix_values");
    DBCollection nominal_value_collection = MongoDatastore.getDB().getCollection("nominal_values");
    DBCollection text_value_collection = MongoDatastore.getDB().getCollection("text_values");
    
    Vector<CellListener> listeners = new Vector<CellListener>();
    
    
    public MongoCell() { }
    
    public MongoCell(ObjectId variable_id, Argument type) {
        this.put("variable_id", variable_id);
        this.put("onset", 0L);
        this.put("offset", 0L);
        this.put("type", type.type.ordinal());
        this.put("selected", false);
        this.put("highlighted", false);
        
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
    
    public ObjectId getVariableID() {
        return (ObjectId)this.get("variable_id");
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
        return convertMStoTimestamp((Long)this.get("offset"));
    }

    @Override
    public long getOffset() { 
        return (Long)this.get("offset");
    }
    
    
    public void setVariableID(int variable_id) {
        this.put("variable_id", variable_id);
    }
    
    @Override
    public void setOffset(final long newOffset) {
        this.put("offset", newOffset);
        
        this.save();
        
        for(CellListener cl : this.listeners ) {
            cl.offsetChanged(newOffset);
        }
    }
    
    @Override
    public void setOffset(final String newOffset) {
        this.put("offset", convertTimestampToMS(newOffset));
        
        this.save();
        
        for(CellListener cl : this.listeners ) {
            cl.offsetChanged(convertTimestampToMS(newOffset));
        }
    }

    @Override
    public long getOnset() {
        return (Long)this.get("onset");
    }

    @Override
    public String getOnsetString() {
        return convertMStoTimestamp((Long)this.get("onset"));
    }

    @Override
    public void setOnset(final String newOnset) {
        this.put("onset", convertTimestampToMS(newOnset));
        
        this.save();
        
        for(CellListener cl : this.listeners ) {
            cl.onsetChanged(convertTimestampToMS(newOnset));
        }
        
        this.save();
    }

    @Override
    public void setOnset(final long newOnset) {
        this.put("onset", newOnset);
        
        this.save();
        
        for(CellListener cl : this.listeners ) {
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
        query.put("parent_id", this.get("_id"));
        
        if((Integer)this.get("type") == Argument.Type.MATRIX.ordinal()) {
            DBCursor cur = matrix_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoMatrixValue)cur.next();
            }
        } else if((Integer)this.get("type") == Argument.Type.NOMINAL.ordinal()) {
            DBCursor cur = nominal_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoNominalValue)cur.next();
            }
        } else if((Integer)this.get("type") == Argument.Type.TEXT.ordinal()) {
            DBCursor cur = text_value_collection.find(query);
            if(cur.hasNext()) {
                value = (MongoTextValue)cur.next();
            }
        } 
        
        return value;
    }

    @Override
    public boolean isSelected() {
        return (Boolean)this.get("selected");
    }

    @Override
    public void setSelected(final boolean selected) {
        this.put("selected", selected);
        
        for(CellListener cl : this.listeners ) {
            cl.selectionChange(selected);
        }
    }

    @Override
    public boolean isHighlighted() {
        return (Boolean)this.get("highlighted");
    }

    @Override
    public void setHighlighted(final boolean highlighted) {
        this.put("highlighted", highlighted);
        
        for(CellListener cl : this.listeners ) {
            cl.highlightingChange(highlighted);
        }
    }

    @Override
    public void addListener(final CellListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final CellListener listener) {
        if(listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
    
    public ObjectId getMongoID() {
        return ((ObjectId)this.get("_id"));
    }
    
    @Override
    public int hashCode() {
        return this.getMongoID().hashCode();
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
        
        if(otherC.getMongoID().toString().equals(this.getMongoID().toString())) {
            return true;
        } else {
            return false;
        }
    }
}

