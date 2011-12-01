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
import database.DataCell;
import database.DataValue;
import database.Database;
import database.Matrix;
import database.SystemErrorException;

/**
 * Value Adapter
 */
@Deprecated
public abstract class DeprecatedValue implements Value {
    
    private static final Logger LOGGER = UserMetrix.getLogger(DeprecatedValue.class);
    
    protected Database legacyDB;
    
    protected long legacyCellID;
    
    public DeprecatedValue(final Database db, final long cellID) {
        legacyDB = db;
        legacyCellID = cellID;
    }

    @Override
    public void clear() {
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);
            Matrix m = dc.getVal();
            DataValue dv = m.getArgCopy(0);
            dv.clearValue();
            m.replaceArg(0, dv);
            dc.setVal(m);
            legacyDB.replaceCell(dc);
        } catch(SystemErrorException se) {
            LOGGER.error("unable to clear text data value", se);
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            DataCell dc = (DataCell) legacyDB.getCell(legacyCellID);
            return dc.getVal().getArgCopy(0).isEmpty();
        } catch (SystemErrorException se) {
            LOGGER.error("unable to determine if data value is empty", se);
        }

        return false;
    }
}
