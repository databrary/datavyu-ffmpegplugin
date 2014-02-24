package org.datavyu.models.db;

import java.util.Comparator;

/**
 * User: jesse
 * Date: 2/5/14
 * Time: 3:56 PM
 */
public class CellComparator implements Comparator<Cell> {
    public int compare(Cell c1, Cell c2) {
        if (c1.getOnset() < c2.getOnset()) {
            return -1;
        } else if (c1.getOnset() > c2.getOnset()) {
            return 1;
        } else {
            if (c1.getOffset() < c2.getOffset()) {
                return -1;
            } else if (c1.getOffset() > c2.getOffset()) {
                return 1;
            }
            return 0;
        }
    }
}
