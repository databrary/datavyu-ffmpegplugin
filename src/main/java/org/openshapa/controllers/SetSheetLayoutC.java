package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.openshapa.views.discrete.layouts.SheetLayoutFactory.SheetLayoutType;

/**
 * Controller for setting the spreadsheet layout.
 *
 * @author switcher (logic of controller - pulled from spreadsheet panel.)
 */
public final class SetSheetLayoutC {

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
