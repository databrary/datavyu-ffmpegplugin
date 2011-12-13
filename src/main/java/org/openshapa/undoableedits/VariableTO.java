/*
 * Copyright (c) 2011 OpenSHAPA Foundation, http://openshapa.org
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
package org.openshapa.undoableedits;

import java.util.ArrayList;
import java.util.List;
import org.openshapa.models.db.Cell;
import org.openshapa.models.db.Variable;

/**
 * Variable Transfer Object for holding changes that need to be transferred
 * from undo/redo states to the datastore.
 */
public final class VariableTO {

    private List<CellTO> cellTOs;

    private String name;

    private Variable.type type;

    private int variablePosition;

    /**
     * Constructor.
     *
     * @param var The variable we are creating a transfer object for.
     * @param varPosition The position of the variable in the spreadsheet.
     */
    public VariableTO(final Variable var, final int varPosition) {
        name = var.getName();
        type = var.getVariableType();
        cellTOs = new ArrayList<CellTO>();
        variablePosition = varPosition;

        for(Cell c : var.getCells()) {
            cellTOs.add(new CellTO(c, var));
        }
    }

    /**
     * @return The list of Cell Transfer Objects that belong to this variable.
     */
    public List<CellTO> getTOCells() {
        return cellTOs;
    }

    /**
     * @return The name of variable
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type of the VariableTO.
     */
    public Variable.type getType() {
        return type;
    }

    /**
     * @return The position of the variable in the spreadsheet.
     */
    public int getPosition() {
        return variablePosition;
    }
}
