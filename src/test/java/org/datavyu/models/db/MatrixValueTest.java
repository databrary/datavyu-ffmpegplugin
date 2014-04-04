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
package org.datavyu.models.db;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.*;

/**
 * Tests for the TextValue Interface
 */
public class MatrixValueTest {

    /**
     * The parent Datastore for the TextValue we are testing.
     */
    private Datastore ds;

    /**
     * The parent variable for the TextValue we are testing.
     */
    private Variable var;

    /**
     * The parent cell for the TextValue we are testing.
     */
    private Cell cell;

    /**
     * The value that we are testing.
     */
    private Value model;

    @BeforeMethod
    public void setUp() throws UserWarningException {
        ds = DatastoreFactory.newDatastore();
        var = ds.createVariable("test", Argument.Type.MATRIX);
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
    public void testAddArgument() {
        assertEquals(var.getRootNode().childArguments.size(), 1);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code01");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 1);

        var.addArgument(Argument.Type.NOMINAL);

        assertEquals(var.getRootNode().childArguments.size(), 2);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code01");
        assertEquals(var.getRootNode().childArguments.get(1).name, "code02");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 2);
    }

    @Test
    public void testMoveArgument() {
        var.addArgument(Argument.Type.NOMINAL);
        var.addArgument(Argument.Type.NOMINAL);

        cell.setMatrixValue(0, "foo1");
        cell.setMatrixValue(1, "foo2");
        cell.setMatrixValue(2, "foo3");

        assertEquals(var.getRootNode().childArguments.size(), 3);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code01");
        assertEquals(var.getRootNode().childArguments.get(1).name, "code02");
        assertEquals(var.getRootNode().childArguments.get(2).name, "code03");
        assertEquals(cell.getMatrixValue(0).toString(), "foo1");
        assertEquals(cell.getMatrixValue(1).toString(), "foo2");
        assertEquals(cell.getMatrixValue(2).toString(), "foo3");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 3);

        var.moveArgument("code01", 1);

        assertEquals(var.getRootNode().childArguments.size(), 3);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code02");
        assertEquals(var.getRootNode().childArguments.get(1).name, "code01");
        assertEquals(var.getRootNode().childArguments.get(2).name, "code03");
        assertEquals(cell.getMatrixValue(0).toString(), "foo2");
        assertEquals(cell.getMatrixValue(1).toString(), "foo1");
        assertEquals(cell.getMatrixValue(2).toString(), "foo3");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 3);

        var.moveArgument("code03", 1);

        assertEquals(var.getRootNode().childArguments.size(), 3);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code02");
        assertEquals(var.getRootNode().childArguments.get(1).name, "code03");
        assertEquals(var.getRootNode().childArguments.get(2).name, "code01");
        assertEquals(cell.getMatrixValue(0).toString(), "foo2");
        assertEquals(cell.getMatrixValue(1).toString(), "foo3");
        assertEquals(cell.getMatrixValue(2).toString(), "foo1");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 3);
    }

    @Test
    public void testSetArgument() {

        cell.setMatrixValue(0, "foo");
        assertEquals(cell.getMatrixValue(0).toString(), "foo");
    }

    @Test
    public void testRemoveArgument() {
        assertEquals(var.getRootNode().childArguments.size(), 1);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code01");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 1);

        var.addArgument(Argument.Type.NOMINAL);

        assertEquals(var.getRootNode().childArguments.size(), 2);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code01");
        assertEquals(var.getRootNode().childArguments.get(1).name, "code02");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 2);

        var.removeArgument("code01");
        assertEquals(var.getRootNode().childArguments.size(), 1);
        assertEquals(var.getRootNode().childArguments.get(0).name, "code02");
        assertEquals(((MatrixValue) cell.getValue()).getArguments().size(), 1);

    }

    @Test
    public void testClearArgument() {
        assertTrue(cell.getMatrixValue(0).isEmpty());

        cell.setMatrixValue(0, "foo");
        assertFalse(cell.getMatrixValue(0).isEmpty());

        cell.getMatrixValue(0).clear();
        assertTrue(cell.getMatrixValue(0).isEmpty());
    }


}
