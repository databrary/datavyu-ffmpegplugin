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
     * @param robot main frame fixture robot
     * @param target mixercontroller class
     */
    public MixerControllerFixture(final Robot robot,
            final MixerControllerV target) {
        r = robot;
        mixControl = target;
    }

    /**
     * Get lock toggle button.
     * @return  JToggleButtonFixture for lock toggle button
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
     * @return JToggleButtonFixture for Snap toggle button
     */
    public final JToggleButtonFixture getSnapRegionButton() {
        return new JToggleButtonFixture(r, "snapRegionButton");
    }

    /**
     * Press zoom in button.
     */
    public final void pressZoomInButton() {
        new JButtonFixture(r, "zoomInButton").click();
    }

    /**
     * @return fixture for the track needle
     */
    public final NeedleFixture getNeedle() {
        return new NeedleFixture(r, mixControl.getNeedleController());
    }

    /**
     * @return fixture for the start and end region markers
     */
    public final RegionFixture getRegion() {
        return new RegionFixture(r, mixControl.getRegionController());
    }

    /**
     * @return fixture for the timescale
     */
    public final TimescaleFixture getTimescale() {
        return new TimescaleFixture(r, mixControl.getTimescaleController());
    }

    /**
     * @return fixture for the tracks editor, which contains all tracks
     */
    public final TracksEditorFixture getTracksEditor() {
        return new TracksEditorFixture(r,
                mixControl.getTracksEditorController());
    }

    /**
     * @return fixture for the zoom slider
     */
    public final JSliderFixture getZoomSlider() {
        return new JSliderFixture(r, "zoomSlider");
    }

    /**
     * Press zoom region button that zooms to the selected region.
     */
    public void pressZoomRegionButton() {
        new JButtonFixture(r, "zoomRegionButton").click();
    }

    /**
     * @return fixture for the track scroll pane, for vertical scrolling.
     */
    public JScrollPaneFixture getScrollPane() {
        return new JScrollPaneFixture(r, "jScrollPane");
    }

    /**
     * @return fixture for the horizontal scroll bar.
     */
    public JScrollBarFixture getHorizontalScrollBar() {
        return new JScrollBarFixture(r, "horizontalScrollBar");
    }
}
