package org.openshapa.util;

/**
 * Class for a couple of helper functions for generating hashcodes.
 */
public final class HashUtils {

    /**
     * Generates a hash code for the supplied object.
     *
     * @param obj The object to generate a hash code for.
     *
     * @return The hashcode for the object, 0 if the supplied object is null.
     */
    public static int Obj2H(final Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return obj.hashCode();
        }
    }

    /**
     * Generates an integer hashcode from a long value.
     *
     * @param l The long value to turn into an integer hashCode.
     *
     * @return The integer hashcode for the long value.
     */
    public static int Long2H(final long l) {
        return (int)(l ^ (l >>> 32));
    }
}
