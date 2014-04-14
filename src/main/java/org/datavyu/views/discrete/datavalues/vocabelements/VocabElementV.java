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
package org.datavyu.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.Datavyu;
import org.datavyu.models.db.Argument;
import org.datavyu.models.db.Variable;
import org.datavyu.views.VocabEditorV;
import org.datavyu.views.discrete.EditorComponent;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;

/**
 * A view for a vocab element.
 */
public class VocabElementV extends JPanel {

    /**
     * The width of icon to use in the vocab element view.
     */
    private static final int VE_WIDTH = 22;

    /**
     * The height of the icon to use in the vocab element view.
     */
    private static final int VE_HEIGHT = 22;

    /**
     * The dimensions to use for icons in the vocab element view.
     */
    private static final Dimension ICON_SIZE = new Dimension(VE_WIDTH,
            VE_HEIGHT);

    /**
     * The label to use for if this vocab element has changed.
     */
    private JLabel invalidBangIcon;

    /**
     * The label to use for if this vocab element is marked for removal.
     */
    private JLabel deleteIcon;

    /**
     * The rootView of the VocabElement.
     */
    private VocabElementRootView veRootView;

    /**
     * The icon to use for if this vocab element has changed or not.
     */
    private ImageIcon bangIcon;
    
    private String bangIconText;

    /**
     * Is the name currently displayed for this column invalid
     */
    private boolean invalidState;
    
     /**
     * Most recent valid name - the name currently stored in model
     */
    private String lastValidState;

    /**
     * Is this vocab element view marked for removal?
     */
    private boolean deleteVE;

    /**
     * The underlying variable model that this vocab element view represents.
     */
    private Variable varModel;

    /**
     * The parent editor for this vocab element view.
     */
    private VocabEditorV parentEditor;

