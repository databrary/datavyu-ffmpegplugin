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
