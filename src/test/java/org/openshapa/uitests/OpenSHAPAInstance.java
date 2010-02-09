package org.openshapa.uitests;

import org.fest.swing.fixture.FrameFixture;

public class OpenSHAPAInstance {
    protected FrameFixture mainFrameFixture;

    private static OpenSHAPAInstance instance;

    private OpenSHAPAInstance() {
    }

    static void setFixture(final FrameFixture f) {
        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }
        instance.mainFrameFixture = f;
    }

    static FrameFixture getFixture() {
        if (instance == null) {
            instance = new OpenSHAPAInstance();
        }
        return instance.mainFrameFixture;
    }
}
