package org.openshapa.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.SystemErrorException;


/**
 * Wrapper / adapter for deprecated variable.
 */
public final class DeprecatedVariable implements Variable {

    /** The legacy database we can fetch data from. */
    Database legacyDB;

    /** The legacy id of the column we are fetching data from. */
    long legacyColumnId;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedVariable.class);

    /**
     * Constructor.
     *
     * @param newVariable The legacy variable that this Variable represents.
     */
    public DeprecatedVariable(DataColumn newVariable) {
        this.setLegacyVariable(newVariable);
    }

    /**
     * @return The legacy data type for portability reasons.
     *
     * @deprecated Should use methods defined in variable rather than the
     * db.legacy package.
     */
    @Deprecated public DataColumn getLegacyVariable() {
        try {
            return legacyDB.getDataColumn(legacyColumnId);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get legacy variable", e);
        } finally {
            return null;
        }
    }

    /**
     * Sets the legacy variable that this new variable will represent.
     *
     * @param newColumn The legacy variable that this variable will represent.
     */
    private void setLegacyVariable(DataColumn newColumn) {
        legacyDB = newColumn.getDB();
        legacyColumnId = newColumn.getID();
    }

    @Override
    public String getName() {
        DataColumn variable = getLegacyVariable();

        if (variable == null) {
            
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
        } finally {
            return null;
        }
    }

    @Override
    public void addCell(Cell newCell) {
        // TODO.
    }
}
