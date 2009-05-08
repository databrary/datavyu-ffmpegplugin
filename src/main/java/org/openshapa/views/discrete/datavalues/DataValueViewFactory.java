package org.openshapa.views.discrete.datavalues;

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
import org.openshapa.db.TimeStampDataValue;
import org.openshapa.db.UndefinedDataValue;
import org.openshapa.views.discrete.Selector;

/**
 * A Factory for creating data value views.
 *
 * @author cfreeman
 */
public class DataValueViewFactory {

    /**
     * Constructor.
     */
    private DataValueViewFactory() {
    }

    /**
     * Creates a data value view from the specified data value within a matrix.
     *
     * @param s The parent selector to use for the created view.
     * @param c The parent data cell that this view resides within.
     * @param m The matrix holding the datavalue that this view will represent.
     * @param i The index of the datavalue within the previous matrix that this
     * view will represent.
     *
     * @return A data value view to represent the specified data value.
     *
     * @throws SystemErrorException If unable to create the view for the
     * specified data value view.
     */
    public static DataValueV build(Selector s, DataCell c, Matrix m, int i)
    throws SystemErrorException {

        DataValue dv = m.getArgCopy(i);

        if (dv.getClass() == FloatDataValue.class) {
            return new FloatDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == IntDataValue.class) {
            return new IntDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == TimeStampDataValue.class) {
            return new TimeStampValueView(s, c, m, i, true);
        } else if (dv.getClass() == TextStringDataValue.class) {
            return new TextStringDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == NominalDataValue.class) {
            return new NominalDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == PredDataValue.class) {
            return new PredicateDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == QuoteStringDataValue.class) {
            return new QuoteStringDataValueView(s, c, m, i, true);
        } else if (dv.getClass() == UndefinedDataValue.class) {
            return new UndefinedDataValueView(s, c, m, i, true);
        }

        return null;
    }

    /**
     * Creates data value view from the specified data value within a predicate.
     *
     * @param s The parent selector to use for the created view.
     * @param c The parent data cell that this view resides within.
     * @param p The predicate holding the data value that this view will
     * represent.
     * @param i The index of the datavalue within the previous predicate that
     * this view will represent.
     *
     * @return A data value view to represent the specified data value.
     *
     * @throws SystemErrorException If unable to create the view for the
     * specified data value view.
     */
    public static DataValueV build(Selector s, DataCell c, PredDataValue p,
                                   int pi, Matrix m, int mi)
    throws SystemErrorException {

        Predicate pred = p.getItsValue();
        DataValue dv = pred.getArgCopy(pi);
        if (dv.getClass() == FloatDataValue.class) {
            return new FloatDataValueView(s, c, p, pi, m, mi, true);
        } else if (dv.getClass() == IntDataValue.class) {
            return new IntDataValueView(s, c, p, pi, m, mi, true);
        } else if (dv.getClass() == TimeStampDataValue.class) {
            return new TimeStampValueView(s, c, p, pi, m, mi, true);
        } else if (dv.getClass() == TextStringDataValue.class) {
            return new TextStringDataValueView(s, c, p, pi, m, mi, true);
        } else if (dv.getClass() == NominalDataValue.class) {
            return new NominalDataValueView(s, c, p, pi, m, mi, true);
        // Currently we are unable to nest predicates and matrices within each
        // other. Need to change the database datavalues to a composite pattern
        // so we can recursively update as needed.

        //} else if (dv.getClass() == PredDataValue.class) {
        //    return new PredicateDataValueView(s, c, p, i, true);
        } else if (dv.getClass() == QuoteStringDataValue.class) {
            return new QuoteStringDataValueView(s, c, p, pi, m, mi, true);
        } else if (dv.getClass() == UndefinedDataValue.class) {
            return new UndefinedDataValueView(s, c, p, pi, m, mi, true);
        }

        return null;
    }
}
