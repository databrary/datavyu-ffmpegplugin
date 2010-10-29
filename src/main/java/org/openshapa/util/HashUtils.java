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

    /**
     * Given a SHA-1 message digest in byte array form, create it's string
     * representation.
     *
     * This was taken from:
     * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
     *
     * License permitting its use:
     * http://www.anyexample.com/license.xml
     * 
     * @param data
     * @return String representation of the message digest
     */
    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
}
