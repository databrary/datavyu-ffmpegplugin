package org.openshapa.models.db;

import java.util.List;
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

    public DeprecatedVariable(DataColumn newColumn) {
        this.setLegacyVariable(newColumn);
    }

    public DataColumn getLegacyVariable() {
        try {
            return legacyDB.getDataColumn(legacyColumnId);
        } catch (SystemErrorException e) {
            // TODO log exception.
        } finally {
            return null;
        }
    }

    public void setLegacyVariable(DataColumn newColumn) {
        legacyDB = newColumn.getDB();
        legacyColumnId = newColumn.getID();
    }

    @Override
    public String getName() {
        return getLegacyVariable().getName();
    }

    @Override
    public List<Cell> getCells() {
        // TODO.
        return null;
    }

    @Override
    public void addCell(Cell newCell) {
        // TODO.
    }
}
