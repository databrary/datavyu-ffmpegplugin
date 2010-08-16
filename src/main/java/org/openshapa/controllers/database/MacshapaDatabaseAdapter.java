package org.openshapa.controllers.database;

import com.usermetrix.jclient.Logger;
import java.util.List;
import org.openshapa.models.database.DeprecatedDatabase;
import org.openshapa.models.db.MacshapaDatabase;
import org.openshapa.models.db.SimpleVariable;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.SimpleCell;
import org.openshapa.models.db.SimpleDatabase;

/**
 * This class is used to implement the interactions with
 * {@link MacshapaDatabase} using methods defined by the
 * {@link org.openshapa.models.database.Database} interface, and the
 * {@link org.openshapa
 */
public class MacshapaDatabaseAdapter implements
        DeprecatedDatabase<MacshapaDatabase>, SimpleDatabase {

    private Logger logger = UserMetrix.getLogger(MacshapaDatabaseAdapter.class);

    private MacshapaDatabase db;

    public MacshapaDatabaseAdapter() {
        try {
            db = new MacshapaDatabase(Constants.TICKS_PER_SECOND);
            // BugzID:449 - Set default database name.
            db.setName("Database1");
        } catch (SystemErrorException e) {
            logger.error("Unable to create new database", e);
        }
    }

    @Override
    public MacshapaDatabase getDatabase() {
        return db;
    }

    @Override
    public void setDatabase(MacshapaDatabase newDB) {
        db = newDB;
    }


    @Override
    public List<SimpleVariable> getAllVariables() {
        List<SimpleVariable> simpleCols = new ArrayList<SimpleVariable>();
        try {
            for (DataColumn dc : db.getDataColumns()) {
                if (dc == null) {
                    System.out.println("Datacolumn was null");
                    continue;
                }
                SimpleVariable sc = new SimpleVariable(dc.getName());
                int numCells = dc.getNumCells();
                for (int i = 1; i < numCells + 1; i++) {
                    Cell cell = db.getCell(dc.getID(), i);
                    if (cell instanceof DataCell) {
                        DataCell dCell = (DataCell) cell;
                        String val = dCell.getVal().getArgCopy(0).toString();
                        long onset = dCell.getOnset().getTime();
                        long offset = dCell.getOffset().getTime();
                        SimpleCell sCell = new SimpleCell(val, onset, offset);
                        sc.addCell(sCell);
                    }
                }
                simpleCols.add(sc);
            }
        } catch (SystemErrorException ex) {
            System.out.println("SystemErrorException prevented db access.");
        }

        return simpleCols;
    }

}
