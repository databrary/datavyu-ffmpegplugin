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
package org.datavyu.views.discrete.datavalues;

import javax.swing.text.JTextComponent;

import java.util.ArrayList;
import java.util.List;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.MatrixValue;
import org.datavyu.models.db.NominalValue;
import org.datavyu.models.db.TextValue;
import org.datavyu.models.db.Value;
import org.datavyu.views.discrete.EditorComponent;


/**
 * A Factory for creating data value editors.
 */
public class DataValueEditorFactory {

    /**
     * Constructor.
     */
    private DataValueEditorFactory() {
    }

    /**
     * Creates a vector of editor components that represent the matrix in a
     * data cell.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent cell the editors resides within.
     *
     * @return A vector of editor components to represent the matrix.
     */
    public static List<EditorComponent> buildMatrix(final JTextComponent ta,
                                                    final Cell c) {

        List<EditorComponent> eds = new ArrayList<EditorComponent>();

        Value val = c.getValue();

        if (val != null) {
            if (val instanceof MatrixValue) {
                MatrixValue mv = (MatrixValue) val;
                eds.add(new FixedText(ta, "("));

                for (int i = 0; i < mv.getArguments().size(); i++) {
                    // TODO update.
                    eds.add(buildMatrixArg(ta, mv.getArguments().get(i)));

                    if ((mv.getArguments().size() > 1) && (i < (mv.getArguments().size() - 1))) {
                        eds.add(new FixedText(ta, ", "));
                    }
                }

                eds.add(new FixedText(ta, ")"));

            } else if (val instanceof TextValue) {
                eds.add(buildTextString(ta, (TextValue) val));

            } else {
                eds.add(buildNominal(ta, (NominalValue) val));
            }
        }

        return eds;
    }

    /**
     * Creates a vector of editor components to represent an argument of a
     * data cell's matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param v The value that we are building an editor for.
     *
     * @return The editor component that represents the supplied value
     */
    public static EditorComponent buildMatrixArg(final JTextComponent ta,
                                                 final Value v) {

        if (v instanceof TextValue) {
            return buildTextString(ta, (TextValue) v);
        } else {
            return buildNominal(ta, (NominalValue) v);
        }
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param v The value this editor manipulates
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildTextString(final JTextComponent ta,
                                                  final TextValue v) {
        return new TextStringDataValueEditor(ta, v);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param v The value this editor manipulates
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildNominal(final JTextComponent ta,
                                               final NominalValue v) {
        return new NominalDataValueEditor(ta, v);
    }
}
