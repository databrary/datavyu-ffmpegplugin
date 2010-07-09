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

    /** This state overwrites the engine state once a task has been handled. */
    TASK_COMPLETE("Task complete");

    private String stateName;

    EngineState(final String stateName) {
        this.stateName = stateName;
    }

    public String toString() {
        return stateName;
    }

}
