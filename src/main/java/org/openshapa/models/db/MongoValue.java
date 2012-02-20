/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.models.db;

import com.mongodb.BasicDBObject;
import java.io.Serializable;

/**
 *
 * @author jesse
 */
public abstract class MongoValue extends BasicDBObject implements Value, Serializable {
    
     String value;
    
     /**
     * @param value The string to test if it is valid.
     *
     * @return True if the supplied value is a valid substitute 
     */
    public boolean isValid(final String value) {
        return true;
    } 

    /**
     * Clears the contents of the value and returns it to a 'null'/Empty state.
     */
    public void clear() {
        value = null;
    }

    /**
     * @return True if the value is empty/'null' false otherwise.
     */
    public boolean isEmpty() {
        if(value == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public abstract void save();

    /**
     * Sets the value, this method leaves the value unchanged if the supplied
     * input is invalid. Use isValid to test.
     *
     * @param value The new content to use for this value.
     */
    public abstract void set(final String value);

    /**
     * @return must override toString in such a way that when isEmpty == true,
     * toString returns a valid empty value i.e. "<argName>"
     */
    
    public abstract String toString();
}
