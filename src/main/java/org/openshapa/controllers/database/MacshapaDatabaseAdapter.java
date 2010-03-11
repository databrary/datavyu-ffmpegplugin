package org.openshapa.controllers.database;

import org.openshapa.models.database.DeprecatedDatabase;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;

/**
 * This class is used to implement the interactions with
 * {@link MacshapaDatabase} using methods defined by the
 * {@link org.openshapa.models.database.Database} interface.
 */
public class MacshapaDatabaseAdapter implements
        DeprecatedDatabase<MacshapaDatabase> {

    private UserMetrix logger =
            UserMetrix.getInstance(MacshapaDatabaseAdapter.class);

    private MacshapaDatabase db;

    public MacshapaDatabaseAdapter() {
        try {
            db = new MacshapaDatabase();
            db.setTicks(Constants.TICKS_PER_SECOND);
        } catch (SystemErrorException e) {
            logger.error("Unable to create new database", e);
        }
    }

    public MacshapaDatabase getDatabase() {
        return db;
    }

}
