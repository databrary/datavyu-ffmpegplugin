package au.com.nicta.openshapa.views.discrete;

/**
 * Selectable interface class.
 * For use with the Selector object.
 * Defines methods so an object can be in a selection of objects.
 * Implementor must be a JPanel or other swing object that receives
 * mouseclick events.
 *
 * @author swhitcher
 */
public interface Selectable {

    /** @return true if the object is deemed "selected". */
    boolean isSelected();

    /**
     * Set the selected state of an object to true or false.
     * @param sel selected state to set.
     */
    void setSelected(boolean sel);

}
