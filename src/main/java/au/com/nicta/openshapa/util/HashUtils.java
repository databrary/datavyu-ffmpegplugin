package au.com.nicta.openshapa.util;

/**
 * Class for a couple of helper functions for generating hashcodes.
 *
 * @author cfreeman
 */
public final class HashUtils {
    public static int Obj2H(final Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return obj.hashCode();
        }
    }

    public static int Long2H(final long l) {
        return (int)(l ^ (l >>> 32));
    }
}
