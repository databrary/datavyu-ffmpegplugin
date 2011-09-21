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
package org.openshapa.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.List;
import database.MacshapaDatabase;
import database.SystemErrorException;
import org.openshapa.util.Constants;

/**
 * Converts legacy database calls into newer datastore calls.
 *
 * @deprecated Should use the datastore interface instead. This is a temporary
 * class to allow us to incrementally migrate to the new API.
 */
@Deprecated public class DeprecatedDatabase implements Datastore {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedDatabase.class);

    /** The legacy database that this datastore represents. */
    private MacshapaDatabase legacyDB;

    /** The list of variables stored in this database. */
    private List<Variable> variables;

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    /**
     * Default constructor.
     */
    public DeprecatedDatabase() {
        try {
            legacyDB = new MacshapaDatabase(Constants.TICKS_PER_SECOND);
            // BugzID:449 - Set default database name.
            legacyDB.setName("Database1");
            variables = new ArrayList<Variable>();
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to create new database", e);
        }
    }

    /**
     * @return The legacy database that this datastore represents.
     *
     * @deprecated Should use methods defined in datastore interface rather than
     * the db.legacy package.
     */
    @Deprecated
    public MacshapaDatabase getDatabase() {
        return legacyDB;
    }

    /**
     * Sets the legacy database that this datastore represents.
     *
     * @param newDB The new legacy databsae that this datastore represents.
     *
     * @deprecated Should use methods defined in datastore interface rather than
     * the db.legacy package.
     */
    @Deprecated
    public void setDatabase(MacshapaDatabase newDB) {
        legacyDB = newDB;
    }

    @Deprecated
    public DeprecatedVariable getByLegacyID(final long colID) {
        try {
            return new DeprecatedVariable(getDatabase().getDataColumn(colID));
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get variable", e);
        }

        // Failed - returned null.
        return null;
    }

    @Override public String getName() {
        return legacyDB.getName();
    }

    @Override public void setName(final String datastoreName) {
        try {
            legacyDB.setName(datastoreName);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set datastore name", e);
        }
    }

    @Override public void addVariable(final Variable var) {
        DeprecatedVariable legacyVar = (DeprecatedVariable) var;

        try {
            long colId = legacyDB.addColumn(legacyVar.getLegacyVariable());
            legacyVar.setLegacyVariable(legacyDB.getDataColumn(colId));
            variables.add(var);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to add variable", e);
        }
    }
    
    @Override public Variable getVariable(String varName) {
        for (Variable v : variables) {
            String variableName = v.getName();
            if (variableName.equals(varName)) {
                return v;
            }
        }

        return null;
    }

    public void addVariable(final Variable var, final int index) {
        DeprecatedVariable legacyVar = (DeprecatedVariable) var;

        try {
            long colId = legacyDB.addColumn(legacyVar.getLegacyVariable());
            legacyVar.setLegacyVariable(legacyDB.getDataColumn(colId));
            variables. add(index, var);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to add variable", e);
        }
    }

    public void removeVariable(final Variable var) {
        variables.remove(var);
    }   
    
    @Override public List<Variable> getAllVariables() {
        return variables;
    }

    @Override public List<Variable> getSelectedVariables() {
        List<Variable> result = new ArrayList<Variable>();

        for (Variable var : variables) {
            if (var.isSelected()) {
                result.add(var);
            }
        }

        return result;
    }

    @Override public List<Cell> getSelectedCells() {
        List<Cell> result = new ArrayList<Cell>();

        for (Variable var : variables) {
            for (Cell cell : var.getCells()) {
                if (cell.isSelected()) {
                    result.add(cell);
                }
            }
        }

        return result;
    }

    public void removeVariable(long colID) {
        for (Variable v : variables) {
            if (((DeprecatedVariable)v).getLegacyVariable().getID() == colID) {
                removeVariable(v);
                return;
            }
        }
    }
}
