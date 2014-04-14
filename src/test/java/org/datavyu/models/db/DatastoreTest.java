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

import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the Datastore interface.
 */
public class DatastoreTest {

    /**
     * The model we are testing.
     */
    private Datastore model;

    /**
     * The modelListener we are testing.
     */
    private DatastoreListener modelListener;

    /**
     * The title notifier we are testing.
     */
    private TitleNotifier titleListener;

    @BeforeClass
    public void spinUp() {
    }

    @AfterClass
    public void spinDown() {
    }

    @BeforeMethod
    public void setUp() {
        model = DatastoreFactory.newDatastore();
        modelListener = mock(DatastoreListener.class);
        titleListener = mock(TitleNotifier.class);
        model.addListener(modelListener);
        model.setTitleNotifier(titleListener);
    }

    @AfterMethod
    public void tearDown() {
        model.removeListener(modelListener);
        modelListener = null;
        titleListener = null;
        model = null;
    }

    @Test
    public void testSetName() {
        model.setName("testName");
        assertEquals(model.getName(), "testName");
    }

    @Test
    public void createVariable() throws UserWarningException {
        assertFalse(model.isChanged());
        model.createVariable("foo", Argument.Type.TEXT);
        assertTrue(model.isChanged());
        Variable var = model.getVariable("foo");
        List<Variable> varList = new ArrayList<Variable>();
        varList.add(var);

        assertNotNull(var);

        // TODO: database should be changed after adding a new variable.
        // assertTrue(model.isChanged());

        assertEquals(model.getSelectedVariables(), varList);
        assertEquals(model.getAllVariables(), varList);
        assertEquals(var.getName(), "foo");
        assertEquals(var.getRootNode().type, Argument.Type.TEXT);
        assertTrue(var.isSelected());
        assertTrue(!var.isHidden());
        assertEquals(var.getCells().size(), 0);
        verify(modelListener).variableAdded(var);
        verify(modelListener, times(0)).variableOrderChanged();
        verify(modelListener, times(0)).variableRemoved(var);
        verify(modelListener, times(0)).variableHidden(var);
        verify(modelListener, times(0)).variableNameChange(var);
        verify(modelListener, times(0)).variableVisible(var);
        verify(titleListener).updateTitle();
    }

    @Test(expectedExceptions = UserWarningException.class)
    public void unableToCreateVariable() throws UserWarningException {
        model.createVariable("foo", Argument.Type.TEXT);
        model.createVariable("foo", Argument.Type.TEXT);
    }

    @Test
    public void removeVariable() throws UserWarningException {
        Variable var = model.createVariable("foo", Argument.Type.TEXT);
        List<Variable> varList = new ArrayList<Variable>();
        varList.add(var);

        assertEquals(model.getAllVariables(), varList);
        verify(modelListener).variableAdded(var);

        model.markAsUnchanged();
        assertFalse(model.isChanged());
        model.removeVariable(var);
        assertTrue(model.isChanged());

        assertEquals(model.getAllVariables().size(), 0);
        assertEquals(model.getSelectedVariables().size(), 0);

        verify(modelListener).variableRemoved(var);
        verify(modelListener, times(0)).variableHidden(var);
        verify(modelListener, times(0)).variableVisible(var);
        verify(modelListener, times(0)).variableNameChange(var);
        verify(modelListener, times(0)).variableOrderChanged();
    }

    @Test
    public void unchangedByDefault() throws UserWarningException {
        assertFalse(model.isChanged());
        model.createVariable("test", Argument.Type.TEXT);
        assertTrue(model.isChanged());
        model.markAsUnchanged();
        assertFalse(model.isChanged());
    }
}
