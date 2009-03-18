package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.util.Constants;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public final class CreateNewCellC {
    public CreateNewCellC() {
        this.createNewCell(-1);
    }

    public CreateNewCellC(final long milliseconds) {
        this.createNewCell(milliseconds);
    }

    public final void createNewCell(final long milliseconds) {
        /*
        try {
            long onset = milliseconds;
            // if not coming from video controller (milliseconds < 0) allow
            // multiple adds
            boolean multiadd = (milliseconds < 0);
            if (milliseconds < 0) {
                onset = 0;
            }

            
            boolean newcelladded = false;
            // try for selected columns
            for (DataColumn col : getSelectedCols()) {
                MatrixVocabElement mve =
                                        database.getMatrixVE(col.getItsMveID());
                DataCell cell = new DataCell(col.getDB(),
                                                col.getID(),
                                                mve.getID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                if (onset > 0) {
                    lastCreatedCellID = database.appendCell(cell);
                } else {
                    lastCreatedCellID = database.insertdCell(cell, 1);
                }
                lastCreatedColID = col.getID();
                newcelladded = true;
                if (!multiadd) {
                    break;
                }
            }

            if (!newcelladded) {
                // next try for selected cells
                Iterator <DataCell> itCells = getSelectedCells().iterator();
                while (itCells.hasNext()) {
                    // reget the selected cell from the database using its id
                    // in case a previous insert has changed its ordinal.
                    // recasting to DataCell without checking as the iterator
                    // only returns DataCells (no ref cells allowed so far)
                    DataCell dc = (DataCell) database
                                               .getCell(itCells.next().getID());
                    DataCell cell = new DataCell(database,
                                                 dc.getItsColID(),
                                                 dc.getItsMveID());
                    if (multiadd) {
                        cell.setOnset(dc.getOnset());
                        cell.setOffset(dc.getOffset());
                        lastCreatedCellID = database
                                            .insertdCell(cell, dc.getOrd() + 1);
                    } else {
                        cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                                                    onset));
                        lastCreatedCellID = database.appendCell(cell);
                    }
                    lastCreatedColID = cell.getItsColID();
                    newcelladded = true;
                    if (!multiadd) {
                        break;
                    }
                }
            }
            // last try lastColCreated
            if (!newcelladded) {
                if (lastCreatedColID == 0) {
                    lastCreatedColID = database.getDataColumns().get(0).getID();
                }
                // would throw by now if no columns exist
                DataColumn col = database.getDataColumn(lastCreatedColID);
                DataCell cell = new DataCell(col.getDB(),
                                                col.getID(),
                                                col.getItsMveID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                lastCreatedCellID = database.appendCell(cell);
            }
            deselectAll();
             */
        /*} catch (SystemErrorException e) {
            logger.error("Unable to create a new cell.", e);
        }*/
    }

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(CreateNewCellC.class);
}