    /**
     * Border adds a line across and a bit of space between
     */
    private static Border LINE_BORDER =
            BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                    BorderFactory.createEmptyBorder(3, 0, 3, 0));

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(VocabElementV.class);

    /**
     * the light blue colour used for backgrounds
     */
    private static Color lightBlue = new Color(224, 248, 255, 255);
    private static Color lightRed = new Color(255, 200, 200, 255);

    public VocabElementV(Argument vocabArgument, Variable var, VocabEditorV vev) {

        ResourceMap rMap = Application.getInstance(Datavyu.class)
                .getContext()
                .getResourceMap(VocabElementV.class);

        URL iconURL = getClass().getResource(rMap.getString("delta.icon"));
        bangIconText = rMap.getString("delta.tooltip");
        
        bangIcon = new ImageIcon(iconURL);
        deleteVE = false;
        parentEditor = vev;
        varModel = var;

        invalidBangIcon = new JLabel();
        invalidBangIcon.setMaximumSize(ICON_SIZE);
        invalidBangIcon.setMinimumSize(ICON_SIZE);
        invalidBangIcon.setPreferredSize(ICON_SIZE);

        veRootView = new VocabElementRootView(var, this);

        JPanel leftPanel = new JPanel();
        FlowLayout flayout = new FlowLayout(FlowLayout.LEFT, 5, 0);
        leftPanel.setLayout(flayout);
        leftPanel.add(invalidBangIcon);
        veRootView.setOpaque(false);
        veRootView.setBackground(Color.WHITE);
        leftPanel.setOpaque(false);

        veRootView.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (!deleteVE) {
                    setBG(lightBlue);
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (!deleteVE) {
                    setBG(Color.WHITE);
                }
            }
        });

        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.add(leftPanel, BorderLayout.WEST);
        this.add(veRootView, BorderLayout.CENTER);
        this.setBackground(Color.WHITE);
        this.setBorder(LINE_BORDER);

        this.rebuildContents();
    }

    /**
     * @return The parent dialog of this vocab element view.
     */
    public final VocabEditorV getParentDialog() {
        return parentEditor;
    }

    /**
     * Replaces the model used for this vocab element view.
     *
     * @param vocabArgument The new model to use with this view.
     */
    public final void setModel(final Argument vocabArgument) {
        varModel.setRootNode(vocabArgument);
        this.rebuildContents();
    }

    /**
     * Updates the display of the vocab element view by rebuilding its contents.
     */
    public final void rebuildContents() {
        veRootView.setVocabElement(varModel, this);
    }

    
    public final void setInvalid(final boolean invalid, final String lastValidName) {
       invalidState = invalid;
       lastValidState = lastValidName;
       
       if(invalid)
       {
           invalidBangIcon.setIcon(bangIcon);
           invalidBangIcon.setToolTipText(bangIconText);
        }
       else 
       {
           invalidBangIcon.setIcon(null);
           invalidBangIcon.setToolTipText(null);
       }

       parentEditor.refreshNameWarnings();
    }
    
    public boolean getInvalid()
    {
        return invalidState;
    }
    
    public String getLastValid()
    {
        return lastValidState;
    }
    
    public String getCurrentNameDisplay()
    {
        return getNameComponent().getText();
    }

    /**
     * Gets a view for the supplied formal argument.
     *
     * @param fa The argument that we want a view for.
     * @return The view for the supplied formal argument if it exists, null
     * otherwise.
     */
    public final FormalArgEditor getArgumentView(final Argument fa) {
        for (EditorComponent ed : veRootView.getEditors()) {
            if (ed.getClass() == FormalArgEditor.class) {
                FormalArgEditor fArgEd = (FormalArgEditor) ed;

                if (fArgEd.getModel().equals(fa)) {
                    return fArgEd;
                }
            }
        }
        return null;
    }

    /**
     * @return The model (VocabElement) that this view represents.
     */
    public final Argument getModel() {
        return varModel.getRootNode();
    }

    public final Variable getVariable() {
        return varModel;
    }

    /**
     * @return The component for the name of the vocab element.
     */
    public final VENameEditor getNameComponent() {
        for (EditorComponent ed : veRootView.getEditors()) {
            if (ed.getClass() == VENameEditor.class) {
                return (VENameEditor) ed;
            }
        }
        return null;
    }

    /**
     * @return True if this component has focus, false otherwise.
     */
    @Override
    public final boolean hasFocus() {
        return veRootView.hasFocus();
    }

    /**
     * @return The view of the formal argument that has focus, null if no formal
     * argument view in this vocab element has focus.
     */
    public final FormalArgEditor getArgWithFocus() {
        EditorComponent ed = veRootView.getEdTracker()
                .findEditor(veRootView.getCaretPosition());
        if (ed.getClass().equals(FormalArgEditor.class)) {
            return (FormalArgEditor) ed;
        }

        return null;
    }

    /**
     * @param argEd The arg editor to set focus on.
     */
    public final void requestArgFocus(FormalArgEditor argEd) {
        veRootView.getEdTracker().setEditor(argEd);
    }

    /**
     * Sets the deleted flag for this vocab element.
     *
     * @param delete True if this entire vocab element is flagged for deletion,
     *               false otherwise.
     */
    public final void setDeleted(final boolean delete) {
        if (delete) {
            setBG(lightRed);
        } else {
            setBG(Color.WHITE);
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

    /**
     * @return VocabElementRootView vocabElementRootView.
     */
    public final VocabElementRootView getDataView() {
        return veRootView;
    }

    /**
     * @return JLabel change/delta icon.
     */
    public final JLabel getInvalidIcon() {
        return invalidBangIcon;
    }

    public final void setBG(Color col) {
        this.setBackground(col);
    }

    @Override
    public final void requestFocus() {
        veRootView.requestFocus();
    }

    public final void requestFocus(VENameEditor veNEd) {
        veRootView.requestFocus();
        veRootView.getEdTracker().setEditor(veNEd);
    }
}
