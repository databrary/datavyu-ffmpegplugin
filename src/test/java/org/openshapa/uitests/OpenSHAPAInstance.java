package org.openshapa.uitests;

import org.fest.swing.fixture.FrameFixture;

/**
 * Singleton of OpenSHAPA to be used for all Fest tests.
 */
public final class OpenSHAPAInstance {
    /** Main Frame fixture. */
    private FrameFixture mainFrameFixture;

    /** Singleton. */
    private static OpenSHAPAInstance instance;

    /** Empty constructor. */
    private OpenSHAPAInstance() {
    }

    /**
     * Set fixture for instance.
     * @param f FrameFixture
     */
    static void setFixture(final FrameFixture f) {
        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }
        instance.mainFrameFixture = f;
    }

    /**
     * @return FrameFixture of OpenSHAPA instance.
     */
    static FrameFixture getFixture() {
        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }
        return instance.mainFrameFixture;
    }
}
