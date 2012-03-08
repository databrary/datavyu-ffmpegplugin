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


public class MongoCell extends BasicDBObject implements Cell {
    
    DBCollection matrix_value_collection = MongoDatastore.getDB().getCollection("matrix_values");
    DBCollection nominal_value_collection = MongoDatastore.getDB().getCollection("nominal_values");
    DBCollection text_value_collection = MongoDatastore.getDB().getCollection("text_values");
    
    Vector<CellListener> listeners = new Vector<CellListener>();
    
    
    public MongoCell() { }
    
    public MongoCell(ObjectId variable_id, Argument type) {
        this.put("variable_id", variable_id);
        this.put("type", type.type.ordinal());
        this.put("selected", false);
        
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
    
    
    /**
     * Helper method to save this cell to the DB.
     * Must be run after each update to the cell.
     */
    public void save() {
        MongoDatastore.getCellCollection().save(this);
    }
    
    /**
     * Get the ID of this cell's parent variable.
     * @return 
     */
    public ObjectId getVariableID() {
        return (ObjectId)this.get("variable_id");
    }
    

    /**
     * @return the offset timestamp in a HH:mm:ss:SSS format, where HH is 24 hour
     * mm is minutes in an hour, ss is seconds in a minute and SSS is
     * milliseconds in a second.
     */
    @Override
    public String getOffsetString() {
        return String.valueOf((Long)this.get("offset"));
    }

    /**
     * @return The offset timestamp in milliseconds. Returns -1 if the offset
     * cannot be resolved.
     */
    @Override
    public long getOffset() { 
        return (Long)this.get("offset");
    }
    
    
    public void setVariableID(int variable_id) {
        this.put("variable_id", variable_id);
    }
    
    /**
     * Sets the offset for this cell.
     *
     * @param newOffset The new offset timestamp in milliseconds to use for this
     * cell.
     */
    @Override
    public void setOffset(final long newOffset) {
        this.put("offset", newOffset);
        
        for(CellListener cl : this.listeners ) {
            cl.offsetChanged(newOffset);
        }
    }
    
    /**
     * Sets the offset for this cell.
     *
     * @param newOffset The new onset timestamp for this cell in string in the
     * format "HH:MM:SS:mmm" where HH = hours, MM = minutes, SS = seconds and
     * mmm = milliseconds.
     */
    @Override
    public void setOffset(final String newOffset) {
        this.put("offset", Long.getLong(newOffset));
        
        for(CellListener cl : this.listeners ) {
            cl.offsetChanged(Long.getLong(newOffset));
        }
    }

    /**
     * @return The onset timestamp in milliseconds. Returns -1 if the onset
     * cannot be resolved.
     */
    @Override
    public long getOnset() {
        return (Long)this.get("onset");
    }
    
    /**
     * @return the onset timestamp in a HH:mm:ss:SSS format, where HH is 24 hour
     * mm is minutes in an hour, ss is seconds in a minute and SSS is
     * milliseconds in a second.
     */
    @Override
    public String getOnsetString() {
        return String.valueOf((Long)this.get("onset"));
    }

    /**
     * Sets the onset for this cell.
     *
     * @param newOnset The new onset timestamp for this cell in string in the
     * format "HH:MM:SS:mmm" where HH = hours, MM = minutes, SS = seconds and
     * mmm = milliseconds.
     */
    @Override
    public void setOnset(final String newOnset) {
        this.put("onset", Long.getLong(newOnset));
        
        for(CellListener cl : this.listeners ) {
            cl.onsetChanged(Long.getLong(newOnset));
        }
    }
    
    /**
     * Sets the onset for this cell.
     *
     * @param newOnset The new onset timestamp in milliseconds to use for this
     * cell.
     */
    @Override
    public void setOnset(final long newOnset) {
        this.put("onset", newOnset);
        
        for(CellListener cl : this.listeners ) {
            cl.onsetChanged(newOnset);
        }
    }

    /**
     * @return The value stored in the cell as a string. Returns null if the
     * string value cannot be resolved.
     */
    @Override
    public String getValueAsString() {
        return getValue().toString();
    }

    /**
     * @return The value of the cell.
     */
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

    /**
     * @return True if the cell is selected, false otherwise.
     */
    @Override
    public boolean isSelected() {
        return (Boolean)this.get("selected");
    }

    /**
     * Selects this cell.
     *
     * @param True if this cell is selected, false if unselected.
     */
    @Override
    public void setSelected(final boolean selected) {
        this.put("selected", selected);
        
        for(CellListener cl : this.listeners ) {
            cl.selectionChange(selected);
        }
    }

    /**
     * @return True if the cell is highlighted, false otherwise.
     */
    @Override
    public boolean isHighlighted() {
        return (Boolean)this.get("highlighted");
    }

    /**
     * Highlights the cell.
     *
     * @param highlighted True if this cell is highlighted, false otherwise.
     */
    @Override
    public void setHighlighted(final boolean highlighted) {
        this.put("highlighted", highlighted);
        
        for(CellListener cl : this.listeners ) {
            cl.highlightingChange(highlighted);
        }
    }

    /**
     * Adds a listener that needs to be notified when the cell changes.
     */
    @Override
    public void addListener(final CellListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of things that need to be notified when
     * the cell changes.
     */
    @Override
    public void removeListener(final CellListener listener) {
        if(listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
}

