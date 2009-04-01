package au.com.nicta.openshapa.views.discrete.datavalues.vocabelements;

import au.com.nicta.openshapa.db.SystemErrorException;
import au.com.nicta.openshapa.db.VocabElement;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author cfreeman
 */
public abstract class VocabElementV extends JPanel implements Cloneable {

    private static final int VE_WIDTH = 16;

    private static final int VE_HEIGHT = 16;

    private static final Dimension ICON_SIZE = new Dimension(VE_WIDTH, VE_HEIGHT);

    private JLabel typeIcon;

    private JLabel deltaIcon;

    private JTextField veNameField;

    private ImageIcon deltaImageIcon;

    boolean hasVEChanged;

    private VocabElement veModel;

    protected VocabElementV(VocabElement vocabElement) {
        URL iconURL = getClass().getResource("/icons/d_16.png");
        deltaImageIcon = new ImageIcon(iconURL);
        hasVEChanged = false;
        veModel = vocabElement;

        deltaIcon = new JLabel();
        deltaIcon.setMaximumSize(ICON_SIZE);
        deltaIcon.setMinimumSize(ICON_SIZE);
        deltaIcon.setPreferredSize(ICON_SIZE);
        //this.add(deltaIcon);

        typeIcon = new JLabel();
        typeIcon.setMaximumSize(ICON_SIZE);
        typeIcon.setMinimumSize(ICON_SIZE);
        typeIcon.setPreferredSize(ICON_SIZE);
        //this.add(typeIcon);

        veNameField = new JTextField();
        veNameField.setBorder(null);

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

    final private void rebuildContents() {
        this.removeAll();
        this.add(deltaIcon);
        this.add(typeIcon);
        
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
            
        }
        this.add(new JLabel(")"));
    }

    @Override
    public abstract Object clone();

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

}
