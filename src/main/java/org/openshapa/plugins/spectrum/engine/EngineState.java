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
    STOP("Stopping"),

    /**
     * This state overwrites the engine state once a task has been handled. Not
     * to be queued.
     */
    TASK_COMPLETE("Task complete");

    /** Name/description of the state. */
    private String stateName;

    /**
     * Create a new state with the given name/description.
     *
     * @param stateName
     */
    EngineState(final String stateName) {
        this.stateName = stateName;
    }

    public String toString() {
        return stateName;
    }

}
