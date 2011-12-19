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
package org.openshapa.models.db;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * NominalValue Adapter
 */
@Deprecated
public class DeprecatedMatrixValue extends DeprecatedValue implements MatrixValue {

    private static final Logger LOGGER = UserMetrix.getLogger(DeprecatedMatrixValue.class);

    public DeprecatedMatrixValue(final Database db, final long cellID) {
        super(db, cellID);
    }

    @Override
    public boolean isValid(final String value) {
        return true;
    }

    // Set the first element (it should be rethinking later)
    @Override
    public void set(final String value) {
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);
            Matrix m = dc.getVal();
            NominalDataValue dv = (NominalDataValue) m.getArgCopy(0);
            dv.setItsValue(value);
            m.replaceArg(0, dv);
            dc.setVal(m);
            legacyDB.replaceCell(dc);
        } catch(SystemErrorException se) {
            LOGGER.error("unable to set text data value", se);
        }
    }

    @Override
    public String toString() {
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);

            if (isEmpty()) {
                DataColumn col = legacyDB.getDataColumn(dc.getItsColID());
                return "<" + col.getName() + ">";
            } else {
                Matrix m = dc.getVal();
                NominalDataValue dv = (NominalDataValue) m.getArgCopy(0);
                return dv.getItsValue();
            }

        } catch(SystemErrorException se) {
            LOGGER.error("unable to toString data value", se);
        }

        return "<ERROR - RESTART OPENSHAPA>";
    }

    @Override
    public List<Value> getArguments() {
        List<Value> values = new ArrayList<Value>();
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);
            Matrix m = dc.getVal();
            for (int i=0; i<m.getNumArgs(); i++) {
                NominalDataValue dv = (NominalDataValue) m.getArgCopy(i);
                Value value = new DeprecatedNominalValue(dc.getDB(), dc.getID());
                values.add(value);
            }
        } catch(SystemErrorException se) {
            LOGGER.error("unable to set text data value", se);
        }    
        return values;
    }
}
