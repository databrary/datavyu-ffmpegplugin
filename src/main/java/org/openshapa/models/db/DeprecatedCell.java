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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * {@link DataCell} adapter.
 */
@Deprecated public final class DeprecatedCell implements Cell {

    private static final Logger LOGGER = UserMetrix.getLogger(DeprecatedCell.class);

    /** The legacy database we can fetch data from. */
    Database legacyDB;

    /** The legacy id of the cell we are fetching data from. */
    long legacyCellId;

    /** Is the cell highlighted or not? */
    boolean isHighlighted;

    /** The list of listeners to be notified when this variable changes. */
    private List<CellListener> listeners;

    /** The value of the cell. */
    private Value cellValue;

    /**
     * Construct a new Cell using the given reference DataCell.
     *
     * @param newCell Reference cell. Cannot be null.
     */
    public DeprecatedCell(final DataCell newCell, final Variable.type newType) {
        setLegacyCell(newCell);
        listeners = new ArrayList<CellListener>();
        isHighlighted = false;

        if (newType.equals(Variable.type.TEXT)) {
            cellValue = new DeprecatedTextValue(legacyDB, legacyCellId);
        } else if (newType.equals(Variable.type.NOMINAL)) {
            cellValue = new DeprecatedNominalValue(legacyDB, legacyCellId);
        } else if (newType.equals(Variable.type.MATRIX)) {
            cellValue = new DeprecatedMatrixValue(legacyDB, legacyCellId);
        } else {

        }
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

    @Override
    public Value getValue() {
        return cellValue;
    }


    @Override
    public String getValueAsString() {
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

    private Long stringToMilli(final String words) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date d = sdf.parse(words);
            return d.getTime();
        } catch (ParseException e) {
            LOGGER.error("Unable to parse time stamp", e);
        }

        return 0L;
    }

    private String milliToString(final long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(milliseconds);
    }

    @Override
    public String getOnsetString() {
        return milliToString(getOnset());
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
            legacyDB.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }

        for (CellListener listener : listeners) {
            listener.onsetChanged(stringToMilli(newOnset));
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
            legacyDB.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }

        for (CellListener listener : listeners) {
            listener.onsetChanged(newOnset);
        }
    }

    @Override
    public String getOffsetString() {
        return milliToString(getOffset());
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
            legacyDB.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }

        for (CellListener listener : listeners) {
            listener.offsetChanged(stringToMilli(newOffset));
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
            legacyDB.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set onset", e);
        }

        for (CellListener listener : listeners) {
            listener.offsetChanged(newOffset);
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
        try {
            legacyDB.replaceCell(cell);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to set selected cell", e);
        }

        for (CellListener listener : listeners) {
            listener.selectionChange(selected);
        }
    }

    @Override
    public boolean isHighlighted() {
        return isHighlighted;
    }

    @Override
    public void setHighlighted(final boolean highlighted) {
        isHighlighted = highlighted;

        for (CellListener listener : listeners) {
            listener.highlightingChange(isHighlighted);
        }
    }

    @Override
    public void addListener(final CellListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final CellListener listener) {
        listeners.remove(listener);
    }

    /**
     * Retrieve the DataCell represented by this Cell.
     */
    @Deprecated
    public DataCell getLegacyCell() {
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
