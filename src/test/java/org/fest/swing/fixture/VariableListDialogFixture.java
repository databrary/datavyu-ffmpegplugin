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
package org.fest.swing.fixture;

import org.fest.swing.core.Robot;

import org.openshapa.views.VariableListV;


/**
 * Fixture for the VariableList dialog.
 */
public class VariableListDialogFixture extends DialogFixture {

    /** Underlying VariableList class. */
    private final VariableListV vldialog;

    /**
     * Constructor.
     * @param robot main frame fixture robot.
     * @param target underlying variable list class
     */
    public VariableListDialogFixture(final Robot robot,
        final VariableListV target) {
        super(robot, target);
        vldialog = (VariableListV) this.target;
    }

    /**
     * @return JTable with all the variables details - visibility, name, type, 
     * comment.
     */
    public final JTableFixture getVariableListTable() {
        return new JTableFixture(robot, "variableList");
    }   
}
