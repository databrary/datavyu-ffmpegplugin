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

import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.text.BadLocationException;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;

import org.openshapa.util.UIUtils;

import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;


/**
 * Fixture for a single Vocab Element.
 */
public class VocabElementFixture extends JPanelFixture {

    /**
     * Constructor.
     * @param robot mainframefixture robot
     * @param panelName name of panel
     */
    public VocabElementFixture(final Robot robot, final String panelName) {
        super(robot, panelName);
    }

    /**
     * Constructor.
     * @param robot mainframefixture robot
     * @param target VocabElementV of vocabe element
     */
    public VocabElementFixture(final Robot robot, final VocabElementV target) {
        super(robot, target);
    }

    /**
     * deltaIcon label - either null or not null.
     * @return deltaIcon label
     */
    public JLabelFixture deltaIcon() {
        return new JLabelFixture(robot, findByName("deltaIcon", JLabel.class));
    }

    /**
     * deleteIcon label - either null or not null.
     * @return deleteIcon label
     */
    public JLabelFixture deleteIcon() {
        return new JLabelFixture(robot, findByName("deleteIcon", JLabel.class));
    }

    /**
     * typeIcon label - either null or not null.
     * @return typeIcon label
     */
    public JLabelFixture typeIcon() {
        return new JLabelFixture(robot, findByName("typeIcon", JLabel.class));
    }

    public JTextComponentFixture value() {
        return new JTextComponentFixture(robot,
                ((VocabElementV) target).getDataView());
    }

    /**
     * Returns the Vocab Element name.
     * @return vocab element name
     */
    public final String getVEName() {
        return ((VocabElementV) target).getDataView().getEditors().get(0).getText();
    }

    /**
    * Returns argument name.
    * @param arg argument number
    * @return argument name
    */
    public final String getArgument(final int arg) {
        int editorNum = (arg * 6) + 3;

        return ((VocabElementV) target).getDataView().getEditors().get(editorNum).getText();
    }

    /**
     * Returns the character position of arg.
     * @param arg argument number
     * @return position of first argument character
     */
    public final int getArgStartIndex(final int arg) {
        int argPos = 0;

        for (int i = 0; i <= arg; i++) {
            argPos = ((VocabElementV) target).getDataView().getText().indexOf(
                    "<", argPos + 1);
        }

        return argPos + 1;
    }

    /**
     * Enters text in a particular argument.
     * @param arg argument number
     * @param text text to input
     * @throws BadLocationException on bad location exception
     */
    public void enterTextInArg(final int arg, final String text)
        throws BadLocationException {
        clickToCharPos(getArgStartIndex(arg), 1);
        value().enterText(text);
    }

    /**
     * Replaces text in a particular argument.
     * @param arg argument number
     * @param text text to input
     * @throws BadLocationException on bad location exception
     */
    public void replaceTextInArg(final int arg, final String text)
        throws BadLocationException {
        clickToCharPos(getArgStartIndex(arg), 2);
        value().enterText(text);
    }

    /**
    * Selects text from start position to end position.
    * @param startPos start postion (0 is beginning)
    * @param endPos end position
    * @throws BadLocationException on bad location exception
    */
    public void select(int startPos, int endPos) throws BadLocationException {
        Point startPoint = UIUtils.centerOf(((VocabElementV) target)
                .getDataView().modelToView(startPos));
        Point endPoint = UIUtils.centerOf(((VocabElementV) target).getDataView()
                .modelToView(endPos));

        // First line is required to get focus on component
        robot.click(((VocabElementV) target).getDataView(), endPoint);

        // Click on start point and hold mouse
        robot.moveMouse(((VocabElementV) target).getDataView(), startPoint);
        robot.pressMouse(((VocabElementV) target).getDataView(), startPoint,
            MouseButton.LEFT_BUTTON);

        // Drag to end pos and release mouse
        robot.moveMouse(((VocabElementV) target).getDataView(), endPoint);
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    /**
     * Clicks on a particular character position a particular number of times.
     * @param charPos character position
     * @param times number of times to click
     * @throws BadLocationException on bad location exception
     */
    public void clickToCharPos(int charPos, int times)
        throws BadLocationException {
        Point charPoint = UIUtils.centerOf(((VocabElementV) target)
                .getDataView().modelToView(charPos));

        for (int i = 0; i < times; i++) {
            robot.click(((VocabElementV) target).getDataView(), charPoint);
        }
    }
}
