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
package org.openshapa.jackrabbit;

import database.DataCellTO;
import database.DataColumnTO;
import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Simple Pojo used to store information on a variable.
 *
 * Note : the path field is not mandatory because an Author
 * is an aggregation of a PressRelease.
 *
 */
@Node
public class VariableOCM {

    @Field(path = true)
    String path;
    @Field
    private String name;
    @Collection
    List<CellOCM> cells;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CellOCM> getCells() {
        return cells;
    }

    public void setCells(List<CellOCM> cells) {
        this.cells = cells;
    }

    /**
     * no arg constructor required by Jackrabbit OCM
     */
    public VariableOCM() {
        super();
    }
    
    public VariableOCM(DataColumnTO colTO) {
        super();
        path = colTO.name;
        name = colTO.name;
        cells = new ArrayList<CellOCM>();
        int ord = 0;
        for (DataCellTO cellTO : colTO.dataCellsTO) {                 
            CellOCM cell = new CellOCM(cellTO, ord);          
            cells.add(cell);
            ord++;
        }         
    }    

}
