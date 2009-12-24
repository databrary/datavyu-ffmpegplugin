package org.openshapa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;
import org.uispec4j.MenuBar;
import org.uispec4j.Panel;
import org.uispec4j.VocabElement;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;

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

        /**
     * Create a new variable.
     *
     * @param window MainWindow of OpenSHAPA
     * @param varName String for the name of the variable
     * @param varType String for the variable type
     * @param varRadio String for the corresponding radio button to click
     *
     * @throws java.lang.Exception on any error
     */
    public static void createNewVariable(final Window mainWindow,
            final String varName, final String varRadio)
            throws Exception {
        MenuBar menuBar = mainWindow.getMenuBar();
        // 2a. Create new variable,
        //open spreadsheet and check that it's there
        Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
                "Spreadsheet").getSubMenu("New Variable").triggerClick());
        newVarWindow.getTextBox("nameField").insertText(varName, 0);
        newVarWindow.getRadioButton(varRadio).click();
        newVarWindow.getButton("Ok").click();
    }


     /**
     * returns array of VocabElements from a Panel.
     * @param panel Panel with vocabElements
     * @return array of VocabElements
     */
    public static VocabElement[] getVocabElements(final Panel panel) {

        int numOfElements = panel.getUIComponents(VocabElement.class).length;

        VocabElement [] veArray = new VocabElement[numOfElements];

        for (int i = 0; i < numOfElements; i++) {
            veArray[i] = new VocabElement((VocabElementV) (panel.
                    getUIComponents(VocabElement.class)[i].getAwtComponent()));
        }
        return veArray;
    }

    /**
      * Parses a matrix value and returns an arg.
      * @param matrixCellValue matrix cell value
      * @param arg argument number
      * @return argument as a string
      */
     public static String getArgFromMatrix(final String matrixCellValue,
             final int arg) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 2);

         String [] tokens = argList.split(", ");

         return tokens[arg];
     }

          /**
      * Parses a matrix value and returns an arg.
      * @param matrixCellValue matrix cell value
      * @return int number of arguments
      */
     public static int getNumberofArgFromMatrix(final String matrixCellValue) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 1);

         String [] tokens = argList.split(", ");

         return tokens.length;
     }

     /**
      * Parses a matrix value and returns array of arguments.
      * @param matrixCellValue matrix cell value
      * @return arguments in an array
      */
     public static String [] getArgsFromMatrix(final String matrixCellValue) {
         String argList = matrixCellValue.substring(1,
                 matrixCellValue.length() - 1);

         String [] tokens = argList.split(", ");

         return tokens;
     }
}
