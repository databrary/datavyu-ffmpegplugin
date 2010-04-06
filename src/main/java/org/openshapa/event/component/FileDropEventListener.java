package org.openshapa.event.component;

/**
 * Interface for defining event handlers for events related to file drag and
 * drop.
 */
public interface FileDropEventListener {

    /**
     * Event handler for files dropped onto some component.
     *
     * @param e The event to handle.
     */
    void filesDropped(FileDropEvent e);

}
