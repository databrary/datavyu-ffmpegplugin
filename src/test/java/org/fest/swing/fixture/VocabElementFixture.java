package org.fest.swing.fixture;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;

public class VocabElementFixture extends JPanelFixture {
    private VocabElementV ve;

    public VocabElementFixture(Robot robot, String panelName) {
        super(robot, panelName);
    }

    public VocabElementFixture(Robot robot, VocabElementV target) {
        super(robot, target);
        ve = (VocabElementV)this.target;
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
        return new JTextComponentFixture(robot, ve.getDataView());
    }

    /**
     * Returns the Vocab Element name.
     * @return vocab element name
     */
    public final String getVEName() {
        return ve.getDataView().getEditors().elementAt(0).getText();
    }

     /**
     * Returns argument name.
     * @param arg argument number
     * @return argument name
     */
    public final String getArgument(final int arg) {
        int editorNum = arg * 6 + 3;
        return ve.getDataView().getEditors().elementAt(editorNum).getText();
    }

    /**
     * Returns the character position of arg.
     * @param arg argument number
     * @return position of first argument character
     */
    public final int getArgStartIndex(final int arg) {
        int argPos = 0;
        for (int i = 0; i <= arg; i++) {
            argPos = ve.getDataView().getText().indexOf("<", argPos + 1);
        }
        return argPos + 1;
    }

    public void enterTextInArg(final int arg, final String text)
            throws BadLocationException {
        clickToCharPos(getArgStartIndex(arg), 1);
        value().enterText(text);
    }

    public void replaceTextInArg(final int arg, final String text)
            throws BadLocationException {
        clickToCharPos(getArgStartIndex(arg), 2);
        value().enterText(text);
    }

    public void select(int startPos, int endPos) throws BadLocationException {
        Point startPoint = centerOf(ve.getDataView().modelToView(startPos));
        Point endPoint = centerOf(ve.getDataView().modelToView(endPos));
        
        //First line is required to get focus on component
        robot.click(ve.getDataView(), endPoint);
        //Click on start point and hold mouse
        robot.moveMouse(ve.getDataView(), startPoint);
        robot.pressMouse(ve.getDataView(), startPoint, MouseButton.LEFT_BUTTON);
        //Drag to end pos and release mouse
        robot.moveMouse(ve.getDataView(), endPoint);       
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
    }

    public void clickToCharPos(int charPos, int times)
            throws BadLocationException {
        Point charPoint = centerOf(ve.getDataView().modelToView(charPos));
        for (int i = 0; i < times; i++) {
            robot.click(ve.getDataView(), charPoint);
        }
    }

    private static Point centerOf(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }


}
