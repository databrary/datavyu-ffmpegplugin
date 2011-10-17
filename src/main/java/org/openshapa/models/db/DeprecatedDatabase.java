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

import com.google.common.collect.HashBiMap;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import database.Column;
import database.DataColumn;
import database.ExternalCascadeListener;
import database.ExternalColumnListListener;
import database.ExternalDataColumnListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import database.MacshapaDatabase;
import database.Database;
import database.LogicErrorException;
import database.MatrixVocabElement;
import database.NominalFormalArg;
import database.SystemErrorException;
import org.openshapa.OpenSHAPA;
import org.openshapa.models.db.VariableType.DeprecatedType;
import org.openshapa.models.db.VariableType.VariableType;
import org.openshapa.util.Constants;

/**
 * Converts legacy database calls into newer datastore calls.
 *
 * @deprecated Should use the datastore interface instead. This is a temporary
 * class to allow us to incrementally migrate to the new API.
 */
@Deprecated public class DeprecatedDatabase implements Datastore, 
                                                       database.TitleNotifier,
                                                       ExternalDataColumnListener,
                                                       ExternalCascadeListener,
                                                       ExternalColumnListListener {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedDatabase.class);

    /** The legacy database that this datastore represents. */
    private MacshapaDatabase legacyDB;

    /** The list of variables stored in this database. */
    private List<Variable> variables;
    
    HashBiMap<Long, Variable> legacyToModelMap;

    private List<DatastoreListener> listeners;

    /** notifier that needs to be informed when the title needs to be updated. */
    private TitleNotifier titleNotifier;

    /** Records changes to column during a cascade. */
    private ColumnChanges colChanges;

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    /**
     * Default constructor.
     */
    public DeprecatedDatabase() {
        try {
            colChanges = new ColumnChanges();
            listeners = new ArrayList<DatastoreListener>();
            legacyToModelMap = HashBiMap.create();
            legacyDB = new MacshapaDatabase(Constants.TICKS_PER_SECOND);
            // BugzID:449 - Set default database name.
            legacyDB.setName("Database1");
            legacyDB.setTitleNotifier(this);
            legacyDB.registerCascadeListener(this);
            legacyDB.registerColumnListListener(this);

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
        try {
            // Tidy up our listeners after ourselves so that we don't hold
            // the database in memory.
            if (legacyDB != null) {
                legacyDB.deregisterCascadeListener(this);
                legacyDB.deregisterColumnListListener(this);

                for (DataColumn dc : legacyDB.getDataColumns()) {
                    legacyDB.deregisterDataColumnListener(dc.getID(), this);
                }
            }

            legacyDB = newDB;
            legacyDB.setTitleNotifier(this);
            legacyDB.registerCascadeListener(this);
            legacyDB.registerColumnListListener(this);
            for (DataColumn dc : legacyDB.getDataColumns()) {
                legacyDB.registerDataColumnListener(dc.getID(), this);
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set new database.", e);
        }
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
    
    @Override public void updateTitle() {
        titleNotifier.updateTitle();
    }

    @Override public void setTitleNotifier(final TitleNotifier newTitleNotifier) {
        titleNotifier = newTitleNotifier;
    }

    @Override public void addVariable(final Variable var) {
        DeprecatedVariable legacyVar = (DeprecatedVariable) var;

        try {
            long colId = legacyDB.addColumn(legacyVar.getLegacyVariable());
            legacyVar.setLegacyVariable(legacyDB.getDataColumn(colId));
            variables.add(var);
            legacyToModelMap.put(colId, var);

            //notify listeners.
            for (DatastoreListener listener : listeners) {
                listener.variableAdded(var);
            }

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

    @Override
    public Variable createVariable(final String name, final VariableType.type type)
    throws UserWarningException {

        try {
            MatrixVocabElement.MatrixType deprecatedType;

            if (type.equals(VariableType.type.MATRIX)) {
                deprecatedType = MatrixVocabElement.MatrixType.MATRIX;

            } else if (type.equals(VariableType.type.NOMINAL)) {
                deprecatedType = MatrixVocabElement.MatrixType.NOMINAL;

            } else {
                // Default to text.
                deprecatedType = MatrixVocabElement.MatrixType.TEXT;
            }

            Column.isValidColumnName(legacyDB, name);
            DataColumn dc = new DataColumn(legacyDB,
                                           name,
                                           deprecatedType);
            // Return the freshly created variable.
            DeprecatedVariable var = new DeprecatedVariable(dc);
            var.setSelected(true);
            addVariable(var);
            dc = var.getLegacyVariable();

            // If the column is a matrix - default to a single nominal variable
            // rather than untyped.
            if (type.equals(VariableType.type.MATRIX)) {
                System.err.println("A");
                MatrixVocabElement mve = legacyDB.getMatrixVE(dc.getItsMveID());
                System.err.println("B");
                mve.deleteFormalArg(0);
                System.err.println("C");
                mve.appendFormalArg(new NominalFormalArg(legacyDB, "<arg0>"));
                System.err.println("D");
                legacyDB.replaceMatrixVE(mve);
                System.err.println("E");
            }

            return var;

        // Whoops, user has done something strange - show warning dialog.
        } catch (LogicErrorException fe) {
            throw new UserWarningException(fe.getMessage());

        // Whoops, programmer has done something strange - show error message.
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to add variable to database", e);
            OpenSHAPA.getApplication().showErrorDialog();

        }

        return null;
    }

    public void addVariable(final Variable var, final int index) {
        DeprecatedVariable legacyVar = (DeprecatedVariable) var;

        try {
            long colId = legacyDB.addColumn(legacyVar.getLegacyVariable());
            legacyVar.setLegacyVariable(legacyDB.getDataColumn(colId));
            variables.add(index, var);
            legacyToModelMap.put(colId, var);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to add variable", e);
        }
    }

    public void removeVariable(final Variable var) {
        variables.remove(var);
        legacyToModelMap.inverse().remove(var);
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

    @Override public void addListener(final DatastoreListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final DatastoreListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void colInsertion(final Database db,
                             final long colID,
                             final Vector<Long> old_cov,
                             final Vector<Long> new_cov) {
        colChanges.changes = true;

        for (Variable var : variables) {
            if (((DeprecatedVariable) var).getLegacyVariable().getID() == colID) {
                for (DatastoreListener listener : listeners) {
                    listener.variableAdded(var);
                }

            }
        }

        try {
            legacyDB.registerDataColumnListener(colID, this);
        } catch (SystemErrorException e) {
            LOGGER.error("unable to register listener", e);
        }
    }

    @Override
    public void colDeletion(Database db,
                            long colID,
                            Vector<Long> old_cov,
                            Vector<Long> new_cov) {
        colChanges.changes = true;
        for (Variable var : variables) {
            if (((DeprecatedVariable) var).getLegacyVariable().getID() == colID) {
                for (DatastoreListener listener : listeners) {
                    listener.variableRemoved(var);
                }
            }
        }

        try {
            legacyDB.deregisterDataColumnListener(colID, this);
        } catch (SystemErrorException e) {
            LOGGER.error("unable to degrester listener", e);
        }
    }

    @Override
    public void colOrderVectorEdited(Database db,
                                     Vector<Long> old_cov,
                                     Vector<Long> new_cov) {
        colChanges.changes = true;

        List<Variable> newVariables = new ArrayList<Variable>();
        for (Long colID : new_cov) {
            newVariables.add(legacyToModelMap.get(colID));
        }
        variables = newVariables;

        for (DatastoreListener listener : listeners) {
            listener.variableOrderChanged();
        }
    }

    @Override
    public void DColCellDeletion(Database db, long colID, long cellID) {
        // Don't care yet
    }

    @Override
    public void DColCellInsertion(Database db, long colID, long cellID) {
        // Don't care yet
    }

    @Override
    public void DColConfigChanged(Database db, long colID, boolean nameChanged,
                                  String oldName, String newName, boolean hiddenChanged,
                                  boolean oldHidden, boolean newHidden, boolean readOnlyChanged,
                                  boolean oldReadOnly, boolean newReadOnly, boolean varLenChanged,
                                  boolean oldVarLen, boolean newVarLen, boolean selectedChanged,
                                  boolean oldSelected, boolean newSelected) {
        if (nameChanged) {
            colChanges.colsNameChanged.add(colID);
            colChanges.changes = true;
        }

        if (hiddenChanged) {
            colChanges.changes = true;
            colChanges.colsHiddenChanged.add(colID);
        }
    }

    @Override
    public void DColDeleted(Database db, long colID) {
    }

    @Override
    public void beginCascade(Database db) {
        colChanges.reset();
    }

    @Override
    public void endCascade(Database db) {

        if (colChanges.colsHiddenChanged.size() > 0) {
            for (Variable var : this.getAllVariables()) {
                DeprecatedVariable dVar = (DeprecatedVariable) var;
                if (colChanges.colsHiddenChanged.contains(dVar.getLegacyVariable().getID())) {

                    for (DatastoreListener listener : listeners) {
                        if (dVar.getLegacyVariable().getHidden()) {
                            listener.variableHidden(var);
                        } else {
                            listener.variableVisible(var);
                        }
                    }
                }
            }
        }

        if (colChanges.colsNameChanged.size() > 0) {
            for (Variable var : this.getAllVariables()) {

                DeprecatedVariable dVar = (DeprecatedVariable) var;
                if (colChanges.colsNameChanged.contains(dVar.getLegacyVariable().getID())) {

                    for (DatastoreListener listener : listeners) {
                        listener.variableNameChange(var);
                    }
                }
            }
        }
        colChanges.reset();
    }

    /**
     * Private class for recording the changes reported by the listener
     * callbacks on this column.
     */
    private final class ColumnChanges {
        /** True if external changes have taken place. */
        boolean changes;
        /** Column name changed. */
        private List<Long> colsNameChanged;
        /** Column hidden changed. */
        private List<Long> colsHiddenChanged;
        /** List of cell IDs of newly inserted cells. */
        private List<Long> colsInserted;
        /** List of cell IDs of deleted cells. */
        private List<Long> colsDeleted;

        /**
         * ColumnChanges constructor.
         */
        private ColumnChanges() {
            colsHiddenChanged = new ArrayList<Long>();
            colsNameChanged = new ArrayList<Long>();
            colsInserted = new ArrayList<Long>();
            colsDeleted = new ArrayList<Long>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            changes = false;
            colsHiddenChanged.clear();
            colsNameChanged.clear();
            colsInserted.clear();
            colsDeleted.clear();
        }
    }
}
