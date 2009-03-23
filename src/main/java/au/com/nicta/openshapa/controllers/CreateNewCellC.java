package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.db.DataCell;
import au.com.nicta.openshapa.db.DataColumn;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.MatrixVocabElement;
import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.TimeStamp;
import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Controller for creating new cell.
 *
 * @author cfreeman (refactored into seperate controller class.)
 * @author switcher (logic of controller - pulled from spreadsheet panel.)
 */
public final class CreateNewCellC {

    /**
     * Default constructor.
     */
    public CreateNewCellC() {
        // The spreadsheet is the view for this controller.
        view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                           .getMainView()
                                           .getComponent();
        model = OpenSHAPA.getDatabase();

        this.createNewCell(-1);
    }

    /**
     * Constructor - creates new controller.
     *
     * @param milliseconds The milliseconds to use for the onset for the new
     * cell.
     */
    public CreateNewCellC(final long milliseconds) {
        // The spreadsheet is the view for this controller.
        view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                           .getMainView()
                                           .getComponent();
        model = OpenSHAPA.getDatabase();

        this.createNewCell(milliseconds);
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to create a new cell from.
     */
    public final void createNewCell(final long milliseconds) {
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
            for (DataColumn col : view.getSelectedCols()) {
                MatrixVocabElement mve = model.getMatrixVE(col.getItsMveID());
                DataCell cell = new DataCell(col.getDB(),
                                             col.getID(),
                                             mve.getID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));

                if (onset > 0) {
                    OpenSHAPA.setLastCreatedCellId(model.appendCell(cell));
                } else {
                    OpenSHAPA.setLastCreatedCellId(model.insertdCell(cell, 1));
                }
                OpenSHAPA.setLastCreatedColId(col.getID());

                newcelladded = true;
                if (!multiadd) {
                    break;
                }
            }

            if (!newcelladded) {
                // next try for selected cells
                Iterator <DataCell> itCells = view.getSelectedCells()
                                                  .iterator();

                while (itCells.hasNext()) {
                    // reget the selected cell from the database using its id
                    // in case a previous insert has changed its ordinal.
                    // recasting to DataCell without checking as the iterator
                    // only returns DataCells (no ref cells allowed so far)
                    DataCell dc = (DataCell) model
                                             .getCell(itCells.next().getID());
                    DataCell cell = new DataCell(model,
                                                 dc.getItsColID(),
                                                 dc.getItsMveID());
                    if (multiadd) {
                        cell.setOnset(dc.getOnset());
                        cell.setOffset(dc.getOffset());
                        OpenSHAPA.setLastCreatedCellId(model
                                           .insertdCell(cell, dc.getOrd() + 1));
                    } else {
                        cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND,
                                                    onset));
                        OpenSHAPA.setLastCreatedCellId(model.appendCell(cell));
                    }
                    OpenSHAPA.setLastCreatedColId(cell.getItsColID());
                    newcelladded = true;
                    if (!multiadd) {
                        break;
                    }
                }
            }

            // last try lastColCreated
            if (!newcelladded) {
                if (OpenSHAPA.getLastCreatedColId() == 0) {
                    OpenSHAPA.setLastCreatedColId(model.getDataColumns()
                                                       .get(0)
                                                       .getID());
                }

                // would throw by now if no columns exist
                DataColumn col = model.getDataColumn(OpenSHAPA
                                                        .getLastCreatedColId());

                DataCell cell = new DataCell(col.getDB(),
                                             col.getID(),
                                             col.getItsMveID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                OpenSHAPA.setLastCreatedCellId(model.appendCell(cell));
            }
            view.deselectAll();
        } catch (SystemErrorException e) {
            logger.error("Unable to create a new cell.", e);
        }
    }

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(CreateNewCellC.class);

    /** The view (the spreadsheet) for this controller. */
    SpreadsheetPanel view;

    /** The model (the database) for this controller. */
    Database model;
}
