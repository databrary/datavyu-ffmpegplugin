package org.openshapa.uitests;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;
import org.openshapa.util.KeysItem;
import org.openshapa.util.StringItem;
import org.openshapa.util.TextItem;
import org.openshapa.util.UIUtils;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the New Cells.
 */
public final class UINewCellTest extends OpenSHAPATestClass {
    /**
     * Nominal test input.
     */
    private String[] nominalTestInput =
            {"Subject stands )up ", "$10,432", "Hand me (the manual!",
                    "Tote_that_bale", "Jeune; fille celebre",
                    "If x>7 then x|2"};

    /**
     * Text test input.
     */
    private String[] textTestInput =
            {"Subject stands up ", "$10,432", "Hand me the manual!",
                    "Tote_that_bale", "Jeune fille celebre",
                    "If x?7 then x? 2" };

    /**
     * Integer test input.
     */
    private String[] integerTestInput =
            {"1a9", "10-432", "!28.9(", "178&", "~~~)", "If x?7 then x? 2 ",
                    "99999999999999999999", "000389.5", "-", "-0", "-123"};

    /**
     * Float test input.
     */
    private String[] floatTestInput =
            {"1a.9", "10-43.2", "!289(", "178.&", "0~~~)",
                    "If x?7 then. x? 2 ", "589.138085638", "000389.5", "-0.1",
                    "0.2", "-0.0", "-", "-0", "-.34", "-23.34", ".34", "12.34",
                    "-123"};

    /**
     * The size of each dimesion of the advanced mixed matrix test.
     */
    private int matrixMixedNumTests = 6;

    /**
     * Test creating a new NOMINAL cell.
     */
    @Test
    public void testNewNominalCell() {
        String varName = "n";
        String varType = "nominal";

        String[] expectedNominalTestOutput =
                {"Subject stands up", "$10432", "Hand me the manual!",
                        "Tote_that_bale", "Jeune fille celebre",
                        "If x7 then x2"};

        // 1. Create new variable
        UIUtils.createNewVariable(mainFrameFixture, varName, varType);

        runStandardTest(varName, nominalTestInput, expectedNominalTestOutput);
    }

    /**
     * Test pasting in Nominal cell.
     */
    @Test
    public void testNominalPasting() {
        String varName = "n";
        String varRadio = "nominal";

        String[] expectedNominalTestOutput =
                {"Subject stands up ", "$10432", "Hand me the manual!",
                        "Tote_that_bale", "Jeune fille celebre",
                        "If x7 then x2"};

        pasteTest(varName, varRadio, nominalTestInput,
                expectedNominalTestOutput);
    }

    /**
     * Test creating a new NOMINAL cell with more advanced input.
     * BugzID:1203
     */
    //@Test
    public void testNewAdvancedNominalCell() {
        String varName = "n";
        String varRadio = "nominal";

        // advanced Input will be provided between testInput
        int[][] advancedInput =
                {
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE } };

