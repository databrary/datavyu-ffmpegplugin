
package org.openshapa.models.db;

import org.bson.types.ObjectId;

public final class MongoNominalValue extends MongoValue implements NominalValue {
    
    public MongoNominalValue() {}
    
    public MongoNominalValue(ObjectId parent_id) {
        this.put("value", null);
        this.put("parent_id", parent_id);
        this.save();
    }
    
    /**
     * Sets the value, this method leaves the value unchanged if the supplied
     * input is invalid. Use isValid to test.
     *
     * @param value The new content to use for this value.
     */
    @Override
    public void set(final String value) {
        this.put("value", value);
        this.save();
    }
    
    @Override
    public String toString() {
        return (String)this.get("value");
    }
    
    @Override
    public void save() {
        MongoDatastore.getDB().getCollection("nominal_values").save(this);
    }
}
