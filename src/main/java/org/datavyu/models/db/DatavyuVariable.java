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

import org.datavyu.Datavyu;

import java.util.*;

/**
 * Maps a variable object to a datastore.
 */
public final class DatavyuVariable implements Variable {
    // All the listeners for variables in teh datastore.
    static Map<UUID, List<VariableListener>> allListeners =
            new HashMap<UUID, List<VariableListener>>();
    final private UUID variableId = UUID.randomUUID();
    private List<Cell> cells = new ArrayList<Cell>();
    private Argument rootNodeArgument = null;
    private Boolean selected;
    private Boolean highlighted;
    private Boolean hidden;
    private String name;
    private int orderIndex = -1;

    private DatavyuDatastore owningDatastore;

    private static CellComparator CellComparator = new CellComparator();

    /**
     * @param variableId The ID of the variable we want the listeners for.
     *
     * @return The list of listeners for the specified variableId.
     */
    private static List<VariableListener> getListeners(UUID variableId) {
        List<VariableListener> result = allListeners.get(variableId);
        if (result == null) {
            result = new ArrayList<VariableListener>();
            allListeners.put(variableId, result);
        }

        return result;
    }

    /**
     * Removes all the listeners for all the variables.
     */
    public static void clearListeners() {
        allListeners.clear();
    }

    /**
     * Default constructor.
     */
    public DatavyuVariable() {
    }

    /**
     * Constructor.
     *
     * @param name The name to use for the variable being constructed.
     * @param type The type to use for the variable being constructed.
     */
    public DatavyuVariable(String name, Argument type) throws UserWarningException {
        this(name, type, false, null);
    }

    /**
     * Constructor.
     *
     * @param name The name to use for the variable being constructed.
     * @param type The type to use for the variable being constructed.
     * @param grandfathered Flag to exempt variable from naming rules.
     * @param dds The datastore to which this variable belongs
     */
    public DatavyuVariable(String name,
                           Argument type,
                           boolean grandfathered,
                           DatavyuDatastore dds) throws UserWarningException {
        owningDatastore = dds;
        this.setName(name, grandfathered);
        this.setRootNode(type);
        this.setHidden(false);
        this.setSelected(true);



        Datavyu.getProjectController().getDB().markDBAsChanged();
    }
    
    
    public void addCell(Cell cell) {
        if (cell.getValue().getArgument() == this.getRootNode()) {
            cells.add(cell);
            for(VariableListener vl : getListeners(getID()) ) {
                vl.cellInserted(cell);
            }
            Datavyu.getProjectController().getDB().markDBAsChanged();
        }
    }



    /**
     * @return The internal ID for this variable.
     */
    public UUID getID() {
        return variableId;
    }

    @Override
    public Cell createCell() {
        Cell c = new DatavyuCell(this, this.getRootNode());

        cells.add(c);

        for(VariableListener vl : getListeners(getID()) ) {
            vl.cellInserted(c);
        }

        Datavyu.getProjectController().getDB().markDBAsChanged();
        return c;
    }

    @Override
    public void removeCell(final Cell cell) {
        cells.remove(cell);

        Datavyu.getProjectController().getDB().markDBAsChanged();

        for(VariableListener vl : getListeners(getID()) ) {
            vl.cellRemoved(cell);
        }

    }

    @Override
    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public Cell getCellTemporally(final int index) {
        Collections.sort(cells, CellComparator);
        return cells.get(index);
    }

    @Override
    public Argument getRootNode() {
        return rootNodeArgument;
    }
    
    @Override
    @Deprecated
    public Argument getVariableType() {
        return getRootNode();
    }

    @Override
    public void setRootNode(final Argument a) {
        Datavyu.getProjectController().getDB().markDBAsChanged();
        rootNodeArgument = a;
    }
    
    @Override
    @Deprecated
    public void setVariableType(final Argument a) {
        setRootNode(a);
    }

    @Override
    public List<Cell> getCellsTemporally() {
        Collections.sort(cells, CellComparator);
        return cells;
    }

    @Override
    public boolean contains(final Cell c) {
        return cells.contains(c);
    }

