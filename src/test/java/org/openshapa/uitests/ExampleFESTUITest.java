package org.openshapa.uitests;

import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.views.AboutV;
import org.openshapa.views.NewVariableV;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.annotations.Test;

/**
 * Fest test for Bug 308. When an error occurs (such as a duplicate variable
 * name), the New Variable window just disappears rather than allowing the user
 * to fix the problem.
 */
public class ExampleFESTUITest extends OpenSHAPATestClass {

	@Test
	public void testAboutDialog() {
		System.err.println("Test About");

		if (Platform.isWindows()) {
			JMenuItemFixture helpMenuItem = mainFrameFixture.menuItemWithPath(
			        "Help", "About");

			helpMenuItem.click();
			// Find the about dialog
			DialogFixture aboutDialog = mainFrameFixture.dialog(
			        new GenericTypeMatcher<JDialog>(JDialog.class) {
				        @Override
				        protected boolean isMatching(JDialog dialog) {
					        return dialog.getClass().equals(AboutV.class);
				        }
			        }, Timeout.timeout(5, TimeUnit.SECONDS));
			// Check if the about dialog is actually visible
			aboutDialog.requireVisible();

			// Close the dialog, or it will be visible in other tests
			aboutDialog.close();
		}
	}

	@Test
	public void testAddNewVariable() {
		System.err.println("TestAddVar");
		JMenuItemFixture spreadsheetMenuItem = mainFrameFixture
		        .menuItemWithPath("Spreadsheet", "New Variable");
		spreadsheetMenuItem.click();
		// Find the new variable dialog
		DialogFixture newVariableDialog = mainFrameFixture.dialog(
		        new GenericTypeMatcher<JDialog>(JDialog.class) {
			        @Override
			        protected boolean isMatching(JDialog dialog) {
				        return dialog.getClass().equals(NewVariableV.class);
			        }
		        }, Timeout.timeout(5, TimeUnit.SECONDS));
		// Check if the new variable dialog is actually visible
		newVariableDialog.requireVisible();

		// Get the variable value text box
		JTextComponentFixture variableValueTextBox = newVariableDialog
		        .textBox();
		// The variable value box should have no text in it
		variableValueTextBox.requireEmpty();
		// It should be editable
		variableValueTextBox.requireEditable();
		// Type in some text.
		variableValueTextBox.enterText("FEST: Hello world!");

		// Get the radio button for text variables
		JRadioButtonFixture variableTypeRadioButton = newVariableDialog
		        .radioButton(new GenericTypeMatcher<JRadioButton>(
		                JRadioButton.class) {
			        @Override
			        protected boolean isMatching(JRadioButton radioButton) {
				        return radioButton.getName().equals("textTypeButton");
			        }
		        });
		// Check that it is selected by default
		variableTypeRadioButton.requireSelected();

		// Find the ok button
		JButtonFixture actionButton = newVariableDialog.button("okButton");
		// Add the variable to the spreadsheet.
		actionButton.click();

		// Find the spreadsheet panel
		JPanelFixture spreadsheetPanel = mainFrameFixture
		        .panel(new GenericTypeMatcher<JPanel>(JPanel.class) {
			        @Override
			        protected boolean isMatching(JPanel panel) {
				        return panel.getClass().equals(SpreadsheetPanel.class);
			        }
		        });

		// Find our new column header
		JPanelFixture headerPanel = spreadsheetPanel.panel("headerView");

		// Find the heading.
		JLabelFixture headingLabel = headerPanel
		        .label(new GenericTypeMatcher<JLabel>(JLabel.class) {
			        @Override
			        protected boolean isMatching(JLabel label) {
				        return label.getText().equals(
				                "FEST: Hello world!  (TEXT)");
			        }
		        });
		headingLabel.requireVisible();
	}
}
