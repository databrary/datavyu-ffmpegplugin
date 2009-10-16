package org.openshapa.util;

/**
 * Utilities for float data type.
 * @author mmuthukrishna
 */
public class FloatUtils {
    /** tolerance value for comparing two doubles for equality. */
    private final static double delta = 0.0000001;

    /**
     * Compare two doubles and return true if close enough.
     * @param d1 first double
     * @param d2 second double
     * @return true if close enough to be considered equal
     */
    public static boolean closeEnough(final double d1, final double d2) {
        return (Math.abs(d1 - d2) < delta);
    }

}
