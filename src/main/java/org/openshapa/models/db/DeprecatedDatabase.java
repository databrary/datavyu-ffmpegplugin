package org.openshapa.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.legacy.DataCell;
import org.openshapa.models.db.legacy.DataColumn;
import org.openshapa.models.db.legacy.MacshapaDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.openshapa.util.Constants;

/**
 * Defines a method for retrieving the deprecated database implementation.
 * 
 * @param <T>
 *            the type of the deprecated database to retrieve.
 */
public class DeprecatedDatabase implements Datastore {

    private static Logger LOGGER = UserMetrix.getLogger(DeprecatedDatabase.class);

    private MacshapaDatabase legacyDB;

    public DeprecatedDatabase() {
        try {
            legacyDB = new MacshapaDatabase(Constants.TICKS_PER_SECOND);
            // BugzID:449 - Set default database name.
            legacyDB.setName("Database1");
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to create new database", e);
        }
    }

    @Deprecated public MacshapaDatabase getDatabase() {
        return legacyDB;
    }

    @Deprecated public void setDatabase(MacshapaDatabase newDB) {
        legacyDB = newDB;
    }

    @Override public List<Variable> getAllVariables() {
        List<Variable> simpleCols = new ArrayList<Variable>();
        try {
            for (DataColumn dc : legacyDB.getDataColumns()) {
                if (dc == null) {
                    System.out.println("Datacolumn was null");
                    continue;
                }
                Variable sc = new DeprecatedVariable(dc);
                int numCells = dc.getNumCells();
                for (int i = 1; i < numCells + 1; i++) {
                    org.openshapa.models.db.legacy.Cell cell = legacyDB.getCell(dc.getID(), i);
                    if (cell instanceof DataCell) {                        
                        Cell sCell = new DeprecatedCell((DataCell) cell);
                        
                        // probably not needed.
                        sc.addCell(sCell);
                    }
                }
                simpleCols.add(sc);
            }
        } catch (SystemErrorException ex) {
            LOGGER.error("System prevented database access", ex);
        }

        return simpleCols;
    }

}
