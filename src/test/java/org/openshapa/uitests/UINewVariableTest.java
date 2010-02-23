package org.openshapa.uitests;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the New Variable window.
 */
public final class UINewVariableTest extends OpenSHAPATestClass {

    /**
     * Test creating a new TEXT variable.
     */
    @Test
    public void testTextVariable() {
        System.err.println("testTextVariable");
        String varName = "text var";
        String varType = "TEXT";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Test creating a new PREDICATE variable.
     */
    @Test
    public void testPredicateVariable() {
        System.err.println("testPredicateVariable");
        String varName = "pred var";
        String varType = "PREDICATE";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Test creating a new INTEGER variable.
     */
    @Test
    public void testIntegerVariable() {
        System.err.println("testIntegerVariable");
        String varName = "int var";
        String varType = "INTEGER";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Test creating a new NOMINAL variable.
     */
    @Test
    public void testNominalVariable() {
        System.err.println("testNominalVariable");
        String varName = "nom var";
        String varType = "NOMINAL";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Test creating a new MATRIX variable.
     */
    @Test
    public void testMatrixVariable() {
        System.err.println("testMatrixVariable");
        String varName = "matrix var";
        String varType = "MATRIX";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Test creating a new FLOAT variable.
     */
    @Test
    public void testFloatVariable() {
        System.err.println("testFloatVariable");
        String varName = "float var";
        String varType = "FLOAT";

        // check that column has no cells
        validateVariableType(varName, varType);
    }

    /**
     * Creates a new variable and checks that it has been created.
     * 
     * @param varName
     *            variable name
     * @param varType
     *            variable type
     */
    private void validateVariableType(final String varName, final String varType) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        // 1. Create new variable
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Variable");
        DialogFixture newVariableDialog = mainFrameFixture.dialog();
        newVariableDialog.requireVisible();
        JTextComponentFixture variableValueTextBox =
                newVariableDialog.textBox();
        variableValueTextBox.requireEmpty();
        variableValueTextBox.requireEditable();
        variableValueTextBox.enterText(varName);
        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
                .click();
        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
                .requireSelected();
        newVariableDialog.button("okButton").click();

        // 2. Check that column has been created
        SpreadsheetColumnFixture col = ssPanel.column(varName);
        Assert.assertNotNull(col);
        Assert.assertEquals(col.getColumnType(), varType);

        // 3. Check that column has no cells
        Assert.assertTrue(col.numOfCells() == 0);
    }
}
