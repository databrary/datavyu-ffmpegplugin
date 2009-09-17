package org.openshapa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.openshapa.OpenSHAPA;
import org.openshapa.db.Column;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.DataValue;
import org.openshapa.db.Database;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.FloatFormalArg;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.IntDataValue;
import org.openshapa.db.IntFormalArg;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.NominalFormalArg;
import org.openshapa.db.QuoteStringDataValue;
import org.openshapa.db.QuoteStringFormalArg;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.UnTypedFormalArg;
import org.openshapa.db.UndefinedDataValue;

/**
 * Controller for opening a database from disk.
 */
public final class OpenDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OpenDatabaseC.class);

    /** The index of the ONSET timestamp in the CSV line. */
    private static final int DATA_ONSET = 0;

    /** The index of the OFFSET timestampe in the CSV line. */
    private static final int DATA_OFFSET = 1;

    /** The start of the data arguments. */
    private static final int DATA_INDEX = 2;

    /**
     * Constructor.
     *
     * @param sourceFile The source file to use when opening a database from
     * disk.
     */
    public OpenDatabaseC(final File sourceFile) {
        this.loadCSV(sourceFile);

        // BugzID:449 - Set filename in spreadsheet window and database if the
        // database name is undefined.
        try {
            if (OpenSHAPA.getDatabase().getName().equals("Undefined")) {
                String dbName = sourceFile.getName();
                dbName = dbName.substring(0, dbName.lastIndexOf('.'));
                OpenSHAPA.getDatabase().setName(dbName);

                // Update the name of the window to include the name we just set
                // in the database.
                JFrame mainFrame = OpenSHAPA.getApplication().getMainFrame();
                ResourceMap rMap = OpenSHAPA.getApplication()
                                            .getContext()
                                            .getResourceMap(OpenSHAPA.class);

                mainFrame.setTitle(rMap.getString("Application.title")
                               + " - " + OpenSHAPA.getDatabase().getName());
            }
        } catch (SystemErrorException se) {
            logger.error("Can't set db name to the name of the CSV file.", se);
        }
    }

    /**
     * This method parses a CSV file and populates the database
     * (and spreadsheet) with data.
     *
     * @param sFile The source file to use when populating the database.
     */
    public void loadCSV(final File sFile) {
        try {
            Database db = OpenSHAPA.getDatabase();
            BufferedReader csvFile = new BufferedReader(new FileReader(sFile));

            // Read each line of the CSV file.
            String line = csvFile.readLine();
            while (line != null) {
                line = parseVariable(csvFile, line, db);
            }

            csvFile.close();
        } catch (FileNotFoundException e) {
            logger.error("Unable to load CSV file.", e);
        } catch (IOException e) {
            logger.error("Unable to read line from CSV file", e);
        } catch (SystemErrorException e) {
            logger.error("Unable to populate databse from CSV file", e);
        } catch (LogicErrorException e) {
            logger.error("Corrupted CSV file", e);
        }
    }

    /**
     * Method to invoke when we encounter a block of text in the CSV file that
     * is the contents of a predicate variable.
     *
     * @param csvFile The csvFile we are currently parsing.
     * @param dc The datacolumn that we will be adding cells too.
     *
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     *
     * @throws IOException If unable to read the file correctly.
     * @throws SystemErrorException If unable to update the database with the
     * predicate variable data.
     */
    private String parsePredicateVariable(final BufferedReader csvFile,
                                          final DataColumn dc)
    throws IOException, SystemErrorException {
        // Keep parsing lines and putting them in the newly formed nominal
        // variable until we get to a line indicating the end of file or a new
        // variable section.
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {
            // Split the line into tokens using a comma delimiter.
            String[] tokens = line.split(",");

            // Create the data cell from line in the CSV file.
            DataCell cell = new DataCell(dc.getDB(),
                                         dc.getID(),
                                         dc.getItsMveID());

            // Set the onset and offset from tokens in the line.
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Empty predicate - just add the empty data cell.
            if (tokens[DATA_INDEX].equals("()")) {
                // Add the populated cell to the database.
                dc.getDB().appendCell(cell);

            // Non empty predicate - need to check if we need to add an entry to
            // the vocab, and create it if it doesn't exist. Otherwise we just
            // plow ahead and add the predicate to the database.
            } else {
                // Still need to parse predicate variables.
            }

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    /**
     * Method to invoke when we encounter a block of text in the CSV file that
     * is the contents of a predicate variable.
     *
     * @param csvFile The csvFile we are currently parsing.
     * @param dc The datacolumn that we will be adding cells too.
     * @param mve The matrix vocab we are using when parsing individual matrix
     * elements to put in the spreadsheet.
     *
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     *
     * @throws IOException If unable to read the file correctly.
     * @throws SystemErrorException If unable to update the database with the
     * predicate variable data.
     */
    private String parseMatrixVariable(final BufferedReader csvFile,
                                       final DataColumn dc,
                                       final MatrixVocabElement mve)
    throws IOException, SystemErrorException {
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {
            // Split the line into tokens using a comma delimiter.
            String[] tokens = line.split(",");

            // Create the data cell from line in the CSV file.
            DataCell cell = new DataCell(dc.getDB(),
                                         dc.getID(),
                                         dc.getItsMveID());

            // Set the onset and offset from tokens in the line.
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Strip the brackets from the first and last argument.
            tokens[DATA_INDEX] = tokens[DATA_INDEX]
                                 .substring(1, tokens[DATA_INDEX].length());

            int end = tokens.length - 1;
            tokens[end] = tokens[end].substring(0, tokens[end].length() - 1);

            Vector<DataValue> arguments = new Vector<DataValue>();
            for (int i = 0; i < mve.getNumFormalArgs(); i++) {
                FormalArgument ma = mve.getFormalArg(i);
                boolean emptyArg = false;
                if (tokens[i + 2].charAt(0) == '<') {
                    emptyArg = true;
                }
                tokens[i + 2] = tokens[i + 2].trim();

                switch(ma.getFargType()) {
                    case TEXT:
                    case QUOTE_STRING:
                        QuoteStringDataValue qsdv =
                                           new QuoteStringDataValue(dc.getDB());
                        if (!emptyArg) {
                            qsdv.setItsValue(tokens[i + 2]);
                        }
                        arguments.add(qsdv);
                        break;
                    case NOMINAL:
                        NominalDataValue ndv = new NominalDataValue(dc.getDB());
                        if (!emptyArg) {
                            ndv.setItsValue(tokens[i + 2]);
                        }
                        arguments.add(ndv);
                        break;
                    case INTEGER:
                        IntDataValue idv = new IntDataValue(dc.getDB());
                        if (!emptyArg) {
                            idv.setItsValue(tokens[i + 2]);
                        }
                        arguments.add(idv);
                        break;
                    case FLOAT:
                        FloatDataValue fdv = new FloatDataValue(dc.getDB());
                        if (!emptyArg) {
                            fdv.setItsValue(tokens[i + 2]);
                        }
                        arguments.add(fdv);
                        break;
                    default:
                        UndefinedDataValue udv =
                                             new UndefinedDataValue(dc.getDB());
                        if (!emptyArg) {
                            udv.setItsValue(tokens[i + 2]);
                        }
                        arguments.add(udv);
                        break;
                }
            }
            Matrix m = new Matrix(dc.getDB(), mve.getID(), arguments);
            cell.setVal(m);

            // Add the populated cell to the database.
            dc.getDB().appendCell(cell);

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    /**
     * Method to invoke when we encounter a block of text in the CSV file that
     * is the contents of a variable.
     *
     * @param csvFile The csvFile we are currently parsing.
     * @param dc The datacolumn that we will be adding cells too.
     * @param The populator to use when converting the contents of the cell into
     * a datavalue that can be inserted into the spreadsheet.
     *
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     *
     * @throws IOException If unable to read the file correctly.
     * @throws SystemErrorException If unable to update the database with the
     * datavalues we are creating from the populator.
     */
    private String parseEntries(final BufferedReader csvFile,
                                final DataColumn dc,
                                final EntryPopulator populator)
    throws IOException, SystemErrorException {

        // Keep parsing lines and putting them in the newly formed nominal
        // variable until we get to a line indicating the end of file or a new
        // variable section.
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {

            // Split the line into tokens using a comma delimiter.
            String[] tokens = line.split(",");

            // Create the data cell from line in the CSV file.
            DataCell cell = new DataCell(dc.getDB(),
                                         dc.getID(),
                                         dc.getItsMveID());

            // Set the onset and offset from tokens in the line.
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Insert the datavalue in the cell.
            long mveId = dc.getDB().getMatrixVE(dc.getItsMveID()).getID();
            Matrix m = Matrix.Construct(dc.getDB(), mveId,
                                        populator.createValue(tokens));
            cell.setVal(m);

            // Add the populated cell to the database.
            dc.getDB().appendCell(cell);

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    /**
     * Method to invoke when we encounter a block of text that is a variable.
     *
     * @param csvFile The CSV file we are currently reading.
     * @param line The line of the CSV file we are currently reading.
     * @param db The database we are populating with data from the CSV file.
     *
     * @return The next String that is not part of the currently variable that
     * we are parsing.
     *
     * @throws IOException When we are unable to read from the csvFile.
     * @throws SystemErrorException When we are unable to populate the variable
     * with information from the CSV file.
     * @throws LogicErrorException When we are unable to create a new variable
     * from the CSV file (i.e the variable already exists in the database).
     */
    private String parseVariable(final BufferedReader csvFile,
                                 final String line,
                                 final Database db)
    throws IOException, SystemErrorException, LogicErrorException {
        // Determine the variable name and type.
        String[] tokens = line.split("\\(");
        String varName = tokens[0].trim();
        String varType = tokens[1].substring(0, tokens[1].indexOf(")"));

        // Create variable to put cells within.
        Column.isValidColumnName(db, varName);
        DataColumn dc = new DataColumn(db, varName, getVarType(varType));
        long colId = db.addColumn(dc);
        dc = db.getDataColumn(colId);

        // Read text variable.
        if (getVarType(varType) == MatrixVocabElement.MatrixType.TEXT) {
            return parseEntries(csvFile, dc, new PopulateText(dc.getDB()));

        // Read nominal variable.
        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.NOMINAL) {
            return parseEntries(csvFile, dc, new PopulateNominal(dc.getDB()));

        // Read integer variable.
        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.INTEGER) {
            return parseEntries(csvFile, dc, new PopulateInteger(dc.getDB()));

        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.FLOAT) {
            return parseEntries(csvFile, dc, new PopulateFloat(dc.getDB()));

        // Read matrix variable.
        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.MATRIX) {
            // Build vocab for matrix.
            String[] vocabString = tokens[1].split("-");
            String[] vocabElems = vocabString[1].split(",");
            MatrixVocabElement mve = db.getMatrixVE(varName);
            // delete default formal argument in column
            mve.deleteFormalArg(0);

            // For each of the formal arguments in the file - parse it and
            // create a formal argument in the matrix vocab element.
            for (int i = 0; i < vocabElems.length; i++) {
                FormalArgument fa;
                String[] vocabElement = vocabElems[i].split("\\|");

                // Add text formal argument.
                if (vocabElement[1].equalsIgnoreCase("quote_string")) {
                    fa = new QuoteStringFormalArg(db,
                                                  "<" + vocabElement[0] + ">");

                // Add nominal formal argument.
                } else if (vocabElement[1].equalsIgnoreCase("nominal")) {
                    fa = new NominalFormalArg(db, "<" + vocabElement[0] + ">");

                // Add integer formal argument.
                } else if (vocabElement[1].equalsIgnoreCase("integer")) {
                    fa = new IntFormalArg(db, "<" + vocabElement[0] + ">");

                // Add float formal argument.
                } else if (vocabElement[1].equalsIgnoreCase("float")) {
                    fa = new FloatFormalArg(db, "<" + vocabElement[0] + ">");

                // Not sure what it is - add undefined formal argument.
                } else {
                    fa = new UnTypedFormalArg(db, "<" + vocabElement[0] + ">");
                }

                mve.appendFormalArg(fa);
            }

            db.replaceMatrixVE(mve);
            mve = db.getMatrixVE(varName);
            return parseMatrixVariable(csvFile, dc, mve);

        // Read predicate variable.
        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.PREDICATE) {
            return parsePredicateVariable(csvFile, dc);
        }

        throw null;
    }

    /**
     * @param type The string containing the variable type.
     *
     * @return The MatrixType of the variable.
     */
    private MatrixVocabElement.MatrixType getVarType(final String type) {
        if (type.equalsIgnoreCase("text")) {
            return MatrixVocabElement.MatrixType.TEXT;

        } else if (type.equalsIgnoreCase("nominal")) {
            return MatrixVocabElement.MatrixType.NOMINAL;

        } else if (type.equalsIgnoreCase("predicate")) {
            return MatrixVocabElement.MatrixType.PREDICATE;

        } else if (type.equalsIgnoreCase("matrix")) {
            return MatrixVocabElement.MatrixType.MATRIX;

        } else if (type.equalsIgnoreCase("integer")) {
            return MatrixVocabElement.MatrixType.INTEGER;

        } else if (type.equalsIgnoreCase("float")) {
            return MatrixVocabElement.MatrixType.FLOAT;
        }

        // Unknown type.
        return MatrixVocabElement.MatrixType.UNDEFINED;
    }

    /**
     * A populator for creating data values that can be used to populate
     * database spreadsheet cells.
     */
    private abstract class EntryPopulator {
        private Database db;

        /**
         * Constructor.
         *
         * @param targetDB The destination database for data values.
         */
        public EntryPopulator(Database targetDB) {
            db = targetDB;
        }

        /**
         * @return The database that this populator is filling.
         */
        Database getDatabase() {
            return db;
        }

        /**
         * Creates a DataValue from the supplied array of tokens.
         *
         * @param tokens The tokens to use when building a DataValue.
         *
         * @return A DataValue that can be used for a new SpreadsheetCell.
         *
         * @throws SystemErrorException When unable to create the DataValue from
         * the supplied array of tokens.
         */
        abstract DataValue createValue(final String[] tokens)
        throws SystemErrorException;
    }

    /**
     * EntryPopulator for creating integer values.
     */
    private class PopulateInteger extends EntryPopulator {

        /**
         * Constructor.
         *
         * @param targetDB The destination database for integer data values.
         */
        public PopulateInteger(Database targetDB) {
            super(targetDB);
        }

        /**
         * Creates a IntDataValue from the supplied array of tokens.
         *
         * @param tokens The tokens to use when building a IntDataValue
         *
         * @return A IntDataValue that can be used in a SpreadsheetCell.
         *
         * @throws SystemErrorException When unable to create the IntDataValue
         * from the supplied array of tokens.
         */
        public DataValue createValue(final String[] tokens)
        throws SystemErrorException {
            IntDataValue idv = new IntDataValue(getDatabase());
            idv.setItsValue(tokens[DATA_INDEX]);
            return idv;
        }
    }

    /**
     * EntryPopulator for creating float values.
     */
    private class PopulateFloat extends EntryPopulator {

        /**
         * Constructor.
         *
         * @param targetDB The destination database for float data values.
         */
        public PopulateFloat(Database targetDB) {
            super(targetDB);
        }

        /**
         * Creates a FloatDataValue from the supplied array of tokens.
         *
         * @param tokens The tokens to use when building a FloatDataValue.
         *
         * @return A FloatDataValue that can be used in a SpreadsheetCell.
         *
         * @throws SystemErrorException When unable to create the FloatDataValue
         * from the supplied array of tokens.
         */
        public DataValue createValue(final String[] tokens)
        throws SystemErrorException {
            FloatDataValue fdv = new FloatDataValue(getDatabase());
            fdv.setItsValue(tokens[DATA_INDEX]);
            return fdv;
        }
    }

    /**
     * EntryPopulator for creating nominal data values.
     */
    private class PopulateNominal extends EntryPopulator {
        /**
         * Constructor.
         *
         * @param targetDB The destination database for nominal data values.
         */
        public PopulateNominal(Database targetDB) {
            super(targetDB);
        }

        /**
         * Creates a NominalDataValue from the supplied array of tokens.
         *
         * @param tokens The tokens to use when building a NominalDataValue.
         *
         * @return A NominalDataValue that can be used in a SpreadsheetCell.
         *
         * @throws SystemErrorException When unable to create the
         * NominalDataValue from the supplied array of tokens.
         */
        public DataValue createValue(final String[] tokens)
        throws SystemErrorException {
            NominalDataValue ndv = new NominalDataValue(getDatabase());
            ndv.setItsValue(tokens[DATA_INDEX]);
            return ndv;
        }
    }

    /**
     * EntryPopulator for creating text data values.
     */
    private class PopulateText extends EntryPopulator {
        /**
         * Constructor.
         *
         * @param targetDB The destination database for the text data values.
         */
        public PopulateText(Database targetDB) {
            super(targetDB);
        }

        /**
         * Creates a TextStringDataValue from the supplied array of tokens.
         *
         * @param tokens The tokens to use when building a TextStringDataValue.
         *
         * @return A TextStringDataValue that can be used in a SpreadsheetCell.
         *
         * @throws SystemErrorException When unable to create the
         * TextStringDataValue from the supplied array of tokens.
         */
        public DataValue createValue(final String[] tokens)
        throws SystemErrorException {
            TextStringDataValue tsdv = new TextStringDataValue(getDatabase());

            String text = new String("");
            for (int i = DATA_INDEX; i < tokens.length; i++) {
                text = text.concat(tokens[i]);

                if (i < (tokens.length - 1)) {
                    text = text.concat(",");
                }
            }
            tsdv.setItsValue(text);

            return tsdv;
        }
    }
}
