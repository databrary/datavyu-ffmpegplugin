/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.models.db;

import java.util.ArrayList;
import java.util.List;

/**
 
 */
public class MongoVariable implements Variable {
        
    @Override
    public Cell createCell() {
        return null;
    }

    @Override
    public void removeCell(final Cell cell) {
    }

    @Override
    public List<Cell> getCells() {
        List<Cell> cells = new ArrayList<Cell>();

        return cells;
    }

    public Cell getCellTemporally(final int index) {
        return null;
    }

    @Override
    public Argument getVariableType() {
        return null;
    }

    @Override
    public void setVariableType(final Argument newType) {
    }

    @Override
    public List<Cell> getCellsTemporally() {
        List<Cell> cells = new ArrayList<Cell>();

        return cells;
    }

    @Override
    public boolean contains(final Cell c) {
        return false;
    }

    @Override
    public void setSelected(final boolean selected) {
        
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setHidden(final boolean hidden) {
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(final String newName) throws UserWarningException {
        
    }

    @Override
    public void addListener(final VariableListener listener) {
    }

    @Override
    public void removeListener(final VariableListener listener) {
    }
}
