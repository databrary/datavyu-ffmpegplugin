/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.db;

import org.openshapa.db.MacshapaODBReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mainzer
 */
public class MacshapaODBReaderTest {

    // The tests in this file are all derived from test files used in the
    // initial implementation of the open database format in MacSHAPA.
    //
    // I decided to use these files as the basis of this round of tests,
    // in the hope that they would form a good basic set of tests.  However,
    // the reader should be aware that due to the fundamental differences
    // between MacSHAPA and OpenSHAPA, some of the tests are not particularly
    // relevant to OpenSHAPA.  I've left such tests in place, as by the
    // time this became fully evident, the test code was already written,
    // and the tests in question didn't strike me as being completely useless.
    //
    // Also, while some of the tests are directed a features not supported
    // in OpenSHAPA at this time (i.e. Alignments, groups, and import formats),
    // that may change.  If so, these tests should give us a leg up.
    //
    //                                          12/7/09

    private boolean globalSaveMismatchFiles = true;
    private int EOF_TOK;
    boolean verbose = true;
    java.io.PrintStream outStream = System.out;

    public MacshapaODBReaderTest() throws Exception {
        EOF_TOK = (Integer) PrivateAccessor.getField(MacshapaODBReader.class,
                "EOF_TOK");

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void lexerTest01()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest01()";
        final boolean continueOnError = false;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_01.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_01.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_01.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_01.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_01.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest01() */


    @Test
    public void lexerTest02()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest02()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_02.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_02.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_02.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_02.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_02.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest02() */


    @Test
    public void lexerTest03()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest03()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_03.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_03.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_03.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_03.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_03.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest03() */


    @Test
    public void lexerTest04()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest04()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_04.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_04.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_04.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_04.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_04.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest04() */


    @Test
    public void lexerTest05()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest05()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_05.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_05.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_05.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_05.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_05.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest05() */


    @Test
    public void lexerTest06()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest06()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 3;
        final String inputFile = "/db/odb/lexer_test/lexer_test_06.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_06.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_06.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_06.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_06.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest06() */


    @Test
    public void lexerTest07()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest07()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 40;
        final String inputFile = "/db/odb/lexer_test/lexer_test_07.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_07.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_07.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_07.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_07.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest07() */


    @Test
    public void lexerTest08()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest08()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_08.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_08.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_08.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_08.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_08.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest08() */


    @Test
    public void lexerTest09()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest09()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_09.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_09.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_09.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_09.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_09.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest09() */


    @Test
    public void lexerTest10()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest10()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_10.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_10.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_10.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_10.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_10.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest10() */


    @Test
    public void lexerTest11()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest11()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_11.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_11.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_11.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_11.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_11.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest11() */


    @Test
    public void lexerTest12()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest12()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_12.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_12.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_12.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_12.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_12.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest12() */


    @Test
    public void lexerTest13()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest13()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_13.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_13.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_13.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_13.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_13.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest13() */


    @Test
    public void lexerTest14()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest14()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 3;
        final String inputFile = "/db/odb/lexer_test/lexer_test_14.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_14.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_14.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_14.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_14.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest14() */


    @Test
    public void lexerTest15()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest15()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 40;
        final String inputFile = "/db/odb/lexer_test/lexer_test_15.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_15.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_15.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_15.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_15.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest15() */


    @Test
    public void lexerTest16()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::lexerTest16()";
        final boolean continueOnError = true;
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 1;
        final String inputFile = "/db/odb/lexer_test/lexer_test_16.txt";
        final String listingFile = "/db/odb/lexer_test/lexer_test_16.lst";
        final String errorFile = "/db/odb/lexer_test/lexer_test_16.err";
        final String expListingFile = "/db/odb/lexer_test/lexer_test_16.lst.exp";
        final String expErrorFile = "/db/odb/lexer_test/lexer_test_16.err.exp";

        runLexerTest(inputFile,
                listingFile,
                errorFile,
                expListingFile,
                expErrorFile,
                errorLimit,
                warningLimit,
                continueOnError,
                saveMismatchFiles,
                saveOutputFiles);
        return;

    } /* MacshapaODBReaderTest::lexerTest16() */


