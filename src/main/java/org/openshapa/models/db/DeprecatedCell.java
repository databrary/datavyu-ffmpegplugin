package org.openshapa.models.db;

import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.SystemErrorException;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.legacy.MatrixVocabElement;


/**
 * {@link DataCell} adapter.
 */
@Deprecated public final class DeprecatedCell implements Cell {

    private static final Logger LOGGER = UserMetrix.getLogger(
            DeprecatedCell.class);

    /** The legacy database we can fetch data from. */
    Database legacyDB;

    /** The legacy id of the cell we are fetching data from. */
    long legacyCellId;

    /**
     * Construct a new Cell using the given reference DataCell.
     *
     * @param newCell
     *            Reference cell. Cannot be null.
     */
    public DeprecatedCell(final DataCell newCell) {
        setLegacyCell(newCell);
    }


    @Override public String toString() {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return null;
        }

        try {
            if (cell.getItsMveType().equals(MatrixVocabElement.MatrixType.MATRIX)) {
                return cell.getVal().toString();
            } else {
                String result = cell.getVal().toString();
                return result.substring(1, (result.length() - 1));
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Accessing cell value failed", e);
        }

        return null;
    }


    @Override public String getValue() {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return null;
        }

        try {

            return cell.getVal().toString();
            //
        } catch (SystemErrorException e) {
            LOGGER.error("Accessing cell value failed", e);
        }

        return null;
    }

    @Override public long getOnset() {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return -1;
        }

        try {
            return cell.getOnset().getTime();
        } catch (SystemErrorException e) {
            LOGGER.error("Accessing cell value failed", e);
        }

        return -1;
    }

    @Override public long getOffset() {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return -1;
        }

        try {
            return cell.getOffset().getTime();
        } catch (SystemErrorException e) {
            LOGGER.error("Accessing cell value failed", e);
        }

        return -1;
    }

    /**
     * Retrieve the DataCell represented by this Cell.
     */
    @Deprecated public DataCell getLegacyCell() {

        try {
            return (DataCell) legacyDB.getCell(legacyCellId);
        } catch (SystemErrorException e) {
            LOGGER.error("Retrieving cell failed", e);
        }

        return null;
    }

    /**
     * Helper for setting the cell to use.
     */
    private void setLegacyCell(final DataCell newCell) {

        if (newCell == null) {
            throw new NullPointerException("Null cell disallowed.");
        }

        legacyDB = newCell.getDB();
        legacyCellId = newCell.getID();
    }
}
