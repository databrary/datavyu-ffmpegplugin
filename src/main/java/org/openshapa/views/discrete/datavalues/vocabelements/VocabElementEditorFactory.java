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

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.openshapa.models.db.Argument;
import org.openshapa.models.db.Variable;
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
     *
     * @return A vector of editor components to represent the element.
     */
    public static List<EditorComponent> buildVocabElement(final JTextComponent ta,
                                                          final Variable var,
                                                          final Argument ve,
                                                          final VocabElementV pv) {

        List<EditorComponent> eds = new ArrayList<EditorComponent>();

        if (ve != null) {
            eds.add(new VENameEditor(ta, ve, var, pv));
            eds.add(new FixedText(ta, "("));

            int numArgs = ve.childArguments.size();
            // For each of the arguments, build a view representation
            for (int i = 0; i < numArgs; i++) {
                eds.addAll(buildFormalArg(ta, ve, var, i, pv));
                if (numArgs > 1 && i < (numArgs - 1)) {
                    eds.add(new FixedText(ta, ","));
                }
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
     *
     * @return A vector of editor components to represent the argument.
     */
    public static List<EditorComponent> buildFormalArg(JTextComponent ta,
                                                       Argument ve,
                                                       Variable var,
                                                       int i,
                                                       VocabElementV pv) {
        List<EditorComponent> eds = new ArrayList<EditorComponent>();

        eds.add(new FixedText(ta, "<"));
        eds.add(new FormalArgEditor(ta, var, i, pv));
        eds.add(new FixedText(ta, ":"));
        eds.add(new FormalArgTypeEditor(ta, ve, i, pv));
        eds.add(new FixedText(ta, ">"));

        return eds;
    }
}