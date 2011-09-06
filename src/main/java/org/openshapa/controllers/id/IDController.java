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
package org.openshapa.controllers.id;

import org.openshapa.models.id.ID;
import org.openshapa.models.id.Identifier;


/**
 * Controller for generating {@link Identifier} objects.
 */
public enum IDController {

    INSTANCE;

    /** Sequence number. */
    private long sn;

    private IDController() {
        sn = 1;
    }

    private Identifier makeID() {
        return new ID(sn++);
    }

    public static synchronized Identifier generateIdentifier() {
        return INSTANCE.makeID();
    }

}
