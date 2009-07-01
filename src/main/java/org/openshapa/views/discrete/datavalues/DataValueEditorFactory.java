package org.openshapa.views.discrete.datavalues;

import java.util.Vector;
import javax.swing.text.JTextComponent;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataValue;
import org.openshapa.db.FloatDataValue;
import org.openshapa.db.IntDataValue;
import org.openshapa.db.Matrix;
import org.openshapa.db.NominalDataValue;
import org.openshapa.db.PredDataValue;
import org.openshapa.db.Predicate;
import org.openshapa.db.QuoteStringDataValue;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.TextStringDataValue;
import org.openshapa.db.UndefinedDataValue;
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
     */
    public static Vector<EditorComponent> buildMatrix(JTextComponent ta,
                                                           DataCell c, Matrix m)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        if (m != null) {
            if (m.getNumArgs() > 1) {
                eds.add(new FixedText(ta, "("));
            }
            // For each of the matrix arguments, build a view representation
            for (int i = 0; i < m.getNumArgs(); i++) {
                eds.addAll(buildMatrixArg(ta, c, m, i));
                if (m.getNumArgs() > 1 && i < (m.getNumArgs() - 1)) {
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
    public static Vector<EditorComponent> buildMatrixArg(JTextComponent ta,
                                                    DataCell c, Matrix m, int i)
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
    public static EditorComponent buildFloat(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
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
    public static EditorComponent buildInt(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
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
    public static EditorComponent buildTextString(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
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
    public static EditorComponent buildNominal(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
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
    public static EditorComponent buildUndefined(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
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
    public static Vector<EditorComponent> buildQuoteString(JTextComponent ta,
                                                DataCell c, Matrix m, int i) {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        EditorComponent leftquote = new FixedText(ta, "\"");
        EditorComponent rightquote = new FixedText(ta, "\"");
        eds.add(leftquote);
        eds.add(new QuoteStringDataValueEditor(ta,
                                               c, m, i, leftquote, rightquote));
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
    public static Vector<EditorComponent> buildPredicate(JTextComponent ta,
                                                DataCell c, Matrix m, int index)
    throws SystemErrorException {

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
    public static Vector<EditorComponent> buildPredicateArgs(JTextComponent ta,
                                                DataCell c, Matrix m, int index)
    throws SystemErrorException {

        Vector<EditorComponent> eds = new Vector<EditorComponent>();

        PredDataValue pdv = (PredDataValue) m.getArgCopy(index);
        int numPredArgs = 0;
        if (!pdv.isEmpty()) {
            numPredArgs = pdv.getItsValue().getNumArgs();
        }

        if (m != null && numPredArgs > 0) {
            eds.add(new FixedText(ta, "("));

            // For each of the predicate arguments, build a view representation
            for (int pi = 0; pi < numPredArgs; pi++) {
                eds.addAll(buildPredArg(ta, c, pdv, pi, m, index));
                if (numPredArgs > 1 && pi < (numPredArgs - 1)) {
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
    public static Vector<EditorComponent> buildPredArg(JTextComponent ta,
                          DataCell c, PredDataValue p, int pi, Matrix m, int mi)
    throws SystemErrorException {

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
    public static EditorComponent buildFloat(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
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
    public static EditorComponent buildInt(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
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
    public static EditorComponent buildTextString(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
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
    public static EditorComponent buildNominal(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
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
    public static EditorComponent buildUndefined(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
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
    public static Vector<EditorComponent> buildQuoteString(JTextComponent ta,
                        DataCell c, PredDataValue p, int pi, Matrix m, int i) {
        Vector<EditorComponent> eds = new Vector<EditorComponent>();
        EditorComponent leftquote = new FixedText(ta, "\"");
        EditorComponent rightquote = new FixedText(ta, "\"");
        eds.add(leftquote);
        eds.add(new QuoteStringDataValueEditor(ta,
                                        c, p, pi, m, i, leftquote, rightquote));
        eds.add(rightquote);
        return eds;
    }

    /**
     * Reset the value of an Editor component.
     * @param ed The editor component.
     * @param c The parent data cell.
     * @param m The matrix.
     */
    public static void resetValue(EditorComponent ed, DataCell c, Matrix m) {
        if (ed.getClass() == FloatDataValueEditor.class) {
            ((FloatDataValueEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == IntDataValueEditor.class) {
            ((IntDataValueEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == TextStringDataValueEditor.class) {
            ((TextStringDataValueEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == NominalDataValueEditor.class) {
            ((NominalDataValueEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == PredicateNameEditor.class) {
            ((PredicateNameEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == QuoteStringDataValueEditor.class) {
            ((QuoteStringDataValueEditor) ed).resetValue(c, m);
        } else if (ed.getClass() == UndefinedDataValueEditor.class) {
            ((UndefinedDataValueEditor) ed).resetValue(c, m);
        }
    }

}
