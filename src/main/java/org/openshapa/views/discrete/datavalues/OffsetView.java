package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStampDataValue;
import org.openshapa.views.discrete.Selector;
import org.apache.log4j.Logger;

/**
 * A view representation for an offset timestamp.
 */
public final class OffsetView extends TimeStampDataValueView {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OffsetView.class);

    /**
     * Constructor.
     *
     * @param cellSelection The parent cell selection.
     * @param cell The parent cell for this offset timestamp.
     * @param timeStampDataValue A datavalue for the offset to wrap around.
     * @param editable Is the datavalue editable or not - true if it is editable
     * false otherwise.
     */
    public OffsetView(final Selector cellSelection,
                      final DataCell cell,
                      final TimeStampDataValue timeStampDataValue,
                      final boolean editable) {
        super(cellSelection, cell, timeStampDataValue, editable);
    }

    /**
     * Hook for updating the database with the latest offset value from the
     * offset editor.
     */
    @Override
    public void updateDatabase() {
        try {
            this.getEditor().storeCaretPosition();
            TimeStampDataValue tsdv = (TimeStampDataValue) this.getModel();
            getParentCell().setOffset(tsdv.getItsValue());
            getParentCell().getDB().replaceCell(getParentCell());
        } catch (SystemErrorException se) {
            logger.error("Unable to update Database: ", se);
        }
    }
}
