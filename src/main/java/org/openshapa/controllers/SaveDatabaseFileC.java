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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Vector;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import database.DataCell;
import database.DataColumn;
import database.FormalArgument;
import database.MatrixVocabElement;
import database.PredicateVocabElement;
import database.SystemErrorException;
import database.FormalArgument.FArgType;
import database.MatrixVocabElement.MatrixType;

import com.usermetrix.jclient.UserMetrix;

import database.MacshapaDatabase;
import java.io.FileOutputStream;

import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.DeprecatedDatabase;
import org.openshapa.models.db.UserWarningException;
import org.openshapa.util.StringUtils;


/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseFileC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(SaveDatabaseFileC.class);

    /**
     * Saves the database to the specified destination, if the file ends with
     * .csv, the database is saved in a CSV format, if the database ends with
     * .odb, the database is saved in a MacSHAPA format.
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
        } else if (extension.equals(".odb")) {
            saveAsMacSHAPADB(destinationFile.toString(), ds);
        }
    }

    /**
     * Saves the database to the specified destination in a MacSHAPA format.
     *
     * @param outFile The path of the file to use when writing to disk.
     * @param ds The datastore to save as a MacSHAPA db format.
     * @throws UserWarningException When unable to save the database as a
     * macshapa database to disk (usually because of permissions errors).
     */
    public void saveAsMacSHAPADB(final String outFile,
                                 final Datastore ds)
    throws UserWarningException {

        try {
            LOGGER.event("save database as ODB");

            PrintStream outStream = new PrintStream(outFile);
            ((DeprecatedDatabase) ds).getDatabase().toMODBFile(outStream, "\r");
            outStream.close();

        } catch (FileNotFoundException e) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                           .getContext().getResourceMap(OpenSHAPA.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", outFile), e);

        } catch (IOException e) {
            ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                          .getContext().getResourceMap(OpenSHAPA.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", outFile), e);

        } catch (SystemErrorException e) {
            LOGGER.error("Can't write macshapa db file '" + outFile + "'", e);
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     * @param ds The datastore to save as a CSV file.
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
     * @throws UserWarningException When unable to save the database as a CSV to
     * disk (usually because of permissions errors).
     */
    public void saveAsCSV(final OutputStream outStream, final Datastore ds)
    throws UserWarningException {

        try {
            MacshapaDatabase db = ((DeprecatedDatabase) ds).getDatabase();
            LOGGER.event("save database as CSV to stream");

            // Dump out an identifier for the version of file.
            PrintStream ps = new PrintStream(outStream);
            ps.println("#4");

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

            // Dump out the data from all the columns.
            Vector<Long> colIds = db.getColOrderVector();

            for (int i = 0; i < colIds.size(); i++) {
                DataColumn dc = db.getDataColumn(colIds.get(i));
                boolean isMatrix = false;
                if (dc.getItsMveType() == MatrixType.UNDEFINED) {
                    ps.printf("%s (%s,%s,%s)", StringUtils.escapeCSV(dc.getName()),
                        MatrixType.NOMINAL, !dc.getHidden(), dc.getComment());
                } else {
                    ps.printf("%s (%s,%s,%s)", StringUtils.escapeCSV(dc.getName()),
                        dc.getItsMveType(), !dc.getHidden(), dc.getComment());
                }

                // If we a matrix type - we need to dump the formal args.
                MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());

                if (dc.getItsMveType() == MatrixType.MATRIX) {
                    isMatrix = true;
                    ps.print('-');

                    for (int j = 0; j < mve.getNumFormalArgs(); j++) {
                        FormalArgument fa = mve.getFormalArgCopy(j);
                        String name = fa.getFargName().substring(1,
                                fa.getFargName().length() - 1);
                        if (fa.getFargType() == FArgType.UNTYPED || fa.getFargType() == FArgType.UNDEFINED) {
                            ps.printf("%s|%s", StringUtils.escapeCSV(name),
                                FArgType.NOMINAL.toString());
                        } else {
                            ps.printf("%s|%s", StringUtils.escapeCSV(name),
                                fa.getFargType().toString());
                        }

                        if (j < (mve.getNumFormalArgs() - 1)) {
                            ps.print(',');
                        }
                    }
                }

                ps.println();

                for (int j = 1; j <= dc.getNumCells(); j++) {
                    DataCell c = (DataCell) dc.getDB().getCell(dc.getID(), j);

                    String value = c.getVal().toEscapedString();

                    if (!isMatrix) {
                        value = value.substring(1, value.length() - 1);
                    }

                    ps.printf("%s,%s,%s", c.getOnset().toString(),
                        c.getOffset().toString(), value);
                    ps.println();
                }
            }
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to save database as CSV file", se);
        }
    }
}
