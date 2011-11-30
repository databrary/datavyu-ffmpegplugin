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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import database.DataCell;
import database.DataColumn;
import database.Database;
import database.ExternalCascadeListener;
import database.ExternalDataCellListener;
import database.ExternalDataColumnListener;
import database.LogicErrorException;
import database.Matrix;
import database.MatrixVocabElement;
import database.SystemErrorException;
import database.TimeStamp;

/**
 * Wrapper/adapter for deprecated data columns.
 */
@Deprecated
public final class DeprecatedVariable
implements Variable,
           ExternalDataColumnListener,
           ExternalCascadeListener,
           ExternalDataCellListener {

    /** The legacy database we can fetch data from. */
    private Database legacyDB;

    /** The legacy data column that this variable wraps. */
    private DataColumn legacyColumn;

    /** The legacy id of the column we are fetching data from. */
    private long legacyColumnId;

    /** The list of cells stored in this variable. */
    private List<Cell> cells;

    HashBiMap<Long, Cell> legacyToModelMap;

    /** The cells that are currently selected in this column. */
    private List<Long> selectedCells;

    /** Is the column currently selected? */
    private boolean isSelected;

    /** Records changes to column during a cascade. */
    private VariableChanges colChanges;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedVariable.class);

    /** The temporal map for this column. */
    Multimap<Long, Long> temporalMap;

    /** The temporal index for this column. */
    List<Long> temporalIndex;

    /** The list of listeners to be notified when this variable changes. */
    private List<VariableListener> listeners;

    /** The type of variable. */
    private Variable.type varType;

    /**
     * Constructor.
     *
     * @param newVariable The legacy variable that this Variable represents.
     */
    public DeprecatedVariable(DataColumn newVariable, Variable.type type) {
        colChanges = new VariableChanges();
        selectedCells = new ArrayList<Long>();
        temporalMap = ArrayListMultimap.create();
        temporalIndex = asSortedList(temporalMap.keySet());
        legacyColumn = newVariable;
        varType = type;
        listeners = new ArrayList<VariableListener>();
        this.setLegacyVariable(newVariable);

        cells = new ArrayList<Cell>();
        legacyToModelMap = HashBiMap.create();
    }

    /**
     * @return The legacy data type for portability reasons.
     *
     * @deprecated Should use methods defined in variable interface rather than
     * the db.legacy package.
     */
    @Deprecated public DataColumn getLegacyVariable() {
        try {
            if (legacyColumnId != 0) {
                return legacyDB.getDataColumn(legacyColumnId);
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get legacy variable", e);
        }

        return legacyColumn;
    }

    private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    /**
     * Sets the legacy variable that this new variable will represent.
     *
     * @param newColumn The legacy variable that this variable will represent.
     */
    @Deprecated public void setLegacyVariable(DataColumn newColumn) {
        try {
            if (legacyDB != null && legacyColumnId != 0) {
                legacyDB.deregisterDataColumnListener(legacyColumnId, this);
                legacyDB.deregisterCascadeListener(this);
            }

            legacyColumn = newColumn;
            legacyDB = newColumn.getDB();
            legacyColumnId = newColumn.getID();

            legacyDB.registerDataColumnListener(legacyColumnId, this);
            legacyDB.registerCascadeListener(this);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set legacy variable", e);
        }
    }

    @Override
    public String getName() {
        legacyColumn = getLegacyVariable();

        if (legacyColumn == null) {
            return null;
        }

        return legacyColumn.getName();
    }

    @Override
    public void setName(final String newName) throws UserWarningException {
        try {
            if (DataColumn.isValidColumnName(legacyDB, newName)) {
                legacyColumn = getLegacyVariable();
                legacyColumn.setName(newName);
                legacyDB.replaceColumn(legacyColumn);
            }

            //notify listeners.
            for (VariableListener listener : listeners) {
                listener.nameChanged(newName);
            }
        } catch (LogicErrorException fe) {
            LOGGER.error("Unable to set variable name: " + fe);
            throw new UserWarningException(fe.getMessage());
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to set variable name: " + se);
        }
    }

    @Override
    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public Cell getCellTemporally(final int index) {
        int i = 0;
        for (Long key : temporalIndex) {
            for (Long cellID : temporalMap.get(key)) {
                if (i == index) {
                    return legacyToModelMap.get(cellID);
                }
                i++;
            }
        }

        return null;
    }

    @Override
    public boolean contains(final Cell c) {
        return cells.contains(c);
    }

    @Override
    public Variable.type getVariableType() {
        return varType;
    }

    @Override
    public List<Cell> getCellsTemporally() {
        List<Cell> result = new ArrayList<Cell>();

        for (Long key : temporalIndex) {
            for(Long cellID : temporalMap.get(key)) {
                result.add(legacyToModelMap.get(cellID));
            }
        }

        return result;
    }

    @Override
    public void setSelected(final boolean selected) {
        isSelected = selected;

        try {
            legacyColumn = getLegacyVariable();
            legacyColumn.setSelected(selected);
            legacyDB.replaceColumn(legacyColumn);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to select column", e);
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected || !selectedCells.isEmpty();
    }

    @Override
    public void setHidden(final boolean hidden) {

        try {
            legacyColumn = getLegacyVariable();
            legacyColumn.setHidden(hidden);
            legacyDB.replaceColumn(legacyColumn);

            for (VariableListener listener : listeners) {
                listener.visibilityChanged(hidden);
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to hide column", e);
        }
    }

    @Override
    public boolean isHidden() {
        return legacyColumn.getHidden();
    }

    @Override
    public Cell createCell() {
        Cell result = null;

        try {
            MatrixVocabElement mve = legacyDB.getMatrixVE(getLegacyVariable().getItsMveID());
            DataCell newCell = new DataCell(getLegacyVariable().getDB(),
                                            getLegacyVariable().getID(),
                                            mve.getID());
            long cellID = legacyDB.appendCell(newCell);
            newCell = (DataCell) legacyDB.getCell(cellID);
            result = new DeprecatedCell(newCell);
            cells.add(result);
            legacyToModelMap.put(cellID, result);

            //notify listeners.
            for (VariableListener listener : listeners) {
                listener.cellInserted(result);
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to create cell", e);
        }

        return result;
    }

    @Override
    public void removeCell(final Cell cell) {
        try {
            DataCell dc = ((DeprecatedCell) cell).getLegacyCell();
            cells.remove(cell);
            legacyToModelMap.remove(dc.getID());
            legacyDB.removeCell(dc.getID());

            // notify listeners.
            for (VariableListener listener : listeners) {
                listener.cellRemoved(cell);
            }
        } catch (SystemErrorException se) {
            LOGGER.error("Unable to delete cell", se);
        }
    }

    @Override
    public void addListener(final VariableListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final VariableListener listener) {
        listeners.remove(listener);
    }

    // --- Interface: ExternalCascadeListener.
    @Override public void beginCascade(final Database db) {
        colChanges.reset();
    }

    @Override public void endCascade(final Database db) {
        try {
            for (Long cellID : colChanges.cellDeleted) {
                // Remove the reference to the cell from the temporal index.
                database.Cell cell = legacyDB.getCell(cellID);
                if (cell instanceof DataCell) {
                    DataCell dataCell = (DataCell) cell;
                    temporalMap.remove(dataCell.getOnset().getTime(), cellID);
                    temporalIndex = asSortedList(temporalMap.keySet());
                }

            }

            for (Long cellID : colChanges.cellInserted) {
                // Push a reference to the new cell into the temporal index.
                database.Cell cell = legacyDB.getCell(cellID);
                if (cell instanceof DataCell) {
                    DataCell dataCell = (DataCell) cell;
                    temporalMap.put(dataCell.getOnset().getTime(), cellID);
                    temporalIndex = asSortedList(temporalMap.keySet());
                }

                legacyDB.registerDataCellListener(cellID, this);
            }

            colChanges.reset();
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to end the column change cascade.", e);
        }
    }


    // --- Interface: ExternalDataColumnListener
    @Override public void DColCellDeletion(final Database db,
                                           final long colID,
                                           final long cellID) {
        colChanges.cellDeleted.add(cellID);
    }

    @Override public void DColCellInsertion(final Database db,
                                            final long colID,
                                            final long cellID) {
        colChanges.cellInserted.add(cellID);
    }

    @Override public void DColConfigChanged(final Database db,
                                            final long colID,
                                            final boolean nameChanged,
                                            final String oldName,
                                            final String newName,
                                            final boolean hiddenChanged,
                                            final boolean oldHidden,
                                            final boolean newHidden,
                                            final boolean readOnlyChanged,
                                            final boolean oldReadOnly,
                                            final boolean newReadOnly,
                                            final boolean varLenChanged,
                                            final boolean oldVarLen,
                                            final boolean newVarLen,
                                            final boolean selectedChanged,
                                            final boolean oldSelected,
                                            final boolean newSelected) {
        colChanges.nameChanged = nameChanged;
        isSelected = newSelected;
    }

    @Override public void DColDeleted(final Database db,
                                      final long colID) {
    }

    // --- Interface: External DataCellListener
    @Override public void DCellChanged(Database db,
                                       long colID,
                                       long cellID,
                                       boolean ordChanged,
                                       int oldOrd,
                                       int newOrd,
                                       boolean onsetChanged,
                                       TimeStamp oldOnset,
                                       TimeStamp newOnset,
                                       boolean offsetChanged,
                                       TimeStamp oldOffset,
                                       TimeStamp newOffset,
                                       boolean valChanged,
                                       Matrix oldVal,
                                       Matrix newVal,
                                       boolean selectedChanged,
                                       boolean oldSelected,
                                       boolean newSelected,
                                       boolean commentChanged,
                                       String oldComment,
                                       String newComment) {
        if (onsetChanged) {
            // Update the temporal index.
            temporalMap.remove(oldOnset.getTime(), cellID);
            temporalMap.put(newOnset.getTime(), cellID);
            temporalIndex = asSortedList(temporalMap.keySet());
        }

        if (selectedChanged && newSelected && selectedCells.indexOf(cellID) == -1) {
            selectedCells.add(cellID);
        }

        if (selectedChanged && !newSelected && selectedCells.indexOf(cellID) != -1) {
            selectedCells.remove(cellID);
        }
    }

    @Override public void DCellDeleted(Database db,
                                       long colID,
                                       long cellID) {
    }


    /**
     * Private class for recording the changes reported by the listener call
     * backs on this column. end cascade is when we can actually apply these
     * changes.
     */
    private final class VariableChanges {
        /** List of cell IDs of newly inserted cells. */
        private List<Long> cellInserted;
        /** List of cell IDs of deleted cells. */
        private List<Long> cellDeleted;
        /** Has the name changed? */
        private boolean nameChanged;

        /**
         * ColumnChanges constructor.
         */
        private VariableChanges() {
            cellInserted = new ArrayList<Long>();
            cellDeleted = new ArrayList<Long>();
            reset();
        }

        /**
         * Reset the ColumnChanges flags and lists.
         */
        private void reset() {
            cellInserted.clear();
            cellDeleted.clear();
            nameChanged = false;
        }
    }
}