    @Override
    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setHidden(final boolean hiddenParm) {
        if (hidden == null || hiddenParm != hidden) {
            Datavyu.getProjectController().getDB().markDBAsChanged();
            hidden = hiddenParm;

            for (VariableListener vl : getListeners(getID())) {
                vl.visibilityChanged(hidden);
            }
        }
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(final String newName) throws UserWarningException
    {
        this.setName(newName, false);
    }
    
    
    public void setName(final String newName, boolean grandfathered) throws UserWarningException {
        // Pre-conditions, the newName must have at least one character.
        if (newName.length() < 1) {
            throw new UserWarningException("Unable to add column, a name must be supplied.");
        }

        // Pre-conditions, check to make sure newName doesn't contain invalid chars or begin with a number or underscore
        if (!grandfathered && !isNameValid(newName)) {
            throw new UserWarningException("Unable to add column:\n\tOnly alphanumeric characters and underscore are permitted.\n\tName must begin with a letter\n\tMust contain fewer than 255 characters");
        }
        
        if (grandfathered && !isNameValid(newName)) {
            owningDatastore.addExemptionVariable(newName);
        }

        if (name != null && owningDatastore != null) {
            owningDatastore.updateVariableName(name, newName, this);
        }
        this.name = newName;
        for(VariableListener vl : getListeners(getID()) ) {
            vl.nameChanged(newName);
        }
    }
    
    
    private boolean isNameValid(String nameCandidate)
    {
        return nameCandidate != null && nameCandidate.matches("[a-zA-Z][a-zA-Z0-9_]*") && nameCandidate.length() < 255;
    }

    @Override
    public Argument addArgument(final Argument.Type type) {
        Argument arg = getRootNode();
        Argument child = arg.addChildArgument(type);

        for(Cell cell : getCells()) {
            cell.addMatrixValue(child);
        }

        this.setRootNode(arg);
        return arg.childArguments.get(arg.childArguments.size()-1);
    }

    @Override
    public void moveArgument(final int old_index, final int new_index) {
        Argument arg = getRootNode();

        // Test to see if this is out of bounds
        if(new_index > arg.childArguments.size() - 1 || new_index < 0) {
            return;
        }

        Argument moved_arg = arg.childArguments.get(old_index);
        arg.childArguments.remove(moved_arg);
        arg.childArguments.add(new_index, moved_arg);

        // Move in all cells
        for(Cell cell : getCells()) {
            cell.moveMatrixValue(old_index, new_index);
        }
        this.setRootNode(arg);
    }

    @Override
    public void moveArgument(final String name, final int new_index) {
        int old_index = getArgumentIndex(name);
        moveArgument(old_index, new_index);
    }

    @Override
    public void removeArgument(final String name) {
        Argument arg = getRootNode();
        int arg_index = getArgumentIndex(name);
        arg.childArguments.remove(arg_index);

        // Now send this change to the cells
        for(Cell cell : getCells()) {
            cell.removeMatrixValue(arg_index);
        }

        this.setRootNode(arg);
    }

    @Override
    public int getArgumentIndex(final String name) {
        Argument arg = getRootNode();
        for(int i = 0; i < arg.childArguments.size(); i++) {
            if(arg.childArguments.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addListener(final VariableListener listener) {
        getListeners(getID()).add(listener);
    }

    @Override
    public void removeListener(final VariableListener listener) {
        getListeners(getID()).remove(listener);
    }

    @Override
    public void setOrderIndex(final int newIndex) {
        orderIndex = newIndex;
    }

    @Override
    public int getOrderIndex() {
        return orderIndex;
    }

    //would like to change the above calls to DatavyuDatastore.markDBAsChanged to this,
    //but am holding off for now to avoid merge complications
    private void markDB() {
        if (owningDatastore != null) {
            owningDatastore.markDBAsChanged();
        } else if (Datavyu.getProjectController() != null) {
            //uncomment the below when markDBAsChanged is non-static
            //Datavyu.getProjectController().getDB().markDBAsChanged();
        } else {
            System.out.println("FAILED TO MARK DATASTORE");
        }
    }
}