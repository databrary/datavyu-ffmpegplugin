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
