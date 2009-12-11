package org.openshapa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 */
public class UiUtil {

    /**
     * Checks if two text files are equal.
     *
     * @param file1 First file
     * @param file2 Second file
     *
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    public static Boolean areFilesSame(final File file1, final File file2)
    throws IOException {
        BufferedReader r1 = new BufferedReader(new FileReader(file1));
        BufferedReader r2 = new BufferedReader(new FileReader(file2));

        String line1 = r1.readLine();
        String line2 = r2.readLine();
        if (!line1.equals(line2)) {
            return false;
        }

        while (line1 != null && line2 != null) {
            if (!line1.equals(line2)) {
                return false;
            }

            line1 = r1.readLine();
            line2 = r2.readLine();
        }

        return true;
    }
}
