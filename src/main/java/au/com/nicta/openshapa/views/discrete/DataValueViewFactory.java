package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.db.FloatDataValue;
import au.com.nicta.openshapa.db.IntDataValue;
import au.com.nicta.openshapa.db.NominalDataValue;
import au.com.nicta.openshapa.db.PredDataValue;
import au.com.nicta.openshapa.db.TextStringDataValue;
import au.com.nicta.openshapa.db.TimeStampDataValue;

/**
 *
 * @author cfreeman
 */
public class DataValueViewFactory {

    /**
     *
     * @param dv
     * @return
     */
    public static DataValueView build(DataValue dv) {
        if (dv.getClass() == FloatDataValue.class) {
            return new FloatDataValueView((FloatDataValue) dv, true);
        } else if (dv.getClass() == IntDataValue.class) {
            return new IntDataValueView((IntDataValue) dv, true);
        } else if (dv.getClass() == TimeStampDataValue.class) {
            return new TimeStampDataValueView((TimeStampDataValue) dv, true);
        } else if (dv.getClass() == TextStringDataValue.class) {
            return new TextStringDataValueView((TextStringDataValue) dv, true);
        } else if (dv.getClass() == NominalDataValue.class) {
            return new NominalDataValueView((NominalDataValue) dv, true);
        } else if (dv.getClass() == PredDataValue.class) {
            return new PredicateDataValueView((PredDataValue) dv, true);
        }

        return null;
    }
}
