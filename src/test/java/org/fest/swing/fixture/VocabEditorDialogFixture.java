package org.fest.swing.fixture;

import java.util.Vector;
import org.fest.swing.core.Robot;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.SpreadsheetColumn;
import org.openshapa.views.discrete.datavalues.vocabelements.VocabElementV;

public class VocabEditorDialogFixture extends DialogFixture {

    VocabEditorV veDialog;

    public VocabEditorDialogFixture(Robot robot, VocabEditorV target) {
        super(robot, target);
        veDialog = target;
    }

    public VocabElementFixture vocabElement(String elementName) {
        Vector<VocabElementV> vocEls = veDialog.getVocabElements();

        for (VocabElementV v : vocEls) {
            String vocName = v.getDataView().getEditors().elementAt(0).getText();
            if (vocName.equalsIgnoreCase(elementName)) {
                return new VocabElementFixture(robot, v);
            }
        }
        return null;
    }

    public Vector<VocabElementFixture> allVocabElements() {
        Vector<VocabElementV> vocEls = veDialog.getVocabElements();
        Vector<VocabElementFixture> result = new Vector<VocabElementFixture>();

        for(VocabElementV v : vocEls) {
            result.add(new VocabElementFixture(robot, v));
        }
        return result;
    }

    public int numOfVocabElements() {
        return veDialog.getVocabElements().size();
    }
    
    public JButtonFixture addPredicateButton() {
        return new JButtonFixture(robot, "addPredicateButton");
    }
    
    public JButtonFixture addMatrixButton() {
        return new JButtonFixture(robot, "addMatrixButton");
    }
    
    public JButtonFixture moveArgLeftButton() {
        return new JButtonFixture(robot, "moveArgLeftButton");
    }
    public JButtonFixture moveArgRightButton() {
        return new JButtonFixture(robot, "moveArgRightButton");
    }
    
    public JButtonFixture addArgButton() {
        return new JButtonFixture(robot, "addArgButton");
    }
    
    public JButtonFixture deleteButton() {
        return new JButtonFixture(robot, "deleteButton");
    }
    
    public JButtonFixture revertButton() {
        return new JButtonFixture(robot, "revertButton");
    }
    
    public JButtonFixture applyButton() {
        return new JButtonFixture(robot, "applyButton");
    }
    
    public JButtonFixture okButton() {
        return new JButtonFixture(robot, "okButton");
    }
    
    public JButtonFixture closeButton() {
        return new JButtonFixture(robot, "closeButton");
    }

}
