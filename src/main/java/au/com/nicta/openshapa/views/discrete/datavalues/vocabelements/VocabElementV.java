package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.VocabElement;
import au.com.nicta.openshapa.views.VocabEditorV;
import au.com.nicta.openshapa.views.discrete.Editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author cfreeman
 */
public abstract class VocabElementV extends JPanel implements KeyListener {

    private static final int VE_WIDTH = 16;

    private static final int VE_HEIGHT = 16;

    private static final Dimension ICON_SIZE = new Dimension(VE_WIDTH,
                                                             VE_HEIGHT);

    private JLabel typeIcon;

    private JLabel deltaIcon;

    private Editor veNameField;

    private ImageIcon deltaImageIcon;

    boolean hasVEChanged;

    private VocabElement veModel;

    private VocabEditorV parentEditor;

    private static Logger logger = Logger.getLogger(VocabElementV.class);

    protected VocabElementV(VocabElement vocabElement, VocabEditorV vev) {
        URL iconURL = getClass().getResource("/icons/d_16.png");
        deltaImageIcon = new ImageIcon(iconURL);
        hasVEChanged = false;
        veModel = vocabElement;
        parentEditor = vev;

        deltaIcon = new JLabel();
        deltaIcon.setMaximumSize(ICON_SIZE);
        deltaIcon.setMinimumSize(ICON_SIZE);
        deltaIcon.setPreferredSize(ICON_SIZE);

        typeIcon = new JLabel();
        typeIcon.setMaximumSize(ICON_SIZE);
        typeIcon.setMinimumSize(ICON_SIZE);
        typeIcon.setPreferredSize(ICON_SIZE);

        veNameField = new VocabElementNameV();
        veNameField.setBorder(null);
        veNameField.addKeyListener(this);

        this.setBackground(Color.WHITE);
        ((FlowLayout) this.getLayout()).setAlignment(FlowLayout.LEFT);
        this.setMaximumSize(new Dimension(50000, VE_HEIGHT));

        this.rebuildContents();
    }

    protected VocabElementV(VocabElementV vocabElementV) {
        typeIcon = vocabElementV.typeIcon;
        deltaIcon = vocabElementV.deltaIcon;
        veNameField = vocabElementV.veNameField;
        deltaImageIcon = vocabElementV.deltaImageIcon;
        hasVEChanged = vocabElementV.hasVEChanged;
        veModel = vocabElementV.veModel;
    }

    final protected void setTypeIcon(final ImageIcon newIcon) {
        this.typeIcon.setIcon(newIcon);
    }

    final protected void rebuildContents() {
        this.removeAll();
        this.add(deltaIcon);
        this.add(typeIcon);
        this.parentEditor.updateDialogState();

        boolean hasFocus = veNameField.hasFocus();
        veNameField.storeCaretPosition();
        veNameField.setText(veModel.getName());

        this.add(veNameField);
        this.add(new JLabel("("));
        try {
            for (int i = 0; i < veModel.getNumFormalArgs(); i++) {
                this.add(new JLabel("<"));
                this.add(new FormalArgumentV(veModel.getFormalArg(i)));
                this.add(new JLabel(">"));

                if (i < veModel.getNumFormalArgs()) {
                    this.add(new JLabel(","));
                }
            }
        } catch (SystemErrorException e) {
            logger.error("unable to rebuild contents.", e);
        }
        this.add(new JLabel(")"));
        validate();

        // Maintain focus after draw.
        if (hasFocus) {
            veNameField.requestFocus();
            veNameField.restoreCaretPosition();
        }        
    }

    final public void setHasChanged(boolean hasChanged) {
        if (hasChanged) {
            deltaIcon.setIcon(deltaImageIcon);
        } else {
            deltaIcon.setIcon(null);
        }

        hasVEChanged = hasChanged;
    }

    final public boolean hasChanged() {
        return hasVEChanged;
    }

    final public VocabElement getVocabElement() {
        return veModel;
    }

    final public Editor getNameComponent() {
        return this.veNameField;
    }

    final public void keyPressed(KeyEvent e) {
        
    }

    final public void keyReleased(KeyEvent e) {
        
    }
}
