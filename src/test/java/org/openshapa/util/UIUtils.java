package org.openshapa.util;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JPanel;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 *
 */
public class UIUtils {

    /**
     * Different cell variable types.
     */
    public static final String[] VAR_TYPES = {"TEXT", "PREDICATE", "INTEGER",
        "NOMINAL", "MATRIX", "FLOAT"
    };

    /**
     * Checks if two text files are equal.
     *
     * @param file1 First file
     * @param file2 Second file
     *
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    public static Boolean areFilesSame(final File file1, final File file2)
            throws IOException {
        FileReader fr1 = new FileReader(file1);
        FileReader fr2 = new FileReader(file2);

        BufferedReader r1 = new BufferedReader(fr1);
        BufferedReader r2 = new BufferedReader(fr2);

        String line1 = r1.readLine();
        String line2 = r2.readLine();
        if (!line1.equals(line2)) {
            return false;
        }

        while (line1 != null && line2 != null) {
            if (!line1.equals(line2)) {
                return false;
            }

            line1 = r1.readLine();
            line2 = r2.readLine();
        }

        r1.close();
        r2.close();

        fr1.close();
        fr2.close();

        return true;
    }

    public static JPanelFixture getSpreadsheet(final FrameFixture ff) {
        return ff.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {

            @Override
            protected boolean isMatching(JPanel panel) {
                return panel.getClass().equals(SpreadsheetPanel.class);
            }
        });
    }

    public static void createNewVariable(FrameFixture ff,
            String varName,
            String varRadio) {
        // 1. Create new variable
        ff.menuItemWithPath("Spreadsheet", "New Variable").click();
        DialogFixture newVariableDialog = ff.dialog();
        // Check if the new variable dialog is actually visible
        newVariableDialog.requireVisible();
        // Get the variable value text box
        JTextComponentFixture variableValueTextBox = newVariableDialog.textBox();
        // The variable value box should have no text in it
        variableValueTextBox.requireEmpty();
        // It should be editable
        variableValueTextBox.requireEditable();
        // Type in some text.
        variableValueTextBox.enterText(varName);
        // Get the radio button for text variables
        newVariableDialog.radioButton(varRadio).click();
        // Check that it is selected
        newVariableDialog.radioButton(varRadio).requireSelected();
        // Click "OK"
        newVariableDialog.button("okButton").click();
    }

    public static void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }
}
