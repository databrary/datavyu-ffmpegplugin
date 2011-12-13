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
