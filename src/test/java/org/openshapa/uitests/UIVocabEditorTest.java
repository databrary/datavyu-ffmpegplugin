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
package org.openshapa.uitests;

import java.awt.event.KeyEvent;

import java.io.File;
import java.util.List;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fest.swing.core.GenericTypeMatcher;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.VocabEditorDialogFixture;
import org.fest.swing.fixture.VocabElementFixture;

import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test vocab editor window.
 */
public final class UIVocabEditorTest extends OpenSHAPATestClass {

    /**
     * Nominal test input.
     */
    private String[] nominalTestInput = {
            "Subject stands )up ", "$10,432", "Hand me (the manual!",
            "Tote_that_bale", "Jeune; fille celebre", "If x>7 then x|2"
        };

    /**
     * Nominal test output.
     */
    private String[] expectedNominalTestOutput = {
            "Subject stands up", "$10432", "Hand me the manual!",
            "Tote_that_bale", "Jeune fille celebre", "If x7 then x2"
        };

    /**
     * Test vocab editor is being populated.
     */
    @Test public void testLoading() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Check that vocab editor is empty
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        // Close vocab editor window - BugzID641
        veDialog.close();

        // 2. Run script to populate

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 3. Check that vocab editor is populated
        veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 2);
    }

    /**
     * Test vocab editor creating new predicate.
     */
    @Test public void testNewPredicateNoEdit() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        String veName = veDialog.allVocabElements().get(0).getVEName();
        veDialog.okButton().click();

        // 2. Create new predicate variable and cell
        String varName = "p";
        String varType = "predicate";
        spreadsheet = mainFrameFixture.getSpreadsheet();
        mainFrameFixture.createNewVariable(varName, varType);

        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(), veName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and replacing VE name.
     */
    @Test public void testNewPredicateReplaceVEName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        String oldVEName = veDialog.allVocabElements().get(0).getVEName();

        String newVEName = "newName";

        try {
            veDialog.vocabElement(oldVEName).select(0, oldVEName.length());
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        veDialog.vocabElement(oldVEName).value().enterText(newVEName);
        Assert.assertFalse(oldVEName.equals(newVEName));

        veDialog.okButton().click();

        // 2. Create new predicate variable and cell
        String varName = "p";
        String varType = "predicate";
        spreadsheet = mainFrameFixture.getSpreadsheet();
        mainFrameFixture.createNewVariable(varName, varType);

        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(newVEName);
        Assert.assertEquals(cell.cellValue().text(), newVEName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE name.
     */
    @Test public void testNewPredicateAddingVEName() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        String oldVEName = veDialog.allVocabElements().get(0).getVEName();

        String addVEName = "newName";
        veDialog.vocabElement(oldVEName).value().enterText(addVEName);

        String newVEName = veDialog.allVocabElements().get(0).getVEName();
        Assert.assertTrue((addVEName + oldVEName).equals(newVEName));

        veDialog.okButton().click();

        // 2. Create new predicate variable and cell
        String varName = "p";
        String varType = "predicate";
        spreadsheet = mainFrameFixture.getSpreadsheet();
        mainFrameFixture.createNewVariable(varName, varType);

        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(newVEName);
        Assert.assertEquals(cell.cellValue().text(), newVEName + "(<arg0>)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE argument.
     */
    @Test public void testNewPredicateAddingVEArgument() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        String oldVEArgName = veDialog.allVocabElements().get(0).getArgument(0);
        String veName = "predicate1";

        String addVEName = "newName";
        int argPos = veDialog.vocabElement(veName).getArgStartIndex(0);

        try {
            veDialog.vocabElement(veName).value().focus();
            veDialog.vocabElement(veName).clickToCharPos(argPos, 1);
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        Assert.assertNull(veDialog.vocabElement(veName).value().component()
            .getSelectedText());
        veDialog.vocabElement(veName).value().enterText(addVEName);

        String newVEArgName = veDialog.allVocabElements().get(0).getArgument(0);
        Assert.assertTrue((addVEName + oldVEArgName).equals(newVEArgName));

        veDialog.okButton().click();

        // 2. Create new predicate variable and cell
        String varName = "p";
        String varType = "predicate";
        spreadsheet = mainFrameFixture.getSpreadsheet();
        mainFrameFixture.createNewVariable(varName, varType);

        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(),
            veName + "(<" + newVEArgName + ">)");
    }

    /**
     * Test vocab editor creating new predicate and adding to VE argument.
     */
    @Test public void testNewPredicateReplaceVEArgument() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        String oldVEArgName = veDialog.allVocabElements().get(0).getArgument(0);
        String veName = "predicate1";

        String replaceVEName = "newName";
        int argPos = veDialog.vocabElement(veName).getArgStartIndex(0);

        try {
            veDialog.vocabElement(veName).value().focus();
            veDialog.vocabElement(veName).clickToCharPos(argPos, 2);
        } catch (BadLocationException ex) {
            Logger.getLogger(UIVocabEditorTest.class.getName()).log(
                Level.SEVERE, null, ex);
        }

        Assert.assertEquals(veDialog.vocabElement(veName).value().component()
            .getSelectedText(), oldVEArgName);
        veDialog.vocabElement(veName).value().enterText(replaceVEName);

        String newVEArgName = veDialog.allVocabElements().get(0).getArgument(0);
        Assert.assertTrue(replaceVEName.equals(newVEArgName));

        veDialog.okButton().click();

        // 2. Create new predicate variable and cell
        String varName = "p";
        String varType = "predicate";
        spreadsheet = mainFrameFixture.getSpreadsheet();
        mainFrameFixture.createNewVariable(varName, varType);

        spreadsheet.column(varName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(1);
        cell.cellValue().enterText(veName);
        Assert.assertEquals(cell.cellValue().text(),
            veName + "(<" + newVEArgName + ">)");
    }

    /**
     * Test vocab editor reverting with multiple changes.
     *
     * @throws BadLocationException
     *             if point not valid
     */
    @Test public void testRevertButton1() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Test input
        String[] testInputArray = {
                "Subject stands )up ", "$10,432", "Hand me (the manual!",
                "Tote_that_bale", "Jeune; fille celebre", "If x>7 then x|2"
            };

        // 1. Run script to populate

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get current data
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        List<VocabElementFixture> vve = veDialog.allVocabElements();
        int numElements = vve.size();
        String[] originalData = new String[numElements];

        for (int i = 0; i < numElements; i++) {
            originalData[i] = vve.get(i).value().text();
        }

        // TEST 1: Make multiple addition changes
        for (VocabElementFixture ve : vve) {
            ve.value().enterText(testInputArray[4]);
            ve.enterTextInArg(0, testInputArray[0]);
            ve.enterTextInArg(1, testInputArray[1]);
            ve.enterTextInArg(2, testInputArray[2]);
            ve.enterTextInArg(3, testInputArray[3]);
        }

        // Check that changes occurred
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertFalse(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // Revert change
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertTrue(vve.get(i).value().text().equals(originalData[i]));
        }
    }

    /**
     * Test vocab editor reverting with single addition changes.
     *
     * @throws BadLocationException
     *             if point not found
     */
    @Test public void testRevertButton2() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Test input
        String[] testInputArray = {
                "Subject stands )up ", "$10,432", "Hand me (the manual!",
                "Tote_that_bale", "Jeune; fille celebre", "If x>7 then x|2"
            };

        // 1. Run script to populate

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get current data
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        List<VocabElementFixture> vve = veDialog.allVocabElements();
        int numElements = vve.size();
        String[] originalData = new String[numElements];

        for (int i = 0; i < numElements; i++) {
            originalData[i] = vve.get(i).value().text();
        }

        // TEST 2a: Make single addition change to vocab name
        for (VocabElementFixture ve : vve) {
            ve.value().enterText(testInputArray[4]);
        }

        // Check that changes occurred
        for (int i = 0; i < numElements; i++) {
            Assert.assertFalse(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // Revert change
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertTrue(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // TEST 2b. Make single addition change to vocab argument
        for (int i = 0; i < 4; i++) {

            for (VocabElementFixture ve : vve) {
                ve.value().focus();
                ve.enterTextInArg(i, testInputArray[i]);
            }

            // Check that changes occurred
            for (int j = 0; j < numElements; j++) {
                Assert.assertFalse(vve.get(j).value().text().equals(
                        originalData[j]));
            }

            // Revert changes
            veDialog.revertButton().click();

            // Check that changes have been reverted
            for (int j = 0; j < vve.size(); j++) {
                Assert.assertTrue(vve.get(j).value().text().equals(
                        originalData[j]));
            }
        }

        // TEST 3a: Make single replacement change to vocab name
        for (VocabElementFixture ve : vve) {

            try {
                ve.select(0, ve.getVEName().length());
            } catch (BadLocationException ex) {
                Logger.getLogger(UIVocabEditorTest.class.getName()).log(
                    Level.SEVERE, null, ex);
            }

            ve.value().enterText(testInputArray[4]);
        }

        // Check that changes occurred
        for (int i = 0; i < numElements; i++) {
            Assert.assertFalse(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // Revert change
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertTrue(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // TEST 3b. Make single replacement change to vocab argument
        for (int i = 0; i < 4; i++) {

            for (VocabElementFixture ve : vve) {
                ve.value().focus();
                ve.replaceTextInArg(i, testInputArray[i]);
            }

            // Check that changes occurred
            for (int j = 0; j < numElements; j++) {
                Assert.assertFalse(vve.get(j).value().text().equals(
                        originalData[j]));
            }

            // Revert changes
            veDialog.revertButton().click();

            // Check that changes have been reverted
            for (int j = 0; j < vve.size(); j++) {
                Assert.assertTrue(vve.get(j).value().text().equals(
                        originalData[j]));
            }
        }
    }

    /**
     * Test vocab editor reverting with standard deleting.
     *
     * @throws BadLocationException
     *             if point not found
     */
    @Test public void testRevertButton3a() throws BadLocationException {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // Test input
        String[] testInputArray = {
                "Subject stands )up ", "$10,432", "Hand me (the manual!",
                "Tote_that_bale", "Jeune; fille celebre", "If x>7 then x|2"
            };

        // 1. Run script to populate

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get current data
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        List<VocabElementFixture> vve = veDialog.allVocabElements();
        int numElements = vve.size();
        String[] originalData = new String[numElements];

        for (int i = 0; i < numElements; i++) {
            originalData[i] = vve.get(i).value().text();
        }

        // TEST 3a: Delete: delete key all ve name
        for (VocabElementFixture ve : vve) {
            int nameLength = ve.getVEName().length();
            ve.value().focus();
            ve.clickToCharPos(0, 1);

            for (int i = 0; i < nameLength; i++) {
                ve.value().pressAndReleaseKey(KeyPressInfo.keyCode(
                        KeyEvent.VK_DELETE));
            }
        }

        // Check that changes occurred
        for (int i = 0; i < numElements; i++) {
            Assert.assertFalse(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // Revert change
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertTrue(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // TEST 3b. delete key all ve argument
        for (int i = 0; i < 4; i++) {

            for (VocabElementFixture ve : vve) {
                ve.value().focus();
                ve.clickToCharPos(ve.getArgStartIndex(i), 1);

                int argLength = ve.getArgument(i).length();

                for (int j = 0; j < argLength; j++) {
                    ve.value().pressAndReleaseKey(KeyPressInfo.keyCode(
                            KeyEvent.VK_DELETE));
                }
            }
        }

        // Check that changes occurred
        for (int j = 0; j < numElements; j++) {
            Assert.assertFalse(vve.get(j).value().text().equals(
                    originalData[j]));
        }

        // Revert changes
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int j = 0; j < vve.size(); j++) {
            Assert.assertTrue(vve.get(j).value().text().equals(
                    originalData[j]));
        }

        // TEST 4a: Backspace all vocab name
        for (VocabElementFixture ve : vve) {
            int nameLength = ve.getVEName().length();
            ve.value().focus();
            ve.clickToCharPos(nameLength, 1);

            for (int i = 0; i < nameLength; i++) {
                ve.value().pressAndReleaseKey(KeyPressInfo.keyCode(
                        KeyEvent.VK_BACK_SPACE));
            }
        }

        // Check that changes occurred
        for (int i = 0; i < numElements; i++) {
            Assert.assertFalse(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // Revert change
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int i = 0; i < vve.size(); i++) {
            Assert.assertTrue(vve.get(i).value().text().equals(
                    originalData[i]));
        }

        // TEST 4b. Backspace all ve argument
        for (int i = 0; i < 4; i++) {

            for (VocabElementFixture ve : vve) {
                ve.value().focus();

                int argLength = ve.getArgument(i).length();
                ve.clickToCharPos(ve.getArgStartIndex(i) + argLength, 1);

                for (int j = 0; j < argLength; j++) {
                    ve.value().pressAndReleaseKey(KeyPressInfo.keyCode(
                            KeyEvent.VK_BACK_SPACE));
                }
            }
        }

        // Check that changes occurred
        for (int j = 0; j < numElements; j++) {
            Assert.assertFalse(vve.get(j).value().text().equals(
                    originalData[j]));
        }

        // Revert changes
        veDialog.revertButton().click();

        // Check that changes have been reverted
        for (int j = 0; j < vve.size(); j++) {
            Assert.assertTrue(vve.get(j).value().text().equals(
                    originalData[j]));
        }
    }

    /**
     * Test vocab editor reverting with select all deleting.
     *
     * @throws java.lang.Exception
     *             on any error FIX BUGZID:636 & change test to FEST
     */
    // public void testRevertButton3b() throws Exception {
    // //Preparation
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    //
    // //Test input
    // String[] testInputArray = {"Subject stands )up ", "$10,432",
    // "Hand me (the manual!", "Tote_that_bale", "Jeune; fille celebre",
    // "If x>7 then x|2"};
    //
    // Vector<Vector<TextItem>> testInput = new Vector<Vector<TextItem>>();
    // for (int i = 0; i < testInputArray.length; i++) {
    // testInput.add(new Vector<TextItem>());
    // testInput.lastElement().add(new StringItem(testInputArray[i]));
    // }
    //
    // Vector<TextItem> returnHome = new Vector<TextItem>();
    // returnHome.add(new KeyItem(Key.HOME));
    //
    // Vector<TextItem> backSpace = new Vector<TextItem>();
    // backSpace.add(new KeyItem(Key.BACKSPACE));
    //
    // Vector<TextItem> deleteKey = new Vector<TextItem>();
    // deleteKey.add(new KeyItem(Key.DELETE));
    //
    // Vector<TextItem> rightKey = new Vector<TextItem>();
    // rightKey.add(new KeyItem(Key.RIGHT));
    //
    // // 1. Run script to populate
    // 
    // File demoFile = new File(testFolder + "/ui/demo_data.rb");
    // assertTrue(demoFile.exists());
    //
    // WindowInterceptor
    // .init(menuBar.getMenu("Script").getSubMenu("Run script")
    // .triggerClick())
    // .process(FileChooserHandler.init()
    // .assertIsOpenDialog()
    // .assertAcceptsFilesOnly()
    // .select(demoFile.getAbsolutePath()))
    // .process(new WindowHandler() {
    // public Trigger process(Window console) {
    // return console.getButton("Close").triggerClick();
    // }
    // })
    // .run();
    //
    //
    // // 2. Get current data.
    // Window vocEdWindow = WindowInterceptor.run(menuBar.getMenu(
    // "Spreadsheet").getSubMenu("Vocab Editor").triggerClick());
    //
    // Panel vocElementsPanel = vocEdWindow.getPanel("currentVocabList")
    // .getPanel("verticalFrame");
    // int numVocElements = vocElementsPanel.getUIComponents(
    // VocabElement.class).length;
    //
    // String [] originalData = new String[numVocElements];
    // Vector<VocabElement> vve = new Vector<VocabElement>(Arrays.asList(
    // UIUtils.getVocabElements(vocElementsPanel)));
    // for (int i = 0; i < numVocElements; i++) {
    // originalData[i] = vve.elementAt(i).getValueText();
    // }
    //
    // /*BugzID:636// TEST 3c: Delete: select all delete key
    // for (VocabElement ve : vve) {
    // ve.replaceTextInName(deleteKey);
    // }
    // // Check that change occurred
    // for (int i = 0; i < vve.size(); i ++) {
    // assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // // Revert change
    // vocEdWindow.getButton("Revert").click();
    // // Check that change has been reverted
    // for (int i = 0; i < vve.size(); i ++) {
    // assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    //
    // for (int j = 0; j < 4; j++) {
    // for (VocabElement ve : vve) {
    // ve.replaceTextInArg(j, deleteKey);
    // }
    // // Check that change occurred
    // for (int i = 0; i < vve.size(); i++) {
    // assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // // Revert change
    // vocEdWindow.getButton("Revert").click();
    // // Check that change has been reverted
    // for (int i = 0; i < vve.size(); i++) {
    // assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // }
    //
    // // TEST 3d: Delete: backspace all
    // for (VocabElement ve : vve) {
    // ve.replaceTextInName(backSpace);
    // }
    // // Check that change occurred
    // for (int i = 0; i < vve.size(); i ++) {
    // assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // // Revert change
    // vocEdWindow.getButton("Revert").click();
    // // Check that change has been reverted
    // for (int i = 0; i < vve.size(); i ++) {
    // assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    //
    // for (int j = 0; j < 4; j++) {
    // for (VocabElement ve : vve) {
    // ve.replaceTextInArg(j, backSpace);
    // }
    // // Check that change occurred
    // for (int i = 0; i < vve.size(); i++) {
    // assertFalse(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // // Revert change
    // vocEdWindow.getButton("Revert").click();
    // // Check that change has been reverted
    // for (int i = 0; i < vve.size(); i++) {
    // assertTrue(vve.elementAt(i).getValueText().equals(originalData[i]));
    // }
    // }

    /**
     * Test vocab editor creating new predicate and reverting w/o script.
     */
    @Test public void testAddNewPredicateAndRevert1() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addPredicateButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == 1);

        // Revert
        veDialog.revertButton().click();

        // Check that ve has been removed
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);
    }

    /**
     * Test vocab editor creating new predicate and reverting w/ script.
     */
    @Test public void testAddNewPredicateAndRevert2() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new variables using script

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get number of elements
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        int origNumVEs = veDialog.numOfVocabElements();
        Assert.assertTrue(origNumVEs > 0);

        veDialog.addPredicateButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == (origNumVEs + 1));

        // Revert
        veDialog.revertButton().click();

        // Check that ve has been removed
        Assert.assertTrue(veDialog.numOfVocabElements() == origNumVEs);
    }

    /**
     * Test vocab editor creating new predicate and reverting w/o script.
     */
    @Test public void testAddNewMatrixAndRevert1() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new predicate
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);

        veDialog.addMatrixButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == 1);

        // Revert
        veDialog.revertButton().click();

        // Check that ve has been removed
        Assert.assertTrue(veDialog.numOfVocabElements() == 0);
    }

    /**
     * Test vocab editor creating new predicate and reverting w/ script.
     */
    @Test public void testAddNewMatrixAndRevert2() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new variables using script

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get number of elements
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        int origNumVEs = veDialog.numOfVocabElements();
        Assert.assertTrue(origNumVEs > 0);

        veDialog.addMatrixButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == (origNumVEs + 1));

        // Revert
        veDialog.revertButton().click();

        // Check that ve has been removed
        Assert.assertTrue(veDialog.numOfVocabElements() == origNumVEs);
    }

    /**
     * Test adding a new matrix after script via vocab editor.
     */
    @Test public void testAddNewMatrix() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new variables using script

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get number of elements
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        int origNumVEs = veDialog.numOfVocabElements();
        Assert.assertTrue(origNumVEs > 0);

        veDialog.addMatrixButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == (origNumVEs + 1));

        List<VocabElementFixture> vef = veDialog.allVocabElements();
        String matrixName = vef.get(vef.size() - 1).getVEName();

        // Click Apply
        veDialog.applyButton().click();

        // Check that new matrix has been created
        spreadsheet = mainFrameFixture.getSpreadsheet();

        SpreadsheetColumnFixture matrixCol = spreadsheet.column(matrixName);
        Assert.assertNotNull(matrixCol);
        matrixCol.click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");

        SpreadsheetCellFixture cell = matrixCol.cell(1);
        String argName = vef.get(vef.size() - 1).getArgument(0);
        Assert.assertEquals(cell.cellValue().text(), "<" + argName + ">");
    }

    /**
     * Open VocEd and create a new matrix. Click OK.
     * Delete matrix column from spreadsheet.
     * Reopen VocEd and create a new matrix. Click Apply.
     * Expect: New martix created
     * Actual: "ve name in use" warning
     * Waiting on Bugz ID 903
     */
    /*BugzID:903@Test*/ public void testBug695WithVocabEditorOpen() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        // 1. Create new variables using script

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        // 2. Get number of elements
        VocabEditorDialogFixture veDialog = mainFrameFixture.openVocabEditor();
        int origNumVEs = veDialog.numOfVocabElements();
        Assert.assertTrue(origNumVEs > 0);

        veDialog.addMatrixButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == (origNumVEs + 1));

        List<VocabElementFixture> vef = veDialog.allVocabElements();
        String matrixName = vef.get(vef.size() - 1).getVEName();

        // Click Apply
        veDialog.applyButton().click();

        // Check that new matrix has been created
        spreadsheet = mainFrameFixture.getSpreadsheet();

        SpreadsheetColumnFixture matrixCol = spreadsheet.column(matrixName);
        Assert.assertNotNull(matrixCol);

        // Matrix column
        matrixCol.click();
        mainFrameFixture.menuItemWithPath("Spreadsheet").click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "Delete Variable");

        // Confirm deleted
        Assert.assertNull(spreadsheet.column(matrixName));

        // Create again VocabEditor
        int numVEs = veDialog.numOfVocabElements();
        Assert.assertTrue(numVEs > 0);

        veDialog.addMatrixButton().click();

        // Check that VE exists
        Assert.assertTrue(veDialog.numOfVocabElements() == (numVEs + 1));

        Assert.assertEquals(vef.get(vef.size() - 1).getVEName(), matrixName);

        // Click Apply
        veDialog.applyButton().click();

        // Check that new matrix has been created
        spreadsheet = mainFrameFixture.getSpreadsheet();

        SpreadsheetColumnFixture recreatedMatrixCol = spreadsheet.column(matrixName);
        Assert.assertNotNull(recreatedMatrixCol);
    }

    /**
     * Test Bug 695.
     */
    @Test public void testBug695WithClosingVocabEditor() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        final String matrixName = "matrix1";

        // 1. Add matrix with vocab editor
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addMatrixButton").click();

        // 2. Confirm matrix exists in vocab editor and spreadsheet
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("matrix1");
                }
            });

        vocabEditor.button("okButton").click();

        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(matrixName));

        // 3. Delete matrix column in spreadsheet
        spreadsheet.column(matrixName).click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet",
            "Delete Variable");

        // 4. Confirm matrix deleted in vocab editor and spreadsheet
        boolean doesNotExist = false;

        try {
            Assert.assertFalse(spreadsheet.panel("headerView").label().text()
                .startsWith(matrixName));
        } catch (Exception e) {
            doesNotExist = true;
        }

        Assert.assertTrue(doesNotExist);

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();
        doesNotExist = false;

        try {
            vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                    JTextComponent.class) {
                    @Override protected boolean isMatching(
                        final JTextComponent vocEl) {
                        return vocEl.getText().startsWith(matrixName);
                    }
                });
        } catch (Exception e) {
            doesNotExist = true;
        }

        Assert.assertTrue(doesNotExist);

        // 5. Add matrix again with vocab editor
        vocabEditor.button("addMatrixButton").click();

        // 6. Confirm matrix in spreadsheet and vocab editorwill fail
        // if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith(matrixName);
                }
            });
        vocabEditor.button("okButton").click();

        spreadsheet = mainFrameFixture.getSpreadsheet();
        Assert.assertNotNull(spreadsheet.column(matrixName));
    }
}