    @Test
    public void parserTest001()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest001()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_001.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_001.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_001.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_001.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_001.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_001.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_001.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest001() */


    @Test
    public void parserTest002()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest002()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_002.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_002.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_002.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_002.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_002.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_002.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_002.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest002() */


    @Test
    public void parserTest003()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest003()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 15;
        final String inputFile = "/db/odb/parser_test/parser_test_003.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_003.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_003.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_003.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_003.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_003.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_003.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest003() */


    @Test
    public void parserTest004()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest004()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 5;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_004.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_004.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_004.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_004.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_004.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_004.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_004.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest004() */


    @Test
    public void parserTest005()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest005()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_005.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_005.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_005.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_005.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_005.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_005.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_005.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest005() */


    @Test
    public void parserTest006()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest006()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_006.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_006.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_006.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_006.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_006.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_006.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_006.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest006() */


    @Test
    public void parserTest007()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest007()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_007.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_007.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_007.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_007.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_007.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_007.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_007.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest007() */


    @Test
    public void parserTest008()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest008()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_008.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_008.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_008.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_008.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_008.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_008.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_008.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest008() */


    @Test
    public void parserTest009()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest009()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_009.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_009.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_009.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_009.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_009.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_009.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_009.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest009() */


    @Test
    public void parserTest010()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest010()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_010.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_010.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_010.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_010.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_010.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_010.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_010.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest010() */


    @Test
    public void parserTest011()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest011()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 3;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_011.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_011.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_011.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_011.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_011.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_011.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_011.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest011() */


    @Test
    public void parserTest012()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest012()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 3;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_012.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_012.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_012.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_012.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_012.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_012.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_012.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest012() */


    @Test
    public void parserTest013()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest013()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 30;
        final String inputFile = "/db/odb/parser_test/parser_test_013.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_013.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_013.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_013.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_013.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_013.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_013.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest013() */


    @Test
    public void parserTest014()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest014()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_014.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_014.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_014.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_014.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_014.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_014.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_014.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest014() */


    @Test
    public void parserTest015()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest015()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 15;
        final String inputFile = "/db/odb/parser_test/parser_test_015.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_015.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_015.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_015.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_015.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_015.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_015.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest015() */


    @Test
    public void parserTest016()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest016()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_016.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_016.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_016.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_016.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_016.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_016.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_016.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest016() */


    @Test
    public void parserTest017()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest017()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_017.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_017.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_017.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_017.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_017.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_017.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_017.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest017() */


    @Test
    public void parserTest018()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest018()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_018.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_018.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_018.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_018.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_018.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_018.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_018.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest018() */


    @Test
    public void parserTest019()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest019()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 1;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_019.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_019.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_019.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_019.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_019.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_019.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_019.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest019() */


    @Test
    public void parserTest020()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest020()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_020.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_020.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_020.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_020.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_020.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_020.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_020.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest020() */


    @Test
    public void parserTest021()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest021()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_021.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_021.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_021.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_021.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_021.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_021.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_021.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest021() */


    @Test
    public void parserTest022()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest022()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_022.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_022.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_022.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_022.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_022.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_022.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_022.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest022() */


    @Test
    public void parserTest023()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest023()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_023.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_023.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_023.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_023.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_023.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_023.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_023.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest023() */


    @Test
    public void parserTest024()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest024()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_024.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_024.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_024.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_024.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_024.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_024.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_024.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest024() */


    @Test
    public void parserTest025()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest025()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_025.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_025.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_025.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_025.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_025.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_025.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_025.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest025() */


    @Test
    public void parserTest026()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest026()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_026.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_026.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_026.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_026.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_026.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_026.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_026.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest026() */


    @Test
    public void parserTest027()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest027()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_027.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_027.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_027.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_027.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_027.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_027.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_027.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest027() */


    @Test
    public void parserTest028()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest028()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_028.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_028.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_028.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_028.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_028.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_028.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_028.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest028() */


    @Test
    public void parserTest029()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest029()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_029.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_029.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_029.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_029.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_029.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_029.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_029.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest029() */


