package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.OpenSHAPA;
import au.com.nicta.openshapa.views.discrete.SpreadsheetPanel;
import au.com.nicta.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * Controller for setting the spreadsheet layout.
 *
 * @author cfreeman
 */
public class SetSheetLayoutC {

    /**
     * Constructor - creates and invokes the controller.
     *
     * @param type The layout type to use on the spreadsheet.
     */
    public SetSheetLayoutC(final SheetLayoutType type) {
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
                                                            .getMainView()
                                                            .getComponent();
        view.setLayoutType(type);
    }
}
