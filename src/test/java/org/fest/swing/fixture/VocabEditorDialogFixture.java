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

import java.util.ArrayList;
import java.util.List;

import org.fest.swing.core.Robot;

import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;


/**
 * Fixture for the Vocab Editor dialog.
 */
public class VocabEditorDialogFixture extends DialogFixture {

    /** Underlying VocavEditor class. */
    private final VocabEditorV veDialog;

    /**
     * Constructor.
     * @param robot main frame fixture robot.
     * @param target underlying vocab editor class
     */
    public VocabEditorDialogFixture(final Robot robot,
        final VocabEditorV target) {
        super(robot, target);
        veDialog = (VocabEditorV) this.target;
    }

    /**
     * Returns VocabElement Fixture by vocab element name.
     * @param elementName vocab element name
     * @return VocabElementFixture with name, or null if not found
     */
    public final VocabElementFixture vocabElement(final String elementName) {
        List<VocabElementV> vocEls = veDialog.getVocabElements();

        for (VocabElementV v : vocEls) {
            String vocName = v.getDataView().getEditors().elementAt(0).getText();

            if (vocName.equalsIgnoreCase(elementName)) {
                return new VocabElementFixture(robot, v);
            }
        }

        return null;
    }

    /**
     * Returns a vector of all VocabElement Fixtures.
     * @return Vector of all VocabElements
     */
    public final List<VocabElementFixture> allVocabElements() {
        List<VocabElementV> vocEls = veDialog.getVocabElements();
        ArrayList<VocabElementFixture> result = new ArrayList<VocabElementFixture>();

        for (VocabElementV v : vocEls) {
            result.add(new VocabElementFixture(robot, v));
        }

        return result;
    }

    /**
     * Number of vocab elements.
     * @return number of vocab elements.
     */
    public final int numOfVocabElements() {
        return veDialog.getVocabElements().size();
    }

    /**
     * JButtonFixture for addPredicate button.
     * @return JButtonFixture for addPredicate button.
     */
    public final JButtonFixture addPredicateButton() {
        return new JButtonFixture(robot, "addPredicateButton");
    }

    /**
    * JButtonFixture for addMatrix button.
    * @return JButtonFixture for addMatrix button.
    */
    public final JButtonFixture addMatrixButton() {
        return new JButtonFixture(robot, "addMatrixButton");
    }

    /**
    * JButtonFixture for moveArgLeft button.
    * @return JButtonFixture for moveArgLeft button.
    */
    public final JButtonFixture moveArgLeftButton() {
        return new JButtonFixture(robot, "moveArgLeftButton");
    }

    /**
     * JButtonFixture for moveArgRight button.
     * @return JButtonFixture for moveArgLeft button.
     */
    public final JButtonFixture moveArgRightButton() {
        return new JButtonFixture(robot, "moveArgRightButton");
    }

    /**
     * JButtonFixture for addArgument button.
     * @return JButtonFixture for addArgument button.
     */
    public final JButtonFixture addArgButton() {
        return new JButtonFixture(robot, "addArgButton");
    }

    /**
     * JButtonFixture for delete button.
     * @return JButtonFixture for delete button.
     */
    public final JButtonFixture deleteButton() {
        return new JButtonFixture(robot, "deleteButton");
    }

    /**
     * JButtonFixture for revert button.
     * @return JButtonFixture for revert button.
     */
    public final JButtonFixture revertButton() {
        return new JButtonFixture(robot, "revertButton");
    }

    /**
     * JButtonFixture for apply button.
     * @return JButtonFixture for apply button.
     */
    public final JButtonFixture applyButton() {
        return new JButtonFixture(robot, "applyButton");
    }

    /**
     * JButtonFixture for ok button.
     * @return JButtonFixture for ok button.
     */
    public final JButtonFixture okButton() {
        return new JButtonFixture(robot, "okButton");
    }

    /**
     * JButtonFixture for close button.
     * @return JButtonFixture for close button.
     */
    public final JButtonFixture closeButton() {
        return new JButtonFixture(robot, "closeButton");
    }

    /**
     * @return JComboBoxFixture for the argument types.
     */
    public final JComboBoxFixture argTypeComboBox() {
        return new JComboBoxFixture(robot, "argTypeComboBox");
    }

}
