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
package org.datavyu.controllers;

import com.usermetrix.jclient.UserMetrix;
import org.datavyu.models.db.Argument;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.db.Variable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Tests for opening Datavyu project and CSV files.
 */
public class OpenCTest {

    // The location of the test files.
    private static final String TEST_FOLDER = System.getProperty("testPath");

    @BeforeClass
    public void spinUp() {
        com.usermetrix.jclient.Configuration config = new com.usermetrix.jclient.Configuration(2);
        UserMetrix.initalise(config);
        UserMetrix.setCanSendLogs(false);
    }

    @AfterClass
    public void spinDown() {
        UserMetrix.shutdown();
    }

    @Test
    public void testLoadCSV() {
        File demoFile = new File(TEST_FOLDER + "IO/simple1.csv");
        OpenC openc = new OpenC();
        openc.openDatabase(demoFile);

        Datastore ds = openc.getDatastore();
        List<Variable> vars = ds.getAllVariables();
        assertEquals(vars.size(), 1);
        assertEquals(vars.get(0).getName(), "TestColumn");
        assertEquals(vars.get(0).getRootNode().type, Argument.Type.TEXT);
        assertEquals(vars.get(0).isHidden(), false);

        List<Cell> cells = vars.get(0).getCells();
        assertEquals(cells.size(), 1);
        assertEquals(cells.get(0).getOnsetString(), "00:01:00:000");
        assertEquals(cells.get(0).getOffsetString(), "00:02:00:000");
        assertEquals(cells.get(0).getValueAsString(), "This is a test cell.");
    }

    @Test
    public void testLoadOPF() {
        File demoFile = new File(TEST_FOLDER + "IO/simple2.opf");
        OpenC openc = new OpenC();
        openc.openProject(demoFile);

        Datastore ds = openc.getDatastore();
        List<Variable> vars = ds.getAllVariables();
        assertEquals(vars.size(), 1);
        assertEquals(vars.get(0).getName(), "TestColumn");
        assertEquals(vars.get(0).getRootNode().type, Argument.Type.TEXT);
        assertEquals(vars.get(0).isHidden(), false);

        List<Cell> cells = vars.get(0).getCells();
        assertEquals(cells.size(), 1);
        assertEquals(cells.get(0).getOnsetString(), "00:01:00:000");
        assertEquals(cells.get(0).getOffsetString(), "00:02:00:000");
        assertEquals(cells.get(0).getValueAsString(), "This is a test cell.");
    }

    @Test
    public void testLoadOPF2() {
        File demoFile = new File(TEST_FOLDER + "IO/simple3.opf");
        OpenC openc = new OpenC();
        openc.openProject(demoFile);

        Datastore ds = openc.getDatastore();
        List<Variable> vars = ds.getAllVariables();
        assertEquals(vars.size(), 4);
        assertEquals(vars.get(0).getName(), "testColumn");
        assertEquals(vars.get(0).getRootNode().type, Argument.Type.TEXT);
        assertEquals(vars.get(0).isHidden(), false);
        List<Cell> cells = vars.get(0).getCells();
        assertEquals(cells.size(), 1);
        assertEquals(cells.get(0).getValueAsString(), "cellA");

        assertEquals(vars.get(1).getName(), "testColumn2");
        assertEquals(vars.get(1).getRootNode().type, Argument.Type.NOMINAL);
        assertEquals(vars.get(1).isHidden(), false);
        cells = vars.get(1).getCells();
        assertEquals(cells.size(), 1);
        assertEquals(cells.get(0).getValueAsString(), "cellB");

        assertEquals(vars.get(2).getName(), "testColumn3");
        assertEquals(vars.get(2).getRootNode().type, Argument.Type.MATRIX);
        assertEquals(vars.get(2).isHidden(), false);
        cells = vars.get(2).getCells();
        assertEquals(cells.size(), 1);
        assertEquals(cells.get(0).getValueAsString(), "(cellC)");

        assertEquals(vars.get(3).getName(), "hiddenColumn");
        assertEquals(vars.get(3).getRootNode().type, Argument.Type.TEXT);
        assertEquals(vars.get(3).isHidden(), true);
        cells = vars.get(3).getCells();
        assertEquals(cells.size(), 0);
    }

    @Test
    public void testLoadOPF3() {
        File demoFile = new File(TEST_FOLDER + "IO/simple4.opf");
        OpenC openc = new OpenC();
        openc.openProject(demoFile);

        Datastore ds = openc.getDatastore();
        List<Variable> vars = ds.getAllVariables();
        assertEquals(vars.size(), 1);
        assertEquals(vars.get(0).getName(), "blah");

        List<Cell> cells = vars.get(0).getCells();
        assertEquals(cells.size(), 1);
    }
}
