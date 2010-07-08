package org.openshapa.plugins.spectrum.engine;

/**
 * Enumeration of engine states.
 */
public enum EngineState {

    /** Engine is initializing. */
    INITIALIZING("Initializing"),

    /** Engine is seeking through media. */
    SEEKING("Seeking"),

    /** Engine is playing back media. */
    PLAYING("Playing"),

    /** Engine is stopping media playback. */
    STOP("Stopping");

    private String stateName;

    EngineState(final String stateName) {
        this.stateName = stateName;
    }

    public String toString() {
        return stateName;
    }

}
