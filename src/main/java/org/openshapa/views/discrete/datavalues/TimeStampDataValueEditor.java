package org.openshapa.views.discrete.datavalues;

import org.openshapa.db.DataCell;
import org.openshapa.db.Matrix;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TimeStamp;
import org.openshapa.db.PredDataValue;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;

/**
 * This class is the character editor of a TimeStampDataValues.
 * TODO: finish TimeStampDataValueEditor
 */
public class TimeStampDataValueEditor extends DataValueEditor {

    /** Conversion factor for converting hours to ticks. */
    private static final long HH_TO_TICKS = 3600000;

    /** Array index for hourse. */
    private static final int HH = 0;

    /** Array index for minutes.  */
    private static final int MM = 1;

    /** Array index for seconds. */
    private static final int SS = 2;

    /** Array index for milliseconds. */
    private static final int MMM = 3;

    /** Conversion factor for converting minutes to ticks. */
    private static final long MM_TO_TICKS = 60000;

    /** Conversion factor for converting seconds to ticks. */
    private static final int SS_TO_TICKS = 1000;

    /** Logger for this class. */
    private static Logger logger = Logger
                                   .getLogger(TimeStampDataValueEditor.class);

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                  final DataCell cell,
                                  final Matrix matrix,
                                  final int matrixIndex) {
        super(ta, cell, matrix, matrixIndex);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     */
    public TimeStampDataValueEditor(final JTextComponent ta,
                                  final DataCell cell,
                                  final PredDataValue p,
                                  final int pi,
                                  final Matrix matrix,
                                  final int matrixIndex) {
        super(ta, cell, p, pi, matrix, matrixIndex);
    }

    /** A list of characters that can not be removed from this view. */
    private Vector<Character> preservedChars;

    /**
     * @return The list of preserved characters.
     */
    public final Vector<Character> getPreservedChars() {
        return preservedChars;
    }

    /**
     * The action to invoke when a key is pressed.
     * @param e The key event that triggered this action.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
    }

    /**
     * The action to invoke when a key is typed.
     * @param e The KeyEvent that triggered this action.
     */
    @Override
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Builds a new value from a string.
     * @param textField The string that you want to create the value from.
     * @return A value that can be set into the database.
     */
    public final TimeStamp buildValue(final String textField) {
        try {
            long ticks = 0;

            String[] timeChunks = textField.split(":");

            ticks += (new Long(timeChunks[HH]) * HH_TO_TICKS);
            ticks += (new Long(timeChunks[MM]) * MM_TO_TICKS);
            ticks += (new Long(timeChunks[SS]) * SS_TO_TICKS);
            ticks += (new Long(timeChunks[MMM]));

            return new TimeStamp(SS_TO_TICKS, ticks);
        } catch (SystemErrorException e) {
            logger.error("Unable to build TimeStamp value", e);
            return null;
        }
    }

    /**
     * Update the model to reflect the value represented by the
     * editor's text representation.
     */
    @Override
    public void updateModelValue() {
    }

    /**
     * Sanity check the current text of the editor and return a boolean.
     * @return true if the text is an okay representation for this DataValue.
     */
    @Override
    public boolean sanityCheck() {
        boolean res = true;
        // TODO: finish TimeStampDataValueEditor
        return res;
    }

}
