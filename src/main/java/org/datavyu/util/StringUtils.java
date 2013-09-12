/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.util;

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
            } else if (input.charAt(n) == '\r') {
                char[] buff = {'\\', '\n'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '-') {
                char[] buff = {'\\', '-'};
                result = result.concat(new String(buff));
            } else {
                result += input.charAt(n);
            }
        }

        // Remove all control characters
        return result.replaceAll("[\u0000-\u0001]", "");
    }
    
    public static String escapeCSVArgument(final String input) {
        String result = "";

        for (int n = 0; n < input.length(); n++) {
            if (input.charAt(n) == '\\') {
                char[] buff = {'\\', '\\'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '\n') {
                char[] buff = {'\\', '\n'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '\r') {
                char[] buff = {'\\', '\n'};
                result = result.concat(new String(buff));
            } else if (input.charAt(n) == '-') {
                char[] buff = {'\\', '-'};
                result = result.concat(new String(buff));
            } else {
                result += input.charAt(n);
            }
        }

        // Remove all control characters
        return result.replaceAll("[\u0000-\u0001]", "");
    }
}
