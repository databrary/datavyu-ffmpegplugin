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
