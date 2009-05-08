package org.openshapa.views.discrete.datavalues.vocabelements;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.SystemErrorException;
import org.openshapa.db.VocabElement;
import org.openshapa.views.VocabEditorV;
import org.openshapa.views.discrete.Editor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * A view for a vocab element.
 *
 * @author cfreeman
 */
public abstract class VocabElementV extends JPanel
implements KeyListener {

    /** The width of icon to use in the vocab element view. */
    private static final int VE_WIDTH = 22;

    /** The height of the icon to use in the vocab element view. */
    private static final int VE_HEIGHT = 22;

    /** The dimensions to use for icons in the vocab element view. */
    private static final Dimension ICON_SIZE = new Dimension(VE_WIDTH,
                                                             VE_HEIGHT);

    /** The label to use for the type of vocab element. */
    private JLabel typeIcon;

    /** The label to use for if this vocab element has changed. */
    private JLabel deltaIcon;

    /** The label to use for if this vocab element is marked for removal. */
    private JLabel deleteIcon;

    /** The field containing the name of the vocab element. */
    private Editor veNameField;

    /** The icon to use for if this vocab element has changed or not. */
    private ImageIcon deltaImageIcon;

    /** Has this vocab element changed or not? */
    private boolean hasVEChanged;

    /** Is this vocab element view marked for removal? */
    private boolean deleteVE;

    /** The underlying model that this vocab element view represents. */
    private VocabElement veModel;

    /** The collection of argument views. */
    private Vector<FormalArgumentV> argViews;

    /** The parent editor for this vocab element view. */
    private VocabEditorV parentEditor;

    /** The error logger for this class. */
    private static Logger logger = Logger.getLogger(VocabElementV.class);

    protected VocabElementV(VocabElement vocabElement, VocabEditorV vev) {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(VocabElementV.class);

        URL iconURL = getClass().getResource(rMap.getString("delta.icon"));
        deltaImageIcon = new ImageIcon(iconURL);
        hasVEChanged = false;
        deleteVE = false;
        veModel = vocabElement;
        parentEditor = vev;
        argViews = new Vector<FormalArgumentV>();

        deltaIcon = new JLabel();
        deltaIcon.setMaximumSize(ICON_SIZE);
        deltaIcon.setMinimumSize(ICON_SIZE);
        deltaIcon.setPreferredSize(ICON_SIZE);
        deltaIcon.setToolTipText(rMap.getString("delta.tooltip"));

        typeIcon = new JLabel();
        typeIcon.setMaximumSize(ICON_SIZE);
        typeIcon.setMinimumSize(ICON_SIZE);
        typeIcon.setPreferredSize(ICON_SIZE);
        typeIcon.setToolTipText(rMap.getString("type.tooltip"));

        deleteIcon = new JLabel();
        deleteIcon.setMaximumSize(ICON_SIZE);
        deleteIcon.setMinimumSize(ICON_SIZE);
        deleteIcon.setPreferredSize(ICON_SIZE);

        veNameField = new VocabElementNameV(vev);
        veNameField.setBorder(null);
        veNameField.addKeyListener(this);
        veNameField.setToolTipText(rMap.getString("name.tooltip"));

        this.setBackground(Color.WHITE);
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        this.setLayout(layout);
        this.setMaximumSize(new Dimension(50000, VE_HEIGHT));

        this.rebuildContents();
    }

    /**
     * Replaces the model used for this vocab element view.
     *
     * @param vocabElement The new model to use with this view.
     */
    public final void setModel(final VocabElement vocabElement) {
        veModel = vocabElement;
        this.rebuildContents();
    }

    /**
     * Replaces the type icon with the supplied parameter.
     *
     * @param newIcon The new icon to use for the type of vocab element.
     */
    protected final void setTypeIcon(final ImageIcon newIcon) {
        this.typeIcon.setIcon(newIcon);
    }

    /**
     * Updates the display of the vocab element view by rebuilding its contents.
     */
    public final void rebuildContents() {
        this.removeAll();
        this.add(deltaIcon);
        this.add(deleteIcon);
        this.add(typeIcon);
        this.parentEditor.updateDialogState();

        boolean hasFocus = veNameField.hasFocus();
        veNameField.storeCaretPosition();
        veNameField.setText(veModel.getName());

        FormalArgument focusedArg = null;

        this.add(veNameField);

        this.add(new JLabel("("));

        try {
            for (int i = 0; i < argViews.size(); i++) {
                FormalArgumentV view = argViews.get(0);
                if (view.hasFocus()) {
                    focusedArg = view.getModel();
                }

                argViews.remove(0);
            }

            for (int i = 0; i < veModel.getNumFormalArgs(); i++) {

                if (i > 0) {
                    this.add(new JLabel(", "));
                }

                this.add(new JLabel("<"));

                FormalArgumentV fargV = new FormalArgumentV(
                                veModel.getFormalArg(i), i, this, parentEditor);
                this.argViews.add(fargV);
                this.add(fargV);
                this.add(new JLabel(">"));
            }

            if (veModel.getVarLen()) {
                if (veModel.getNumFormalArgs() > 0) {
                    this.add(new JLabel(", "));
                }

                this.add(new JLabel("..."));
            }
        } catch (SystemErrorException e) {
            logger.error("unable to rebuild contents.", e);
        }

        this.add(new JLabel(")"));

        // Redraw the component (to clear anything underlying).
        repaint();
        validate();

        // Maintain focus after draw.
        if (hasFocus && focusedArg == null) {
            veNameField.requestFocus();
        } else if (focusedArg != null) {
            getArgumentView(focusedArg).requestFocus();
        }

    }

    /**
     * Updates the vocab element view with a visual representation of if the
     * vocab element has changed or not.
     *
     * @param hasChanged Has the vocab element changed or not; true if yes,
     * false otherwise.
     */
    public final void setHasChanged(final boolean hasChanged) {
        if (hasChanged) {
            deltaIcon.setIcon(deltaImageIcon);
        } else {
            deltaIcon.setIcon(null);
        }

        hasVEChanged = hasChanged;
    }

    /**
     * @return Has the vocab element changed or not; true if yes, false
     * otherwise.
     */
    public final boolean hasChanged() {
        return hasVEChanged;
    }

    /**
     * Gets a view for the supplied formal argument.
     *
     * @param fa The formal argument for which we want a view for.
     * @return The view for the supplied formal argument if it exists, null
     * otherwise.
     */
    public final FormalArgumentV getArgumentView(final FormalArgument fa) {
        for (FormalArgumentV view : argViews) {
            if (view.getModel().equals(fa)) {
                return view;
            }
        }

        return null;
    }

    /**
     * @return The model (VocabElement) that this view represents.
     */
    public final VocabElement getModel() {
        return veModel;
    }

    /**
     * @return The component for the name of the vocab element.
     */
    public final Editor getNameComponent() {
        return this.veNameField;
    }

    /**
     * The action to invoke when the user presses a key.
     *
     * @param e The event that triggered this action.
     */
    public final void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                // Ignore - handled when the key is typed.
                e.consume();
                break;
            default:
                break;
        }
    }

    /**
     * The action to invoke when the user releases a key.
     *
     * @param e The event that triggered this action.
     */
    public final void keyReleased(final KeyEvent e) {
        // Ignore key release
    }

    /**
     * @return True if this component has focus, false otherwise.
     */
    @Override
    public final boolean hasFocus() {
        if (this.veNameField.hasFocus()) {
            return true;
        } else {
            for (FormalArgumentV view : argViews) {
                if (view.hasFocus()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return The view of the formal argument that has focus, null if no formal
     * argument view in this vocab element has focus.
     */
    public final FormalArgumentV getArgWithFocus() {
        for (FormalArgumentV view : argViews) {
            if (view.hasFocus()) {
                return view;
            }
        }

        return null;
    }

    /**
     * Sets the deleted flag for this vocab element.
     *
     * @param delete True if this entire vocab element is flagged for deletion,
     * false otherwise.
     */
    public final void setDeleted(final boolean delete) {
        if (delete) {
            deleteIcon.setText("D");
        } else {
            deleteIcon.setText(null);
        }
        deleteVE = delete;

        this.rebuildContents();
    }

    /**
     * @return True if this entire vocab element is flagged for deletion, false
     * otherwise.
     */
    public final boolean isDeletable() {
        return deleteVE;
    }
}
