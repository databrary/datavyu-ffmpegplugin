package org.openshapa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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
import org.openshapa.db.MacshapaODBReader;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.NominalFormalArg;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.PredicateVocabElement;
import org.openshapa.db.QuoteStringDataValue;
import org.openshapa.db.QuoteStringFormalArg;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.UnTypedFormalArg;
import org.openshapa.db.UndefinedDataValue;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.SpreadsheetPanel;

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
        String inputFile = sourceFile.toString().toLowerCase();

        // If the file ends with CSV - treat it as a comma seperated file.
        if (inputFile.endsWith(".csv")) {
            this.openAsCSV(sourceFile);

        // Otherwise treat it as a macshapa database file.
        } else {
            this.openAsMacSHAPADB(sourceFile);
        }

        // BugzID:449 - Set filename in spreadsheet window and database if the
        // database name is undefined.
        try {
            Database db = OpenSHAPA.getDatabase();

            if (db.getName().equals("Undefined")) {
                String dbName = sourceFile.getName();
                dbName = dbName.substring(0, dbName.lastIndexOf('.'));
                db.setName(dbName);
                db.setSourceFile(sourceFile);
            }

            // Calling this will erase the "modified" variable and update
            // the title, not actuall save the database.
            db.saveDatabase();

        } catch (SystemErrorException se) {
            logger.error("Can't set db name to the name of the CSV file.", se);
        }

        // Display any changes to the database.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                            .getMainView()
                                                            .getComponent();
        view.relayoutCells();
    }

    /**
     * This method treats a file as a MacSHAPA database file and attempts to
     * populate the database with data.
     *
     * @param sFile The source file to use when populating the database.
     */
    public void openAsMacSHAPADB(final File sFile) {
        try {
            FileReader sReader = new FileReader(sFile);
            BufferedReader sourceStream = new BufferedReader(sReader);
            PrintStream listStream = new PrintStream(new File("read_list.log"));
            PrintStream errorStream = new PrintStream(new File("error.log"));

            MacshapaODBReader modbr = new MacshapaODBReader(sourceStream,
                                                            listStream,
                                                            errorStream);
            OpenSHAPA.setDatabase(modbr.readDB());
        } catch (FileNotFoundException e) {
            logger.error("Unable to load macshapa database:'" + sFile + "'", e);
        } catch (SystemErrorException e) {
            logger.error("Unable to load macshapa database:'" + sFile + "'", e);
        } catch (IOException e) {
            logger.error("Unable to load macshapa database:'" + sFile + "'", e);
        } catch (LogicErrorException e) {
            OpenSHAPA.getApplication().showWarningDialog(e);
        }
    }

    /**
     * This method parses a CSV file and populates the database
     * (and spreadsheet) with data.
     *
     * @param sFile The source file to use when populating the database.
     */
    public void openAsCSV(final File sFile) {
        try {
            Database db = OpenSHAPA.getDatabase();
            BufferedReader csvFile = new BufferedReader(new FileReader(sFile));

            // Read each line of the CSV file.
            String line = csvFile.readLine();

            // If we have a version identifier parse the file using the schema
            // that matches that identifier.
            if (line.equalsIgnoreCase("#2")) {

                // Parse predicate definitions first.
                line = parseDefinitions(csvFile, db);

                while (line != null) {
                    line = parseVariable(csvFile, line, db);
                }

            // Use the original schema to load the file - just variables, and no
            // escape characters.
            } else {
                while (line != null) {
                    line = parseVariable(csvFile, line, db);
                }
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
     * Strips escape characters from a line of text.
     *
     * @param line The line of text to strip escape characters from.
     *
     * @return The line free fo escape characters, i.e. '\'.
     */
    private String stripEscChars(final String line) {
        String result = null;

        if (line != null) {
            result = "";
            for (int i = 0; i < line.length(); i++) {
                if (i < line.length() - 1) {
                    if (line.charAt(i) == '\\' && line.charAt(i + 1) == '\\') {
                        char[] buff = {'\\'};
                        result = result.concat(new String(buff));
                        // Move over the escape character.
                        i++;
                    } else if (line.charAt(i) == '\\'
                               && line.charAt(i + 1) == ',') {
                        char[] buff = {','};
                        result = result.concat(new String(buff));
                        // Move over the escape character.
                        i++;
                    } else {
                        result += line.charAt(i);
                    }
                } else {
                    result += line.charAt(i);
                }
            }
        }

        return result;
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
            // Split the line into tokens using '\,' '(' & ')' as delimiters.
            String[] tokens = line.split("[,\\()]");

            // Create the data cell from line in the CSV file.
            DataCell cell = new DataCell(dc.getDB(),
                                         dc.getID(),
                                         dc.getItsMveID());

            // Set the onset and offset from tokens in the line.
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Empty predicate - just add the empty data cell.
            if (tokens.length == DATA_INDEX) {
                // Add the populated cell to the database.
                dc.getDB().appendCell(cell);

            // Non empty predicate - need to check if we need to add an entry to
            // the vocab, and create it if it doesn't exist. Otherwise we just
            // plow ahead and add the predicate to the database.
            } else {
                PredicateVocabElement pve = dc.getDB()
                                              .getPredVE(tokens[DATA_INDEX]);

                Predicate p = new Predicate(dc.getDB(), pve.getID(),
                              parseFormalArgs(tokens, DATA_INDEX + 1, dc, pve));
                PredDataValue pdv = new PredDataValue(dc.getDB());
                pdv.setItsValue(p);

                // Insert the datavalue in the cell.
                long mveId = dc.getDB().getMatrixVE(dc.getItsMveID()).getID();
                Matrix m = Matrix.Construct(dc.getDB(), mveId, pdv);
                cell.setVal(m);

                // Add the populated cell to the database.
                dc.getDB().appendCell(cell);
            }

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    /**
     * Method to create data values for the formal arguments of a vocab element.
     *
     * @param tokens The array of string tokens.
     * @param startI The starting index to
     * @param targetCol The target column for the parsed formal arguments.
     * @param patternVE The pattern vocab element that the formal arguments must
     * match when being parsed from a file.
     *
     * @return An array of data values that suitably matches the formal
     * arguments of the pattern vocab element.
     *
     * @throws SystemErrorException If unable to create the data values from the
     * supplied tokens.
     */
    private Vector<DataValue> parseFormalArgs(final String[] tokens,
                                              final int startI,
                                              final DataColumn targetCol,
                                              final VocabElement patternVE)
    throws SystemErrorException {
        Vector<DataValue> arguments = new Vector<DataValue>();
        Database db = targetCol.getDB();

        for (int i = 0; i < patternVE.getNumFormalArgs(); i++) {
            FormalArgument fa = patternVE.getFormalArgCopy(i);
            boolean emptyArg = false;

            if (tokens[startI + i].length() == 0) {
                emptyArg = true;
            }
            tokens[startI + i] = tokens[startI + i].trim();

            switch(fa.getFargType()) {
                case TEXT:
                case QUOTE_STRING:
                    QuoteStringDataValue qsdv = new QuoteStringDataValue(db);
                    if (!emptyArg) {
                        // Strip quotes from quote string.
                        int newL = tokens[startI + i].length() - 1;
                        qsdv.setItsValue(tokens[startI + i].substring(1, newL));
                    }
                    arguments.add(qsdv);
                    break;
                case NOMINAL:
                    NominalDataValue ndv = new NominalDataValue(db);
                    if (!emptyArg) {
                        ndv.setItsValue(tokens[startI + i]);
                    }
                    arguments.add(ndv);
                    break;
                case INTEGER:
                    IntDataValue idv = new IntDataValue(db);
                    if (!emptyArg) {
                        idv.setItsValue(tokens[startI + i]);
                    }
                    arguments.add(idv);
                    break;
                case FLOAT:
                    FloatDataValue fdv = new FloatDataValue(db);
                    if (!emptyArg) {
                        fdv.setItsValue(tokens[startI + i]);
                    }
                    arguments.add(fdv);
                    break;
                default:
                    UndefinedDataValue udv = new UndefinedDataValue(db);
                    if (!emptyArg) {
                        udv.setItsValue(tokens[startI + i]);
                    }
                    arguments.add(udv);
                    break;
            }
        }

        return arguments;
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
     *
     * Changes: Replace call to vocabElement.getFormalArg() with call
     *          to vocabElement.getFormalArgCopy().
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

            Matrix m = new Matrix(dc.getDB(),
                                  mve.getID(),
                                  parseFormalArgs(tokens, DATA_INDEX, dc, mve));
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
     * Parses the predicate definitions from the CSV file.
     *
     * @param csvFile The buffered reader containing the contents of the CSV
     * file we are trying parse.
     * @param db The destination database for the csv file.
     *
     * @return The next line to be parsed from the file.
     *
     * @throws IOException If unable to read from the csvFile.
     * @throws SystemErrorException If unable to create the predicate vocab
     * element to add to the database.
     */
    private String parseDefinitions(final BufferedReader csvFile,
                                    final Database db)
    throws IOException, SystemErrorException {

        // Keep parsing lines and putting them in the newly formed nominal
        // variable until we get to a line indicating the end of file or a new
        // variable section.
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {
            // Parse arguments - for predicate vocab element.
            String[] token = line.split("[:-]+");
            PredicateVocabElement pve = new PredicateVocabElement(db, token[1]);
            for (String arg : token[2].split(",")) {
                pve.appendFormalArg(parseFormalArgument(arg, db));
            }
            db.addPredVE(pve);

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    /**
     * Method to build a formal argument.
     *
     * @param content The string holding the formal argument content to be
     * parsed.
     * @param db The parent database for the formal argument.
     *
     * @return The formal argument.
     *
     * @throws SystemErrorException If unable to create a formal argument from
     * the supplied content.
     */
    private FormalArgument parseFormalArgument(final String content,
                                               final Database db)
    throws SystemErrorException {
        FormalArgument fa;
        String[] formalArgument = content.split("\\|");

        // Add text formal argument.
        if (formalArgument[1].equalsIgnoreCase("quote_string")) {
            fa = new QuoteStringFormalArg(db, "<" + formalArgument[0] + ">");

        // Add nominal formal argument.
        } else if (formalArgument[1].equalsIgnoreCase("nominal")) {
            fa = new NominalFormalArg(db, "<" + formalArgument[0] + ">");

        // Add integer formal argument.
        } else if (formalArgument[1].equalsIgnoreCase("integer")) {
            fa = new IntFormalArg(db, "<" + formalArgument[0] + ">");

        // Add float formal argument.
        } else if (formalArgument[1].equalsIgnoreCase("float")) {
            fa = new FloatFormalArg(db, "<" + formalArgument[0] + ">");

        // Not sure what it is - add undefined formal argument.
        } else {
            fa = new UnTypedFormalArg(db, "<" + formalArgument[0] + ">");
        }

        return fa;
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

            // Get the vocab element for the matrix and clean it up to be
            // populated with arguments from the CSV file.
            MatrixVocabElement mve = db.getMatrixVE(varName);
            mve.deleteFormalArg(0);

            // For each of the formal arguments in the file - parse it and
            // create a formal argument in the matrix vocab element.
            for (String arg : vocabString[1].split(",")) {
                mve.appendFormalArg(parseFormalArgument(arg, db));
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

            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                idv.setItsValue(tokens[DATA_INDEX]);
            }
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

            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                fdv.setItsValue(tokens[DATA_INDEX]);
            }
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

            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                ndv.setItsValue(stripEscChars(tokens[DATA_INDEX]));
            }
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

            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                String text = new String("");
                for (int i = DATA_INDEX; i < tokens.length; i++) {
                    text = text.concat(tokens[i]);

                    if (i < (tokens.length - 1)) {
                        text = text.concat(",");
                    }
                }
                tsdv.setItsValue(stripEscChars(text));
            }

            return tsdv;
        }
    }
}
