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

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotSame;
import static org.mockito.Mockito.*;

/**
 * Tests for the Variable Interface
 */
public class VariableTest {
    /** The parent datastore for the variable. */
    private Datastore ds;

    /** The model we are testing. */
    private Variable model;

    /** the modelListener we are testing. */
    private VariableListener modelListener;

    @BeforeMethod
    public void setUp() throws UserWarningException {
        ds = new DeprecatedDatabase();
        model = ds.createVariable("test", Variable.type.TEXT);
        modelListener = mock(VariableListener.class);
        model.addListener(modelListener);
    }

    @AfterMethod
    public void tearDown() {
        model.removeListener(modelListener);
        modelListener = null;
        model = null;
    }

    @Test
    public void testSetName() throws UserWarningException {
        assertEquals(model.getName(), "test");
        model.setName("test2");
        assertEquals(model.getName(), "test2");
        verify(modelListener).nameChanged("test2");
        verify(modelListener, times(0)).visibilityChanged(true);
        verify(modelListener, times(0)).cellInserted(null);
        verify(modelListener, times(0)).cellRemoved(null);
    }

    @Test
    public void testIsHidden() {
        assertFalse(model.isHidden());
        model.setHidden(true);
        assertTrue(model.isHidden());
        verify(modelListener).visibilityChanged(true);
        verify(modelListener, times(0)).nameChanged(null);
        verify(modelListener, times(0)).cellInserted(null);
        verify(modelListener, times(0)).cellRemoved(null);
    }

    @Test
    public void testIsSelected() {
        List<Variable> vars = new ArrayList<Variable>();
        vars.add(model);
        assertTrue(model.isSelected());
        assertEquals(ds.getSelectedVariables(), vars);
        model.setSelected(false);
        assertFalse(model.isSelected());
        verify(modelListener, times(0)).visibilityChanged(true);
        verify(modelListener, times(0)).nameChanged(null);
        verify(modelListener, times(0)).cellInserted(null);
        verify(modelListener, times(0)).cellRemoved(null);
    }

    @Test
    public void testGetVariableType() {
        assertEquals(model.getVariableType(), Variable.type.TEXT);
    }

    @Test
    public void testCreateCell() {
        List<Cell> cells = new ArrayList<Cell>();
        Cell c = model.createCell();
        cells.add(c);
        assertTrue(model.contains(c));
        assertEquals(ds.getVariable(c), model);
        assertEquals(model.getCells(), cells);
        verify(modelListener).cellInserted(c);
        verify(modelListener, times(0)).visibilityChanged(true);
        verify(modelListener, times(0)).nameChanged(null);
        verify(modelListener, times(0)).cellRemoved(null);
    }

    @Test
    public void testRemoveCell() {
        Cell c = model.createCell();
        ds.removeCell(c);

        assertFalse(model.contains(c));
        assertEquals(model.getCells().size(), 0);
        assertEquals(model.getCellsTemporally().size(), 0);

        verify(modelListener).cellRemoved(c);
        verify(modelListener).cellInserted(c);
        verify(modelListener, times(0)).nameChanged(null);
        verify(modelListener, times(0)).visibilityChanged(true);
    }

    @Test
    public void testRemoveCell2() {
        Cell c = model.createCell();
        model.removeCell(c);

        assertFalse(model.contains(c));
        assertEquals(model.getCells().size(), 0);
        assertEquals(model.getCellsTemporally().size(), 0);

        verify(modelListener).cellRemoved(c);
        verify(modelListener).cellInserted(c);
        verify(modelListener, times(0)).nameChanged(null);
        verify(modelListener, times(0)).visibilityChanged(true);
    }

    @Test
    public void testTemporalOrder() {
        List<Cell> cells = new ArrayList<Cell>();
        List<Cell> orderedCells = new ArrayList<Cell>();
        Cell c1 = model.createCell();
        Cell c2 = model.createCell();

        cells.add(c1);
        cells.add(c2);

        orderedCells.add(c2);
        orderedCells.add(c1);

        assertEquals(model.getCells(), cells);
        assertEquals(model.getCellsTemporally(), cells);
        assertNotSame(model.getCells(), orderedCells);
        assertNotSame(model.getCellsTemporally(), orderedCells);

        c1.setOnset(100);

        assertEquals(model.getCells(), cells);
        assertEquals(model.getCellsTemporally(), orderedCells);
        assertEquals(model.getCellTemporally(0), c2);
    }
}
