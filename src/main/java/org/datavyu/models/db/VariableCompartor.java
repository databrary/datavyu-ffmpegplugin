package org.datavyu.models.db;

import java.util.Comparator;

/**
 * User: jesse
 * Date: 2/5/14
 * Time: 3:56 PM
 */
public class VariableCompartor implements Comparator<Variable> {
    public int compare(Variable c1, Variable c2) {
        if (c1.getOrderIndex() < c2.getOrderIndex()) {
            return -1;
        } else if (c1.getOrderIndex() == c2.getOrderIndex()) {
            return 0;
        } else {
            return 1;
        }
//        return c1.getName().compareToIgnoreCase(c2.getName());
    }
}
