/*
 * Copyright (c) 2011 OpenSHAPA Foundation, http://openshapa.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.openshapa.models.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 
 */
public class MongoVariable extends BasicDBObject implements Variable  {
    
//    DB db;
    
    List<VariableListener> listeners = new ArrayList<VariableListener>();
    
    public MongoVariable() {
        
    }
    
    public MongoVariable(String name, Argument type) {
        this.put("name", name);
        this.put("type", serializeArgument(type));
        this.put("hidden", false);
        this.put("selected", false);
        
        this.save();
    }
    
    /** 
     * Helper method to save this variable to the DB.
     * This must be run after any changes to the variable.
     */
    public void save() {
        MongoDatastore.getVariableCollection().save(this);
    }
    
    public int getVariableID() {
        return (Integer)this.get("_id");
    }
    
    private BasicDBObject serializeArgument(Argument type) {
        BasicDBObject serial_type = new BasicDBObject();
        
        serial_type.put("type_ordinal", type.type.ordinal());
        serial_type.put("name", type.name);
        
        List<BasicDBObject> childArguments = new ArrayList<BasicDBObject>();
        if(type.childArguments.size() > 0) {

            for(Argument child : type.childArguments) {
                childArguments.add(serializeArgument(child));
            }
        }
        serial_type.put("child_arguments", childArguments);
        
        return serial_type;
    }
    
    private Argument deserializeArgument(BasicDBObject serial_type) {
        
        String name = (String)serial_type.get("name");
        int type_ordinal = (Integer)serial_type.get("type_ordinal");
        Argument.Type type = Argument.Type.values()[type_ordinal];
        
        Argument arg = new Argument(name, type);
        
        if(type == Argument.Type.MATRIX) {
            List<BasicDBObject> DBchildArguments = (ArrayList<BasicDBObject>)serial_type.get("child_arguments");
            List<Argument> childArguments = new ArrayList<Argument>();
            
            for (BasicDBObject child : DBchildArguments) {
                childArguments.add(deserializeArgument(child));
            }
            arg.childArguments = childArguments;
        }
        
        return arg;
    }
    
    /**
     * Creates and inserts a cell into the variable.
     *
     * @return The newly created cell.
     */
    @Override
    public Cell createCell() {
        Cell c = new MongoCell((ObjectId)this.get("_id"), deserializeArgument((BasicDBObject)this.get("type")));
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");
        
        cell_collection.save((MongoCell)c);
        
        return c;
    }
    
    
    /**
     * Removes a cell from the variable.
     *
     * @param The cell to remove from the variable.
     */
    @Override
    public void removeCell(final Cell cell) {
        // TODO: removecell
    }

    
    /**
     * @return All the cells stored in the variable.
     */
    @Override
    public List<Cell> getCells() {
        List<Cell> cells = new ArrayList<Cell>();
        
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");
        BasicDBObject query = new BasicDBObject();

        query.put("variable_id", this.get("_id"));  // e.g. find all where i > 50
        
        DBCursor cur = cell_collection.find(query);
        
        while(cur.hasNext()) {
            cells.add((MongoCell)cur.next());
        }
        
        return cells;
    }

    /**
     * Gets the 'index' cell from the variable that has been sorted temporally.
     *
     * @param index The index (from first onset to last offset) of the cell.
     *
     * @return The cell.
     */
    public Cell getCellTemporally(final int index) {
        // TODO: getcellstemp
        return null;
    }

    /**
     * @return The type of the variable.
     */
    @Override
    public Argument getVariableType() {
        return deserializeArgument((BasicDBObject)this.get("type"));
    }

    @Override
    public void setVariableType(final Argument newType) {
        this.put("argument", serializeArgument(newType));
    }

    @Override
    public List<Cell> getCellsTemporally() {
        List<Cell> cells = new ArrayList<Cell>();

        return cells;
    }

    @Override
    public boolean contains(final Cell c) {
        return false;
    }

    @Override
    public void setSelected(final boolean selected) {
        this.put("selected", selected);
        this.save();
    }

    @Override
    public boolean isSelected() {
        return (Boolean)this.get("selected");
    }

    @Override
    public void setHidden(final boolean hidden) {
        this.put("hidden", hidden);
        this.save();
    }

    @Override
    public boolean isHidden() {
        return (Boolean)this.get("hidden");
    }

    @Override
    public String getName() {
        return (String)this.get("name");
    }

    @Override
    public void setName(final String newName) throws UserWarningException {
        // TODO: Add code to check to make sure this name is OK
        this.put("name", newName);
        this.save();
    }

    @Override
    public void addListener(final VariableListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final VariableListener listener) {
        listeners.remove(listener);
    }
}
