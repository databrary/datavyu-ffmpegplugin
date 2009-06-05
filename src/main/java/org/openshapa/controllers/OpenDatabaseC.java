package org.openshapa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.db.Column;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.FloatFormalArg;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.IntFormalArg;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.Matrix;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.NominalFormalArg;
import org.openshapa.db.QuoteStringFormalArg;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.UnTypedFormalArg;

/**
 * Controller for opening a database from disk.
 */
public class OpenDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OpenDatabaseC.class);

    /**
     * Constructor.
     *
     * @param sourceFile The source file to use when opening a database from
     * disk.
     */
    public OpenDatabaseC(final String sourceFile) {
        this.loadCSV(sourceFile);
    }

    private void loadCSV(final String sourceFile) {
        try {
            Database db = OpenSHAPA.getDatabase();
            File file = new File(sourceFile);
            BufferedReader csvFile = new BufferedReader(new FileReader(file));

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
     * is the contents of a text variable.
     *
     * @param csvFile The csvFile we are currently parsing.
     * @param dc The datacolumn that we will be adding cells too.
     *
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     *
     * @throws IOException If unable to read the file correctly.
     * @throws SystemErrorException If unable to update the database with the
     * text variable data.
     */
    private String parseTextVariable(BufferedReader csvFile,
                                     DataColumn dc)
    throws IOException, SystemErrorException {


        // Keep parsing lines and putting them in the newly formed text variable
        // until we get to a line indicating the end of file or a new variable
        // section.
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {

            // Split the line into tokens using a comma delimiter.
            String[] tokens = line.split(",");

            // Create the data cell from line in the CSV file.
            DataCell cell = new DataCell(dc.getDB(),
                                         dc.getID(),
                                         dc.getItsMveID());

            // Set the onset and offset from tokens in the line.
            cell.setOnset(new TimeStamp(tokens[0]));
            cell.setOffset(new TimeStamp(tokens[1]));

            // Create the data value from the last token in the line
            TextStringDataValue tsdv = new TextStringDataValue(dc.getDB());

            String text = new String("");
            for (int i = 2; i < tokens.length; i++) {
                text = text.concat(tokens[i]);

                if (i < (tokens.length - 1)) {
                    text = text.concat(",");
                }
            }
            tsdv.setItsValue(text);

            // Insert the datavalue in the cell.
            long mveId = dc.getDB().getMatrixVE(dc.getItsMveID()).getID();
            Matrix m = Matrix.Construct(dc.getDB(), mveId, tsdv);
            cell.setVal(m);

            // Add the populated cell to the database.
            dc.getDB().appendCell(cell);

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        // Return the next line - which is basically the next variable section
        // we have encountered.
        return line;
    }

    private String parseNominalVariable(BufferedReader csvFile,
                                        DataColumn dc)
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
            cell.setOnset(new TimeStamp(tokens[0]));
            cell.setOffset(new TimeStamp(tokens[1]));

            // Create the data value from the last token in the line
            NominalDataValue ndv = new NominalDataValue(dc.getDB());
            //TextStringDataValue tsdv = new TextStringDataValue(dc.getDB());
            ndv.setItsValue(tokens[2]);

            // Insert the datavalue in the cell.
            long mveId = dc.getDB().getMatrixVE(dc.getItsMveID()).getID();
            Matrix m = Matrix.Construct(dc.getDB(), mveId, ndv);
            cell.setVal(m);

            // Add the populated cell to the database.
            dc.getDB().appendCell(cell);

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    private String parsePredicateVariable(BufferedReader csvFile,
                                          DataColumn dc)
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
            cell.setOnset(new TimeStamp(tokens[0]));
            cell.setOffset(new TimeStamp(tokens[1]));

            // Empty predicate - just add the empty data cell.
            if (tokens[2].equals("()")) {
                // Add the populated cell to the database.
                dc.getDB().appendCell(cell);

            // Non empty predicate - need to check if we need to add an entry to
            // the vocab, and create it if it doesn't exist. Otherwise we just
            // plow ahead and add the predicate to the database.
            } else {
                int a = 5;
                /*
                // Create the data value from the last token in the line
                NominalDataValue ndv = new NominalDataValue(dc.getDB());
                //TextStringDataValue tsdv = new TextStringDataValue(dc.getDB());
                ndv.setItsValue(tokens[2]);

                // Insert the datavalue in the cell.
                long mveId = dc.getDB().getMatrixVE(dc.getItsMveID()).getID();
                Matrix m = Matrix.Construct(dc.getDB(), mveId, ndv);
                cell.setVal(m);

                dc.getDB().appendCell(cell);
                 */
            }


            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    private String parseMatrixVariable(BufferedReader csvFile,
                                       DataColumn dc)
    throws IOException, SystemErrorException {
        String line = csvFile.readLine();
        while (line != null && Character.isDigit(line.charAt(0))) {

            // Get the next line in the file for reading.
            line = csvFile.readLine();
        }

        return line;
    }

    private String parseVariable(BufferedReader csvFile,
                                 String line,
                                 Database db)
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
            return parseTextVariable(csvFile, dc);

        // Read nominal variable.
        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.NOMINAL) {
            return parseNominalVariable(csvFile, dc);

        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.MATRIX) {
            // Build vocab for matrix.
            String[] vocabString = tokens[1].split("-");
            String[] vocabElems = vocabString[1].split(",");
            MatrixVocabElement mve = db.getMatrixVE(varName);
            // delete default formal argument in column
            mve.deleteFormalArg(0);

            for (int i = 0; i < vocabElems.length; i++) {
                FormalArgument fa;
                String[] vocabElement = vocabElems[i].split("\\|");

                if (vocabElement[1].equalsIgnoreCase("text")) {
                    fa = new QuoteStringFormalArg(db, "<" + vocabElement[0] + ">");
                } else if (vocabElement[1].equalsIgnoreCase("nominal")) {
                    fa = new NominalFormalArg(db, "<" + vocabElement[0] + ">");
                } else if (vocabElement[1].equalsIgnoreCase("integer")) {
                    fa = new IntFormalArg(db, "<" + vocabElement[0] + ">");
                } else if (vocabElement[1].equalsIgnoreCase("float")) {
                    fa = new FloatFormalArg(db, "<" + vocabElement[0] + ">");
                } else {
                    fa = new UnTypedFormalArg(db, "<" + vocabElement[0] + ">");
                }

                mve.appendFormalArg(fa);
            }

            db.replaceMatrixVE(mve);
            return parseMatrixVariable(csvFile, dc);

        } else if (getVarType(varType)
                   == MatrixVocabElement.MatrixType.PREDICATE) {
            return parsePredicateVariable(csvFile, dc);
        }

        throw null;
    }

    private MatrixVocabElement.MatrixType getVarType(final String type) {
        if (type.equalsIgnoreCase("text")) {
            return MatrixVocabElement.MatrixType.TEXT;

        } else if (type.equalsIgnoreCase("nominal")) {
            return MatrixVocabElement.MatrixType.NOMINAL;

        } else if (type.equalsIgnoreCase("predicate")) {
            return MatrixVocabElement.MatrixType.PREDICATE;

        } else if (type.equalsIgnoreCase("matrix")) {
            return MatrixVocabElement.MatrixType.MATRIX;

        }

        // Unknown type.
        return MatrixVocabElement.MatrixType.UNDEFINED;
    }
}
