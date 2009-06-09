package org.openshapa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import org.apache.log4j.Logger;
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
public class OpenDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OpenDatabaseC.class);

    private static int DATA_ONSET = 0;

    private static int DATA_OFFSET = 1;

    /** The start of the data arguments. */
    private static int DATA_INDEX = 2;

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
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Create the data value from the last token in the line
            TextStringDataValue tsdv = new TextStringDataValue(dc.getDB());

            String text = new String("");
            for (int i = DATA_INDEX; i < tokens.length; i++) {
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
            cell.setOnset(new TimeStamp(tokens[DATA_ONSET]));
            cell.setOffset(new TimeStamp(tokens[DATA_OFFSET]));

            // Create the data value from the last token in the line
            NominalDataValue ndv = new NominalDataValue(dc.getDB());
            //TextStringDataValue tsdv = new TextStringDataValue(dc.getDB());
            ndv.setItsValue(tokens[DATA_INDEX]);

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

    private String parseMatrixVariable(BufferedReader csvFile,
                                       DataColumn dc,
                                       MatrixVocabElement mve)
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

                switch(ma.getFargType()) {
                    case TEXT:
                        QuoteStringDataValue qsdv = new QuoteStringDataValue(dc.getDB());                        
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
                        UndefinedDataValue udv = new UndefinedDataValue(dc.getDB());                        
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
            mve = db.getMatrixVE(varName);
            return parseMatrixVariable(csvFile, dc, mve);

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
