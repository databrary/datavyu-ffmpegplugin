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
package org.openshapa.views.discrete.datavalues;

import java.util.Vector;

import javax.swing.text.JTextComponent;

import database.DataCell;
import database.DataValue;
import database.FloatDataValue;
import database.IntDataValue;
import database.Matrix;
import database.NominalDataValue;
import database.PredDataValue;
import database.Predicate;
import database.QuoteStringDataValue;
import database.SystemErrorException;
import database.TextStringDataValue;
import database.UndefinedDataValue;

import org.openshapa.views.discrete.EditorComponent;


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
     * @param c The parent data cell this editor resides within.
     * @param m The matrix in the data cell.
     *
     * @return A vector of editor components to represent the matrix.
     *
     * @throws SystemErrorException If unable to build editor components from
     * the supplied matrix.
     */
    public static Vector<EditorComponent> buildMatrix(final JTextComponent ta,
        final DataCell c, final Matrix m) throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        if (m != null) {

            if (m.getNumArgs() > 1) {
                eds.add(new FixedText(ta, "("));
            }

            // For each of the matrix arguments, build a view representation
            for (int i = 0; i < m.getNumArgs(); i++) {
                eds.addAll(buildMatrixArg(ta, c, m, i));

                if ((m.getNumArgs() > 1) && (i < (m.getNumArgs() - 1))) {
                    eds.add(new FixedText(ta, ", "));
                }
            }

            if (m.getNumArgs() > 1) {
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
    public static Vector<EditorComponent> buildMatrixArg(
        final JTextComponent ta, final DataCell c, final Matrix m, final int i)
        throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        DataValue dv = m.getArgCopy(i);

        if (dv.getClass() == FloatDataValue.class) {
            eds.add(buildFloat(ta, c, m, i));
        } else if (dv.getClass() == IntDataValue.class) {
            eds.add(buildInt(ta, c, m, i));
        } else if (dv.getClass() == TextStringDataValue.class) {
            eds.add(buildTextString(ta, c, m, i));
        } else if (dv.getClass() == NominalDataValue.class) {
            eds.add(buildNominal(ta, c, m, i));
        } else if (dv.getClass() == UndefinedDataValue.class) {
            eds.add(buildUndefined(ta, c, m, i));
        } else if (dv.getClass() == QuoteStringDataValue.class) {
            eds.addAll(buildQuoteString(ta, c, m, i));
        } else if (dv.getClass() == PredDataValue.class) {
            eds.addAll(buildPredicate(ta, c, m, i));
        }

        return eds;
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildFloat(final JTextComponent ta,
        final DataCell c, final Matrix m, final int i) {
        return new FloatDataValueEditor(ta, c, m, i);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildInt(final JTextComponent ta,
        final DataCell c, final Matrix m, final int i) {
        return new IntDataValueEditor(ta, c, m, i);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildTextString(final JTextComponent ta,
        final DataCell c, final Matrix m, final int i) {
        return new TextStringDataValueEditor(ta, c, m, i);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildNominal(final JTextComponent ta,
        final DataCell c, final Matrix m, final int i) {
        return new NominalDataValueEditor(ta, c, m, i);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildUndefined(final JTextComponent ta,
        final DataCell c, final Matrix m, final int i) {
        return new UndefinedDataValueEditor(ta, c, m, i);
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix holding the datavalue this editor will represent.
     * @param i The index of the datavalue within the matrix.
     *
     * @return A vector of editor components to represent the QuoteString.
     */
    public static Vector<EditorComponent> buildQuoteString(
        final JTextComponent ta, final DataCell c, final Matrix m,
        final int i) {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        EditorComponent leftquote = new FixedText(ta, "\"");
        EditorComponent rightquote = new FixedText(ta, "\"");
        eds.add(leftquote);
        eds.add(new QuoteStringDataValueEditor(ta, c, m, i, leftquote,
                rightquote));
        eds.add(rightquote);

        return eds;
    }

    /**
     * Creates a vector of editor components that represent a predicate in a
     * matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix in the data cell.
     * @param i The index of the predicate within the matrix.
     *
     * @return A vector of editor components to represent the predicate.
     */
    public static Vector<EditorComponent> buildPredicate(
        final JTextComponent ta, final DataCell c, final Matrix m,
        final int index) throws SystemErrorException {

        Vector<EditorComponent> args = buildPredicateArgs(ta, c, m, index);

        // make the PredicateNameEditor and pass it a vector of its args
        PredicateNameEditor pn = new PredicateNameEditor(ta, c, m, index, args);

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        // insert the predicate name at the front
        eds.add(pn);
        eds.addAll(args);

        return eds;
    }

    /**
     * Creates a vector of editor components that represent the args in
     * a predicate in a matrix.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param m The matrix in the data cell.
     * @param i The index of the predicate within the matrix.
     *
     * @return A vector of editor components to represent the predicate.
     */
    public static Vector<EditorComponent> buildPredicateArgs(
        final JTextComponent ta, final DataCell c, final Matrix m,
        final int index) throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        PredDataValue pdv = (PredDataValue) m.getArgCopy(index);
        int numPredArgs = 0;

        if (!pdv.isEmpty()) {
            numPredArgs = pdv.getItsValue().getNumArgs();
        }

        if (numPredArgs > 0) {
            eds.add(new FixedText(ta, "("));

            // For each of the predicate arguments, build a view representation
            for (int pi = 0; pi < numPredArgs; pi++) {
                eds.addAll(buildPredArg(ta, c, pdv, pi, m, index));

                if ((numPredArgs > 1) && (pi < (numPredArgs - 1))) {
                    eds.add(new FixedText(ta, ", "));
                }
            }

            eds.add(new FixedText(ta, ")"));
        }

        return eds;
    }

    /**
     * Creates a vector of editor components to represent an argument of a
     * predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix containing the argument.
     * @param mi The index of the argument within the matrix.
     *
     * @return A vector of editor components to represent the matrix argument.
     */
    public static Vector<EditorComponent> buildPredArg(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int mi) throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        Predicate pred = p.getItsValue();
        DataValue dv = pred.getArgCopy(pi);

        if (dv.getClass() == FloatDataValue.class) {
            eds.add(buildFloat(ta, c, p, pi, m, mi));
        } else if (dv.getClass() == IntDataValue.class) {
            eds.add(buildInt(ta, c, p, pi, m, mi));
        } else if (dv.getClass() == TextStringDataValue.class) {
            eds.add(buildTextString(ta, c, p, pi, m, mi));
        } else if (dv.getClass() == NominalDataValue.class) {
            eds.add(buildNominal(ta, c, p, pi, m, mi));
        } else if (dv.getClass() == QuoteStringDataValue.class) {
            eds.addAll(buildQuoteString(ta, c, p, pi, m, mi));
        } else if (dv.getClass() == UndefinedDataValue.class) {
            eds.add(buildUndefined(ta, c, p, pi, m, mi));
        }

        return eds;
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildFloat(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int i) {
        return new FloatDataValueEditor(ta, c, p, pi, m, i);
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildInt(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int i) {
        return new IntDataValueEditor(ta, c, p, pi, m, i);
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildTextString(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int i) {
        return new TextStringDataValueEditor(ta, c, p, pi, m, i);
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildNominal(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int i) {
        return new NominalDataValueEditor(ta, c, p, pi, m, i);
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static EditorComponent buildUndefined(final JTextComponent ta,
        final DataCell c, final PredDataValue p, final int pi, final Matrix m,
        final int i) {
        return new UndefinedDataValueEditor(ta, c, p, pi, m, i);
    }

    /**
     * Creates a data value view from the specified data value in a predicate.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param c The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param m The matrix holding the predicate.
     * @param i The index of the predicate within the matrix.
     *
     * @return An editor component to represent the specified data value.
     */
    public static Vector<EditorComponent> buildQuoteString(
        final JTextComponent ta, final DataCell c, final PredDataValue p,
        final int pi, final Matrix m, final int i) {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        EditorComponent leftquote = new FixedText(ta, "\"");
        EditorComponent rightquote = new FixedText(ta, "\"");
        eds.add(leftquote);
        eds.add(new QuoteStringDataValueEditor(ta, c, p, pi, m, i, leftquote,
                rightquote));
        eds.add(rightquote);

        return eds;
    }
}
