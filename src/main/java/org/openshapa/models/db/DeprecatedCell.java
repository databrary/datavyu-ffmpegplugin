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

import database.DataCell;
import database.Database;
import database.SystemErrorException;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import database.MatrixVocabElement;
import database.TimeStamp;


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

    @Override public void setOnset(final String newOnset) {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return ;
        }

        try {
            TimeStamp timeStamp = new TimeStamp(newOnset);
            cell.setOnset(timeStamp);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }
    }

    @Override public void setOnset(final long newOnset) {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return;
        }

        try {
            TimeStamp timeStamp = new TimeStamp(1000, newOnset);
            cell.setOnset(timeStamp);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }
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
    
   @Override public void setOffset(final String newOffset) {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return ;
        }

        try {
            TimeStamp timeStamp = new TimeStamp(newOffset);
            cell.setOffset(timeStamp);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }
    }

    @Override public void setOffset(final long newOffset) {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return;
        }

        try {
            TimeStamp timeStamp = new TimeStamp(1000, newOffset);
            cell.setOffset(timeStamp);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }
    }

    @Override public boolean isSelected() {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return false;
        }

        return cell.getSelected();
    }

    @Override public void setSelected(final boolean selected) {
        DataCell cell = getLegacyCell();

        if (cell == null) {
            return;
        }

        cell.setSelected(selected);
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