        String[] expectedTestOutput =
                {"Subject stands u$10432p ", "$1043Hand me the manual!2",
                        "Hand me the manuaTote_that_balel",
                        "Tote_that_aJeune fille celebrel", "If x7 then x2" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runAdvancedTest(varName, nominalTestInput, advancedInput,
                expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell.
     */
    @Test
    public void testNewTextCell() {
        String varName = "t";
        String varRadio = "text";

        String[] expectedTestOutput = textTestInput;

        // 1. Create new TEXT variable,
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runStandardTest(varName, textTestInput, expectedTestOutput);
    }

    /**
     * Test pasting in TEXT cell.
     */
    @Test
    public void testTextPasting() {
        String varName = "t";
        String varRadio = "text";

        String[] expectedTestOutput = textTestInput;
        pasteTest(varName, varRadio, textTestInput, expectedTestOutput);
    }

    /**
     * Test pasting in INTEGER cell.
     */
    @Test
    public void testIntegerPasting() {
        String varName = "i";
        String varRadio = "integer";

        String[] expectedTestOutput =
                {"19", "-43210", "289", "178", "<val>", "72",
                        "999999999999999999", "3895", "-", "0", "-123" };
        pasteTest(varName, varRadio, integerTestInput, expectedTestOutput);
    }

    /**
     * Test creating a new TEXT cell with more advanced input.
     */
    @Test
    public void testNewAdvancedTextCell() {
        String varName = "t";
        String varRadio = "text";

        // advanced Input will be provided between testInput
        int[][] advancedInput =
                {
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT } };

        String[] advancedExpectedOutput =
                {"Subject stands u$10,432p ", "$10,43Hand me the manual!2",
                        "Hand me the manuaTote_that_balel",
                        "Tote_that_aJeune fille celebrel",
                        "Jeune fille celebreIf x?7 then x? 2" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runAdvancedTest(varName, textTestInput, advancedInput,
                advancedExpectedOutput);
    }

    /**
     * Test creating a new FLOAT cell.
     * @throws java.lang.Exception
     *             on any error
     */
    @Test
    public void testNewFloatCell() throws Exception {
        String varName = "f";
        String varRadio = "float";

        String[] expectedTestOutput =
                {"1.9", "-43.21", "289", "178", "0", "7.2", "589.138085",
                        "389.5", "-0.1", "0.2", "0", "0", "0", "-0.34",
                        "-23.34", "0.34", "12.34", "-123" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runStandardTest(varName, floatTestInput, expectedTestOutput);
    }

    /**
     * Test pasting with INTEGER cell.
     */
    @Test
    public void testFloatPasting() {
        String varName = "f";
        String varRadio = "float";

        String[] expectedTestOutput =
                {"1.9", "-43.21", "289", "178", "0", "7.2", "589.138085",
                        "389.5", "-0.1", "0.2", "0", "0", "0", "-0.34",
                        "-23.34", "0.34", "12.34", "-123" };

        pasteTest(varName, varRadio, floatTestInput, expectedTestOutput);
    }

    /**
     * Test creating a new FLOAT cell with advanced input.
     * BugzID:1201
     */
    // @Test
    public void testNewAdvancedFloatCell() {
        String varName = "f";
        String varRadio = "float";

        String[] testInput =
                {"1a.9", "10-43.2", "!289(", "178.&", "0~~~)",
                        "If x?7 then.- x? 8", "-589.138085638", "12.3" };

        int[][] advancedInput =
                {
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE },
                        {KeyEvent.VK_RIGHT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT } };

        // int[][] advancedInput =
        // {
        // { KeyEvent.VK_LEFT, KeyEvent.VK_LEFT },
        // { KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT },
        // { KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT },
        // { KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT },
        // { KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
        // KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
        // KeyEvent.VK_BACK_SPACE },
        // { KeyEvent.VK_RIGHT },
        // { KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
        // KeyEvent.VK_LEFT } };

        String[] expectedTestOutput =
                {"-43.21019", "-43.289210", "2178.8", "7", "-87",
                        "589.138085", "-589.138085" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runAdvancedTest(varName, testInput, advancedInput, expectedTestOutput);
    }

    /**
     * Test creating a new INTEGER cell.
     */
    @Test
    public void testNewIntegerCell() {
        String varName = "i";
        String varRadio = "integer";

        String[] expectedTestOutput =
                {"19", "-43210", "289", "178", "<val>", "72",
                        "999999999999999999", "3895", "<val>", "0", "-123" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runStandardTest(varName, integerTestInput, expectedTestOutput);
    }

    /**
     * Test creating a new INTEGER cell with advanced input.
     * BugzID:1202
     */
    // @Test
    public void testNewAdvancedIntegerCell() {
        String varName = "i";
        String varRadio = "integer";

        String[] testInput =
                {"1a9", "10-432", "!289(", "178&", "If x?7. then x? 2",
                        "17-8&", "()12.3" };

        // advanced Input will be provided between testInput
        int[][] advancedInput =
                {
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_DELETE, KeyEvent.VK_RIGHT },
                        {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE,
                                KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE},
                        {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
                                KeyEvent.VK_LEFT } };

        String[] expectedTestOutput =
                {"-4321019", "-43289210", "21788", "772", "-817", "-817" };

        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        runAdvancedTest(varName, testInput, advancedInput, expectedTestOutput);
    }

    /**
     * Test creating a new MATRIX cell.
     */
    @Test
    public void testNewMatrixCellSingleArgNominal() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test single cell types
        // Test nominal
        String varName = "mN1";

        String[] expectedNominalTestOutput =
                {"Subject stands up", "$10432", "Hand me the manual!",
                        "Tote_that_bale", "Jeune fille celebre",
                        "If x7 then x2" };

        runStandardTest(varName, expectedNominalTestOutput,
                expectedNominalTestOutput, "<nominal>");
    }

    /**
     * Test creating a new MATRIX cell.
     * BugzID:1198
     */
    // @Test
    public void testNewMatrixCellSingleArgFloat() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test single cell types
        // Test integer
        String varName = "mF1";

        String[] expectedFloatTestOutput =
                {"1.9", "-43.21", "289", "178", "0", "7.2", "589.138085",
                        "389.5", "-0.1", "0.2", "0", "0", "0", "-0.34",
                        "-23.34", "0.34", "12.34", "-123" };

        runStandardTest(varName, floatTestInput, expectedFloatTestOutput,
                "<float>");
    }

    /**
     * Test creating a new MATRIX cell.
     * BugzID:1199
     */
    // @Test
    public void testNewMatrixCellSingleArgInteger() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test single cell types
        // Test integer
        String varName = "mI1";

        String[] expectedIntTestOutput =
                {"19", "-43210", "289", "178", "<int>", "72",
                        "999999999999999999", "3895", "<int>", "0", "-123" };

        runStandardTest(varName, integerTestInput, expectedIntTestOutput,
                "<int>");
    }

    /**
     * Test creating a new MATRIX cell.
     * BugzID:1199
     */
    // @Test
    public void testNewMatrixCellDoubleArgInteger() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test double cell type
        String varName = "mI2";

        String[] expectedInt2TestOutput =
                {"19", "-43210", "289", "178", "<int1>", "72",
                        "999999999999999999", "3895", "<int1>", "0", "-123" };

        int numOfTests = integerTestInput.length;

        // 2a. Test integer, only first arg
        for (int i = 0; i < numOfTests; i++) {
            expectedInt2TestOutput[i] =
                    "(" + expectedInt2TestOutput[i] + ", <int2>)";
        }

        runStandardTest(varName, integerTestInput, expectedInt2TestOutput,
                "(<int1>, <int2>)");

        // 2b. Recursively test all permutations of test input
        String[][][] testInput =
                new String[expectedInt2TestOutput.length]
                [expectedInt2TestOutput.length][2];

        String[] expectedInt2bTempOutput =
                {"19", "-43210", "289", "178", "<int1>", "72",
                        "999999999999999999", "3895", "<int1>", "0", "-123" };

        String[][] expectedInt2bTestOutput =
                new String[expectedInt2TestOutput.length]
                [expectedInt2TestOutput.length];

        for (int i = 0; i < numOfTests; i++) {
            for (int j = 0; j < numOfTests; j++) {
                testInput[i][j][0] = integerTestInput[i];
                testInput[i][j][1] = integerTestInput[j];
                if (expectedInt2bTempOutput[i].equals("<int2>")) {
                    expectedInt2bTestOutput[i][j] =
                            "(<int1>" + ", " + expectedInt2bTempOutput[j] + ")";
                } else if (expectedInt2bTempOutput[j].equals("<int1>")) {
                    expectedInt2bTestOutput[i][j] =
                            "(" + expectedInt2bTempOutput[i] + ", <int2>)";
                } else {
                    expectedInt2bTestOutput[i][j] =
                            "(" + expectedInt2bTempOutput[i] + ", "
                                    + expectedInt2bTempOutput[j] + ")";
                }
            }
        }

