package au.com.nicta.openshapa.views.discrete;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 * Manages clicking around a group of "Selectable" objects.
 * @author swhitcher
 */
public class Selector implements MouseListener {

    /** Holder for the selected items. */
    private Vector<Selectable> selection;

    /** Holder for other selectors. */
    private Vector<Selector> otherSelectors;

    /** Selector constructor. */
    public Selector() {
        selection = new Vector<Selectable>();
        otherSelectors = new Vector<Selector>();
    }

    /**
     * Add other Selectors.
     * @param sel Selector to add.
     */
    public final void addOther(final Selector sel) {
        otherSelectors.add(sel);
    }

    /** Deselect other selectors. */
    private void deselectOthers() {
        for (int i = 0; i < otherSelectors.size(); i++) {
            Selector other = (Selector) otherSelectors.get(i);
            other.deselectAll();
        }
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
     * ----------MouseListener Overrides-----------
     */

    /**
     * Invoked when the mouse is clicked in a cell.
     * @param me event detail
     */
    public final void mouseClicked(final MouseEvent me) {

        int mod = me.getModifiers();
        if ((mod & ActionEvent.SHIFT_MASK) != 0
                || (mod & ActionEvent.CTRL_MASK) != 0) {
            // Simple answer for now - toggle the selection
            toggleSelection((Selectable) me.getComponent());
        } else {
            deselectAll();
            addSelection((Selectable) me.getComponent());
        }
    }

    /**
     * Invoked when the mouse enters a component. No function.
     * @param me event detail
     */
    public void mouseEntered(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse exits a component. No function.
     * @param me event detail
     */
    public void mouseExited(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse is pressed in a component. No function.
     * @param me event detail
     */
    public void mousePressed(final MouseEvent me) {
    }

    /**
     * Invoked when the mouse is released in a component. No function.
     * @param me event detail
     */
    public void mouseReleased(final MouseEvent me) {
    }

}
