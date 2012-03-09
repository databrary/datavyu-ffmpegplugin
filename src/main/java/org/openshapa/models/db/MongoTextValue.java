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

import org.bson.types.ObjectId;

public final class MongoTextValue extends MongoValue implements TextValue {
    
    public MongoTextValue() { }
    
    public MongoTextValue(ObjectId parent_id) {
        this.put("value", null);
        this.put("parent_id", parent_id);
        this.put("name", "val");
        this.save();
    }
    
    public MongoTextValue(ObjectId parent_id, String name) {
        this(parent_id);
        this.put("name", name);
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
    public void save() {
        MongoDatastore.getDB().getCollection("text_values").save(this);
    }
    
    @Override
    public String toString() {
        String val = (String)this.get("value");
        if(val == null) {
            return (String)this.get("name");
        }
        return (String)this.get("value");
    }
}