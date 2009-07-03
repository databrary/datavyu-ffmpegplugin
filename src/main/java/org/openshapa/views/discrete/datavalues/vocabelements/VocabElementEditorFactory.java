package org.openshapa.views.discrete.datavalues.vocabelements;

import org.openshapa.views.discrete.datavalues.*;
import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;

/**
 * A Factory for creating data value editors.
 */
public class VocabElementEditorFactory {

    /**
     * Constructor.
     */
    private VocabElementEditorFactory() {
    }

    /**
     * Creates a vector of editor components that represent the matrix in a
     * data cell.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix in the data cell.
     *
     * @return A vector of editor components to represent the matrix.
     */
    public static Vector<EditorComponent> buildVocabElement(JTextComponent ta,
                                            VocabElement ve, VocabElementV pv)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        if (ve != null) {
            eds.add(new VENameEditor(ta, ve, pv));
            eds.add(new FixedText(ta, "("));

            int numArgs = ve.getNumFormalArgs();
            // For each of the arguments, build a view representation
            for (int i = 0; i < numArgs; i++) {
                eds.addAll(buildFormalArgWithType(ta, ve, i, pv));
                if (numArgs > 1 && i < (numArgs - 1)) {
                    eds.add(new FixedText(ta, ", "));
                }
            }
            // check for variable args flag and show indicator
            if (ve.getVarLen()) {
                if (numArgs > 0) {
                    eds.add(new FixedText(ta, ", "));
                }
                eds.add(new FixedText(ta, "..."));
            }
            if (numArgs > 1) {
                eds.add(new FixedText(ta, ")"));
            }
        }
        return eds;
    }

    /**
     * Creates a vector of editor components to represent an argument of a
     * data cell's matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix containing the argument.
     * @param i The index of the argument within the matrix.
     *
     * @return A vector of editor components to represent the matrix argument.
     */
    public static Vector<EditorComponent> buildFormalArg(JTextComponent ta,
                                       VocabElement ve, int i, VocabElementV pv)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        eds.add(new FixedText(ta, "<"));
        eds.add(new FormalArgEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ">"));

        return eds;
    }

    /**
     * Creates a vector of editor components to represent an argument of a
     * data cell's matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix containing the argument.
     * @param i The index of the argument within the matrix.
     *
     * @return A vector of editor components to represent the matrix argument.
     */
    public static Vector<EditorComponent> buildFormalArgWithType(
                    JTextComponent ta, VocabElement ve, int i, VocabElementV pv)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        eds.add(new FixedText(ta, "<"));
        eds.add(new FormalArgEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ":"));
        eds.add(new FormalArgTypeEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ">"));

        return eds;
    }

    /**
     * Reset the value of an Editor component.
     * @param ed The editor component.
     * @param c The parent data cell.
     * @param m The matrix.
     */
    public static void resetValue(EditorComponent ed, VocabElement ve) {
        if (ed.getClass() == VENameEditor.class) {
            ((VENameEditor) ed).resetValue(ve);
        } else if (ed.getClass() == FormalArgEditor.class) {
            ((FormalArgEditor) ed).resetValue(ve);
        }
    }

}
