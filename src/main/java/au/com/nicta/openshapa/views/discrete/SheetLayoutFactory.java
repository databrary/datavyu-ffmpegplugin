package au.com.nicta.openshapa.views.discrete;

import java.util.Vector;

/**
 * Factory for SheetLayouts.
 * @author swhitcher
 */
public final class SheetLayoutFactory {

    /** Types of SheetLayouts for a spreadsheet. */
    public enum SheetLayoutType {
        /**
         * Ordinal style layout. Cells stack underneath each other with
         * no temporal relation to each other across the spreadsheet.
         */
        Ordinal,
        /** Weak temporal order layout.  Cells distribute down and across
         * the spreadsheet preserving their temporal order but in the
         * least space required to show this.
         */
        WeakTemporal,
        /** Strong temporal order layout.  Cells distribute down and across
         * the spreadsheet preserving their temporal order and with their
         * sizes and gaps between them relative to their onsets and offsets.
         */
        StrongTemporal
    }

    /**
     * Creates the SheetLayout requested by type.
     * @param type Type of layout to create.
     * @param cols SpreadsheetColumns to lay out.
     * @return SheetLayout to use.
     */
    public static SheetLayout getLayout(final SheetLayoutType type,
                                         final Vector<SpreadsheetColumn> cols) {

        switch (type) {
            case Ordinal:
                return new SheetLayoutOrdinal(cols);
            case WeakTemporal:
                return new SheetLayoutWeakTemporal(cols);
            case StrongTemporal:
                return new SheetLayoutStrongTemporal(cols);
            default:
                throw new IllegalArgumentException("Unknown layout - " + type);
        }
    }

    /**
     * Private constructor.
     */
    private SheetLayoutFactory() {
    }
}
