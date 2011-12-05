package org.openshapa.jackrabbit;

import database.DataCellTO;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Simple Pojo used to store information on a cell.
 *
 */
@Node
public class CellOCM {

    @Field(path = true)
    String path;
    @Field
    String onset;
    @Field
    String offset;
    @Field
    String val;

    /**
     * no arg constructor required by the OCM
     */
    public CellOCM() {
        super();
    }
  
    public CellOCM(DataCellTO cellTO, int ord) {
        super();
        path = Integer.toString(ord++);
        onset = cellTO.onset.toHMSFString();
        offset = cellTO.offset.toHMSFString();
        val = cellTO.argListToString();        
    }    


    public CellOCM(String path, String onset, String offset, String val) {
        super();
        this.path = path;
        this.onset = onset;
        this.offset = offset;
        this.val = val;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
