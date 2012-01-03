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

/**
 * TextValue Adapter
 */
@Deprecated
public class DeprecatedTextValue extends DeprecatedValue implements TextValue {

    private static final Logger LOGGER = UserMetrix.getLogger(DeprecatedTextValue.class);

    public DeprecatedTextValue(final Database db, final long cellID) {
        super(db, cellID);
    }

    @Override
    public boolean isValid(final String value) {
        // I Think I might want to deprecated this.
        return true;
    }

    @Override
    public void set(final String value) {
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);
            Matrix m = dc.getVal();
            TextStringDataValue dv = (TextStringDataValue) m.getArgCopy(0);
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
                TextStringDataValue dv = (TextStringDataValue) m.getArgCopy(0);
                return dv.getItsValue();
            }

        } catch(SystemErrorException se) {
            LOGGER.error("unable to toString data value", se);
        }

        return "<ERROR - RESTART OPENSHAPA>";
    }
}
