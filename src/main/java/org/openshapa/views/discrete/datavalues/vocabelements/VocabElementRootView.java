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
package org.openshapa.views.discrete.datavalues.vocabelements;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import org.openshapa.models.db.Argument;
import org.openshapa.views.discrete.EditorComponent;
import org.openshapa.views.discrete.EditorTracker;

/**
 * JTextArea view of the Matrix (database cell) data.
*/
public final class VocabElementRootView extends JTextArea  {

    /** The editors that make up the representation of the data. */
    private List<EditorComponent> editors;

    /** The editor tracker responsible for the editor components. */
    private EditorTracker edTracker;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(VocabElementRootView.class);

    /** Border to set the text area more aligned to the icons. */
    private static Border GAP_BORDER = BorderFactory.createEmptyBorder(2, 0, 0, 0);
    /**
     * Constructor
     *
     * @param vocabElement The vocab element that this root view is representing.
     * @param pv The parent view that this root view belongs too.
     */
    public VocabElementRootView(final Argument vocabElement,
                                final VocabElementV pv) {
        super();
        setLineWrap(true);
        setWrapStyleWord(true);
        // just push down a little bit
        setBorder(GAP_BORDER);

        editors = new ArrayList<EditorComponent>();
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
     * Sets the argument that this VocabElementRootView will represent.
     *
     * @param ve The new vocab element that his root view will represent.
     * @param pv The parent view for this root view.
     */
    public void setVocabElement(final Argument ve, VocabElementV pv) {
        editors.clear();
        if (editors.isEmpty()) {
            editors.addAll(VocabElementEditorFactory.buildVocabElement(this, ve, pv));
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
     * @return The editor tracker for this MatrixRootView.
     */
    public EditorTracker getEdTracker() {
        return edTracker;
    }

    /**
     * @return The editors used for this root view.
     */
    public List<EditorComponent> getEditors() {
        return editors;
    }
}