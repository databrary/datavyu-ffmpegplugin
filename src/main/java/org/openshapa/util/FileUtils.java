package org.openshapa.util;

/**
 * Utilities for files.
 */
public final class FileUtils {

    public static String getFilenameNoExtension(final String filename) {
        if (filename.equals(".")) {
            return filename;
        }
        if (filename.equals("..")) {
            return filename;
        }
        int match = filename.lastIndexOf(".");
        if (match == -1) {
            return filename;
        }
        return filename.substring(0, match);
    }

}
