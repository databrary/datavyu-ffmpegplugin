package au.com.nicta.openshapa.views.discrete;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Manages clicking around a group of "Selectable" objects.
 * @author swhitcher
 */
public class Selector {

    /** Holder for the selected items. */
    private Vector<Selectable> selection;

    /** Selector constructor. */
    public Selector() {
        selection = new Vector<Selectable>();
    }

    /** Deselect all selected items. */
    public final void deselectAll() {
        for (int i = 0; i < selection.size(); i++) {
            Selectable item = (Selectable) selection.get(i);
            item.setSelected(false);
        }
        selection.clear();
    }

    /**
     * Remove an item from the selection.
     * @param item Item to remove.
     */
    private final void removeSelection(final Selectable item) {
        item.setSelected(false);

        selection.remove(item);
    }

    /**
     * Add an item from the selection.
     * @param item Item to add.
     */
    private final void addSelection(final Selectable item) {
        item.setSelected(true);

        selection.add(item);
    }

    /**
     * Toggle the selected state of an item.
     * @param item Item to change select state.
     */
    private final void toggleSelection(final Selectable item) {
        if (item.isSelected()) {
            removeSelection(item);
        } else {
            addSelection(item);
        }
    }

    /**
     * Invoked when the mouse is clicked in a cell.
     * @param me event detail
     */
    public final void addToSelection(final MouseEvent me, Selectable s) {
        int mod = me.getModifiers();
        if ((mod & ActionEvent.SHIFT_MASK) != 0
                || (mod & ActionEvent.CTRL_MASK) != 0) {
            // Simple answer for now - toggle the selection
            toggleSelection(s);
        } else {
            deselectAll();
            addSelection(s);
        }
    }
}
