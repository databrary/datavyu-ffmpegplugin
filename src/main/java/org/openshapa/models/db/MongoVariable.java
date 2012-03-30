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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

/**
 * Maps a variable object to a mongo powered datastore.
 */
public final class MongoVariable extends BasicDBObject implements Variable  {
    // All the listeners for variables in teh datastore.
    static Map<ObjectId, List<VariableListener>> allListeners =
                                new HashMap<ObjectId, List<VariableListener>>();

    /**
     * @param variableId The ID of the variable we want the listeners for.
     *
     * @return The list of listeners for the specified variableId.
     */
    private static List<VariableListener> getListeners(ObjectId variableId) {
        List<VariableListener> result = allListeners.get(variableId);

        if (result == null) {
            result = new ArrayList<VariableListener>();
            allListeners.put(variableId, result);
        }

        return result;
    }

    /**
     * Removes all the listeners for all the variables.
     */
    public static void clearListeners() {
        allListeners.clear();
    }

    /**
     * Default constructor.
     */
    public MongoVariable() {

    }

    /**
     * Constructor.
     *
     * @param name The name to use for the variable being constructed.
     * @param type The type to use for the variable being constructed.
     */
    public MongoVariable(String name, Argument type) throws UserWarningException {
        this.setName(name);
        this.put("type", serializeArgument(type));
        this.put("hidden", false);
        this.put("selected", true);

        this.save();
    }

    /**
     * Helper method to save this variable to the DB.
     * This must be run after any changes to the variable.
     */
    public final void save() {
        MongoDatastore.getVariableCollection().save(this);
    }

    /**
     * @return The internal ID (mongo id) for this variable.
     */
    public ObjectId getID() {
        return (ObjectId) this.get("_id");
    }

    /**
     * Serializes the argument into a mongo object.
     *
     * @param type The Argument being serialized into a mongo object.
     *
     * @return The serialized argument.
     */
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

    /**
     * Deserializes a mongo object into an Argument.
     *
     * @param serial_type The serialized argument.
     *
     * @return The argument held inside the mongo object.
     */
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

    @Override
    public Cell createCell() {
        Cell c = new MongoCell((ObjectId)this.get("_id"), deserializeArgument((BasicDBObject)this.get("type")));
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");

        cell_collection.save((MongoCell)c);

        for(VariableListener vl : getListeners(getID()) ) {
            vl.cellInserted(c);
        }

        return c;
    }

    @Override
    public void removeCell(final Cell cell) {
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");

        cell_collection.remove((MongoCell)cell);

        for(VariableListener vl : getListeners(getID()) ) {
            vl.cellRemoved(cell);
        }
    }

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

    @Override
    public Cell getCellTemporally(final int index) {
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");
        BasicDBObject query = new BasicDBObject();
        BasicDBObject sort = new BasicDBObject();

        query.put("variable_id", this.get("_id"));  // e.g. find all where i > 50
        sort.put("onset", 1);

        DBCursor cur = cell_collection.find(query).sort(sort);

        int i = 0;
        while(cur.hasNext()) {
            if(i == index) {
                return (MongoCell)cur.next();
            }
            i++;
        }

        return null;
    }

    @Override
    public Argument getVariableType() {
        return deserializeArgument((BasicDBObject)this.get("type"));
    }

    @Override
    public void setVariableType(final Argument newType) {
        this.put("type", serializeArgument(newType));
        this.save();
    }

    @Override
    public List<Cell> getCellsTemporally() {
        List<Cell> cells = new ArrayList<Cell>();

        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");
        BasicDBObject query = new BasicDBObject();
        BasicDBObject sort = new BasicDBObject();

        query.put("variable_id", this.get("_id"));  // e.g. find all where i > 50
        sort.put("onset", 1);

        DBCursor cur = cell_collection.find(query).sort(sort);

        while(cur.hasNext()) {
            cells.add((MongoCell)cur.next());
        }

        return cells;
    }

    @Override
    public boolean contains(final Cell c) {
        DBCollection cell_collection = MongoDatastore.getDB().getCollection("cells");
        DBCursor cur = cell_collection.find((MongoCell)c);

        if(cur.hasNext()) {
            MongoCell found = (MongoCell)cur.next();
            if(found.getVariableID().equals(this.getID())) {
                return true;
            }
        }
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

        for(VariableListener vl : getListeners(getID()) ) {
            vl.visibilityChanged(hidden);
        }
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
        // Pre-conditions, the newName must have at least one character.
        if (newName.length() < 1) {
            throw new UserWarningException("Unable to add variable, a name must be supplied.");
        }

        // Pre-conditions, check to make sure newName doesn't contain invalid chars.
        if (newName.contains("(") || newName.contains(")") || newName.contains("<") || newName.contains(">") || newName.contains(",") || newName.contains("\"")) {
            throw new UserWarningException("Unable to add variable, name must not contain any: ') ( > < , \"'");
        }

        this.put("name", newName.trim());
        this.save();

        for(VariableListener vl : getListeners(getID()) ) {
            vl.nameChanged(newName);
        }
    }
    
    @Override
    public void addArgument(final Argument.Type type) {
        Argument arg = getVariableType();
        arg.addChildArgument(type);
        
        for(Cell cell : getCells()) {
            cell.addMatrixValue(type);
        }
        
        this.setVariableType(arg);
        this.save();
        
        // TODO: Notify listeners
        
    }
    
    @Override
    public void moveArgument(final int old_index, final int new_index) {
        Argument arg = getVariableType();
        Argument moved_arg = arg.childArguments.get(old_index);
        arg.childArguments.remove(moved_arg);
        arg.childArguments.add(new_index, moved_arg);
        
        // Move in all cells
        for(Cell cell : getCells()) {
            cell.moveMatrixValue(old_index, new_index);
        }
        this.setVariableType(arg);
        this.save();
        
        // TODO: Notify listeners
    }
    
    @Override
    public void moveArgument(final String name, final int new_index) {
        int old_index = getArgumentIndex(name);
        moveArgument(old_index, new_index);
    }
    
    @Override
    public void removeArgument(final String name) {
        Argument arg = getVariableType();
        int arg_index = getArgumentIndex(name);
        arg.childArguments.remove(arg_index);
        
        // Now send this change to the cells
        for(Cell cell : getCells()) {
            cell.removeMatrixValue(arg_index);
        }
        
        this.setVariableType(arg);
        this.save();
        
        // TODO: Notify appropriate listeners that this happened.
    }
    
    @Override
    public int getArgumentIndex(final String name) {
        Argument arg = getVariableType();
        for(int i = 0; i < arg.childArguments.size(); i++) {
            if(arg.childArguments.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addListener(final VariableListener listener) {
        getListeners(getID()).add(listener);
    }

    @Override
    public void removeListener(final VariableListener listener) {
        getListeners(getID()).remove(listener);
    }
}
