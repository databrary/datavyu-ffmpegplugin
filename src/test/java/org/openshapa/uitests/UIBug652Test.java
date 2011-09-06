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

import java.io.File;

import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;


import org.testng.Assert;

import org.testng.annotations.Test;


/**
 * Test for Bug 652. Bug 652: Everytime VocEditor window is reopened, the
 * predicate counter goes back to 1
 */
public final class UIBug652Test extends OpenSHAPATestClass {

    /**
     * Test for closing window and creating new predicate.
     */
    @Test public void testNewPredicateAfterClose() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate1");
                }
            });

        vocabEditor.button("okButton").click();

        // Create a new predicate and make sure the number has incremented
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate1");
                }
            });

        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate2");
                }
            });

        vocabEditor.button("okButton").click();
    }

    /**
     * Test for running script, then creating predicates.
     */
    @Test public void testNewPredicateAfterScript() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());

        File demoFile = new File(testFolder + "/ui/demo_data.rb");
        Assert.assertTrue(demoFile.exists());

        // 1. Run script to populate
        mainFrameFixture.runScript(demoFile);

        // Close script console
        mainFrameFixture.closeScriptConsoleOnFinish();

        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        DialogFixture vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate2");
                }
            });

        vocabEditor.button("okButton").click();

        // Create a new predicate and make sure the number has incremented
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "Vocab Editor");

        vocabEditor = mainFrameFixture.dialog();

        vocabEditor.button("addPredicateButton").click();

        // will fail if can not find
        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate2");
                }
            });

        vocabEditor.textBox(new GenericTypeMatcher<JTextComponent>(
                JTextComponent.class) {
                @Override protected boolean isMatching(
                    final JTextComponent vocEl) {
                    return vocEl.getText().startsWith("predicate3");
                }
            });

        vocabEditor.button("okButton").click();
    }
}
