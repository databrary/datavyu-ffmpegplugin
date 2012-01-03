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
package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.io.*;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.*;
import org.openshapa.util.StringUtils;


/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseFileC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SaveDatabaseFileC.class);

    /**
     * Saves the database to the specified destination, if the file ends with
     * .csv, the database is saved in a CSV format.
     *
     * @param destinationFile The destination to save the database too.
     * @param ds The datastore to save to disk.
     *
     * @throws UserWarningException If unable to save the database to the
     * desired location.
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
     * @param ds The datastore to save as a CSV file.
     *
     * @throws UserWarningException When unable to save the database as a CSV to
     * disk (usually because of permissions errors).
     */
    public void saveAsCSV(final String outFile, final Datastore ds)
    throws UserWarningException {

        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            saveAsCSV(fos, ds);
            fos.close();
        } catch (IOException ie) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                          .getContext().getResourceMap(OpenSHAPA.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", outFile), ie);
        }
    }

    /**
     * Serialize the database to the specified stream in a CSV format.
     *
     * @param outStream The stream to use when serializing.
     * @param ds The datastore to save as a CSV file.
     *
     * @throws UserWarningException When unable to save the database as a CSV to
     * disk (usually because of permissions errors).
     */
    public void saveAsCSV(final OutputStream outStream, final Datastore ds)
    throws UserWarningException {
        LOGGER.event("save database as CSV to stream");

        // Dump out an identifier for the version of file.
        PrintStream ps = new PrintStream(outStream);
        ps.println("#4");

        /**
        PREDICATES CURRENTLY UNSUPPORTED - TODO REIMPLEMENT.

        // Dump out all the predicates held within the database.
        Vector<PredicateVocabElement> predicates = db.getPredVEs();
        if (predicates.size() > 0) {
            int counter = 0;

            //Read them in reverse because they're loaded in reverse.
            //This can be resolved by finding why they're loaded in reverse (database)
            //for (PredicateVocabElement pve : predicates) {\
            for (int i = predicates.size() - 1; i >= 0; i--) {
                PredicateVocabElement pve = predicates.elementAt(i);
                ps.printf("%d:%s-", counter,
                    StringUtils.escapeCSV(pve.getName()));

                for (int j = 0; j < pve.getNumFormalArgs(); j++) {
                    FormalArgument fa = pve.getFormalArgCopy(j);
                    String name = fa.getFargName().substring(1,
                            fa.getFargName().length() - 1);
                    if (fa.getFargType() == FArgType.UNTYPED || fa.getFargType() == FArgType.UNDEFINED) {
                        ps.printf("%s|%s", StringUtils.escapeCSV(name),
                            FArgType.NOMINAL.toString());
                    } else {
                        ps.printf("%s|%s", StringUtils.escapeCSV(name),
                            fa.getFargType().toString());
                    }

                    if (j < (pve.getNumFormalArgs() - 1)) {
                        ps.print(',');
                    }
                }

                ps.println();
                counter++;
            }
        }
        */

        for (Variable variable : ds.getAllVariables()) {
            ps.printf("%s (%s,%s,%s)",
                        StringUtils.escapeCSV(variable.getName()),
                        variable.getVariableType().type,
                        !variable.isHidden(),
                        "");

            if (variable.getVariableType().type == Argument.Type.MATRIX) {
                ps.print('-');

                int numArgs = 0;
                for (Argument arg : variable.getVariableType().childArguments) {
                    ps.printf("%s|%s",
                              StringUtils.escapeCSV(arg.name),
                              arg.type);

                    if (numArgs < (variable.getVariableType().childArguments.size() - 1)) {
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
                          cell.getValueAsString());
                ps.println();
            }
        }
    }
}
