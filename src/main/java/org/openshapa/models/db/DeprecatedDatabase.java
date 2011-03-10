package org.openshapa.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.legacy.MacshapaDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.util.Constants;

/**
 * Converts legacy database calls into newer datastore calls.
 *
 * @deprecated Should use the datastore interface instead. This is a temporary
 * class to allow us to incrementally migrate to the new API.
 */
@Deprecated public class DeprecatedDatabase implements Datastore {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedDatabase.class);

    /** The legacy database that this datastore represents. */
    private MacshapaDatabase legacyDB;

    /** The list of variables stored in this database. */
    private List<Variable> variables;

    /**
     * Default constructor.
     */
    public DeprecatedDatabase() {
        try {
            legacyDB = new MacshapaDatabase(Constants.TICKS_PER_SECOND);
            // BugzID:449 - Set default database name.
            legacyDB.setName("Database1");
            variables = new ArrayList<Variable>();
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to create new database", e);
        }
    }

    /**
     * @return The legacy database that this datastore represents.
     *
     * @deprecated Should use methods defined in datastore interface rather than
     * the db.legacy package.
     */
    @Deprecated public MacshapaDatabase getDatabase() {
        return legacyDB;
    }

    /**
     * Sets the legacy database that this datastore represents.
     *
     * @param newDB The new legacy databsae that this datastore represents.
     *
     * @deprecated Should use methods defined in datastore interface rather than
     * the db.legacy package.
     */
    @Deprecated public void setDatabase(MacshapaDatabase newDB) {
        legacyDB = newDB;
    }

    @Deprecated public DeprecatedVariable getByLegacyID(final long colID) {
        try {
            return new DeprecatedVariable(getDatabase().getDataColumn(colID));
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get variable", e);
        }

        // Failed - returned null.
        return null;
    }

    @Override public String getName() {
        return legacyDB.getName();
    }

    @Override public void addVariable(final Variable var) {
        DeprecatedVariable legacyVar = (DeprecatedVariable) var;

        try {
            long colId = legacyDB.addColumn(legacyVar.getLegacyVariable());
            legacyVar.setLegacyVariable(legacyDB.getDataColumn(colId));
            variables.add(var);
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to add variable", e);
        }
    }

    @Override public List<Variable> getAllVariables() {
        return variables;
    }
}
