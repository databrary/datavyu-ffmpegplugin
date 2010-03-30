package org.openshapa.uitests;

import org.fest.swing.fixture.OpenSHAPAFrameFixture;


/**
 * Singleton of OpenSHAPA to be used for all Fest tests.
 */
public final class OpenSHAPAInstance {

    /** Singleton. */
    private static OpenSHAPAInstance instance;

    /** Main Frame fixture. */
    private OpenSHAPAFrameFixture mainFrameFixture;

    /** Empty constructor. */
    private OpenSHAPAInstance() {
    }

    /**
     * Set fixture for instance.
     *
     * @param fixture
     *            FrameFixture
     */
    static void setFixture(final OpenSHAPAFrameFixture fixture) {

        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }

        instance.mainFrameFixture = fixture;
    }

    /**
     * @return FrameFixture of OpenSHAPA instance.
     */
    static OpenSHAPAFrameFixture getFixture() {

        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }

        return instance.mainFrameFixture;
    }
}
