package org.fest.swing.fixture;

import static org.fest.reflect.core.Reflection.method;

import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.fest.swing.core.Robot;
import org.openshapa.controllers.component.TrackController;
import org.openshapa.controllers.component.TracksEditorController;
import org.openshapa.views.component.SnapMarkerPainter;

/**
 * Fixture for OpenSHAPA NeedlePainter.
 */
public class TracksEditorFixture extends JPanelFixture {
    /** The underlying mixercontroller. */
    private TracksEditorController tracksEditorC;

    /**
     * Constructor.
     *
     * @param robot
     *            mainframe robot
     * @param target
     *            TracksEditorController
     */
    public TracksEditorFixture(final Robot robot,
            final TracksEditorController target) {
        super(robot, (JPanel) target.getView());
        tracksEditorC = target;
    }

    /**
     * @return Vector of all tracks as TrackFixtures.
     */
    public Vector<TrackFixture> getTracks() {
        Vector<TrackFixture> tracks = new Vector<TrackFixture>();

        method("getAllTrackControllers").withReturnType(List.class).in(
                tracksEditorC).invoke();

        List<TrackController> trackControllers =
                method("getAllTrackControllers").withReturnType(List.class).in(
                        tracksEditorC).invoke();

        for (TrackController tc : trackControllers) {
            tracks.add(new TrackFixture(robot, tc));
        }

        return tracks;
    }

    /**
     * @param Track
     *            number, starting from 0 at the top.
     * @return track at track number.
     */
    public TrackFixture getTrack(final int n) {
        TrackFixture track;
        List<TrackController> trackControllers =
                method("getAllTrackControllers").withReturnType(List.class).in(
                        tracksEditorC).invoke();

        track = new TrackFixture(robot, trackControllers.get(n));

        return track;
    }

    /**
     * @param trackName
     *            name of the track, i.e. the file name.
     * @return track with trackname, else null.
     */
    public TrackFixture getTrack(final String trackName) {
        TrackFixture track;
        List<TrackController> trackControllers =
                method("getAllTrackControllers").withReturnType(List.class).in(
                        tracksEditorC).invoke();

        for (TrackController tc : trackControllers) {
            if (tc.getTrackName().equalsIgnoreCase(trackName)) {
                track = new TrackFixture(robot, tc);
                return track;
            }
        }
        return null;
    }

    public SnapMarkerFixture getSnapMarker() {
        return new SnapMarkerFixture(robot,
                (SnapMarkerPainter)tracksEditorC.getMarkerView());
    }
}
