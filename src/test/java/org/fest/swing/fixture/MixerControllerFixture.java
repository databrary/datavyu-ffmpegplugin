package org.fest.swing.fixture;

import org.fest.swing.core.Robot;
import org.openshapa.views.MixerControllerV;

/**
 * Fixture for OpenSHAPA MixerController.
 */
public class MixerControllerFixture {
    /** The underlying mixercontroller. */
    private MixerControllerV mixControl;

    /** Robot for FEST. */
    private Robot r;
    /**
     * Constructor.
     * @param r main frame fixture robot
     * @param target mixercontroller class
     */
    public MixerControllerFixture(final Robot robot,
            final MixerControllerV target) {
        r = robot;
        mixControl = target;
    }

    /**
     * Get lock toggle button.
     */
    public final JToggleButtonFixture getLockToggleButton() {
        return new JToggleButtonFixture(r, "lockToggleButton");
    }

    /**
     * Press bookmark button.
     */
    public final void pressBookmarkButton() {
        new JButtonFixture(r, "bookmarkButton").click();
    }

    /**
     * Get snap toggle button.
     */
    public final JToggleButtonFixture getSnapToggleButton() {
        return new JToggleButtonFixture(r, "snapToggleButton");
    }

    /**
     * Press zoom in button.
     */
    public final void pressZoomInButton() {
        new JButtonFixture(r, "zoomInButton").click();
    }

    public final NeedleFixture getNeedle() {
        return new NeedleFixture(r, mixControl.getNeedleController());
    }

    public final RegionFixture getRegion() {
        return new RegionFixture(r, mixControl.getRegionController());
    }

    public final TimescaleFixture getTimescale() {
        return new TimescaleFixture(r, mixControl.getTimescaleController());
    }

    public final TracksEditorFixture getTracksEditor() {
        return new TracksEditorFixture(r,
                mixControl.getTracksEditorController());
    }
}