        for (int i = 0; i < numOfTests; i++) {
            runMatrixTest(varName, testInput[i], expectedInt2bTestOutput[i]);
        }
    }

    /**
     * Test creating a new MATRIX cell.
     */
    @Test
    public void testNewMatrixCellDoubleArgNominal() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test double cell type
        // 2a. Test nominal
        String varName = "mN2";

        String[] expectedTestOutput =
                {"Subject stands up", "$10432", "Hand me the manual!",
                        "Tote_that_bale", "Jeune fille celebre",
                        "If x7 then x2" };

        int numOfTests = expectedTestOutput.length;

        for (int i = 0; i < numOfTests; i++) {
            expectedTestOutput[i] =
                    "(" + expectedTestOutput[i] + ", <nominal2>)";
        }

        runStandardTest(varName, nominalTestInput, expectedTestOutput,
                "(<nominal1>, <nominal2>)");

        // 2b. Recursively test all permutations of test input
        String[][][] testInput =
                new String[nominalTestInput.length][nominalTestInput.length][2];

        String[] expectedNominal2bTempOutput =
                {"Subject stands up", "$10432", "Hand me the manual!",
                        "Tote_that_bale", "Jeune fille celebre",
                        "If x7 then x2" };

        String[][] expectedNominal2bTestOutput =
                new String[expectedNominal2bTempOutput.length]
                [expectedNominal2bTempOutput.length];

        for (int i = 0; i < numOfTests; i++) {
            for (int j = 0; j < numOfTests; j++) {
                testInput[i][j][0] = nominalTestInput[i];
                testInput[i][j][1] = nominalTestInput[j];
                if (expectedNominal2bTempOutput[i].equals("<nominal2>")) {
                    expectedNominal2bTestOutput[i][j] =
                            "(<nominal1>" + ", "
                                    + expectedNominal2bTempOutput[j] + ")";
                } else if (expectedTestOutput[j].equals("<nominal1>")) {
                    expectedNominal2bTestOutput[i][j] =
                            "(" + expectedNominal2bTempOutput[i]
                                    + ", <nominal2>)";
                } else {
                    expectedNominal2bTestOutput[i][j] =
                            "(" + expectedNominal2bTempOutput[i] + ", "
                                    + expectedNominal2bTempOutput[j] + ")";
                }
            }
        }

        numOfTests = nominalTestInput.length;
        for (int i = 0; i < numOfTests; i++) {
            runMatrixTest(varName, testInput[i],
                    expectedNominal2bTestOutput[i]);
        }
    }

    /**
     * Test creating a new MATRIX cell.
     */
    // @Test
    public void testNewMatrixCellDoubleArgFloat() {
        // 1. Create new variables using script
        String root = System.getProperty("testPath");
        final File demoFile = new File(root + "/ui/matrix_tests.rb");
        Assert.assertTrue(demoFile.exists(),
                "Expecting matrix_tests.rb to exist.");

        mainFrameFixture.menuItemWithPath("Script", "Run script").click();
        mainFrameFixture.fileChooser().selectFile(demoFile).approve();

        // Close script console
        DialogFixture scriptConsole = mainFrameFixture.dialog();
        scriptConsole.button("closeButton").click();

        // 2. Test double cell type
        String varName = "mF2";

        String[] expectedFloat2TestOutput =
                {"1.9", "-43.21", "289", "178", "0", "7.2", "589.138085",
                        "389.5", "-0.1", "0.2", "0", "0", "0", "-0.34",
                        "-23.34", "0.34", "12.34", "-123" };

        int numOfTests = floatTestInput.length;

        // 2a. Test integer, only first arg
        for (int i = 0; i < numOfTests; i++) {
            expectedFloat2TestOutput[i] =
                    "(" + expectedFloat2TestOutput[i] + ", <float2>)";
        }

        runMatrixTest(varName, floatTestInput, expectedFloat2TestOutput,
                "<float2>");

        // 2b. Recursively test all permutations of test input
        String[][][] testInput =
                new String[expectedFloat2TestOutput.length]
                [expectedFloat2TestOutput.length][2];

        String[] expectedInt2bTempOutput =
                {"1.9", "-43.21", "289", "178", "0", "7.2", "589.138085",
                        "389.5", "-0.1", "0.2", "0", "0", "0", "-0.34",
                        "-23.34", "0.34", "12.34", "-123" };

        String[][] expectedInt2bTestOutput =
                new String[expectedFloat2TestOutput.length]
                [expectedFloat2TestOutput.length];

        for (int i = 0; i < numOfTests; i++) {
            for (int j = 0; j < numOfTests; j++) {
                testInput[i][j][0] = floatTestInput[i];
                testInput[i][j][1] = floatTestInput[j];
                if (expectedInt2bTempOutput[i].equals("<float2>")) {
                    expectedInt2bTestOutput[i][j] =
                            "(<float1>" + ", " + expectedInt2bTempOutput[j]
                                    + ")";
                } else if (expectedInt2bTempOutput[j].equals("<float1>")) {
                    expectedInt2bTestOutput[i][j] =
                            "(" + expectedInt2bTempOutput[i] + ", <float2>)";
                } else {
                    expectedInt2bTestOutput[i][j] =
                            "(" + expectedInt2bTempOutput[i] + ", "
                                    + expectedInt2bTempOutput[j] + ")";
                }
            }
        }

        for (int i = 0; i < numOfTests; i++) {
            runMatrixTest(varName, testInput[i], expectedInt2bTestOutput[i]);
        }
    }

    // /**
    // * Test creating a new MATRIX cell.
    // * @throws java.lang.Exception on any error
    // */
    // public void testNewAdvancedMatrixCellMixed() throws Exception {
    //    
    // System.err.println("testNewAdvancedMatrixCellMixed " +
    // "still has commented out sections.");
    //    
    // // These are just variables and can be left uncommented.
    // String [] expectedInt2bTempOutput = {"19", "-4321", "289", "178", "0",
    // "72"};
    //    
    // String [] expectedNominal2bTempOutput = {"Subject stands up",
    // "$10432", "Hand me the manual!", "Tote_that_bale",
    // "Jeune fille celebre", "If x7 then x2"};
    //    
    // String[] expectedFloat2bTempOutput = {"1.9", "-43.210",
    // "289", "178", "0)", "7.2"};
    //    
    // String varName = "matrixMixed1";
    //    
    // // Retrieve the components
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    //    
    // Spreadsheet ss;
    //    
    // //1. Create new variables using script
    // String root = System.getProperty("testPath");
    // final File demoFile = new File(root + "/ui/matrix_tests.rb");
    // assertTrue(demoFile.exists());
    //    
    // WindowInterceptor
    // .init(menuBar.getMenu("Script").getSubMenu("Run script")
    // .triggerClick())
    // .process(FileChooserHandler.init()
    // .assertAcceptsFilesOnly()
    // .select(demoFile.getAbsolutePath()))
    // .process(new WindowHandler() {
    // public Trigger process(Window console) {
    // return console.getButton("Close").triggerClick();
    // }
    // })
    // .run();
    //    
    // // 1a. Check that database is populated
    // ss = new Spreadsheet((SpreadsheetPanel)
    // (window.getUIComponents(Spreadsheet.class)[0].getAwtComponent()));
    // assertTrue(ss.getColumns().size() > 0);
    //    
    // // BugzID:584 - "Create advanced matrix tests"
    // // **** UNFINISHED ADVANCED TEST HERE *******
    // // **** FOR A SIMPLE TEST: SCROLL DOWN ******
    // // Test with the simple test first.
    //    
    // /* Within this block, we have a large multi-dimensional matrix
    // * of tests that can eventually be uncommented and tested, after
    // * the simple test below this block is tested (and passes).
    // *
    //    
    //    
    // int numOfTests = matrixMixedNumTests;
    //    
    // String [] tmpExpect1 = new String[numOfTests];
    // // String [] tmpExpect2 = new String[numOfTests];
    // // String [] tmpExpect3 = new String[numOfTests];
    // // String [] tmpExpect4 = new String[numOfTests];
    //    
    //    
    // //2b. Recursively test all permutations of test input
    // String [][][][][] testInput = new String [numOfTests]
    // [numOfTests][numOfTests][numOfTests][4];
    //    
    //    
    //    
    //    
    // String [][][][] expectedMixedTestOutput =
    // new String [numOfTests]
    // [numOfTests]
    // [numOfTests]
    // [numOfTests];
    //    
    //    
    // for (int i = 0; i < numOfTests; i++) {
    // for (int j = 0; j < numOfTests; j++) {
    // for (int k = 0; k < numOfTests; k++) {
    // for (int l = 0; l < numOfTests; l++) {
    // testInput[i][j][k][l][0] = floatTestInput[i];
    // testInput[i][j][k][l][1] = integerTestInput[j];
    // testInput[i][j][k][l][2] = nominalTestInput[k];
    // testInput[i][j][k][l][3] = textTestInput[l];
    //    
    // {
    // expectedMixedTestOutput[i][j][k][l] = "("
    // + expectedFloat2bTempOutput[i]
    // + ", " + expectedInt2bTempOutput[j]
    // + ", " + expectedNominal2bTempOutput[k]
    // + ", " + textTestInput[l] + ")";
    // }
    // }
    // }
    // }
    // }
    //    
    // numOfTests = nominalTestInput.length;
    // for (int i = 0; i < numOfTests; i++) {
    // for (int j = 0; j < numOfTests; j++) {
    // for (int k = 0; k < numOfTests; k++) {
    // runMatrixTest(varName, testInput[i][j][k],
    // expectedMixedTestOutput[i][j][k]);
    // }
    // }
    // }
    //
    // */
    //
    // // // ******* SIMPLE TEST BELOW **********
    // //
    // // // Below this line we have a very simple test which definitely runs,
    // // // but will fail due to quote strings having the odd behaviour of only
    // // // retaining the last character of input during UI testing (although
    // // // strangely this does not occur when testing manually).
    // //
    // // String[] reducedTempTest = new String[4];
    // // reducedTempTest[0] = floatTestInput[0];
    // // reducedTempTest[1] = integerTestInput[0];
    // // reducedTempTest[2] = nominalTestInput[0];
    // // reducedTempTest[3] = "godzilla"; // textTestInput[0];
    // // // **** By testing the string "godzilla" instead of a string with a
    // // // trailing space, it can easily be seen that the current behaviour
    // // // leaves the quote string field with just the last character entered,
    // // // in this case being "a".
    // //
    // //
    // //
    // // ss.getSpreadsheetColumn(varName).requestFocus();
    // // menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
    // // Cell c = ss.getSpreadsheetColumn(varName).getCells().elementAt(0);
    // // c.enterMatrixText(reducedTempTest);
    // //
    // // TextBox t = c.getValue();
    // // System.out.println("Textbox had " + t.getText());
    // // int numOfArgs = getNumberofArgFromMatrix(t.getText());
    // // String [] actualValues = getArgsFromMatrix(t.getText());
    // // String [] expectedValues = new String[4];
    // // expectedValues[0] = expectedFloat2bTempOutput[0];
    // // expectedValues[1] = expectedInt2bTempOutput[0];
    // // expectedValues[2] = expectedNominal2bTempOutput[0];
    // // expectedValues[3] = textTestInput[0];
    // //
    // //
    // //
    // // for (int j = 0; j < numOfArgs; j++) {
    // // System.out.println("Compare " + actualValues[j] + " and " +
    // expectedValues[j]);
    // // if (j == 3 && !actualValues[j].equals(expectedValues[j])) {
    // //
    // System.err.println("Test will fail here unless quote string behaviour has been fixed.");
    // // }
    // // assertTrueEqualValues(actualValues[j], expectedValues[j]);
    // // }
    //
    //
    //
    // // Code below this line will be used later to augment the advanced test.
    //
    // // String xVarName = "textVar";
    // // String varRadio = "text";
    // //
    // // //advanced Input will be provided between testInput
    // // Key[][] advancedInput = {{KeyEvent.VK_LEFT, KeyEvent.VK_LEFT},
    // // {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
    // {KeyEvent.VK_BACK_SPACE,
    // KeyEvent.VK_LEFT},
    // // {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
    // KeyEvent.VK_LEFT,
    // KeyEvent.VK_DELETE,
    // // KeyEvent.VK_RIGHT}, {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT}};
    // //
    // // String[] advancedExpectedOutput = {"Subject stands u$10,432p ",
    // // "$10,43Hand me the manual!2", "hand me the manuaTote_that_balel",
    // // "Tote_that_aJeune fille celebrel",
    // // "Jeune fille celebreIf x?7 then x? 2"};
    // //
    // // runAdvancedTest(xVarName, textTestInput, advancedInput,
    // // advancedExpectedOutput);
    //
    // }
    //
    // /**
    // * Test creating a new cell by pressing enter instead of clicking.
    // * @throws java.lang.Exception on any error
    // */
    // public void testCreateNewCellWithEnter() throws Exception {
    // String varName = "testVar";
    // String varType = UIUtils.VAR_TYPES[(int) (Math.random()
    // * UIUtils.VAR_TYPES.length)];
    // String varRadio = varType.toLowerCase();
    //
    // // 1. Retrieve the components
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    // // 2. Create new variable,
    // Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
    // "Spreadsheet").getSubMenu("New Variable").triggerClick());
    // newVarWindow.getTextBox("nameField").insertText(varName, 0);
    // newVarWindow.getRadioButton(varRadio).click();
    // assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
    // newVarWindow.getButton("Ok").click();
    //
    // Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
    // (window.getUIComponents(Spreadsheet.class)[0]
    // .getAwtComponent()));
    //
    // //Create new cell
    // //Instead of clicking, just press "Enter"
    // /* Code to be written
    // * Must click the column title
    // * Then press enter on it
    // */
    // Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
    // // readyToExit = true;
    // }
    //

    // // // ******* SIMPLE TEST BELOW **********
    // //
    // // // Below this line we have a very simple test which definitely runs,
    // // // but will fail due to quote strings having the odd behaviour of only
    // // // retaining the last character of input during UI testing (although
    // // // strangely this does not occur when testing manually).
    // //
    // // String[] reducedTempTest = new String[4];
    // // reducedTempTest[0] = floatTestInput[0];
    // // reducedTempTest[1] = integerTestInput[0];
    // // reducedTempTest[2] = nominalTestInput[0];
    // // reducedTempTest[3] = "godzilla"; // textTestInput[0];
    // // // **** By testing the string "godzilla" instead of a string with a
    // // // trailing space, it can easily be seen that the current behaviour
    // // // leaves the quote string field with just the last character entered,
    // // // in this case being "a".
    // //
    // //
    // //
    // // ss.getSpreadsheetColumn(varName).requestFocus();
    // // menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
    // // Cell c = ss.getSpreadsheetColumn(varName).getCells().elementAt(0);
    // // c.enterMatrixText(reducedTempTest);
    // //
    // // TextBox t = c.getValue();
    // // System.out.println("Textbox had " + t.getText());
    // // int numOfArgs = getNumberofArgFromMatrix(t.getText());
    // // String [] actualValues = getArgsFromMatrix(t.getText());
    // // String [] expectedValues = new String[4];
    // // expectedValues[0] = expectedFloat2bTempOutput[0];
    // // expectedValues[1] = expectedInt2bTempOutput[0];
    // // expectedValues[2] = expectedNominal2bTempOutput[0];
    // // expectedValues[3] = textTestInput[0];
    // //
    // //
    // //
    // // for (int j = 0; j < numOfArgs; j++) {
    // // System.out.println("Compare " + actualValues[j] + " and " +
    // expectedValues[j]);
    // // if (j == 3 && !actualValues[j].equals(expectedValues[j])) {
    // //
    // System.err.println("Test will fail here unless quote string behaviour has been fixed.");
    // // }
    // // assertTrueEqualValues(actualValues[j], expectedValues[j]);
    // // }
    //    
    //    
    //    
    // // Code below this line will be used later to augment the advanced test.
    //    
    // // String xVarName = "textVar";
    // // String varRadio = "text";
    // //
    // // //advanced Input will be provided between testInput
    // // Key[][] advancedInput = {{KeyEvent.VK_LEFT, KeyEvent.VK_LEFT},
    // // {KeyEvent.VK_LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
    // {KeyEvent.VK_BACK_SPACE,
    // KeyEvent.VK_LEFT},
    // // {KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT, KeyEvent.VK_LEFT,
    // KeyEvent.VK_LEFT,
    // KeyEvent.VK_DELETE,
    // // KeyEvent.VK_RIGHT}, {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT}};
    // //
    // // String[] advancedExpectedOutput = {"Subject stands u$10,432p ",
    // // "$10,43Hand me the manual!2", "hand me the manuaTote_that_balel",
    // // "Tote_that_aJeune fille celebrel",
    // // "Jeune fille celebreIf x?7 then x? 2"};
    // //
    // // runAdvancedTest(xVarName, textTestInput, advancedInput,
    // // advancedExpectedOutput);
    //    
    // }

    //
    // /**
    // * Test creating a new cell by pressing enter instead of clicking.
    // * @throws java.lang.Exception on any error
    // */
    // public void testCreateNewCellWithEnter() throws Exception {
    // String varName = "testVar";
    // String varType = UIUtils.VAR_TYPES[(int) (Math.random()
    // * UIUtils.VAR_TYPES.length)];
    // String varRadio = varType.toLowerCase();
    //
    // // 1. Retrieve the components
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    // // 2. Create new variable,
    // Window newVarWindow = WindowInterceptor.run(menuBar.getMenu(
    // "Spreadsheet").getSubMenu("New Variable").triggerClick());
    // newVarWindow.getTextBox("nameField").insertText(varName, 0);
    // newVarWindow.getRadioButton(varRadio).click();
    // assertTrue(newVarWindow.getRadioButton(varRadio).isSelected());
    // newVarWindow.getButton("Ok").click();
    //
    // Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
    // (window.getUIComponents(Spreadsheet.class)[0]
    // .getAwtComponent()));
    //
    // //Create new cell
    // //Instead of clicking, just press "Enter"
    // /* Code to be written
    // * Must click the column title
    // * Then press enter on it
    // */
    // Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
    // // readyToExit = true;
    // }
    //

    /**
     * Runs advanced tests.
     * @param varName
     *            name of variable and therefore column header name
     * @param testInput
     *            array of test input
     * @param advancedInput
     *            extra advanced input
     * @param expectedTestOutput
     *            expected test output
     */
    private void runAdvancedTest(final String varName,
            final String[] testInput, final int[][] advancedInput,
            final String[] expectedTestOutput) {
        int numOfTests = testInput.length;

        // 1. Create new cells, check that they have been created
        for (int ordinal = 1; ordinal <= numOfTests - 1; ordinal++) {
            createCell(varName);
            Assert.assertTrue(cellExists(varName, ordinal),
                    "Expecting cell to have been created.");
            Assert.assertTrue(cellHasOnset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have onset of 00:00:00:000");
            Assert.assertTrue(cellHasOffset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have offset of 00:00:00:000");
            Assert.assertTrue(cellHasValue(varName, ordinal, "<val>"),
                    "Expecting different blank cell value");

            // 2. Test different inputs as per specifications
            List<TextItem> inputs = new LinkedList<TextItem>();
            inputs.add(new StringItem(testInput[ordinal - 1]));
            inputs.add(new KeysItem(advancedInput[ordinal - 1]));
            inputs.add(new StringItem(testInput[ordinal]));

            changeCellValue(varName, ordinal, inputs);

            Assert.assertTrue(cellHasValue(varName, ordinal,
                    expectedTestOutput[ordinal - 1]),
                    "Expecting different cell contents.");
        }
    }

    // ORIGINAL:
    // /**
    // * Runs advanced tests.
    // * @param varName name of variable and therefore column header name
    // * @param testInput array of test input
    // * @param advancedInput extra advanced input
    // * @param expectedTestOutput expected test output
    // * @throws SystemErrorException on system error exception
    // */
    // private void runAdvancedTest(final String varName,
    // final String[] testInput, final Key[][] advancedInput,
    // final String[] expectedTestOutput) throws SystemErrorException {
    // // Retrieve the components and set variable
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    // Spreadsheet ss = new Spreadsheet((SpreadsheetPanel) (
    // window.getUIComponents(Spreadsheet.class)[0]
    // .getAwtComponent()));
    //
    // int numOfTests = testInput.length;
    // //2. Create new cells, check that they have been created
    // for (int i = 0; i < numOfTests; i++) {
    // menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
    // }
    // Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
    // assertTrue(cells.size() == numOfTests);
    // for (int i = 0; i < numOfTests - 1; i++) {
    // assertTrue(cells.elementAt(i).getOrd() == i + 1);
    // assertTrue((cells.elementAt(i).getOnset().getText()).equals(
    // "00:00:00:000"));
    // assertTrue((cells.elementAt(i).getOffset().getText()).equals(
    // "00:00:00:000"));
    // assertTrue(cells.elementAt(i).getValue().getText().equals("<val>"));
    // //4. Test different inputs as per specifications
    // Cell c = cells.elementAt(i);
    // TextBox t = c.getValue();
    //
    // Vector<TextItem> vti = new Vector<TextItem>();
    // vti.add(new StringItem(testInput[i]));
    // vti.add(new KeysItem(advancedInput[i]));
    // vti.add(new StringItem(testInput[i + 1]));
    //
    // c.enterText(Cell.VALUE, vti);
    //
    // assertTrueEqualValues(t.getText(), expectedTestOutput[i]);
    // }
    // }
    //

    /**
     * Runs a double argument matrix test.
     * @param varName
     *            name of variable and therefore column header name
     * @param testInput
     *            Array of arguments for matrix
     * @param expectedTestOutput
     *            expected test output
     */
    private void runMatrixTest(final String varName,
            final String[][] testInput, final String[] expectedTestOutput) {
        int numOfTests = testInput.length;

        // 1. Delete existing cells.
        deleteAllCells(varName);

        // 2. Create new cells, check that they have been created
        for (int ordinal = 1; ordinal < numOfTests; ordinal++) {
            createCell(varName);
            Assert.assertTrue(cellExists(varName, ordinal),
                    "Expecting cell to have been created.");
            Assert.assertTrue(cellHasOnset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have onset of 00:00:00:000");
            Assert.assertTrue(cellHasOffset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have offset of 00:00:00:000");

            // 4. Test different inputs as per specifications
            changeCellMatrixValue(varName, ordinal, testInput[ordinal - 1]);

            Assert.assertTrue(cellHasValue(varName, ordinal,
                    expectedTestOutput[ordinal - 1]),
                    "Expecting different cell value");
        }
    }

    // /**
    // * Runs a double argument matrix test.
    // *
    // * @param varName
    // * name of variable and therefore column header name
    // * @param testInput
    // * Array of arguments for matrix
    // * @param expectedTestOutput
    // * expected test output
    // */
    // private void runMatrixTest(final String varName,
    // final String[][] testInput, final String[] expectedTestOutput) {
    // // Retrieve the components and set variable
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    // Spreadsheet ss =
    // new Spreadsheet((SpreadsheetPanel) (window
    // .getUIComponents(Spreadsheet.class)[0]
    // .getAwtComponent()));
    //
    // int numOfTests = testInput.length;
    // ss.getSpreadsheetColumn(varName).requestFocus();
    // // 2. Create new cells, check that they have been created
    // for (int i = 0; i < numOfTests; i++) {
    // menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
    // }
    // Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
    // for (int i = 0; i < numOfTests - 1; i++) {
    // assertTrue((cells.elementAt(i).getOnset().getText())
    // .equals("00:00:00:000"));
    // assertTrue((cells.elementAt(i).getOffset().getText())
    // .equals("00:00:00:000"));
    // // 4. Test different inputs as per specifications
    // Cell c = cells.elementAt(i);
    // TextBox t = c.getValue();
    // c.enterMatrixText(testInput[i]);
    // int numOfArgs = UIUtils.getNumberofArgFromMatrix(t.getText());
    // String[] actualValues = UIUtils.getArgsFromMatrix(t.getText());
    // String[] expectedValues =
    // UIUtils.getArgsFromMatrix(expectedTestOutput[i]);
    // for (int j = 0; j < numOfArgs; j++) {
    // assertTrueEqualValues(actualValues[j], expectedValues[j]);
    // }
    // }
    // }

    //
    /**
     * matrix test exclusively for single argument matrix tests.
     * @param varName
     *            name of variable and therefore column header name
     * @param testInput
     *            array of test input
     * @param expectedTestOutput
     *            expected test output
     * @param customBlank
     *            customBlank second argument
     */
    private void runMatrixTest(final String varName, final String[] testInput,
            final String[] expectedTestOutput, final String customBlank) {
        String[][] matricisedInput = new String[testInput.length][2];
        for (int i = 0; i < testInput.length; i++) {
            matricisedInput[i][0] = testInput[i];
            matricisedInput[i][1] = "";
        }
        runMatrixTest(varName, matricisedInput, expectedTestOutput);
    }

    /**
     * Runs standard tests without advanced input, default custom blank used.
     * @param varName
     *            name of variable and therefore column header name
     * @param testInput
     *            array of test input
     * @param expectedTestOutput
     *            expected test output
     */
    private void runStandardTest(final String varName,
            final String[] testInput, final String[] expectedTestOutput) {
        runStandardTest(varName, testInput, expectedTestOutput, "<val>");
    }

    /**
     * Runs standard tests without advanced input.
     * @param varName
     *            name of variable and therefore column header name
     * @param testInput
     *            array of test input
     * @param expectedTestOutput
     *            expected test output
     * @param customBlank
     *            the placeholder if a value is blank
     */
    private void runStandardTest(final String varName,
            final String[] testInput, final String[] expectedTestOutput,
            final String customBlank) {

        int numOfTests = testInput.length;

        // 1. Create new cells, check that they have been created
        for (int ordinal = 1; ordinal <= numOfTests; ordinal++) {
            createCell(varName);
            Assert.assertTrue(cellExists(varName, ordinal),
                    "Expecting cell to have been created.");
            Assert.assertTrue(cellHasOnset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have onset of 00:00:00:000");
            Assert.assertTrue(cellHasOffset(varName, ordinal, "00:00:00:000"),
                    "Expecting cell to have offset of 00:00:00:000");
            Assert.assertTrue(cellHasValue(varName, ordinal, customBlank),
                    "Expecting different blank cell value");

            // 2. Test different inputs as per specifications
            changeCellValue(varName, ordinal, testInput[ordinal - 1]);
            clickCell(varName, ordinal);
            Assert.assertTrue(cellHasValue(varName, ordinal,
                    expectedTestOutput[ordinal - 1]),
                    "Expecting different cell value");
        }

    }

    /**
     * Tests for pasting.
     * @param varName
     *            variable name
     * @param varRadio
     *            radio for variable
     * @param testInput
     *            test input values
     * @param expectedTestOutput
     *            expected test output values
     */
    private void pasteTest(final String varName, final String varRadio,
            final String[] testInput, final String[] expectedTestOutput) {
        int numOfTests = testInput.length;

        // 1. Create new variable
        UIUtils.createNewVariable(mainFrameFixture, varName, varRadio);

        // 2. Create new cells, check that they have been created
        for (int ordinal = 1; ordinal <= numOfTests; ordinal++) {
            createCell(varName);
            Assert.assertTrue(cellExists(varName, ordinal),
                    "Expecting cell to have been created.");
        }

        // 3. Check copy pasting
        for (int ordinal = 1; ordinal <= numOfTests; ordinal++) {
            // Don't want to paste a value that is potentially already in the
            // cell
            int inputIndex = (ordinal + 2) % numOfTests;

            // Delete existing cell contents.
            changeCellValue(varName, ordinal, "");

            // Check that it actually was deleted
            Assert.assertTrue(cellHasValue(varName, ordinal, "<val>"),
                    "Expecting cell contents to be deleted.");

            // Paste new contents.
            pasteCellValue(varName, ordinal, testInput[inputIndex]);

            // Check that cell contents are pasted in
            Assert.assertTrue(cellHasValue(varName, ordinal,
                    expectedTestOutput[inputIndex]),
                    "Expecting different cell contents.");
        }
    }

    //
    // /**
    // * Tests for pasting.
    // * @param varName variable name
    // * @param varRadio radio for variable
    // * @param testInput test input values
    // * @param expectedTestOutput expected test output values
    // * @throws java.lang.Exception on any exception
    // */
    // private void pasteTest(final String varName,
    // final String varRadio, final String[] testInput,
    // final String[] expectedTestOutput) throws Exception {
    // // Retrieve the components and set variables
    // Window window = getMainWindow();
    // MenuBar menuBar = window.getMenuBar();
    //
    // int numOfTests = testInput.length;
    // //1. Create new TEXT variable,
    // //open spreadsheet and check that it's there
    // createNewVariable(varName, varRadio);
    // Spreadsheet ss = new Spreadsheet((SpreadsheetPanel)
    // (window.getUIComponents(Spreadsheet.class)[0]
    // .getAwtComponent()));
    // //3. Create 6 new cell, check that they have been created
    // for (int i = 0; i < numOfTests; i++) {
    // menuBar.getMenu("Spreadsheet").getSubMenu("New Cell").click();
    // }
    // Vector<Cell> cells = ss.getSpreadsheetColumn(varName).getCells();
    // //5. Check copy pasting
    // for (int i = 0; i < numOfTests; i++) {
    // int j = i % numOfTests;
    // Clipboard.putText(testInput[j]);
    // // Delete existing cell contents.
    // Cell c = cells.elementAt(i);
    // c.selectAllAndTypeKey(Cell.VALUE, KeyEvent.VK_DELETE);
    // //Check that it actually was deleted
    // assertTrue(c.getValueText().equals("<val>")
    // || c.getValueText().equals(""));
    // // Paste new contents.
    // TextBox t = c.getValue();
    // t.pasteFromClipboard();
    // assertTrueEqualValues(t.getText(), expectedTestOutput[i]);
    // }
    // }

//    /** SHOULD BE DELETED
//     * Creates a variable.
//     * @param varName variable name
//     * @param varType variable type
//     */
//    private void createNewVariable(final String varName, final String varType) {
//        // 1. Create new variable
//        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Variable")
//                .click();
//
//        // 2. Enter variable name
//        DialogFixture newVariableDialog = mainFrameFixture.dialog();
//        newVariableDialog.requireVisible();
//        JTextComponentFixture variableValueTextBox =
//                newVariableDialog.textBox();
//        variableValueTextBox.requireEmpty();
//        variableValueTextBox.requireEditable();
//        variableValueTextBox.enterText(varName);
//
//        // 3. Choose variable type
//        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
//                .click();
//        newVariableDialog.radioButton(varType.toLowerCase() + "TypeButton")
//                .requireSelected();
//        newVariableDialog.button("okButton").click();
//    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     */
    private void createCell(final String varName) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        final int numCells = spreadsheet.column(varName).numOfCells();
        if (numCells == 0) {
            spreadsheet.column(varName).clickHeader();
        } else {
            spreadsheet.column(varName).cell(numCells).selectCell();
        }

        mainFrameFixture.menuItemWithPath("Spreadsheet", "New Cell").click();
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     */
    private void clickCell(final String varName, final int id) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).cell(id).selectCell();
    }

    /**
     * @param varName
     *            column to test against, assumes that the column already exists
     * @param id
     *            cell ordinal value
     * @return true if the cell with ordinal 'id' exists, false otherwise
     */
    private boolean cellExists(final String varName, final int id) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        return id <= spreadsheet.column(varName).numOfCells();
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param onset
     *            Should be in the format HH:mm:ss:SSS
     * @return boolean - true if has onset
     */
    private boolean cellHasOnset(final String varName, final int id,
            final String onset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        return spreadsheet.column(varName).cell(id).onsetTimestamp().text()
                .equals(onset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param offset
     *            Should be in the format HH:mm:ss:SSS
     * @return boolean - true if has offset
     */
    private boolean cellHasOffset(final String varName, final int id,
            final String offset) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        return spreadsheet.column(varName).cell(id).offsetTimestamp().text()
                .equals(offset);
    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param value
     *            expected cell string value
     * @return true if the cell contains the expected cell value, false
     *         otherwise
     */
    private boolean cellHasValue(final String varName, final int id,
            final String value) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        return UIUtils.equalValues(spreadsheet.column(varName).cell(id)
                .cellValue().text(), value);

    }

    /**
     * Change cell values using a string as the target value.
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param value
     *            new cell value
     */
    private void changeCellValue(final String varName, final int id,
            final String value) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).cell(id).cellValue().enterText(value);
    }

    /**
     * Change cell value using a list of inputs as the target value.
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param inputs
     *            list of value inputs
     */
    private void changeCellValue(final String varName, final int id,
            final List<TextItem> inputs) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        JTextComponentFixture text =
                spreadsheet.column(varName).cell(id).cellValue();

        for (TextItem input : inputs) {
            input.enterItem(text);
        }

    }

    /**
     * Change cell value using a string array of inputs as the target value,
     * where each input is separated by the key press defined as separator.
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param values
     *            array of input values
     */
    private void changeCellMatrixValue(final String varName, final int id,
            final String[] values) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        SpreadsheetCellFixture cell = spreadsheet.column(varName).cell(id);
        JTextComponentFixture textField = cell.cellValue();

        for (int inputColumn = 1; inputColumn <= values.length; inputColumn++) {
            cell.selectCell();

            // Tab to the cell value
            for (int positions = 2 + inputColumn; positions > 0; positions--) {
                mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_TAB);
                // textField.pressAndReleaseKeys(KeyEvent.VK_TAB);
                textField.selectAll();
            }

            textField.enterText(values[inputColumn - 1]);
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_TAB);

        }

    }

    /**
     * @param varName
     *            name of column that contains the cell, assumes that the column
     *            already exists.
     * @param id
     *            cell ordinal value, assumes that the cell already exists
     * @param value
     *            cell value to paste
     */
    private void pasteCellValue(final String varName, final int id,
            final String value) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        UIUtils.setClipboard(value);

        spreadsheet.column(varName).cell(id).cellValue().focus()
                .pressAndReleaseKey(
                        KeyPressInfo.keyCode(KeyEvent.VK_V).modifiers(
                                Platform.controlOrCommandMask()));
    }

    /**
     * Deletes all cells in a particular column.
     * @param varName variable name.
     */
    private void deleteAllCells(final String varName) {
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture spreadsheet =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        spreadsheet.column(varName).clickHeader();

        int numOfCells = spreadsheet.column(varName).numOfCells();
        for (int ordinal = 1; ordinal <= numOfCells; ordinal++) {
            spreadsheet.column(varName).cell(ordinal).selectCell();
        }

        mainFrameFixture.menuItemWithPath("Spreadsheet", "Delete Cells")
                .click();
    }
}