    @Test
    public void parserTest030()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest030()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_030.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_030.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_030.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_030.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_030.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_030.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_030.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest030() */


    @Test
    public void parserTest031()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest031()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_031.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_031.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_031.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_031.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_031.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_031.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_031.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest031() */


    @Test
    public void parserTest032()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest032()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_032.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_032.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_032.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_032.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_032.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_032.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_032.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest032() */


    @Test
    public void parserTest033()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest033()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_033.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_033.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_033.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_033.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_033.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_033.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_033.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest033() */


    @Test
    public void parserTest034()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest034()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_034.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_034.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_034.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_034.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_034.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_034.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_034.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest034() */


    @Test
    public void parserTest035()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest035()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_035.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_035.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_035.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_035.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_035.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_035.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_035.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest035() */


    @Test
    public void parserTest036()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest036()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_036.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_036.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_036.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_036.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_036.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_036.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_036.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest036() */


    @Test
    public void parserTest037()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest037()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_037.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_037.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_037.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_037.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_037.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_037.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_037.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest037() */


    @Test
    public void parserTest038()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest038()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_038.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_038.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_038.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_038.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_038.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_038.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_038.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest038() */


    @Test
    public void parserTest039()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest039()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_039.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_039.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_039.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_039.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_039.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_039.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_039.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest039() */


    @Test
    public void parserTest040()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest040()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_040.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_040.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_040.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_040.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_040.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_040.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_040.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest040() */


    @Test
    public void parserTest041()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest041()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_041.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_041.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_041.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_041.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_041.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_041.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_041.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest041() */


    @Test
    public void parserTest042()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest042()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_042.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_042.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_042.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_042.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_042.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_042.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_042.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest042() */


    @Test
    public void parserTest043()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest043()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_043.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_043.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_043.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_043.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_043.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_043.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_043.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest043() */


    @Test
    public void parserTest044()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest044()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_044.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_044.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_044.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_044.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_044.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_044.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_044.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest044() */


    @Test
    public void parserTest045()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest045()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_045.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_045.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_045.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_045.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_045.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_045.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_045.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest045() */


    @Test
    public void parserTest046()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest046()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_046.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_046.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_046.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_046.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_046.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_046.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_046.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest046() */


    @Test
    public void parserTest047()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest047()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_047.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_047.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_047.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_047.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_047.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_047.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_047.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest047() */


    @Test
    public void parserTest048()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest048()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_048.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_048.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_048.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_048.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_048.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_048.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_048.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest048() */


    @Test
    public void parserTest049()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest049()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_049.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_049.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_049.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_049.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_049.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_049.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_049.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest049() */


    @Test
    public void parserTest050()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest050()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_050.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_050.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_050.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_050.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_050.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_050.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_050.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest050() */


    @Test
    public void parserTest051()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest051()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_051.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_051.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_051.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_051.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_051.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_051.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_051.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest051() */


    @Test
    public void parserTest052()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest052()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_052.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_052.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_052.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_052.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_052.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_052.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_052.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest052() */


    @Test
    public void parserTest053()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest053()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_053.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_053.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_053.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_053.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_053.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_053.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_053.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest053() */


    @Test
    public void parserTest054()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest054()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_054.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_054.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_054.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_054.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_054.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_054.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_054.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest054() */


    @Test
    public void parserTest055()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest055()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_055.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_055.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_055.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_055.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_055.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_055.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_055.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest055() */


    @Test
    public void parserTest056()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest056()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_056.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_056.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_056.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_056.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_056.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_056.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_056.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest056() */


    @Test
    public void parserTest057()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest057()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_057.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_057.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_057.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_057.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_057.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_057.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_057.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest057() */


    @Test
    public void parserTest058()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest058()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_058.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_058.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_058.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_058.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_058.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_058.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_058.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest058() */


    @Test
    public void parserTest059()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest059()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_059.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_059.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_059.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_059.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_059.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_059.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_059.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest059() */


    @Test
    public void parserTest060()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest060()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_060.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_060.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_060.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_060.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_060.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_060.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_060.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest060() */


    @Test
    public void parserTest061()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest061()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_061.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_061.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_061.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_061.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_061.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_061.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_061.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest061() */


