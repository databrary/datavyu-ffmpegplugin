package org.openshapa.util;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.io.IOUtils;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.util.Platform;

import org.openshapa.controllers.RunScriptC;

import org.openshapa.plugins.PluginManager;

import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;


/**
 * Utilities for UI tests.
 */
public final class UIUtils {

    /**
     * Different cell variable types.
     */
    public static final String[] VAR_TYPES = {
            "TEXT", "PREDICATE", "INTEGER", "NOMINAL", "MATRIX", "FLOAT"
        };

    /** BLOCK_SIZE for files. */
    private static final int BLOCK_SIZE = 65536;

    /** Maximum time allowed for a script to load. */
    public static final int SCRIPT_LOAD_TIMEOUT = 5000;

    /**
     * Empty constructor for utility class.
     */
    private UIUtils() {
    }

    /**
     * Checks if two text files are equal.
     * @param file1 First file
     * @param file2 Second file
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    public static Boolean areFilesSameByteComp(final File file1,
        final File file2) throws IOException {

        // Check file sizes first
        if (file1.length() != file2.length()) {
            return false;
        }

        // Compare bytes
        InputStream i1 = new FileInputStream(file1);
        InputStream i2 = new FileInputStream(file2);
        byte[] stream1Block = new byte[BLOCK_SIZE];
        byte[] stream2Block = new byte[BLOCK_SIZE];
        int b1, b2;

        do {
            b1 = i1.read(stream1Block);
            b2 = i2.read(stream2Block);
        } while ((b1 == b2) && (b1 != -1));

        i1.close();
        i2.close();

        // Check if we've reached the end of the file. If we have, they're
        // identical
        return b1 == -1;
    }

    /**
     * Checks if two text files are equal.
     * @param file1 First file
     * @param file2 Second file
     * @throws IOException on file read error
     * @return true if equal, else false
     */
    public static Boolean areFilesSameLineComp(final File file1,
        final File file2) throws IOException {
        FileReader fr1 = new FileReader(file1);
        FileReader fr2 = new FileReader(file2);

        BufferedReader r1 = new BufferedReader(fr1);
        BufferedReader r2 = new BufferedReader(fr2);

        String line1 = r1.readLine();
        String line2 = r2.readLine();

        if (!line1.equals(line2)) {
            return false;
        }

        while ((line1 != null) || (line2 != null)) {

            if (!line1.equals(line2)) {
                return false;
            }

            line1 = r1.readLine();
            line2 = r2.readLine();
        }

        IOUtils.closeQuietly(r1);
        IOUtils.closeQuietly(r2);

        IOUtils.closeQuietly(fr1);
        IOUtils.closeQuietly(fr2);

        return true;
    }

    /**
     * "Copies" str to the clipboard.
     * @param str String to copy
     */
    public static void setClipboard(final String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Checks if two values are equal as doubles or as strings.
     * @param value1 first value
     * @param value2 second value
     * @return true if equal
     */
    public static boolean equalValues(final String value1,
        final String value2) {

        if ((value1.startsWith("<") && value1.endsWith(">"))
                || (value2.startsWith("<") && value2.endsWith(">"))) {
            boolean result = value1.equalsIgnoreCase(value2);

            if (!result) {
                System.out.println(value1 + "\n" + value2 + "\n");
            }

            return value1.equalsIgnoreCase(value2);
        } else {

            try {

                // Handle doubles
                boolean result = FloatUtils.closeEnough(Double.parseDouble(
                            value1), Double.parseDouble(value2));

                if (!result) {
                    System.out.println(value1 + "\n" + value2 + "\n");
                }

                return FloatUtils.closeEnough(Double.parseDouble(value1),
                        Double.parseDouble(value2));
            } catch (NumberFormatException nfe) {

                // Handle other variable types
                boolean result = value1.equalsIgnoreCase(value2);

                if (!result) {
                    System.out.println(value1 + "\n" + value2 + "\n");
                }

                return value1.equalsIgnoreCase(value2);
            }
        }
    }

    /**
     * Returns all arguments in a matrix type as a String array.
     * @param matrixValue matrix string
     * @return String array with all matrix arguments
     */
    public static String[] getArgsFromMatrix(final String matrixValue) {
        String argList = matrixValue.substring(1, matrixValue.length() - 1);

        return argList.split(", ", -1);
    }

    /**
     * Converts milliseconds to a timestamp string.
     * @param milliseconds number of milliseconds
     * @return Timestamp String
     */
    public static String millisecondsToTimestamp(final long milliseconds) {

        // DateFormat is not thread safe.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        sdf.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));

        return sdf.format(milliseconds);
    }

    /**
    * @param r rectange to find centre of
    * @return point at centre of rectange.
    */
    public static Point centerOf(final Rectangle r) {
        return new Point(r.x + (r.width / 2), r.y + (r.height / 2));
    }

    /**
     * Copies one file to another.
     * @param src source file
     * @param dst destination file
     * @throws IOException on IOException
     */
    public static void copy(final File src, final File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Due to a bug in FEST we need to invoke OpenSHAPA code directly to run
     * scripts in OSX.
     * @param script script to run
     */
    public static void runScriptOnOSX(final File script) {
        GuiActionRunner.execute(new GuiTask() {

                public void executeInEDT() {

                    try {
                        RunScriptC scriptC = new RunScriptC(script.toString());
                        scriptC.execute();
                    } catch (IOException e) {
                        System.err.println(
                            "Unable to invoke script:" + e.toString());
                    }
                }
            });
    }

    public static String getInnerTextFromHTML(final String html) {
        final StringBuilder s = new StringBuilder();

        try {
            HTMLEditorKit.ParserCallback callback =
                new HTMLEditorKit.ParserCallback() {
                    public void handleText(final char[] data, final int pos) {
                        s.append(data);
                    }
                };
            new ParserDelegator().parse(new StringReader(html), callback,
                false);
        } catch (IOException ex) {
            Logger.getLogger(UIUtils.class.getName()).log(Level.SEVERE, null,
                ex);
        }

        return s.toString();
    }

    /**
     * Open data using data controller.
     * @param videoFile file to open
     * @param dcf DataControllerFixture
     */
    public static void openData(final File videoFile,
        final DataControllerFixture dcf) {

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();
            GuiActionRunner.execute(new GuiTask() {

                    public void executeInEDT() {
                        OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                        fc.setVisible(false);

                        for (FileFilter f : pm.getPluginFileFilters()) {
                            fc.addChoosableFileFilter(f);
                        }

                        fc.setSelectedFile(videoFile);
                        method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class).in(
                            (DataControllerV) dcf.component()).invoke(fc);
                    }
                });
        } else {
            boolean worked = false;
            JFileChooserFixture jfcf = null;

            final long startTime = System.currentTimeMillis();
            final long maxTestRunTime = 5 * 1000;

            do {
                dcf.button("addDataButton").click();

                try {
                    jfcf = dcf.fileChooser();
                    jfcf.selectFile(videoFile).approve();
                    worked = true;
                } catch (Exception e) {
                    // keep trying
                }
            } while ((worked == false)
                && (System.currentTimeMillis() < (startTime + maxTestRunTime)));
        }
    }
}
