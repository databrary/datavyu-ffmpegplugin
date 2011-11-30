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
import database.Database;

/**
 * NominalValue Adapter
 */
@Deprecated
public class DeprecatedNominalValue implements NominalValue {
    
    private static final Logger LOGGER = UserMetrix.getLogger(DeprecatedNominalValue.class);
    
    Database legacyDB;
    
    private long legacyCellID;
    
    public DeprecatedNominalValue(final Database db, final long cellID) {
        legacyDB = db;
        legacyCellID = cellID;
    }

    @Override
    public boolean isValid(final String value) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void set(final String value) {
        
    }
    
    @Override
    public String toString() {
        // Must override toString in such a way that when isEmpty == true, toString
        // returns a valid empty value i.e. "<argName>"

        return "null";
    }
}
