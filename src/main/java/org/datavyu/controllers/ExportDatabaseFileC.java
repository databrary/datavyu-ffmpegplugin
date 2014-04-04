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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Controller for saving the database to disk.
 */
public final class ExportDatabaseFileC {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(ExportDatabaseFileC.class);

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     * @param ds      The datastore to save as a CSV file.
     * @throws UserWarningException When unable to save the database as a CSV to
     *                              disk (usually because of permissions errors).
     */
    public void exportByFrame(final String outFile, final Datastore ds)
            throws UserWarningException {

        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintStream ps = new PrintStream(fos);

            List<Variable> variables = ds.getAllVariables();
            Collections.sort(variables, new org.datavyu.util.VariableSort());

            ArrayList<List<Cell>> cellCache = new ArrayList<List<Cell>>();
            int[] currentIndex = new int[variables.size()];

            // Get all of the cells from the DB and store them locally
            for (Variable v : variables) {
                cellCache.add(v.getCellsTemporally());
            }

            // Now obtain the first and last time point by sweeping over the cells
            long first_time = Long.MAX_VALUE;
            long last_time = 0;

            for (int i = 0; i < variables.size(); i++) {
                List<Cell> cells = cellCache.get(i);
                if (cells.isEmpty()) {
                    continue;
                }
                if (cells.get(0).getOnset() < first_time) {
                    first_time = cells.get(0).getOnset();
                }
                if (cells.get(0).getOffset() < first_time) {
                    first_time = cells.get(0).getOffset();
                }

                if (cells.get(cells.size() - 1).getOnset() > last_time) {
                    last_time = cells.get(cells.size() - 1).getOnset();
                }
                if (cells.get(cells.size() - 1).getOffset() > last_time) {
                    last_time = cells.get(cells.size() - 1).getOffset();
                }
            }

            // Now that we have the first and last time, we loop over it using
            // an assumed 30fps framerate. TODO: Make this support other FRs
            double framerate = 30.0;
            long current_time = first_time;

            // Print header
            String header = "framenum,time,";
            for (Variable v : variables) {

                header += v.getName() + ".ordinal";
                header += "," + v.getName() + ".onset";
                header += "," + v.getName() + ".offset";

                // Test if the variable is a matrix. If it is, then
                // we have to print out all of its arguments.
                if (v.getRootNode().type == Argument.Type.MATRIX) {
                    for (Argument a : v.getRootNode().childArguments) {
                        header += "," + v.getName() + "." + a.name;
                    }
                } else {
                    header += "," + v.getName() + ".value";
                }
            }
            header = header.trim();

            // Write header
            ps.println(header);

            int framenum = 1;
            while (current_time <= last_time) {
                // Update the currentIndex list
                for (int i = 0; i < variables.size(); i++) {
                    Cell c = cellCache.get(i).get(currentIndex[i]);
                    if (current_time > c.getOffset()) {
                        for (int j = currentIndex[i]; j < cellCache.get(i).size(); j++) {
                            if (current_time <= c.getOnset()) {
                                currentIndex[i] = j;
                            }
                        }
                    }
                }


                // Now print each frame as we loop through it
                String row = Integer.toString(framenum) + "," +
                        Long.toString(current_time) + ",";
                for (int i = 0; i < variables.size(); i++) {
                    Cell cell = cellCache.get(i).get(currentIndex[i]);

                    if (cell.getOnset() >= current_time && cell.getOffset() <= current_time) {

                        Value value = cell.getValue();

                        // Print ordinal, onset, offset
                        row = Integer.toString(i) + "," +
                                Long.toString(cell.getOnset()) + "," +
                                Long.toString(cell.getOffset());


                        if (value instanceof MatrixValue) {
                            // Then this is a matrix value, get the sub arguments
                            MatrixValue mv = (MatrixValue) value;
                            for (Value v : mv.getArguments()) {
                                // Loop over each value and print it with a comma
                                // seperator
                                row += "," + v.toString();
                            }
                        } else {
                            // Otherwise just print the single argument
                            row += "," + cell.getValue().toString();
                        }
                    } else {
                        // Figure out what to print if we don't have a cell here
                        Value value = cell.getValue();

                        // Print ordinal, onset, offset
                        row += ",,";


                        if (value instanceof MatrixValue) {
                            // Then this is a matrix value, get the sub arguments
                            MatrixValue mv = (MatrixValue) value;
                            for (Value v : mv.getArguments()) {
                                // Loop over each value and print it with a comma
                                // seperator
                                row += ",";
                            }
                        } else {
                            // Otherwise just print the single argument
                            row += ",";
                        }
                    }
                }
                ps.println(row);
                current_time += framenum * framerate;
                ++framenum;
            }

            fos.close();
        } catch (IOException ie) {
            ie.printStackTrace();
            ResourceMap rMap = Application.getInstance(Datavyu.class)
                    .getContext().getResourceMap(Datavyu.class);
            throw new UserWarningException(rMap.getString("UnableToSave.message", outFile), ie);
        }
    }

    public void exportAsCells(final String outFile, final Datastore ds)
            throws UserWarningException {
        try {
            FileOutputStream outStream = new FileOutputStream(outFile);
            PrintStream ps = new PrintStream(outStream);

            // Get the variables, sort them, and cache the cells
            List<Variable> variables = ds.getAllVariables();
            Collections.sort(variables, new org.datavyu.util.VariableSort());

            ArrayList<List<Cell>> cellCache = new ArrayList<List<Cell>>();
            int[] currentIndex = new int[variables.size()];

            int max_length = 0;
            // Get all of the cells from the DB and store them locally
            for (Variable v : variables) {
                cellCache.add(v.getCellsTemporally());
                if (v.getCells().size() > max_length) {
                    max_length = v.getCells().size();
                }
            }

            // Print header
            String header = "";

            List<Integer> arglengths = new ArrayList<Integer>();
            for (Variable v : variables) {

                header += v.getName() + ".ordinal";
                header += "," + v.getName() + ".onset";
                header += "," + v.getName() + ".offset,";

                // Test if the variable is a matrix. If it is, then
                // we have to print out all of its arguments.
                if (v.getRootNode().type == Argument.Type.MATRIX) {
                    for (Argument a : v.getRootNode().childArguments) {
                        header += v.getName() + "." + a.name + ",";
                    }
                    arglengths.add(v.getRootNode().childArguments.size() + 3);
                } else {
                    header += v.getName() + ".value,";
                    arglengths.add(4);
                }
            }
            header = header.trim();

            // Write header
            ps.println(header);

            // Now get the column that has the most cells, we are going to use
            // that number as the number of iterations to loop over everything
            // printing blanks if that column does not have a cell there
            StringBuilder row;
            for (int i = 0; i < max_length; i++) {
                row = new StringBuilder();
                for (int j = 0; j < variables.size(); j++) {
                    Variable v = variables.get(j);
                    if (cellCache.get(j).size() > i) {
                        // Print the cell
                        Cell c = cellCache.get(j).get(i);
                        row.append(i);
                        row.append(",");
                        row.append(c.getOnset());
                        row.append(",");
                        row.append(c.getOffset());
                        row.append(",");
                        if (v.getRootNode().type == Argument.Type.MATRIX) {
                            for (int k = 0; k < v.getRootNode().childArguments.size(); k++) {
                                row.append(c.getMatrixValue(k).toString());
                                row.append(",");
                            }
                        }
                    } else {
                        // Print a placeholder: we are out of cells
                        for (int k = 0; k < arglengths.get(j); k++) {
                            row.append(",");
                        }
                    }

                }
                ps.println(row);
            }
            ps.flush();
            ps.close();
            outStream.flush();
            outStream.close();
        } catch (IOException ie) {
            ie.printStackTrace();
            ResourceMap rMap = Application.getInstance(Datavyu.class).getContext().getResourceMap(Datavyu.class);
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
    public void exportAsCSV(final OutputStream outStream, final Datastore ds)
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
                        cell.getValueAsString());
                ps.println();
            }
        }
    }
}
