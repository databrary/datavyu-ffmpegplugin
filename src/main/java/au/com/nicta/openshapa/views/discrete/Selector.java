package au.com.nicta.openshapa.views.discrete;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Manages clicking around a group of "Selectable" objects.
 * @author swhitcher
 */
public class Selector {

    /** The parent component for the selection that this class represents. */
    private Component selectionParent;

    /** Holder for the selected items. */
    private Vector<Selectable> selection;

    /** Holder for other selectors. */
    private Vector<Selector> otherSelectors;

    /**
     * Constructor.
     *
     * @param parentOfSelection The parent component of the things we are
     * selecting.
     */
    public Selector(Component parentOfSelection) {
        selectionParent = parentOfSelection;
        selection = new Vector<Selectable>();
        otherSelectors = new Vector<Selector>();
    }

    /**
     * Add other Selectors.
     *
     * @param sel Selector to add.
     */
    public final void addOther(final Selector sel) {
        otherSelectors.add(sel);
    }

    /**
     * Deselect other selectors.
     */
    public void deselectOthers() {
        for (int i = 0; i < otherSelectors.size(); i++) {
            Selector other = (Selector) otherSelectors.get(i);
            other.deselectAll();
        }
    }

    /**
     * Deselect all selected items.
     */
    public final void deselectAll() {
        for (int i = 0; i < selection.size(); i++) {
            Selectable item = (Selectable) selection.get(i);
            item.setSelected(false);
        }
        selection.clear();
    }

    /**
     * Remove an item from the selection.
     *
     * @param item Item to remove.
     */
    public final void removeSelection(final Selectable item) {
        item.setSelected(false);

        selection.remove(item);
    }

    /**
     * Add an item from the selection.
     * @param item Item to add.
     */
    public final void addSelection(final Selectable item) {
        deselectOthers();
        item.setSelected(true);

        selection.add(item);
    }

    /**
     * Toggle the selected state of an item.
     *
     * @param item Item to change select state.
     */
    public final void toggleSelection(final Selectable item) {
        if (item.isSelected()) {
            removeSelection(item);
        } else {
            addSelection(item);
        }
    }

    /**
     * Invoked when the mouse is clicked in a cell.
     *
     * @param me event detail
     * @param s The item to be added to the selection.
     */
    public final void addToSelection(final MouseEvent me, final Selectable s) {
        selectionParent.requestFocus();

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
