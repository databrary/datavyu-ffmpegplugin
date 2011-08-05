/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.models.db.legacy;

import java.util.Vector;

/**
 *
 * @author harold
 */
public class DataColumnTO {
    /** column name */
    public String name; 
    /** Type of associated matrix VE */
    public MatrixVocabElement.MatrixType itsMveType;
    /** Column's cells */
    public Vector<DataCellTO> dataCellsTO; 
    
    public DataColumnTO(DataColumn col) {
        this.name = col.getName();
        this.itsMveType = col.getItsMveType();
        this.dataCellsTO = new Vector<DataCellTO>();
        Vector<DataCell> cells = col.getItsCells();
        if (cells != null) {
            for (DataCell cell : cells) {
                DataCellTO cellTO = new DataCellTO(cell);
                this.dataCellsTO.add(cellTO);
            }
        }    
    }
}
