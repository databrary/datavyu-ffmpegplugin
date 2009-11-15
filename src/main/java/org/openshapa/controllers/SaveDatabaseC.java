package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.MacshapaDatabase;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.FileFilters.CSVFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.MatrixVocabElement.MatrixType;
import org.openshapa.util.FileFilters.MODBFilter;

/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveDatabaseC.class);

    /**
     * Constructor.
     *
     * @param destinationFile The destination to use when saving the c
     */
    public SaveDatabaseC(final File destinationFile) {
        String outputFile = destinationFile.getName().toLowerCase();
        String extension = outputFile.substring(outputFile.lastIndexOf('.'),
                                                outputFile.length());

        if (extension.equals(".csv")) {
            saveAsCSV(destinationFile.toString());
        } else if (extension.equals(".odb")) {
            saveAsMacSHAPADB(destinationFile.toString());
        }
    }

    /**
     * Constructor.
     *
     * @param destinationFile The destination to use when saving the database.
     * @param fileFilter The selected filter to use when saving the database.
     */
    public SaveDatabaseC(final String destinationFile,
                         final FileFilter fileFilter) {

        String outputFile = destinationFile.toLowerCase();
        File outFile = new File(outputFile);

        if (fileFilter.getClass() == CSVFilter.class) {
            // BugzID:541 - Don't append ".csv" if the path already contains it.
            if (!outputFile.contains(".csv")) {
                outputFile = destinationFile.concat(".csv");
            }
            saveAsCSV(outputFile);

        } else if (fileFilter.getClass() == MODBFilter.class) {
            // Don't append ".db" if the path already contains it.
            if (!outputFile.contains(".odb")) {
                outputFile = destinationFile.concat(".odb");
            }

            saveAsMacSHAPADB(outputFile);
        }

        // BugzID:449 - Set filename in spreadsheet window and database to
        // be the same as the file specified.
        try {
            String dbName = outFile.getName().substring(0, outFile.getName()
                                                           .lastIndexOf('.'));
            OpenSHAPA.getDatabase().setName(dbName);
            OpenSHAPA.getDatabase().setSourceFile(outFile);

            // Update the name of the window to include the name we just
            // set in the database.
            JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
            ResourceMap rMap = OpenSHAPA.getApplication().getContext()
                                        .getResourceMap(OpenSHAPA.class);
            mainFrame.setTitle(rMap.getString("Application.title")
                               + " - " + outFile.getName());
        } catch (SystemErrorException se) {
            logger.error("Can't set db name to specified file.", se);
        }
    }

    /**
     * Saves the database to the specified destination in a MacSHAPA format.
     *
     * @param outFile The path of the file to use when writing to disk.
     */
    public void saveAsMacSHAPADB(final String outFile) {
        try {
            PrintStream outStream = new PrintStream(outFile);
            OpenSHAPA.getDatabase().toMODBFile(outStream, "\r");
            outStream.close();
        } catch (FileNotFoundException e) {
            logger.error("Can't write macshapa db file '" + outFile + "'", e);
        } catch (SystemErrorException e) {
            logger.error("Can't write macshapa db file '" + outFile + "'", e);
        } catch (IOException e) {
            logger.error("Can't write macshapa db file '" + outFile + "'", e);
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     */
    public void saveAsCSV(final String outFile) {
        MacshapaDatabase db = OpenSHAPA.getDatabase();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            Vector<Long> colIds = db.getColOrderVector();

            for (int i = 0; i < colIds.size(); i++) {
                DataColumn dc = db.getDataColumn(colIds.get(i));
                boolean isMatrix = false;

                out.write(dc.getName() + " (" + dc.getItsMveType() + ")");

                // If we a matrix type - we need to dump the formal args.
                MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());
                if (dc.getItsMveType() == MatrixType.MATRIX) {
                    isMatrix = true;
                    out.write("-");
                    for (int j = 0; j < mve.getNumFormalArgs(); j++) {
                        FormalArgument fa = mve.getFormalArgCopy(j);
                        String name = fa.getFargName()
                                   .substring(1, fa.getFargName().length() - 1);
                        out.write(name + "|" + fa.getFargType().toString());

                        if (j < mve.getNumFormalArgs() - 1) {
                            out.write(",");
                        }
                    }
                }

                out.newLine();
                for (int j = 1; j <= dc.getNumCells(); j++) {
                    DataCell c = (DataCell) dc.getDB().getCell(dc.getID(), j);
                    out.write(c.getOnset().toString());
                    out.write(",");
                    out.write(c.getOffset().toString());
                    out.write(",");
                    String value = c.getVal().toEscapedString();

                    if (!isMatrix) {
                        value = value.substring(1, value.length() - 1);
                    }
                    out.write(value);
                    out.newLine();
                }
            }
            out.close();

        } catch (IOException e) {
            logger.error("unable to save database as CSV file", e);
        } catch (SystemErrorException se) {
            logger.error("Unable to save database as CSV file", se);
        }
    }
}
