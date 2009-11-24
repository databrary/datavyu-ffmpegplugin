package org.openshapa.util;

/**
 * Direction for navigating arrays (left or right).
 */
public enum ArrayDirection {
    /** LEFT create direction. */
    LEFT (-1),

    /** RIGHt create direction. */
    RIGHT (1);


    /** The modifier for navigating arrays in the desired direction. */
    private final int modifier;

    /**
     * Constructor.
     *
     * @param mod The direction modifier (for navigating arrays). LEFT =
     * backwards, RIGHT = forwards.
     */
    ArrayDirection(final int mod) {
        this.modifier = mod;
    }

    /**
     * @return The direction modifier (for navigating arrays).
     */
    public int getModifier() {
        return this.modifier;
    }
}
