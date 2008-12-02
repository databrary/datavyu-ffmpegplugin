package au.com.nicta.openshapa.disc;

import au.com.nicta.openshapa.db.Database;

/**
 *
 * @author Felix
 */
public abstract class DiscreteDataManager {
    public DiscreteDataManager() {
    }

    public abstract Database createDatabase();

    public abstract Database openDatabase();
    public abstract Database saveDatabase(Database db);
    public abstract Database saveDatabaseAs(Database db);
    public abstract void closeDatabase(Database db);
}
