package org.openshapa.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.UserMetrix;
import org.openshapa.models.db.SystemErrorException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import org.openshapa.models.db.VocabElement;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * JTextArea view of the Matrix (database cell) data.
*/
public final class VocabElementRootView extends JTextArea  {

    /** The editors that make up the representation of the data. */
    private Vector<EditorComponent> editors;

    /** The editor tracker responsible for the editor components. */
    private EditorTracker edTracker;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix
            .getInstance(VocabElementRootView.class);

    /** Border to set the text area more aligned to the icons. */
    private static Border GAP_BORDER =
                                   BorderFactory.createEmptyBorder(2, 0, 0, 0);
    /**
     * Creates a new instance of MatrixV.
     *
     * @param cellSelection The parent selection for spreadsheet cells.
     * @param cell The parent datacell for this spreadsheet cell.
     * @param matrix The Matrix holding datavalues that this view label will
     * represent.
     */
    public VocabElementRootView(final VocabElement vocabElement,
                                final VocabElementV pv) {
        super();
        setLineWrap(true);
        setWrapStyleWord(true);
        // just push down a little bit
        setBorder(GAP_BORDER);

        editors = new Vector<EditorComponent>();
        edTracker = new EditorTracker(this, editors);

        setVocabElement(vocabElement, pv);

        this.addFocusListener(edTracker);
        this.addKeyListener(edTracker);
        this.addMouseListener(edTracker);
    }

    /**
     * @return true if a viewport should force the Scrollables width
     * to match its own.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Sets the matrix that this MatrixRootView will represent.
     * @param m The Matrix to display.
     */
    public void setVocabElement(final VocabElement ve, VocabElementV pv) {
        try {
            editors.clear();
            if (editors.size() == 0) {
                editors.addAll(VocabElementEditorFactory.
                                   buildVocabElement(this, ve, pv));
            }
        } catch (SystemErrorException e) {
            logger.error("Unable to set/reset VocabE for VERootView.", e);
        }

        rebuildText();
    }

    /**
     * Recalculates and sets the text to display.
     */
    public void rebuildText() {
        String ans = "";
        for (EditorComponent item : editors) {
            ans += item.getText();
        }
        setText(ans);
    }

    /**
     * Used in the UISpec4j tests.
     * @return The editor tracker for this MatrixRootView.
     */
    public EditorTracker getEdTracker() {
        return edTracker;
    }

    /**
     * Used in VocabElementV.
     * @return the editors.
     */
    public Vector<EditorComponent> getEditors() {
        return editors;
    }
}