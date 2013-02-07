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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bson.types.ObjectId;
import org.datavyu.models.db.Argument;
import org.datavyu.models.db.MatrixValue;
import org.datavyu.models.db.Value;


public final class MongoMatrixValue extends MongoValue implements MatrixValue {
    
    public MongoMatrixValue() {}
    
    /**
     * Default constructor for serializing/deserializing with Mongo
     */
    public MongoMatrixValue(ObjectId parent_id, Argument type) {
        this.put("parent_id", parent_id);
        
        this.save();
        
        for(Argument arg : type.childArguments) {
            createArgument(Argument.Type.NOMINAL);
        }
        
    }
    
    @Override
    public void set(final String value) {
        this.put("value", value);
        this.save();
    }
    
    @Override
    public String toString() {
        String result = "";
        List<Value> values = getArguments();
	
	result += "(";
        for(int i = 0; i < values.size(); i++) {
            Value v = values.get(i);
            if (v.toString() == null) {
                result += "<arg"+String.valueOf(i)+">";
            } else {
                result += v.toString();
            }
            if (i < values.size() - 1) {
                result += ",";
            }
        }
	result += ")";

        return result;
    }

    @Override
    public List<Value> getArguments() {
        List<MongoValue> mongo_values = new ArrayList<MongoValue>();
        List<Value> values = new ArrayList<Value>();
        BasicDBObject query = new BasicDBObject();
        query.put("parent_id", this.get("_id"));
        BasicDBObject sortOrder = new BasicDBObject();
        sortOrder.put("index", 1);
        
        
        DBCursor cur = MongoDatastore.getDB().getCollection("matrix_values").find(query);
        while(cur.hasNext()) {
            mongo_values.add( (MongoMatrixValue)cur.next() );
        }

        cur = MongoDatastore.getDB().getCollection("nominal_values").find(query);
        while(cur.hasNext()) {
            mongo_values.add( (MongoNominalValue)cur.next() );
        }

        cur = MongoDatastore.getDB().getCollection("text_values").find(query);
        while(cur.hasNext()) {
            mongo_values.add( (MongoTextValue)cur.next() );
        }

        for(Value v : mongo_values) {
            values.add(v);
        }
        
        order(values);
        
        return values;
    }
    
    // Method to order the values coming out of the DB.
    private static void order(List<Value> values) {

        Collections.sort(values, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                int x1 = ((MongoValue) o1).getIndex();
                int x2 = ((MongoValue) o2).getIndex();

                if (x1 != x2) {
                    return x1 - x2;
                } else {
                    return 0;
                }
            }
    });
}

    
    @Override
    public void save() {
        MongoDatastore.getDB().getCollection("matrix_values").save(this);
    }

    @Override
    public Value createArgument(Argument.Type argType) {
        Value val = null;
        String name = String.format("arg%02d", getArguments().size() + 1);
        if(argType == Argument.Type.NOMINAL) {
            val = new MongoNominalValue((ObjectId)this.get("_id"), name, getArguments().size());
        } else if(argType == Argument.Type.TEXT) {
            val = new MongoTextValue((ObjectId)this.get("_id"), name, getArguments().size());
        }
        this.getArguments().add(val);
        this.save();
        return val;
    }
    
    @Override
    public void removeArgument(final int index) {
        Value val = this.getArguments().get(index);
        if(val instanceof MongoNominalValue) {
            MongoDatastore.getNominalValuesCollection().remove((MongoNominalValue)val);
            System.out.println("VALUE REMOVED");
        } else if (val instanceof MongoTextValue) {
            MongoDatastore.getTextValuesCollection().remove((MongoTextValue)val);
        }
        
        // GO OVER THE REST OF THE VALUES AFTER THIS ONE AND DECREMENT THEIR INDEX
        
        List<Value> args = getArguments();
        Value v;
        for(int i = 0; i < args.size(); i++) {
            v = args.get(i);
            ((MongoNominalValue)v).setIndex(i);
            ((MongoNominalValue)v).save();
        }
        
        this.save();
    }
}
