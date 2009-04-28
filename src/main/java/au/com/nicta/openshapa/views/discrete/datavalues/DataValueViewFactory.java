package au.com.nicta.openshapa.views.discrete.datavalues;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.FloatDataValue;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.Matrix;
import au.com.nicta.openshapa.db.NominalDataValue;
import au.com.nicta.openshapa.db.PredDataValue;
import au.com.nicta.openshapa.db.QuoteStringDataValue;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TextStringDataValue;
import au.com.nicta.openshapa.db.TimeStampDataValue;
import au.com.nicta.openshapa.db.UndefinedDataValue;
import au.com.nicta.openshapa.views.discrete.Selector;

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
}
