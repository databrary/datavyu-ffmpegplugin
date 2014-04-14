/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.Datavyu;
import org.datavyu.models.db.*;

import javax.swing.*;
import java.io.*;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for opening a database from disk.
 */
public final class OpenDatabaseFileC {

    /**
     * The index of the ONSET timestamp in the CSV line.
     */
    private static final int DATA_ONSET = 0;
    /**
     * The index of the OFFSET timestamp in the CSV line.
     */
    private static final int DATA_OFFSET = 1;
    /**
     * The start of the data arguments.
     */
    private static final int DATA_INDEX = 2;
    /**
     * Value to put into values we cannot read in the event of an error
     */
    private static final String error_value = "XXXXX";
    /**
     * Bool so we know whether or not we've had an error while reading in a file
     */
    private static boolean parse_error = false;

    private int numVarsRead = 0;

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(OpenDatabaseFileC.class);

    /**
     * Opens a database.
     *
     * @param sourceFile The source file to open.
     * @return populated MacshapaDatabase on success, null otherwise.
     */
    public Datastore open(final File sourceFile) {
        Datastore db;
        String inputFile = sourceFile.toString().toLowerCase();

        // If the file ends with CSV - treat it as a comma seperated file.
        if (inputFile.endsWith(".csv")) {
            db = openAsCSV(sourceFile);

            // Otherwise treat it as a macshapa database file.
        } else {
            db = openAsMacSHAPADB(sourceFile);
        }

        return db;
    }

    /**
     * This method treats a file as a MacSHAPA database file and attempts to
     * populate the database with data.
     *
     * @param sFile The source file to use when populating the database.
     * @return populated database on success, null otherwise.
     */
    public Datastore openAsMacSHAPADB(final File sFile) {

        // Currently no implementation of opening older MacSHAPA database.
        // ... One day.

        // Error occured - return null.
        return null;
    }

    /**
     * This method parses a CSV file and populates the database (and
     * spreadsheet) with data.
     *
     * @param sFile The source file to use when populating the database.
     * @return populated database on success, null otherwise.
     */
    public Datastore openAsCSV(final File sFile) {

        try {
            LOGGER.event("open csv database from file");

            FileInputStream fis = new FileInputStream(sFile);
            Datastore result = openAsCSV(fis);
            fis.close();

            return result;
        } catch (Exception fe) {
            LOGGER.error("Unable to open as CSV", fe);
            fe.printStackTrace();
        }

        // Error encountered - return null.
        return null;
    }

    /**
     * This method parses a CSV input stream and populates the database (and
     * spreadsheet) with data. The caller is responsible for managing the
     * stream.
     *
     * @param inStream The stream to deserialized when populating the database.
     * @return populated database on sucess, null otherwise.
     */
    public Datastore openAsCSV(final InputStream inStream) {
        try {
            LOGGER.event("open csv database from stream");

            Datastore db = DatastoreFactory.newDatastore();
            db.setTitleNotifier(Datavyu.getApplication());
            InputStreamReader isr = new InputStreamReader(inStream);
            BufferedReader csvFile = new BufferedReader(isr);

            // Read each line of the CSV file.
            String line = csvFile.readLine();

            // If we have a version identifier parse the file using the schema
            // that matches that identifier.
            if ("#4".equalsIgnoreCase(line)) {

                //Version 4 includes a comment for columns.
                line = csvFile.readLine();
                while (line != null) {
                    line = parseVariable(csvFile, line, db, "#4");
                }
            } else if ("#3".equalsIgnoreCase(line)) {

                //Version 3 includes column visible status after the column type
                line = csvFile.readLine();
                while (line != null) {
                    line = parseVariable(csvFile, line, db, "#3");
                }
            } else if ("#2".equalsIgnoreCase(line)) {

                line = csvFile.readLine();
                while (line != null) {
                    line = parseVariable(csvFile, line, db);
                }

            } else {

                // Use the original schema to load the file - just variables,
                // and no escape characters.
                while (line != null) {
                    line = parseVariable(csvFile, line, db);
                }
            }

            csvFile.close();
            isr.close();

            return db;
        } catch (IOException e) {
            LOGGER.error("Unable to read line from CSV file", e);
            e.printStackTrace();
        } catch (UserWarningException e) {
            LOGGER.error("Unable to create new variable.", e);
            e.printStackTrace();
        }

        // Error encountered - return null.
        return null;
    }

