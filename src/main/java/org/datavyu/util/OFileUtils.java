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

import org.apache.commons.io.FilenameUtils;

import java.io.File;


/**
 * Utilities for files.
 */
public final class OFileUtils {

    /**
     * Generates the longest common directory for the two given absolute paths.
     *
     * @param path1
     * @param path2
     * @return String representing the longest common directory for path1 and
     * path2. null if no such common directory (i.e. if the files were
     * on different drives)
     */
    public static String longestCommonDir(final String path1,
                                          final String path2) {

        if ((path1 == null) || (path2 == null)) {
            throw new NullPointerException();
        }

        String pathA = FilenameUtils.normalize(path1, true);
        String pathB = FilenameUtils.normalize(path2, true);

        final char sep = '/';

        int iA = pathA.indexOf(sep);
        int iB = pathB.indexOf(sep);

        if ((iA == -1) || (iB == -1)) {
            return null;
        }

        String lcd = null;

        while (pathA.substring(0, iA).equals(pathB.substring(0, iB))) {
            lcd = pathA.substring(0, iA + 1);

            iA = pathA.indexOf(sep, iA + 1);
            iB = pathB.indexOf(sep, iB + 1);

            if ((iA == -1) || (iB == -1)) {
                break;
            }
        }

        return lcd;
    }

    /**
     * Calculate the difference in directory levels between basePath and path.
     * basePath must be a predecessor of path.
     * basePath must be a directory.
     * basePath and path must be valid paths of the same filesystem and mount
     * point.
     * basePath and path must be absolute paths.
     * Directories must have '/' at the end of the path.
     *
     * @param basePath
     * @param path
     * @return a positive integer >= 0 denoting the difference in directory
     * levels if the difference can be determined. -1 if the difference
     * cannot be determined.
     */
    public static int levelDifference(final String basePath,
                                      final String path) {

        if ((basePath == null) || (path == null)) {
            throw new NullPointerException();
        }

        File base = new File(FilenameUtils.normalize(basePath, true));
        File ancestor = new File(FilenameUtils.getFullPath(
                FilenameUtils.normalize(path, true)));

        int diff = 0;


        while (!base.equals(ancestor)) {
            ancestor = ancestor.getParentFile();

            if (ancestor != null) {
                diff++;
            } else {
                return -1;
            }
        }

        return diff;
    }

    /**
     * Generate a string S such that basePath.concat(S).equals(filePath)
     * basePath must be a predecessor of file path.
     * basePath must be a directory.
     * Directories must have '/' at the end of the path.
     *
     * @param basePath
     * @param filePath
     * @return null if filePath does not have basePath as a prefix.
     */
    public static String relativeToBase(final String basePath,
                                        final String filePath) {

        if ((basePath == null) || (filePath == null)) {
            throw new NullPointerException();
        }

        String base = FilenameUtils.normalize(basePath, true);
        String file = FilenameUtils.normalize(filePath, true);

        if (!file.startsWith(base)) {
            return null;
        }

        return file.substring(base.length());
    }

}
