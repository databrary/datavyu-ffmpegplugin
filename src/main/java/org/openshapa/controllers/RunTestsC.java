package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.SystemErrorException;
import org.openshapa.views.ConsoleV;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Controller for running regression tests on the OpenSHAPA code-base.
 */
public class RunTestsC {

    /**
     * Constructor - creates and invokes the controller.
     */
    public RunTestsC() {
        try {
            this.runRegressionTests();
        } catch (SystemErrorException e) {
            logger.error("Unable to run regression tests", e);
        }
    }

    /**
     * Creates a class and adds it to the supplied list of unit tests if it ends
     * with 'Test.class' and is concrete (i.e. not abstract).
     *
     * @param unitTests The list of unit tests that you wish to test.
     * @param className The name of the class that you wish to determine if it
     * is a test or not. A class is considered a Junit test if it ends with
     * 'Test.class' and is concrete (no abstract tests are created and invoked).
     * @throws java.lang.ClassNotFoundException If unable to build the class
     * of the unit test to invoke.
     */
    private static void addTest(Vector<Class> unitTests, final String className)
    throws ClassNotFoundException {
        String cName = className;

        if (cName.endsWith("Test.class")) {
            // Build the class for the found test.
            cName = cName.substring(0, cName.length() - ".class".length());
            cName = cName.replace('/', '.');
            Class test = Class.forName(cName);

            // If the class is not abstract - add it to the list of
            // tests to perform.
            if (!Modifier.isAbstract(test.getModifiers())) {
                unitTests.add(test);
            }
        }

    }

    /**
     * All regression tests (Junits) and present results to the user.
     */
    public void runRegressionTests() throws SystemErrorException {
        OpenSHAPA.getApplication().show(ConsoleV.getInstance());

        // Build a list of unit tests to invoke.
        PrintWriter consoleWriter = OpenSHAPA.getConsoleWriter();
        consoleWriter.println("Running OpenSHAPA unit tests:");
        consoleWriter.flush();
        Vector<Class> unitTests = new Vector<Class>();

        // Build the list of unitTests to perform.
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resource = loader.getResource("org/openshapa");
            if (resource == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }

            // The classloader references a jar - open the jar file up and
            // iterate through all the entries and add the entries that are
            // concrete unit tests and add them to our list of tests to perform.
            if (resource.getFile().contains(".jar!")) {
                String file = resource.getFile();
                file = file.substring(0, file.indexOf("!"));
                URI uri = new URI(file);
                File f = new File(uri);
                JarFile jar = new JarFile(f);

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    addTest(unitTests, entries.nextElement().getName());
                }


            // The classloader references a bunch of .class files on disk,
            // recusively inspect contents of each of the resources. If it is
            // a directory at it to our workStack, otherwise check to see if it
            // is a concrete unit tests and add it to our list of tests to
            // perform.
            } else {
                Stack<File> workStack = new Stack<File>();
                workStack.push(new File(resource.getFile()));

                Stack<String> packages = new Stack<String>();
                packages.push("org.openshapa.");

                while (!workStack.empty()) {
                    File dir = workStack.pop();
                    String pkgName = packages.pop();

                    // For each of the children of the directory - look for
                    // tests or more directories to recurse inside.
                    String[] files = dir.list();
                    for (int i = 0; i < files.length; i++) {
                        File file = new File(dir.getAbsolutePath() + "/"
                                             + files[i]);
                        if (file == null) {
                            throw new ClassNotFoundException("Null file");
                        }

                        // If the file is a directory - add it to our work list.
                        if (file.isDirectory()) {
                            workStack.push(file);
                            packages.push(pkgName + file.getName() + ".");

                        // If the file ends with Test.class - it is a unit test,
                        // add it to our list of tests.
                        } else {
                            addTest(unitTests, pkgName.concat(files[i]));
                        }
                    }
                }
            }

        // Whoops - something went bad. Chuck a spaz.
        } catch (ClassNotFoundException e) {
            logger.error("Unable to build unit test", e);
        } catch (IOException ie) {
            logger.error("Unable to load jar file", ie);
        } catch (URISyntaxException se) {
            logger.error("Unable to build path to jar file", se);
        }

        // With the list of tests built - for each of them, excute the test and
        // report to the user a bunch of notification information.
        float totalTime = 0.0f;
        int totalTests = 0;
        int totalFailures = 0;

        // Build the testing framework and invoke all the regression tests.
        JUnitCore core = new JUnitCore();
        for (int i = 0; i < unitTests.size(); i++) {
            Result results = core.run(unitTests.get(i));

            // Display results to user.
            consoleWriter.println("\n******************************");
            consoleWriter.println("Running Test: "
                                  + unitTests.get(i).getName());

            float seconds = results.getRunCount() / 1000.0f;
            consoleWriter.println("Test Run time: " + seconds + "s");
            totalTime += seconds;

            consoleWriter.println("Tests Performed: "
                                  + results.getRunCount());
            totalTests += results.getRunCount();

            if (!results.wasSuccessful()) {
                consoleWriter.println("Tests failed: "
                                      + results.getFailureCount());
                totalFailures += results.getFailureCount();

                List<Failure> fails = results.getFailures();
                for (int j = 0; j < fails.size(); j++) {
                    consoleWriter.println("Failure: "
                                          + fails.get(j).getTestHeader());
                    consoleWriter.println(fails.get(j).getTrace());
                }
            }
            consoleWriter.flush();
        }

        consoleWriter.println("\n\n\n******************************");
        consoleWriter.println("         Test Summary");
        consoleWriter.println("******************************");
        consoleWriter.println("Total Time: " + totalTime + "s");
        consoleWriter.println("Total Tests: " + totalTests);
        consoleWriter.println("Test Failures: " + totalFailures);
        consoleWriter.flush();
    }

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(RunTestsC.class);
}