    /**
     * Strips escape characters from a line of text.
     *
     * @param line The line of text to strip escape characters from.
     * @return The line free of escape characters, i.e. '\'.
     */
    private String stripEscChars(final String line) {
        String result = null;

        if (line != null) {
            result = "";

            for (int i = 0; i < line.length(); i++) {

                if (i < (line.length() - 1)) {

                    if ((line.charAt(i) == '\\')
                            && (line.charAt(i + 1) == '\\')) {
                        char[] buff = {'\\'};
                        result = result.concat(new String(buff));

                        // Move over the escape character.
                        i++;
                    } else if ((line.charAt(i) == '\\')
                            && (line.charAt(i + 1) == ',')) {
                        char[] buff = {','};
                        result = result.concat(new String(buff));

                        // Move over the escape character.
                        i++;
                    } else if ((line.charAt(i) == '\\')
                            && (line.charAt(i + 1) == '-')) {
                        char[] buff = {'-'};
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
     * Method to create data values for the formal arguments of a vocab element.
     *
     * @param tokens    The array of string tokens.
     * @param startI    The starting index to
     * @param destValue The destination value that we are populating.
     */
    private void parseFormalArgs(final String[] tokens,
                                 final int startI,
                                 final Argument destPattern,
                                 final MatrixValue destValue) {


        // Check to see if the list of tokens we have here is correct.
        // If it is not, then mark an error state and do our best to parse.
        // Fill in missing info with a missing value.

        List<Value> args = destValue.getArguments();
        
        int endIndex = tokens.length;
        if (args.size() != tokens.length - startI) {
            // We have a problem. Arguments are of different length.
            // Get as much from the string as we can.

            parse_error = true; //do something with this: warning, more informative exception?
            endIndex = min(tokens.length,destValue.getArguments().size() + startI);
        }

        for (int tokenIndex = startI; tokenIndex < endIndex; tokenIndex++) {
            int argIndex = tokenIndex - startI;
            Argument fa = destPattern.childArguments.get(argIndex);
            boolean emptyArg = false;

            // If the field doesn't contain anything or matches the FargName
            // we consider the argument to be 'empty'. 
            if ((tokens[tokenIndex].length() == 0) || tokens[tokenIndex].equals("<"+fa.name+">")) {
                emptyArg = true;
                tokens[tokenIndex] = ""; //set <placeholder> to empty string. 
            }

            tokens[tokenIndex] = tokens[tokenIndex].trim(); //is this desirable?
            destValue.getArguments().get(argIndex).set(tokens[tokenIndex]);
        }
    }

    /**
     * Method to invoke when we encounter a block of text in the CSV file that
     * is the contents of a matrix variable.
     *
     * @param csvFile The csvFile we are currently parsing.
     * @param var     The variable that we will be adding cells too.
     * @param arg     The matrix template we are using when parsing individual
     *                matrix elements to put in the spreadsheet.
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     * @throws IOException If unable to read the file correctly.
     */
    private String parseMatrixVariable(final BufferedReader csvFile,
                                       final Variable var,
                                       final Argument arg) throws IOException {
        String line = csvFile.readLine();

        while ((line != null) && Character.isDigit(line.charAt(0))) {

            ArrayList tokensList = new ArrayList<String>();
            String[] onsetOffsetVals = line.split(",", 3);
            tokensList.add(onsetOffsetVals[0]); //onset
            tokensList.add(onsetOffsetVals[1]); //offset
            
            String valuesStr = onsetOffsetVals[2];
            StringBuilder sb = new StringBuilder();
                    
            for(int i = 0; i < valuesStr.length(); i++)      
            {
                char cur = valuesStr.charAt(i);
                if(cur == '\\')
                {
                    if (i+1 == valuesStr.length()) //newline
                    {
                        sb.append('\n');
                        valuesStr += csvFile.readLine();
                    }     
                    else //stuff following escape backslash
                    {
                        i++;
                        sb.append(valuesStr.charAt(i));
                    }
                }
                else if(cur == ',') //structural comma
                {
                    tokensList.add(sb.toString());
                    sb = new StringBuilder();
                }
                else sb.append(cur); //ordinary char
            }
            tokensList.add(sb.toString());
            
            String[] tokens = (String[]) tokensList.toArray(new String[tokensList.size()]);
            
            Cell newCell = var.createCell();
            // Set the onset and offset from tokens in the line.
            newCell.setOnset(tokens[DATA_ONSET]);
            newCell.setOffset(tokens[DATA_OFFSET]);

            // Strip the first and last chars - presumably parens
            tokens[DATA_INDEX] = tokens[DATA_INDEX].substring(1, tokens[DATA_INDEX].length());
            int end = tokens.length - 1;
            tokens[end] = tokens[end].substring(0, tokens[end].length() - 1);
            
            parseFormalArgs(tokens, DATA_INDEX, var.getRootNode(), (MatrixValue) newCell.getValue());
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
     * @param var     The variable that we will be adding cells too.
     * @param The     populator to use when converting the contents of the cell into
     *                a datavalue that can be inserted into the spreadsheet.
     * @return The next line in the file that is not part of the block of text
     * in the CSV file.
     * @throws IOException If unable to read the file correctly.
     */
    private String parseEntries(final BufferedReader csvFile,
                                final Variable var,
                                final EntryPopulator populator)
            throws IOException {

        // Keep parsing lines and putting them in the newly formed nominal
        // variable until we get to a line indicating the end of file or a new
        // variable section.
        String line = csvFile.readLine();

        boolean error_line = false;
        int error_count = 0;

        while ((line != null) && Character.isDigit(line.charAt(0))) {

            // Remove backslashes if there are more than would be used for 
            // newline escapes

            if (line.contains("\\")) {
                if (line.endsWith("\\") || line.endsWith("\\\\")) {
                    line = line.replace("\\", "") + "\\";
                } else {
                    line = line.replace("\\", "");
                }
            }

            try {
                // Split the line into tokens using a comma delimiter.
                String[] tokens = line.split(",");

                // BugzID: 1075 - If the line ends with an escaped new line - add
                // the next line to the current text field.
                while ((line != null) && line.endsWith("\\")
                        && !line.endsWith("\\\\")) {
                    line = csvFile.readLine();

                    String content = tokens[tokens.length - 1];
                    content = content.substring(0, content.length() - 1);
                    tokens[tokens.length - 1] = content + '\n' + line;
                }

                Cell newCell = var.createCell();

                // Set the onset and offset from tokens in the line.
                newCell.setOnset(tokens[DATA_ONSET]);
                newCell.setOffset(tokens[DATA_OFFSET]);
                populator.populate(tokens, newCell.getValue());

                // Get the next line in the file for reading.
                line = csvFile.readLine();

                // Test to see if the new line is an error line
                if ((line != null) && !Character.isDigit(line.charAt(0))) {
                    if (testForCorruptLine(line)) {
                        error_line = true;
                        error_count += 1;
                        line = fixCorruptLine(line);
                        System.out.println("ERROR: " + line);
                    }
                }
            } catch (Exception e) {
                // TODO: Add in fix here for matrix cells that
                // are corrupted in the data values
                e.printStackTrace();
                error_line = true;
                error_count += 1;
                System.out.println("ERROR: " + line);
            }
        }

        if (error_line) {
            JOptionPane.showMessageDialog(null,
                    "Error reading file. " + String.valueOf(error_count) +
                            " cells could not be read.\nRecovered files have time 99:00:00:000.\nPlease send this file to Datavyu Support for further analysis!",
                    "Error reading file: Corrupted cells",
                    JOptionPane.ERROR_MESSAGE);
        }

        return line;
    }

    private boolean testForCorruptLine(String line) {
        String[] tokens = line.split("\\(");
        if (tokens.length == 2) {
            return false;
        } else {
            return true;
        }
    }

    private String fixCorruptLine(String line) {
        line = "99:00:00:000,99:00:00:000," + line;

        return line;
    }

    /**
     * Method to build a formal argument.
     *
     * @param content The string holding the formal argument content to be
     *                parsed.
     * @param db      The parent database for the formal argument.
     * @return The formal argument.
     */
    private Argument parseFormalArgument(final String content) {
        Argument fa;
        String[] formalArgument = content.split("\\|");
        formalArgument[0] = this.stripEscChars(formalArgument[0]);

        // Add text formal argument.
        if (formalArgument[1].equalsIgnoreCase("quote_string")) {
            fa = null;

        } else if (formalArgument[1].equalsIgnoreCase("integer")) {
            // Add integer formal argument.
            fa = new Argument(formalArgument[0], Argument.Type.NOMINAL);

        } else if (formalArgument[1].equalsIgnoreCase("float")) {
            // Add float formal argument.
            fa = new Argument(formalArgument[0], Argument.Type.NOMINAL);

        } else {
            // Add nominal formal argument.
            fa = new Argument(formalArgument[0], Argument.Type.NOMINAL);
        }

        return fa;
    }

    /**
     * Method to invoke when we encounter a block of text that is a variable.
     *
     * @param csvFile The CSV file we are currently reading.
     * @param line    The line of the CSV file we are currently reading.
     * @param db      The data store we are populating with data from the CSV file.
     * @return The next String that is not part of the currently variable that
     * we are parsing.
     * @throws IOException          When we are unable to read from the csvFile.
     * @throws UserWarningException When we are unable to create a new variable.
     */
    private String parseVariable(final BufferedReader csvFile,
                                 final String line,
                                 final Datastore db)
            throws IOException, UserWarningException {
        return parseVariable(csvFile, line, db, "#2");
    }

    /**
     * Method to invoke when we encounter a block of text that is a variable.
     *
     * @param csvFile The CSV file we are currently reading.
     * @param line    The line of the CSV file we are currently reading.
     * @param db      The data store we are populating with data from the CSV file.
     * @return The next String that is not part of the currently variable that
     * we are parsing.
     * @throws IOException          When we are unable to read from the csvFile.
     * @throws UserWarningException When we are unable to create variables.
     */
    private String parseVariable(final BufferedReader csvFile,
                                 final String line,
                                 final Datastore ds,
                                 final String version)
            throws IOException, UserWarningException {
        // Determine the variable name and type.
        String[] tokens = line.split("\\(");
        String varName = this.stripEscChars(tokens[0].trim());
        String varType = null;
        String varComment = "";
        boolean varVisible = true;

        System.out.println(line);
        System.out.println(tokens.length);
        if (version.equals("#4")) {
            String[] varArgs = tokens[1].split(",");
            varType = varArgs[0];
            varVisible = Boolean.parseBoolean(varArgs[1]);
            varComment = varArgs[2].substring(0, varArgs[2].indexOf(")"));
        } else if (version.equals("#3")) {
            varType = tokens[1].substring(0, tokens[1].indexOf(","));
            varVisible = Boolean.parseBoolean(tokens[1].substring(
                    tokens[1].indexOf(",") + 1, tokens[1].indexOf(")")));
        } else {
            varType = tokens[1].substring(0, tokens[1].indexOf(")"));
        }

        // BugzID:1703 - Ignore old macshapa query variables, we don't have a
        // reliable mechanisim for loading their predicates. Given problems
        // between the untyped nature of macshapa and the typed nature of
        // Datavyu.
        if (varName.equals("###QueryVar###")) {
            String lineEater = csvFile.readLine();

            while ((lineEater != null)
                    && Character.isDigit(lineEater.charAt(0))) {
                lineEater = csvFile.readLine();
            }

            return lineEater;
        }

        // Create variable to put cells within.
        Argument.Type variableType = getVarType(varType);
        Variable newVar = ds.createVariable(varName, variableType, true);
        newVar.setHidden(!varVisible);

        newVar.setOrderIndex(numVarsRead);
        numVarsRead++;
        // Read text variable.
        if (variableType == Argument.Type.TEXT) {
            return parseEntries(csvFile,
                    newVar,
                    new PopulateText());

        } else if (variableType == Argument.Type.NOMINAL) {
            // Read nominal variable.
            return parseEntries(csvFile,
                    newVar,
                    new PopulateNominal());

        } else if (variableType == Argument.Type.MATRIX) {

            // Read matrix variable - Build vocab for matrix.
            String[] vocabString = tokens[1].split("(?<!\\\\)-");

            // Get the vocab element for the matrix and clean it up to be
            // populated with arguments from the CSV file.
            Argument newArg = newVar.getRootNode();
            newArg.clearChildArguments();

            // For each of the formal arguments in the file - parse it and
            // create a formal argument in the matrix vocab element.
            for (String arg : vocabString[1].split(",")) {
                newArg.childArguments.add(parseFormalArgument(arg));
            }
            newVar.setRootNode(newArg);

            return parseMatrixVariable(csvFile, newVar, newArg);

        } 
        throw new IllegalStateException("Unknown variable type.");
    }

    /**
     * @param type The string containing the variable type.
     * @return The type of the variable.
     */
    private Argument.Type getVarType(final String type) {

        if (type.equalsIgnoreCase("text")) {
            return Argument.Type.TEXT;

        } else if (type.equalsIgnoreCase("nominal")) {
            return Argument.Type.NOMINAL;

        } else if (type.equalsIgnoreCase("predicate")) {
            // TODO - support predicate types.
            return null;

        } else if (type.equalsIgnoreCase("matrix")) {
            return Argument.Type.MATRIX;

        } else if (type.equalsIgnoreCase("integer")) {
            // TODO - support integer types.
            return null;

        } else if (type.equalsIgnoreCase("float")) {
            // TODO - support float types.
            return null;
        }

        // Error - Unknown type.
        return null;
    }

    /**
     * A populator for creating data values that can be used to populate
     * database spreadsheet cells.
     */
    private abstract class EntryPopulator {

        /**
         * Populates a DataValue from the supplied array of tokens.
         *
         * @param tokens    The tokens to use when building a DataValue.
         * @param destValue That this populator is filling with content.
         */
        abstract void populate(final String[] tokens, final Value destValue);
    }

    /**
     * EntryPopulator for creating nominal data values.
     */
    private class PopulateNominal extends EntryPopulator {

        /**
         * Populates a DataValue from the supplied array of tokens.
         *
         * @param tokens    The tokens to use when building a DataValue.
         * @param destValue That this populator is filling with content.
         */
        @Override
        void populate(final String[] tokens, final Value destValue) {
            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                destValue.set(stripEscChars(tokens[DATA_INDEX]));
            }
        }
    }

    /**
     * EntryPopulator for creating text data values.
     */
    private class PopulateText extends EntryPopulator {

        /**
         * Populates a DataValue from the supplied array of tokens.
         *
         * @param tokens    The tokens to use when building a DataValue.
         * @param destValue That this populator is filling with content.
         */
        @Override
        void populate(final String[] tokens, final Value destValue) {
            // BugzID:722 - Only populate the value if we have one from the file
            if (tokens.length > DATA_INDEX) {
                String text = "";

                for (int i = DATA_INDEX; i < tokens.length; i++) {
                    text = text.concat(tokens[i]);

                    if (i < (tokens.length - 1)) {
                        text = text.concat(",");
                    }
                }

                destValue.set(stripEscChars(text));
            }
        }
    }
}
