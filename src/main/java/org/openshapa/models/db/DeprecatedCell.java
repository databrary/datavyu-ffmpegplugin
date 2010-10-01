package org.openshapa.models.db;

import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.SystemErrorException;


public final class DeprecatedCell implements Cell {

    /** The legacy database we can fetch data from. */
    Database legacyDB;

    /** The legacy id of the cell we are fetching data from. */
    long legacyCellId;

    public DeprecatedCell(DataCell newCell) {
        this.setLegacyCell(newCell);
    }

    @Override
    public String getValue() {
        // TODO: implement.
        return null;
    }

    @Override
    public long getOnset() {
        // TODO: implement.
        return 0;

    }

    @Override
    public long getOffset() {
        // TODO: implement.       
        return 0;
    }

    public DataCell getLegcayCell() {
        try {
            return (DataCell) legacyDB.getCell(legacyCellId);
        } catch (SystemErrorException e) {
            // TODO: log error
        } finally {
            return null;
        }
    }

    public void setLegacyCell(DataCell newCell) {
        legacyDB = newCell.getDB();
        legacyCellId = newCell.getID();
    }
}