    @Test
    public void parserTest062()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest062()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_062.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_062.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_062.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_062.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_062.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_062.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_062.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest062() */


    @Test
    public void parserTest063()
            throws SystemErrorException,
            LogicErrorException,
            java.io.IOException {
        final String mName = "MacshapaODBReaderTest::parserTest063()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_063.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_063.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_063.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_063.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_063.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_063.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_063.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest063() */


    @Test
    public void parserTest064()
            throws SystemErrorException,
            LogicErrorException,
            java.io.IOException {
        final String mName = "MacshapaODBReaderTest::parserTest064()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_064.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_064.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_064.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_064.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_064.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_064.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_064.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_064.lsteaderTest::parserTest064() */


    @Test
    public void parserTest065()
            throws SystemErrorException,
            LogicErrorException,
            java.io.IOException {
        final String mName = "MacshapaODBReaderTest::parserTest065()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_065.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_065.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_065.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_065.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_065.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_065.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_065.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest065() */


    @Test
    public void parserTest066()
            throws SystemErrorException,
            LogicErrorException,
            java.io.IOException {
        final String mName = "MacshapaODBReaderTest::parserTest066()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_066.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_066.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_066.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_066.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_066.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_066.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_066.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest066() */


    @Test
    public void parserTest067()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest067()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_067.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_067.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_067.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_067.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_067.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_067.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_067.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest067() */


    @Test
    public void parserTest068()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest068()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_068.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_068.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_068.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_068.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_068.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_068.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_068.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest068() */


    @Test
    public void parserTest069()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest069()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_069.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_069.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_069.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_069.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_069.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_069.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_069.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest069() */



    @Test
    public void parserTest070()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest070()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_070.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_070.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_070.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_070.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_070.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_070.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_070.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest070() */


    @Test
    public void parserTest071()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest071()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_071.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_071.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_071.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_071.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_071.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_071.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_071.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest071() */


    @Test
    public void parserTest072()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest072()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_072.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_072.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_072.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_072.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_072.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_072.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_072.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest072() */


    @Test
    public void parserTest073()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest073()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_073.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_073.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_073.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_073.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_073.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_073.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_073.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest073() */


    @Test
    public void parserTest074()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest074()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_074.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_074.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_074.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_074.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_074.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_074.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_074.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_074() */


    @Test
    public void parserTest075()
        throws SystemErrorException,
            LogicErrorException,
            java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest075()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_075.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_075.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_075.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_075.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_075.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_075.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_075.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest075() */


    @Test
    public void parserTest076()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest076()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_076.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_076.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_076.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_076.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_076.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_076.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_076.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest076() */


    @Test
    public void parserTest077()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest077()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_077.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_077.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_077.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_077.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_077.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_077.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_077.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest077() */


    @Test
    public void parserTest078()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest078()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_078.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_078.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_078.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_078.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_078.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_078.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_078.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest078() */


    @Test
    public void parserTest079()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest079()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_079.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_079.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_079.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_079.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_079.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_079.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_079.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest079() */


    @Test
    public void parserTest080()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest080()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_080.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_080.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_080.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_080.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_080.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_080.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_080.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest080() */


    @Test
    public void parserTest081()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest081()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_081.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_081.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_081.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_081.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_081.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_081.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_081.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest081() */


    @Test
    public void parserTest082()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest082()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_082.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_082.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_082.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_082.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_082.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_082.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_082.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest082() */


    @Test
    public void parserTest083()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest083()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_083.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_083.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_083.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_083.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_083.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_083.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_083.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest083() */


    @Test
    public void parserTest084()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest084()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_084.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_084.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_084.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_084.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_084.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_084.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_084.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_084() */


    @Test
    public void parserTest085()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest085()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_085.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_085.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_085.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_085.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_085.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_085.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_085.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest085() */


    @Test
    public void parserTest086()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest086()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_086.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_086.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_086.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_086.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_086.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_086.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_086.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest086() */


    @Test
    public void parserTest087()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest087()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_087.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_087.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_087.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_087.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_087.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_087.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_087.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest087() */


    @Test
    public void parserTest088()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest088()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_088.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_088.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_088.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_088.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_088.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_088.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_088.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest088() */


    @Test
    public void parserTest089()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest089()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_089.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_089.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_089.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_089.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_089.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_089.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_089.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest089() */


    @Test
    public void parserTest090()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest090()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_090.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_090.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_090.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_090.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_090.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_090.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_090.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest090() */


    @Test
    public void parserTest091()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest091()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_091.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_091.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_091.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_091.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_091.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_091.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_091.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest091() */


    @Test
    public void parserTest092()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest092()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_092.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_092.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_092.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_092.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_092.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_092.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_092.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest092() */


    @Test
    public void parserTest093()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest093()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_093.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_093.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_093.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_093.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_093.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_093.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_093.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest093() */


    @Test
    public void parserTest094()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest094()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_094.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_094.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_094.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_094.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_094.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_094.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_094.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_094() */


    @Test
    public void parserTest095()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest095()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_095.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_095.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_095.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_095.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_095.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_095.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_095.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest095() */


    @Test
    public void parserTest096()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest096()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_096.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_096.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_096.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_096.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_096.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_096.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_096.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest096() */


    @Test
    public void parserTest097()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest097()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_097.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_097.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_097.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_097.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_097.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_097.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_097.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest097() */


    @Test
    public void parserTest098()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest098()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_098.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_098.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_098.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_098.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_098.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_098.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_098.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest098() */


    @Test
    public void parserTest099()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest099()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_099.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_099.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_099.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_099.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_099.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_099.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_099.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest099() */


    @Test
    public void parserTest100()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest100()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_100.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_100.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_100.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_100.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_100.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_100.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_100.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest100() */


    @Test
    public void parserTest101()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest101()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_101.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_101.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_101.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_101.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_101.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_101.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_101.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest101() */


    @Test
    public void parserTest102()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest102()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_102.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_102.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_102.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_102.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_102.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_102.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_102.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest102() */


    @Test
    public void parserTest103()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest103()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_103.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_103.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_103.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_103.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_103.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_103.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_103.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest103() */


    @Test
    public void parserTest104()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest104()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_104.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_104.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_104.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_104.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_104.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_104.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_104.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_104() */


    @Test
    public void parserTest105()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest105()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_105.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_105.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_105.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_105.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_105.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_105.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_105.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest105() */


    @Test
    public void parserTest106()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest106()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 10;
        final String inputFile = "/db/odb/parser_test/parser_test_106.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_106.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_106.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_106.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_106.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_106.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_106.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest106() */


    @Test
    public void parserTest107()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest107()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_107.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_107.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_107.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_107.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_107.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_107.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_107.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest107() */


    @Test
    public void parserTest108()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest108()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_108.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_108.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_108.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_108.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_108.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_108.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_108.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest108() */


    @Test
    public void parserTest109()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest109()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_109.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_109.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_109.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_109.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_109.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_109.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_109.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest109() */


    @Test
    public void parserTest110()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest110()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_110.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_110.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_110.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_110.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_110.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_110.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_110.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest110() */


    @Test
    public void parserTest111()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest111()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_111.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_111.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_111.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_111.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_111.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_111.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_111.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest111() */


    @Test
    public void parserTest112()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest112()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 11;
        final String inputFile = "/db/odb/parser_test/parser_test_112.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_112.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_112.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_112.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_112.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_112.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_112.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest112() */


    @Test
    public void parserTest113()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest113()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_113.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_113.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_113.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_113.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_113.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_113.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_113.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest113() */


    @Test
    public void parserTest114()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest114()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_114.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_114.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_114.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_114.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_114.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_114.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_114.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_114() */


    @Test
    public void parserTest115()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest115()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 11;
        final String inputFile = "/db/odb/parser_test/parser_test_115.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_115.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_115.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_115.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_115.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_115.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_115.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest115() */


    @Test
    public void parserTest116()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest116()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 11;
        final String inputFile = "/db/odb/parser_test/parser_test_116.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_116.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_116.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_116.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_116.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_116.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_116.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest116() */


    @Test
    public void parserTest117()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest117()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_117.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_117.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_117.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_117.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_117.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_117.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_117.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest117() */


    @Test
    public void parserTest118()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest118()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_118.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_118.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_118.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_118.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_118.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_118.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_118.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest118() */


    @Test
    public void parserTest119()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest119()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_119.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_119.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_119.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_119.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_119.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_119.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_119.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest119() */



    @Test
    public void parserTest120()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest120()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_120.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_120.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_120.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_120.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_120.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_120.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_120.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest120() */


    @Test
    public void parserTest121()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest121()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_121.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_121.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_121.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_121.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_121.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_121.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_121.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest121() */


    @Test
    public void parserTest122()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest122()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_122.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_122.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_122.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_122.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_122.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_122.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_122.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest122() */


    // Note: The input file for this (and many subsequent tests) contains
    //       alignment, groups, and import format data that is currently
    //       discarded by MacshapaODBReader.
    //
    //       It does no harm for now, but it will break the tests (and
    //       perhaps give us a leg up on testing?) should we ever start
    //       loading these sections of the database.
    //                                              12/6/09

    @Test
    public void parserTest123()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest123()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_123.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_123.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_123.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_123.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_123.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_123.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_123.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest123() */


    @Test
    public void parserTest124()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest124()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 20;
        final String inputFile = "/db/odb/parser_test/parser_test_124.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_124.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_124.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_124.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_124.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_124.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_124.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBRparser_test_124() */


    @Test
    public void parserTest125()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest125()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_125.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_125.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_125.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_125.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_125.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_125.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_125.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest125() */


    @Test
    public void parserTest126()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest126()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_126.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_126.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_126.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_126.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_126.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_126.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_126.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest126() */


    @Test
    public void parserTest127()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest127()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_127.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_127.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_127.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_127.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_127.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_127.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_127.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest127() */


    @Test
    public void parserTest128()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest128()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_128.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_128.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_128.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_128.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_128.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_128.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_128.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest128() */


    @Test
    public void parserTest129()
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::parserTest129()";
        final boolean saveMismatchFiles = globalSaveMismatchFiles || false;
        final boolean saveOutputFiles = false;
        final int errorLimit = 2;
        final int warningLimit = 5;
        final String inputFile = "/db/odb/parser_test/parser_test_129.txt";
        final String listingFile = "/db/odb/parser_test/parser_test_129.lst";
        final String errorFile = "/db/odb/parser_test/parser_test_129.err";
        final String dumpFile = "/db/odb/parser_test/parser_test_129.dump";
        final String expListingFile = "/db/odb/parser_test/parser_test_129.lst.exp";
        final String expErrorFile = "/db/odb/parser_test/parser_test_129.err.exp";
        final String expDumpFile = "/db/odb/parser_test/parser_test_129.dump.exp";

        runParserTest(inputFile,
                listingFile,
                errorFile,
                dumpFile,
                expListingFile,
                expErrorFile,
                expDumpFile,
                saveMismatchFiles,
                saveOutputFiles,
                errorLimit,
                warningLimit);

        return;

    } /* MacshapaODBReaderTest::parserTest129() */



//    /**
//     * Test of get_next_token method, of class MacshapaODBReader.
//     */
//    @Test
//    public void testGet_next_token() throws Exception {
//        System.out.println("get_next_token");
//        MacshapaODBReader instance = new MacshapaODBReader();
//        instance.get_next_token();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of read_boolean_or_alist_tag_token method, of class MacshapaODBReader.
//     */
//    @Test
//    public void testRead_boolean_or_alist_tag_token() throws Exception {
//        System.out.println("read_boolean_or_alist_tag_token");
//        char first_char = ' ';
//        Token token = null;
//        MacshapaODBReader instance = new MacshapaODBReader();
//        instance.read_boolean_or_alist_tag_token(first_char, token);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parse_s_var_cell method, of class MacshapaODBReader.
//     */
//    @Test
//    public void testParse_s_var_cell() throws Exception {
//        System.out.println("parse_s_var_cell");
//        long s_var_col_ID = 0L;
//        MatrixType s_var_type = null;
//        MatrixVocabElement s_var_mve = null;
//        MacshapaODBReader instance = new MacshapaODBReader();
//        instance.parse_s_var_cell(s_var_col_ID, s_var_type, s_var_mve);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parse_user_section method, of class MacshapaODBReader.
//     */
//    @Test
//    public void testParse_user_section() throws Exception {
//        System.out.println("parse_user_section");
//        MacshapaODBReader instance = new MacshapaODBReader();
//        instance.parse_user_section();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    private boolean compareFiles(File expectedFile,
                                 File actualFile)
        throws java.io.IOException
    {
        boolean done = false;
        boolean filesAreEqual = true;
        FileReader expectedFileReader = new FileReader(expectedFile);
        BufferedReader expectedStream = new BufferedReader(expectedFileReader);
        FileReader actualFileReader = new FileReader(actualFile);
        BufferedReader actualStream = new BufferedReader(actualFileReader);
        String expectedLine = null;
        String actualLine = null;

        while ( ! done )
        {
            expectedLine = expectedStream.readLine();
            actualLine = actualStream.readLine();

            if ( ( expectedLine == null ) &&
                 ( actualLine == null ) )
            {
                done = true;
            } 
            else if ( ( expectedLine == null ) ||
                      ( actualLine == null ) )
            {
                filesAreEqual = false;
                done = true;
            } 
            else if ( expectedLine.compareTo(actualLine) != 0 )
            {
                filesAreEqual = false;
                done = true;
            }
        }

        expectedStream.close();
        actualStream.close();

        return filesAreEqual;
    }

    private void runLexerTest(String inputFileName,
                              String listingFileName,
                              String errorFileName,
                              String expectedListingFileName,
                              String expectedErrorFileName,
                              int errorLimit,
                              int warningLimit,
                              boolean continueOnError,
                              boolean saveMismatchFiles,
                              boolean saveOutputFiles)
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::runLexerTest()";
        boolean listingMismatch = false;
        boolean errorMismatch = false;
        String root = System.getProperty("testPath");
        File input = new File(root + inputFileName);
        FileReader reader = new FileReader(input);
        BufferedReader input_stream = new BufferedReader(reader);
        File listing = new File(root + listingFileName);
        File errors = new File(root + errorFileName);
        File expected_listing = new File(root + expectedListingFileName);
        File expected_errors = new File(root + expectedErrorFileName);
        MacshapaODBReader modbr = null;

        java.io.PrintStream listing_stream = null;
        PrintStream error_stream = null;

        if (!listing.createNewFile()) {
            throw new SystemErrorException(mName + "can't create listing file");
        }
        listing_stream = new PrintStream(listing);

        if (!errors.createNewFile()) {
            throw new SystemErrorException(mName + "can't create error file");
        }
        error_stream = new PrintStream(errors);


        modbr = new MacshapaODBReader(input_stream,
                listing_stream,
                error_stream);

        modbr.max_errors = errorLimit;
        modbr.max_warnings = warningLimit;

        do {
            modbr.get_next_token();

            modbr.listing_stream.println(modbr.l2_tok.toString());
            modbr.listing_stream.println();

            if (continueOnError) {
                if (modbr.abort_scan) {
                    modbr.listing_stream.println(
                            "forcing modbr.abort_scan back to false...");
                    modbr.listing_stream.println();
                    modbr.abort_scan = false;
                }
            }
        } while (modbr.l2_tok.code != EOF_TOK);

        input_stream.close();
        listing_stream.close();
        error_stream.close();

        if (!this.compareFiles(expected_listing, listing)) {
            listingMismatch = true;
        }

        if (!this.compareFiles(expected_errors, errors)) {
            errorMismatch = true;
        }

        if ((!saveOutputFiles) &&
                ((!listingMismatch) || (!saveMismatchFiles))) {
            if (!listing.delete()) {
                throw new SystemErrorException(mName +
                        "can't delete listing file");
            }
        }

        if ((!saveOutputFiles) &&
                ((!errorMismatch) || (!saveMismatchFiles))) {
            if (!errors.delete()) {
                throw new SystemErrorException(mName +
                        "can't delete errors file");
            }
        }

        if (listingMismatch) {
            org.junit.Assert.fail(
                    "actual listing doesn't match expected listing.");
        }

        if (errorMismatch) {
            org.junit.Assert.fail("" +
                    "actual error output doesn't match expected error ourput.");
        }

        return;

    } /* Macshapa_DBReaderTest::runLexerTest() */


    private void runParserTest(String inputFileName,
                               String listingFileName,
                               String errorFileName,
                               String dumpFileName,
                               String expectedListingFileName,
                               String expectedErrorFileName,
                               String expectedDumpFileName,
                               boolean saveMismatchFiles,
                               boolean saveOutputFiles,
                               int errorLimit,
                               int warningLimit)
        throws SystemErrorException,
               LogicErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaODBReaderTest::runParserTest()";
        boolean listingMismatch = false;
        boolean errorMismatch = false;
        boolean dumpMismatch = false;
        String root = System.getProperty("testPath");
        File input = new File(root + inputFileName);
        FileReader reader = new FileReader(input);
        BufferedReader input_stream = new BufferedReader(reader);
        File listing = new File(root + listingFileName);
        File errors = new File(root + errorFileName);
        File dump = new File(root + dumpFileName);
        File expected_listing = new File(root + expectedListingFileName);
        File expected_errors = new File(root + expectedErrorFileName);
        File expected_dump = new File(root + expectedDumpFileName);
        MacshapaODBReader modbr = null;
        MacshapaDatabase db = null;
        java.io.PrintStream listing_stream = null;
        java.io.PrintStream error_stream = null;
        java.io.PrintStream dump_stream = null;

        if ( ! listing.createNewFile() )
        {
            throw new SystemErrorException(mName + "can't create listing file");
        }
        listing_stream = new PrintStream(listing);

        if ( ! errors.createNewFile() )
        {
            throw new SystemErrorException(mName + "can't create error file");
        }
        error_stream = new PrintStream(errors);

        if ( ! dump.createNewFile() )
        {
            throw new SystemErrorException(mName + "can't create dump file");
        }
        dump_stream = new PrintStream(dump);


        modbr = new MacshapaODBReader(input_stream,
                listing_stream,
                error_stream);

        modbr.max_errors = errorLimit;
        modbr.max_warnings = warningLimit;

        db = modbr.readDB();

        db.toMODBFile(dump_stream, "\n");

        input_stream.close();
        listing_stream.close();
        error_stream.close();
        dump_stream.close();

        if ( ! this.compareFiles(expected_listing, listing) )
        {
            listingMismatch = true;
        }

        if ( ! this.compareFiles(expected_errors, errors) )
        {
            errorMismatch = true;
        }

        if ( ! this.compareFiles(expected_dump, dump) )
        {
            dumpMismatch = true;
        }

        if ( ( ! saveOutputFiles ) &&
             ( ( ! listingMismatch ) || ( ! saveMismatchFiles ) ) )
        {
            if ( ! listing.delete() )
            {
                throw new SystemErrorException(mName +
                        "can't delete listing file");
            }
        }

        if ( ( ! saveOutputFiles ) &&
             ( ( ! errorMismatch ) ||  ( ! saveMismatchFiles ) ) )
        {
            if ( ! errors.delete() )
            {
                throw new SystemErrorException(mName +
                        "can't delete errors file");
            }
        }

        if ( ( ! saveOutputFiles ) &&
             ( ( ! dumpMismatch ) || ( ! saveMismatchFiles ) ) )
        {
            if ( ! dump.delete() )
            {
                throw new SystemErrorException(mName +
                        "can't delete dump file");
            }
        }

        if ( listingMismatch )
        {
            org.junit.Assert.fail(
                    "actual listing doesn't match expected listing.");
        }

        if ( errorMismatch )
        {
            org.junit.Assert.fail(
                    "actual error output doesn't match expected error ourput.");
        }

        if ( dumpMismatch )
        {
            org.junit.Assert.fail(
                    "actual db dump output doesn't match expected dump ourput.");
        }

        return;

    } /* Macshapa_DBReaderTest::runParserTest() */
}