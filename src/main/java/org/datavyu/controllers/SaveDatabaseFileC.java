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
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.Datavyu;
import org.datavyu.models.db.*;
import org.datavyu.util.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import java.io.*;


/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseFileC {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(SaveDatabaseFileC.class);

    /**
     * Saves the database to the specified destination, if the file ends with
     * .csv, the database is saved in a CSV format.
     *
     * @param destinationFile The destination to save the database too.
     * @param ds              The datastore to save to disk.
     * @throws UserWarningException If unable to save the database to the
     *                              desired location.
     */
    public void saveDatabase(final File destinationFile,
                             final Datastore ds)
            throws UserWarningException {

        // We bypass any overwrite checks here.
        String outputFile = destinationFile.getName().toLowerCase();
        String extension = outputFile.substring(outputFile.lastIndexOf('.'), outputFile.length());

        if (extension.equals(".csv")) {
            saveAsCSV(destinationFile.toString(), ds);
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     * @param ds      The datastore to save as a CSV file.
     * @throws UserWarningException When unable to save the database as a CSV to
     *                              disk (usually because of permissions errors).
     */
    public void saveAsCSV(final String outFile, final Datastore ds)
            throws UserWarningException {

        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            saveAsCSV(fos, ds);
            fos.close();
        } catch (IOException ie) {
            ResourceMap rMap = Application.getInstance(Datavyu.class)
                    .getContext().getResourceMap(Datavyu.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", outFile), ie);
        }
    }

    /**
     * Serialize the database to the specified stream in a CSV format.
     *
     * @param outStream The stream to use when serializing.
     * @param ds        The datastore to save as a CSV file.
     * @throws UserWarningException When unable to save the database as a CSV to
     *                              disk (usually because of permissions errors).
     */
    public void saveAsCSV(final OutputStream outStream, final Datastore ds)
            throws UserWarningException {
        LOGGER.event("save database as CSV to stream");

        // Dump out an identifier for the version of file.
        PrintStream ps = new PrintStream(outStream);
        ps.println("#4");

        for (Variable variable : ds.getAllVariables()) {
            ps.printf("%s (%s,%s,%s)",
                    StringUtils.escapeCSV(variable.getName()),
                    variable.getRootNode().type,
                    !variable.isHidden(),
                    "");

            if (variable.getRootNode().type == Argument.Type.MATRIX) {
                ps.print('-');

                int numArgs = 0;
                for (Argument arg : variable.getRootNode().childArguments) {
                    ps.printf("%s|%s",
                            StringUtils.escapeCSV(arg.name),
                            arg.type);

                    if (numArgs < (variable.getRootNode().childArguments.size() - 1)) {
                        ps.print(',');
                    }
                    numArgs++;
                }
            }

            ps.println();

            for (Cell cell : variable.getCells()) {
                ps.printf("%s,%s,%s",
                        cell.getOnsetString(),
                        cell.getOffsetString(),
                        cell.getValue().serialize());
                ps.println();
            }
        }
    }
}
