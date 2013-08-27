/*
 * Copyright (c) 2011 Datavyu Foundation, http://datavyu.org
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

package org.datavyu.models.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import java.io.Serializable;
import org.bson.types.ObjectId;
import org.datavyu.models.db.Value;
import org.datavyu.models.db.Variable;


public abstract class MongoValue extends BasicDBObject implements Value, Serializable, Comparable<MongoValue> {
    
    String value;
    
    @Override
    public boolean isValid(final String value) {
        return true;
    } 

    @Override
    public void clear() {
        this.put("value", null);
        this.save();
    }

    @Override
    public boolean isEmpty() {
        if(this.get("value") == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public int getIndex() {
        return (Integer)this.get("index");
    }
    
    public void setIndex(int index) {
        this.put("index", index);
        this.save();
    }
    
    public Variable getParentVariable() {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", (ObjectId)this.get("parent_id"));
        DBCursor cur = MongoDatastore.getVariableCollection().find(query);
        if(cur.hasNext())
            return (Variable)cur.next();
        else { // We have an argument in a matrix, so fetch that.
            cur = MongoDatastore.getMatrixValuesCollection().find(query);
            query.put("_id", (ObjectId)cur.next().get("parent_id"));
            cur = MongoDatastore.getCellCollection().find(query);
            query.put("_id", (ObjectId)cur.next().get("variable_id"));
            cur = MongoDatastore.getVariableCollection().find(query);
            return (Variable)cur.next();
        }
            
    }
    
    public String getArgName(final int index) {
        return getParentVariable().getVariableType().childArguments.get(index).name;
    }
    
    public String getName() {
        return (String)getArgName((Integer)this.get("index"));
    }
    
    public abstract void save();
    
    @Override
    public int compareTo(MongoValue v) {
        if (this.getIndex() < v.getIndex()) {
            return -1;
        } else if (this.getIndex() > v.getIndex()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public abstract void set(final String value);

    @Override
    public String toString() {
        if(this.get("value") != null)
            return (String)this.get("value");
        else if((Integer)this.get("index") != -1)
            return "<" + getArgName((Integer)this.get("index")) + ">";
        else
            return "<var>";
    }
}
