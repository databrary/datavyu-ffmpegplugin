package org.openshapa.util;

public final class StringUtils {

    /**
     * @param input The string to escape ',' and '\' characters.
     *
     * @return A copy of the input string - but with ',', '-' and '\'
     * characters escaped with a leading '\'.
     */
    public static String escapeCSV(final String input) {
        String result = "";

        for (int n = 0; n < input.length(); n++) {
            if (input.charAt(n) == '\\') {
                char[] buff = {'\\', '\\'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == ',') {
                char[] buff = {'\\', ','};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '\n') {
                char[] buff = {'\\', '\n'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '-') {
                char[] buff = {'\\', '-'};
                result = result.concat(new String(buff));
            } else {
                result += input.charAt(n);
            }
        }

        return result;
    }
}
