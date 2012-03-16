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
package org.openshapa.controllers;

import com.usermetrix.jclient.UserMetrix;
import java.io.File;
import java.io.IOException;
import org.openshapa.models.db.*;
import org.openshapa.util.UIUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for saving OpenSHAPA project and CSV files.
 */
public class SaveCTest {
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
    public void testSaveCSV() throws UserWarningException, IOException {
        File outFile = new File("target/test1.csv");
        // Clean up the out file only if it exists.
        if (outFile.exists()) {
            outFile.delete();
        }
        File demoFile = new File(TEST_FOLDER + "IO/simple1.csv");
        
        Datastore ds = DatastoreFactory.newDatastore();
        Variable var = ds.createVariable("TestColumn", Argument.Type.TEXT);
        Cell c = var.createCell();
        c.setOnset("00:01:00:000");
        c.setOffset("00:02:00:000");
        c.getValue().set("This is a test cell.");

        SaveC savec = new SaveC();
        savec.saveDatabase(outFile, ds);

        assertTrue(UIUtils.areFilesSameLineComp(outFile, demoFile));
    }
    
    
}
