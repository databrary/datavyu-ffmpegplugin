package org.openshapa.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import org.openshapa.models.project.Project;


/**
 * Utilities for files.
 */
public final class FileUtils {

    public static String getFilenameNoExtension(final String filename) {
        return FilenameUtils.removeExtension(filename);
    }

    /**
     * Generates the longest common directory for the two given absolute paths.
     *
     * @param path1
     * @param path2
     * @return String representing the longest common directory for path1 and
     *         path2. null if no such common directory (i.e. if the files were
     *         on different drives)
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
     *
     * @param basePath
     * @param path
     * @return a positive integer >= 0 denoting the difference in directory
     *         levels if the difference can be determined. -1 if the difference
     *         cannot be determined.
     */
    public static int levelDifference(final String basePath,
        final String path) {

        if ((basePath == null) || (path == null)) {
            throw new NullPointerException();
        }

        File base = new File(basePath);
        File ancestor = new File(FilenameUtils.getFullPath(path));

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
     * Builds a relative filepath to check for data files that were missing
     * from their absolute path.
     * @param proj The project file, which importantly stores the original
     * directory the project was saved to.
     * @param dataFile The (absolute path specified) data file we are trying
     * to find.
     * @return A (relative path specified) file, which points at the
     * dataFile passed in if a relative file representing it exists.
     */
    public static File getRelativeFile(final Project proj,
        final File dataFile) {

        String origPath = proj.getOriginalProjectDirectory();
        String filePath = dataFile.getAbsolutePath();
        String relPath = proj.getProjectDirectory();

        if (!relPath.endsWith("\\") && !relPath.endsWith("/")) {
            relPath += "/";
        }

        // Depth of origPath
        int origDepth = 0;

        for (int i = 0; i < origPath.length(); i++) {

            if (origPath.substring(i, i + 1).equals("/")
                    || origPath.substring(i, i + 1).equals("\\")) {
                origDepth++;
            }
        }

        // Matching depth of origPath and dataFile's path
        int matchingDepth = 0;

        // The last (substring index) point at which the paths are the same
        int mathchingIndex = 0;
        int i = 0;

        while ((i < (origPath.length() - 1)) && (i < (filePath.length() - 1))) {

            if (
                !origPath.substring(i, i + 1).equals(
                        filePath.substring(i, i + 1))) {
                break;
            }

            if (origPath.substring(i, i + 1).equals("/")
                    || origPath.substring(i, i + 1).equals("\\")) {
                matchingDepth++;
                mathchingIndex = i;
            }

            i++;
        }

        // Build the relative path

        for (int j = 0; j < (origDepth - matchingDepth); j++) {

            // We go up to the level where our paths match
            relPath += "../";
        }

        // Append the rest of the file's path. The matchingIndex will be the
        // point we start from, since that's where the paths started to diverge.
        relPath += filePath.substring(mathchingIndex);

        return new File(relPath);
    }

}
