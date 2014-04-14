/*
 * Copyright (c) 2011 Datavyu Foundation, http://datavyu.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.datavyu.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.util.*;

/**
 * Acts as a connector between Datavyu's various data structures.
 */
public class DatavyuDatastore implements Datastore {

    // The logger for the datastore
    private static Logger LOGGER = UserMetrix.getLogger(DatavyuDatastore.class);

    // Name of the datastore - does not need to persist - is used for file names.
    private String name = "untitled";

    // The notifier to ping when the application's title changes.
    private static TitleNotifier titleNotifier = null;

    // Has tbhe datastore changed since it has last been marked as unchanged?
    private boolean changed;
    //
    private List<DatastoreListener> dbListeners = new ArrayList<DatastoreListener>();
    private Map<String, Variable> variables;

    private VariableComparator VariableComparator = new VariableComparator();


    public DatavyuDatastore() {

        // Set up variable collection
        variables = new HashMap<String, Variable>();

        // Clear variable listeners.
        DatavyuVariable.clearListeners();
        changed = false;
    }

    @Override
    public void markDBAsChanged() {
        if (!changed) {
            changed = true;

            if (DatavyuDatastore.titleNotifier != null) {
                DatavyuDatastore.titleNotifier.updateTitle();
            }
        }
    }

    @Override
    public List<Variable> getAllVariables() {
        List<Variable> varList = new ArrayList<Variable>();
        for (String s : variables.keySet()) {
            varList.add(variables.get(s));
        }
        Collections.sort(varList, VariableComparator);
        return varList;
    }

    @Override
    public List<Variable> getSelectedVariables() {
        List<Variable> varList = new ArrayList<Variable>();
        for (String s : variables.keySet()) {
            if (variables.get(s).isSelected()) {
                varList.add(variables.get(s));
            }
        }
        return varList;
    }

    @Override
    public void clearVariableSelection() {
        for (String s : variables.keySet()) {
            variables.get(s).setSelected(false);
        }
    }

    @Override
    public List<Cell> getSelectedCells() {
        List<Cell> selectedCells = new ArrayList<Cell>();

        for (Variable v : variables.values()) {
            for (Cell c : v.getCells()) {
                if (c.isSelected()) selectedCells.add(c);
            }
        }

        return selectedCells;
    }

    @Override
    public void clearCellSelection() {
        for (Variable v : variables.values()) {
            for (Cell c : v.getCells()) {
                if (c.isSelected()) c.setSelected(false);
            }
        }
    }

    @Override
    public void deselectAll() {
        this.clearCellSelection();
        this.clearVariableSelection();
    }

    @Override
    public Variable getVariable(String varName) {
        return variables.get(varName);
    }

    @Override
    public Variable getVariable(Cell cell) {
        for (Variable v : variables.values()) {
            if (v.getCells().contains(cell)) return v;
        }
        return null;
    }

    @Override
    public Variable createVariable(final String name, final Argument.Type type)
            throws UserWarningException {
        return createVariable(name, type, false);
    }
    
    @Override
    public Variable createVariable(final String name, final Argument.Type type, boolean grandfathered)
            throws UserWarningException {
        // Check to make sure the variable name is not already in use:
        Variable varTest = getVariable(name);
        if (varTest != null) {
            throw new UserWarningException("Unable to add column with name '"+name+"', one with the same name already exists.");
        }

        Argument rootNode;
        if (type == Argument.Type.MATRIX) rootNode = new Argument(name+name.hashCode(), type);
        else rootNode = new Argument("var", type);
                
        Variable v = new DatavyuVariable(name, rootNode, grandfathered, this);
        variables.put(name, v);

        for (DatastoreListener dbl : this.dbListeners) {
            dbl.variableAdded(v);
        }

        markDBAsChanged();
        return v;
    }        

    @Override
    public void removeVariable(final Variable var) {
        for (DatastoreListener dbl : this.dbListeners) {
            dbl.variableRemoved(var);
        }

        variables.remove(var.getName());
        markDBAsChanged();
    }

    @Override
    public void removeCell(final Cell cell) {
        getVariable(cell).removeCell(cell);
        markDBAsChanged();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void canSetUnsaved(final boolean canSet) {
    }

    @Override
    public void markAsUnchanged() {
        if (changed) {
            changed = false;

            if (DatavyuDatastore.titleNotifier != null) {
                DatavyuDatastore.titleNotifier.updateTitle();
            }
        }
    }

    @Override
    public void updateVariableName(String oldName, String newName, Variable variable) {
        this.variables.remove(oldName);
        this.variables.put(newName, variable);
        if(!oldName.equals(newName)) markDBAsChanged();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setName(final String datastoreName) {
        name = datastoreName;
    }

    @Override
    public void setTitleNotifier(final TitleNotifier titleNotifier) {
        DatavyuDatastore.titleNotifier = titleNotifier;
    }

    @Override
    public void addListener(final DatastoreListener listener) {
        dbListeners.add(listener);
    }

    @Override
    public void removeListener(final DatastoreListener listener) {
        if (dbListeners.contains(listener)) {
            dbListeners.remove(listener);
        }
    }
}
