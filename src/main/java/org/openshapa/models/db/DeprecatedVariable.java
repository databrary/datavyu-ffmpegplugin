package org.openshapa.models.db;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ExternalCascadeListener;
import org.openshapa.models.db.legacy.ExternalDataCellListener;
import org.openshapa.models.db.legacy.ExternalDataColumnListener;
import org.openshapa.models.db.legacy.Matrix;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.models.db.legacy.TimeStamp;

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

    /** Records changes to column during a cascade. */
    private VariableChanges colChanges;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedVariable.class);

    /** The temporal map for this column. */
    Multimap<Long, Long> temporalMap;

    /** The temporal index for this column. */
    List<Long> temporalIndex;

    /**
     * Constructor.
     *
     * @param newVariable The legacy variable that this Variable represents.
     */
    public DeprecatedVariable(DataColumn newVariable) {
        colChanges = new VariableChanges();
        temporalMap = HashMultimap.create();
        temporalIndex = asSortedList(temporalMap.keySet());
        legacyColumn = newVariable;
        this.setLegacyVariable(newVariable);
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
        DataColumn variable = getLegacyVariable();

        if (variable == null) {
            return null;
        }

        return variable.getName();
    }

    @Override
    public List<Cell> getCells() {
        List<Cell> result = new ArrayList<Cell>();

        try {
            DataColumn variable = getLegacyVariable();
            if (variable != null) {
                int numCells = variable.getNumCells();
                for (int i = 1; i < numCells + 1; i++) {
                    org.openshapa.models.db.legacy.Cell cell = legacyDB.getCell(variable.getID(), i);
                    if (cell instanceof DataCell) {
                        result.add(new DeprecatedCell((DataCell) cell));
                    }
                }                
            }

            // We always return an empty list - even when no cells exist.
            return result;
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get cells.", e);
            return new ArrayList<Cell>();
        }
    }

    @Override
    public List<Cell> getCellsTemporally() {
        List<Cell> result = new ArrayList<Cell>();

        try {
            for (Long key : temporalIndex) {
                for(Long cellID : temporalMap.get(key)) {
                    org.openshapa.models.db.legacy.Cell cell = legacyDB.getCell(cellID);
                    if (cell instanceof DataCell) {
                        result.add(new DeprecatedCell((DataCell) cell));
                    }
                }
            }

            return result;
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get cells temporally", e);
            return new ArrayList<Cell>();
        }
    }

    @Override public void addCell(Cell newCell) {
        // TODO.
    }

    // --- Interface: ExternalCascadeListener.
    @Override public void beginCascade(final Database db) {
        colChanges.reset();
    }

    @Override public void endCascade(final Database db) {
        try {
            for (Long cellID : colChanges.cellDeleted) {
                // Remove the reference to the cell from the temporal index.
                org.openshapa.models.db.legacy.Cell cell = legacyDB.getCell(cellID);
                if (cell instanceof DataCell) {
                    DataCell dataCell = (DataCell) cell;
                    temporalMap.remove(dataCell.getOnset().getTime(), cellID);
                    temporalIndex = asSortedList(temporalMap.keySet());
                }

                legacyDB.deregisterDataCellListener(cellID, this);
            }

            for (Long cellID : colChanges.cellInserted) {
                // Push a reference to the new cell into the temporal index.
                org.openshapa.models.db.legacy.Cell cell = legacyDB.getCell(cellID);
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
        }
    }
}
