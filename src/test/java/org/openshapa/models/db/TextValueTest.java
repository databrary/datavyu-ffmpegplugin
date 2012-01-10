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
package org.openshapa.models.db;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

/**
 * Tests for the TextValue Interface
 */
public class TextValueTest {
    
    /** The parent Datastore for the TextValue we are testing. */
    private Datastore ds;

    /** The parent variable for the TextValue we are testing. */
    private Variable var;

    /** The parent cell for the TextValue we are testing. */
    private Cell cell;

    /** The value that we are testing. */
    private Value model;

    @BeforeMethod
    public void setUp() throws UserWarningException {
        ds = DatastoreFactory.newDatastore();
        var = ds.createVariable("test", Argument.Type.TEXT);
        cell = var.createCell();
        model = cell.getValue();
    }

    @AfterMethod
    public void tearDown() {
        model = null;
        cell = null;
        var = null;
        ds = null;
    }

    @Test
    public void testClear() {
        assertTrue(model.isEmpty());

        model.set("test");
        assertFalse(model.isEmpty());

        model.clear();
        assertTrue(model.isEmpty());
    }
    
    
    
}
