/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views.continuous;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pwaller
 */
public class ProjectDescriptorTest {

    public ProjectDescriptorTest() {
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

    /**
     * Test of process method, of class ProjectDescriptor.
     */
    @Test
    public void testProcess() {
        try {
            System.out.println("process");

            File tmp001 = File.createTempFile("openSHAPAtest-", ".tmp");
            File tmp002 = File.createTempFile("openSHAPAtest-", ".tmp");

            StringBuilder sb = new StringBuilder();
            sb
                    .append("-").append("\n")
                    .append("  ")
                        .append("path: ").append(tmp001.getPath())
                        .append("\n")
                    .append("  ")
                        .append("plugin: ")
                        .append(this.getClass().getName())
                        .append("\n")
                    .append("  ").append("offset: -10").append("\n")
                    .append("-").append("\n")
                    .append("  ")
                        .append("path: ").append(tmp002.getPath())
                        .append("\n")
                    .append("  ")
                        .append("plugin: ")
                        .append(this.getClass().getName())
                        .append("\n")
                    .append("  ").append("offset: 100").append("\n")
                ;

            Reader reader = new StringReader(sb.toString());
            ProjectDescriptor instance = new ProjectDescriptor();
            instance.process(reader);

            tmp001.delete();
            tmp002.delete();

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

}