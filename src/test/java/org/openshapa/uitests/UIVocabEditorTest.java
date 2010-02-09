package org.openshapa.uitests;



import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.fixture.VocabEditorDialogFixture;
import org.openshapa.util.UIUtils;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test vocab editor window.
 *
 */
public class UIVocabEditorTest extends OpenSHAPATestClass {
     /**
      * Nominal test input.
      */
     private String[] nominalTestInput = {"Subject stands )up ", "$10,432",
            "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
            "If x>7 then x|2"};

     /**
      * Nominal test output.
      */
     private String[] expectedNominalTestOutput = {"Subject stands up",
            "$10432", "Hand me the manual!", "Tote_that_bale",
            "Jeune fille celebre", "If x7 then x2"};

    /**
     * Test vocab editor is being populated.
     */
    //@Test
    public void testLoading() {
        System.err.println("testLoading");
        //1. Check that vocab editor is empty
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        //Close vocab editor window - BugzID641
        veDialog.close();

        //2. Run script to populate
        String root = System.getProperty("testPath");
        File demoFile = new File(root + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();

        JFileChooserFixture jfcf = mainFrameFixture.fileChooser();
        jfcf.selectFile(demoFile).approve();

        //Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        //3. Check that vocab editor is populated
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 2);        
    }

    /**
     * Test vocab editor creating new predicate.
     */
    //@Test
    public void testNewPredicateNoEdit() {
        System.err.println("testNewPredicateNoEdit");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String veName = veDialog.allVocabElements().elementAt(0).getVEName();
        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(), veName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and replacing VE name.
     */
    //@Test
    public void testNewPredicateReplaceVEName() {
        System.err.println("testNewPredicateReplaceVEName");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String oldVEName = veDialog.allVocabElements().elementAt(0).getVEName();

        String newVEName = "newName";
        try {
            veDialog.vocabElement(oldVEName).select(0, oldVEName.length());
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        veDialog.vocabElement(oldVEName).value().enterText(newVEName);
        Assert.assertFalse(oldVEName.equals(newVEName));

        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(newVEName);
        Assert.assertEquals(cell.cellValue().text(), newVEName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE name.
     */
    //@Test
    public void testNewPredicateAddingVEName() {
        System.err.println("testNewPredicateAddingVEName");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String oldVEName = veDialog.allVocabElements().elementAt(0).getVEName();

        String addVEName = "newName";
        veDialog.vocabElement(oldVEName).value().enterText(addVEName);
        String newVEName = veDialog.allVocabElements().elementAt(0).getVEName();
        Assert.assertTrue((addVEName + oldVEName).equals(newVEName));

        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(newVEName);
        Assert.assertEquals(cell.cellValue().text(), newVEName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE argument.
     */
    @Test
    public void testNewPredicateAddingVEArgument() {
        System.err.println("testNewPredicateAddingVEArgument");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String oldVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        String veName = "predicate1";

        String addVEName = "newName";
        int argPos = veDialog.vocabElement(veName).getArgStartIndex(0);
        try {
            veDialog.vocabElement(veName).value().focus();
            veDialog.vocabElement(veName).clickToCharPos(argPos, 1);
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertNull(veDialog.vocabElement(veName).value().component().getSelectedText());
        veDialog.vocabElement(veName).value().enterText(addVEName);
        String newVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        Assert.assertTrue((addVEName + oldVEArgName).equals(newVEArgName));

        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(), veName + "(<" + newVEArgName+ ">)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE argument.
     */
    @Test
    public void testNewPredicateReplaceVEArgument() {
        System.err.println("testNewPredicateReplaceVEArgument");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String oldVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        String veName = "predicate1";

        String replaceVEName = "newName";
        int argPos = veDialog.vocabElement(veName).getArgStartIndex(0);
        try {
            veDialog.vocabElement(veName).value().focus();
            veDialog.vocabElement(veName).clickToCharPos(argPos, 2);
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals(veDialog.vocabElement(veName).value().component().getSelectedText(), oldVEArgName);
        veDialog.vocabElement(veName).value().enterText(replaceVEName);
        String newVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        Assert.assertTrue(replaceVEName.equals(newVEArgName));

        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(), veName + "(<" + newVEArgName+ ">)");
    }

    /**
     * Test vocab editor reverting with multiple changes.
     */
    @Test
    public void testRevertButton1() {
        System.err.println("testRevertButton1");
        //1. Create new predicate
        mainFrameFixture.menuItemWithPath("Spreadsheet", "Vocab Editor").click();
        VocabEditorDialogFixture veDialog = new VocabEditorDialogFixture(mainFrameFixture.robot, (VocabEditorV)mainFrameFixture.dialog().component());
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();
        String oldVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        String veName = "predicate1";

        String replaceVEName = "newName";
        int argPos = veDialog.vocabElement(veName).getArgStartIndex(0);
        try {
            veDialog.vocabElement(veName).value().focus();
            veDialog.vocabElement(veName).clickToCharPos(argPos, 2);
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals(veDialog.vocabElement(veName).value().component().getSelectedText(), oldVEArgName);
        veDialog.vocabElement(veName).value().enterText(replaceVEName);
        String newVEArgName = veDialog.allVocabElements().elementAt(0).getArgument(0);
        Assert.assertTrue(replaceVEName.equals(newVEArgName));

        veDialog.okButton().click();

        //2. Create new predicate variable and cell
        String varName = "predicate";
        SpreadsheetPanelFixture ssPanel = new SpreadsheetPanelFixture(mainFrameFixture.robot, (SpreadsheetPanel)UIUtils.getSpreadsheet(mainFrameFixture).component());
        UIUtils.createNewVariable(mainFrameFixture, varName, varName + "TypeButton");

        ssPanel.column(varName).header().click();
        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
        SpreadsheetCellFixture cell = ssPanel.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(), veName + "(<" + newVEArgName+ ">)");
    }
}
