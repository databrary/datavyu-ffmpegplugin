/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fest.swing.fixture;

import java.awt.Frame;
import java.awt.Point;

import java.io.File;

import javax.swing.JPanel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.util.Platform;

import org.openshapa.util.UIUtils;

import org.openshapa.views.ConsoleV;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.NewVariableV;
import org.openshapa.views.VariableListV;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.SpreadsheetPanel;


/**
 *
 */
public class OpenSHAPAFrameFixture extends FrameFixture {

    /** X window offset for clicking menu in OSX. */
    private static final int WINDOW_X_OFFSET = 25;

    /** Y window offset for clicking menu in OSX. */
    private static final int WINDOW_Y_OFFSET = 25;

    /** Dialog timeout in milliseconds. */
    private static final int DIALOG_TIMEOUT = 10000;

    /**
     * Constructor.
     * @param target Frame
     */
    public OpenSHAPAFrameFixture(final Frame target) {
        super(target);
    }

    /**
     * Constructor.
     * @param name String
     */
    public OpenSHAPAFrameFixture(final String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param robot Robot
     * @param target Frame
     */
    public OpenSHAPAFrameFixture(final Robot robot, final Frame target) {
        super(robot, target);
    }

    /**
     * Constructor.
     * @param robot Robot
     * @param name String
     */
    public OpenSHAPAFrameFixture(final Robot robot, final String name) {
        super(robot, name);
    }

    /**
     * Click menu item with path.
     * @param path path to menu item, top down.
     * @return JMenuItemFixture for menu item
     */
    public final JMenuItemFixture clickMenuItemWithPath(final String... path) {
        JMenuItemFixture result = super.menuItemWithPath(path).click();

        if (Platform.isOSX()) {
            Point edgeOfWindow = new Point(component().getWidth()
                    - WINDOW_X_OFFSET,
                    component().getHeight() - WINDOW_Y_OFFSET);
            robot.click(target, edgeOfWindow);
        }

        return result;
    }

    /**
     * @return title of window.
     */
    public final String getTitle() {
        return target.getTitle();
    }

    /**
     * Creates a new variable (column).
     * @param varName name of variable
     * @param varRadio type of variable (radio button to click)
     */
    public final void createNewVariable(final String varName,
        final String varRadio) {
        String varRadioCompName;

        if (varRadio.endsWith("TypeButton")) {
            varRadioCompName = varRadio;
        } else {
            varRadioCompName = varRadio.toLowerCase() + "TypeButton";
        }

        // 1. Create new variable
        clickMenuItemWithPath("Spreadsheet", "New Variable");

        DialogFixture newVariableDialog = WindowFinder.findDialog(
                NewVariableV.class).withTimeout(DIALOG_TIMEOUT).using(robot);

        // Check if the new variable dialog is actually visible
        newVariableDialog.requireVisible();

        // Get the variable value text box
        JTextComponentFixture variableValueTextBox =
            newVariableDialog.textBox();

        // The variable value box should have no text in it
        variableValueTextBox.requireEmpty();

        // It should be editable
        variableValueTextBox.requireEditable();

        // Type in some text.
        variableValueTextBox.enterText(varName);

        // Get the radio button for text variables
        newVariableDialog.radioButton(varRadioCompName).click();

        // Check that it is selected
        newVariableDialog.radioButton(varRadioCompName).requireSelected();

        // Click "OK"
        newVariableDialog.button("okButton").click();
    }

    /**
    * Returns the spreadsheet from the mainframefixture.
    * @return JPanelFixture of the spreadsheet
    */
    public final SpreadsheetPanelFixture getSpreadsheet() {
        JPanelFixture jPanel = panel(new GenericTypeMatcher<JPanel>(
                    JPanel.class) {
                    @Override protected boolean isMatching(final JPanel panel) {
                        return panel.getClass().equals(SpreadsheetPanel.class);
                    }
                });

        return new SpreadsheetPanelFixture(robot,
                (SpreadsheetPanel) jPanel.component());
    }

    /**
    * Runs script in File script.
    * @param script Script File to run
    */
    public final void runScript(final File script) {

        if (Platform.isOSX()) {
            UIUtils.runScriptOnOSX(script);
        } else {
            clickMenuItemWithPath("Script", "Run script");

            JFileChooserFixture jfcf = fileChooser();
            jfcf.selectFile(script).approve();
        }
    }

    /**
     * Closes the script console.
     */
    public final void closeScriptConsoleOnFinish() {
        DialogFixture scriptConsole = WindowFinder.findDialog(ConsoleV.class)
            .withTimeout(DIALOG_TIMEOUT).using(robot);

        long currentTime = System.currentTimeMillis();
        long maxTime = currentTime + UIUtils.SCRIPT_LOAD_TIMEOUT;

        while ((System.currentTimeMillis() < maxTime)
                && (!scriptConsole.textBox().text().contains("Finished"))) {
            Thread.yield();
        }

        scriptConsole.button("closeButton").click();
    }


    /**
     * Open and return the data controller.
     * @return DataControllerFixture for data controller
     */
    public final DataControllerFixture openDataController() {
        clickMenuItemWithPath("Controller", "Data Viewer Controller");

        DialogFixture dcf = WindowFinder.findDialog(DataControllerV.class)
            .withTimeout(DIALOG_TIMEOUT).using(robot);
        dcf.moveTo(new Point(0, 100));

        return new DataControllerFixture(robot,
                (DataControllerV) dcf.component());
    }

    /**
     * Open and return the data controller. Also moves to specified coordinates.
     * @param xLocation x location on screen to move to
     * @param yLocation y location on screen to move to
     * @return DataControllerFixture for data controller
     */
    public final DataControllerFixture openDataController(final int xLocation,
        final int yLocation) {
        clickMenuItemWithPath("Controller", "Data Viewer Controller");

        DialogFixture dcf = WindowFinder.findDialog(DataControllerV.class)
            .withTimeout(DIALOG_TIMEOUT).using(robot);
        dcf.moveTo(new Point(xLocation, yLocation));

        return new DataControllerFixture(robot,
                (DataControllerV) dcf.component());
    }

    /**
    * Open and return the data controller.
    * @return VocabEditorDialogFixture for vocab editor
    */
    public final VocabEditorDialogFixture openVocabEditor() {
        clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture ve = WindowFinder.findDialog(VocabEditorV.class)
            .withTimeout(DIALOG_TIMEOUT).using(robot);
        ve.moveTo(new Point(100, 100));

        return new VocabEditorDialogFixture(robot,
                (VocabEditorV) ve.component());
    }

    /**
     * Open and return the variable list dialog.
     * @return VariableListDialogFixture for the variable list dialog
     */
    public final VariableListDialogFixture openVariableList() {
        clickMenuItemWithPath("Spreadsheet", "Variable List");

        DialogFixture vl = WindowFinder.findDialog(VariableListV.class)
            .withTimeout(DIALOG_TIMEOUT).using(robot);
        vl.moveTo(new Point(300, 100));

        return new VariableListDialogFixture(robot,
                (VariableListV) vl.component());
    }
}
