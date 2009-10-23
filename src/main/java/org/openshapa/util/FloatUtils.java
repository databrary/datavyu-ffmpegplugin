package org.openshapa.util;

/**
 * Utilities for float data type.
 */
public final class FloatUtils {

    /**
     * Constructor.
     */
    private FloatUtils() {
    }

    /** tolerance value for comparing two doubles for equality. */
    private static final double DELTA = 0.0000001;

    /** Factor to use when converting doubles to fraction strings. */
    private static final int FACTOR = 100000;

    /**
     * Compare two doubles and return true if close enough.
     * @param d1 first double
     * @param d2 second double
     * @return true if close enough to be considered equal
     */
    public static boolean closeEnough(final double d1, final double d2) {
        return (Math.abs(d1 - d2) < DELTA);
    }

    /**
     * Determines the greatest common divisor of two integers.
     *
     * @param a The first of two digits to determine the divisor.
     * @param b The second of two digits to determine the divisor of.
     *
     * @return The greatest common divisor of the two supplied arguments.
     */
    public static int gcd(final int a, final int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    /**
     * Converts a double to a string fraction, i.e. this converts 0.5 into a
     * string reading "1/2".
     *
     * @param d The double to convert into a string fraction.
     *
     * @return The supplied double as a fraction string.
     */
    public static String doubleToFractionStr(final Double d) {
        boolean negative = false;
        Double v = d;

        // Determine if we are dealing with a negative value.
        if (v < 0) {
            negative = true;
            v = -v;
        }

        // Determine the the fraction: "whole numerator/denominator"
        int w = (int) ((v - Math.floor(v)) * FACTOR);
        int gcd = gcd(w, FACTOR);
        int whole = (int) Math.floor(v);
        int numerator = w / gcd;

        // If a negative value was supplied, display a '-'.
        String result = "";
        if (negative) {
            result += "-";
        }

        // If we have a whole number component add it to the output.
        if (whole != 0) {
            result += whole;
        }

        // If we have a whole part, and a fraction add a spacer to the output.
        if (whole != 0 && numerator != 0) {
            result += " ";
        }

        // If we have a fraction component, add it to the output.
        if (numerator != 0) {
            int denominator = FACTOR / gcd;
            result += numerator + "/" + denominator;
        }

        return result;
    }

}
