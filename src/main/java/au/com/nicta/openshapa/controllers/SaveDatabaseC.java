package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.util.FileFilters.CSVFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;

/**
 * Controller for saving the database to disk.
 *
 * @author cfreeman
 */
public final class SaveDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveDatabaseC.class);

    /**
     * Constructor.
     *
     * @param destinationFile The destination to use when saving the CSV file.
     * @param fileFilter The selected filter to use when saving the file.
     */
    public SaveDatabaseC(final String destinationFile,
                         final FileFilter fileFilter) {
        if (fileFilter.getClass() == CSVFilter.class) {
            saveAsCSV(destinationFile + ".csv");
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     */
    public void saveAsCSV(final String outFile) {
        Database db = OpenSHAPA.getDatabase();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            Vector<DataColumn> cols = db.getDataColumns();
            for (int i = 0; i < cols.size(); i++) {
                DataColumn dc = cols.get(i);

                out.write(dc.getName());
                out.newLine();
                for (int j = 1; j <= dc.getNumCells(); j++) {
                    DataCell c = (DataCell) dc.getDB().getCell(dc.getID(), j);
                    out.write(c.getOnset().toString());
                    out.write(",");
                    out.write(c.getOffset().toString());
                    out.write(",");
                    String value = c.getVal().toString();
                    value = value.substring(1, value.length() - 1);
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
