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
package org.openshapa.views.discrete.datavalues.vocabelements;

import java.util.Vector;
import javax.swing.text.JTextComponent;

import database.SystemErrorException;
import database.VocabElement;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.datavalues.FixedText;

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
     * @param ve The parent VocabElement the editor is in.
     * @param pv The parent VocabElementV the editor is in.
     * @throws SystemErrorException if problem getting vocab element from db.
     * @return A vector of editor components to represent the element.
     */
    public static Vector<EditorComponent> buildVocabElement(
                                                        final JTextComponent ta,
                                                        final VocabElement ve,
                                                        final VocabElementV pv)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        if (ve != null) {
            eds.add(new VENameEditor(ta, ve, pv));
            eds.add(new FixedText(ta, "("));

            int numArgs = ve.getNumFormalArgs();
            // For each of the arguments, build a view representation
            for (int i = 0; i < numArgs; i++) {
                eds.addAll(buildFormalArg(ta, ve, i, pv));
                if (numArgs > 1 && i < (numArgs - 1)) {
                    eds.add(new FixedText(ta, ","));
                }
            }
            // check for variable args flag and show indicator
            if (ve.getVarLen()) {
                if (numArgs > 0) {
                    eds.add(new FixedText(ta, ","));
                }
                eds.add(new FixedText(ta, "..."));
            }

            eds.add(new FixedText(ta, ")"));
        }
        return eds;
    }

    /**
     * Creates a vector of editor components to represent an argument of a
     * data cell's matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param ve The parent VocabElement the editor is in.
     * @param i The index of the argument within the element.
     * @param pv The parent VocabElementV the editor is in.
     * @throws SystemErrorException if problem getting vocab element from db.
     * @return A vector of editor components to represent the argument.
     */
    public static Vector<EditorComponent> buildFormalArg(JTextComponent ta,
                                                         VocabElement ve,
                                                         int i,
                                                         VocabElementV pv)
    throws SystemErrorException {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        eds.add(new FixedText(ta, "<"));
        eds.add(new FormalArgEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ":"));
        eds.add(new FormalArgTypeEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ">"));

        return eds;
    }
}
