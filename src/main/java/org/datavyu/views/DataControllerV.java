/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.views;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.Datavyu;
import org.datavyu.Datavyu.Platform;
import org.datavyu.controllers.CreateNewCellC;
import org.datavyu.controllers.SetNewCellStopTimeC;
import org.datavyu.controllers.SetSelectedCellStartTimeC;
import org.datavyu.controllers.SetSelectedCellStopTimeC;
import org.datavyu.controllers.component.MixerController;
import org.datavyu.controllers.id.IDController;
import org.datavyu.event.component.CarriageEvent;
import org.datavyu.event.component.TimescaleEvent;
import org.datavyu.event.component.TracksControllerEvent;
import org.datavyu.event.component.TracksControllerListener;
import org.datavyu.models.PlaybackModel;
import org.datavyu.models.component.*;
import org.datavyu.models.id.Identifier;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.Plugin;
import org.datavyu.plugins.PluginManager;
import org.datavyu.util.ClockTimer;
import org.datavyu.util.ClockTimer.ClockListener;
import org.datavyu.util.FloatUtils;
import org.datavyu.views.component.TrackPainter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Quicktime video controller.
 */
public final class DataControllerV extends DatavyuDialog
        implements ClockListener, TracksControllerListener, DataController,
        PropertyChangeListener {

    private static final double LOW_RATE = 5D;

    /**
     * One second in milliseconds.
     */
    private static final long ONE_SECOND = 1000L;

    /**
     * Rate of playback for rewinding.
     */
    private static final float REWIND_RATE = -32F;

    /**
     * Rate of normal playback.
     */
    private static final float PLAY_RATE = 1F;

    /**
     * Rate of playback for fast forwarding.
     */
    private static final float FFORWARD_RATE = 32F;

    /**
     * Sequence of allowable shuttle rates.
     */
    private static final float[] SHUTTLE_RATES;

    /**
     * The threshold to use while synchronising viewers (augmented by rate).
     */
    private static final long SYNC_THRESH = 200;

    /**
     * How often to synchronise the viewers with the master clock.
     */
    private static final long SYNC_PULSE = 500;
    /**
     * The jump multiplier for shift-jogging.
     */
    private static final int SHIFTJOG = 5;
    /**
     * The jump multiplier for ctrl-jogging.
     */
    private static final int CTRLJOG = 10;
    /**
     * The 45x45 size for numpad keys
     */
    private static final int NUMPAD_KEY_HEIGHT = 45;
    private static final int NUMPAD_KEY_WIDTH = 45;
    private static final String NUMPAD_KEY_SIZE = "w " + NUMPAD_KEY_HEIGHT
            + "!, h " + NUMPAD_KEY_WIDTH + "!";
    /**
     * The 45x95 size for tall numpad keys (enter, PC plus)
     */
    private static final int TALL_NUMPAD_KEY_HEIGHT = 95;
    private static final String TALL_NUMPAD_KEY_SIZE = "span 1 2, w " + NUMPAD_KEY_WIDTH
            + "!, h " + TALL_NUMPAD_KEY_HEIGHT + "!";
    /**
     * The 80x40 size for the text fields to the right of numpad
     */
    private static final int WIDE_TEXT_FIELD_WIDTH = 90;
    private static final int WIDE_TEXT_FIELD_HEIGHT = 45;
    private static final String WIDE_TEXT_FIELD_SIZE = "w " + WIDE_TEXT_FIELD_WIDTH + "!, h "
            + WIDE_TEXT_FIELD_HEIGHT + "!";
    private static final Font TEXT_FIELD_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final Font TEXT_LABEL_FONT = new Font("Arial", Font.PLAIN, 10);
    /**
     * Format for representing time.
     */
    private static final DateFormat CLOCK_FORMAT;
    private static final DateFormat CLOCK_FORMAT_HTML;
    private static final String QT_URL = "http://www.datavyu.org/user-guide/guide/install.html";
    private static final String QT_IN_PAGE_HREF = "#software-requirements";
    // -------------------------------------------------------------------------
    // [static]
    //
    private static final String QT_WARNING_MSG =
            "<html><body>Quicktime's Java libraries are not found.<br />"
                    + "You must install these libraries to load videos using Quicktime.<br />"
                    + "Note that for Quicktime versions 7.7.5 or later under Windows, you must manually include the Java libraries under Custom install<br />"
                    + "See the User Guide at <a href=\"" + QT_URL + QT_IN_PAGE_HREF + "\">" + QT_URL + "</a> for details.</body></html>";
    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(DataControllerV.class);

    // initialize standard date format for clock display.
    static {
        CLOCK_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        CLOCK_FORMAT.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));

        Color hoursColor = TimescaleConstants.HOURS_COLOR;
        Color minutesColor = TimescaleConstants.MINUTES_COLOR;
        Color secondsColor = TimescaleConstants.SECONDS_COLOR;
        Color millisecondsColor = TimescaleConstants.MILLISECONDS_COLOR;

        CLOCK_FORMAT_HTML = new SimpleDateFormat("'<html>" + "<font color=\""
                + toRGBString(hoursColor) + "\">'HH'</font>':"
                + "'<font color=\"" + toRGBString(minutesColor)
                + "\">'mm'</font>':" + "'<font color=\""
                + toRGBString(secondsColor) + "\">'ss'</font>':"
                + "'<font color=\"" + toRGBString(millisecondsColor)
                + "\">'SSS'</font>" + "</html>'");
        CLOCK_FORMAT_HTML.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }

    // Initialize SHUTTLE_RATES
    static {
        SHUTTLE_RATES = new float[]{
                -32F, -16F, -8F, -4F, -2F, -1F,
                -1 / 2F, -1 / 4F, -1 / 8F, -1 / 16F, -1 / 32F,
                0F,
                1 / 32F, 1 / 16F, 1 / 8F, 1 / 4F, 1 / 2F,
                1F, 2F, 4F, 8F, 16F, 32F
        };
    }

    private boolean visible = false;
    /**
     * Determines whether or not Shift is being held.
     */
    private boolean shiftMask = false;

    /**
     * Determines whether or not Control is being held.
     */
    private boolean ctrlMask = false;

    // -------------------------------------------------------------------------
    //
    //
    /**
     * The list of viewers associated with this controller.
     */
    private Set<DataViewer> viewers;

    /**
     * Clock timer.
     */
    private ClockTimer clock = new ClockTimer();

    /**
     * Is the tracks panel currently shown?
     */
    private boolean tracksPanelEnabled = true;

    /**
     * The controller for manipulating tracks.
     */
    private MixerController mixerController;

    /** */
    private javax.swing.JButton createNewCell;

    /** */
    private javax.swing.JButton createNewCellSettingOffset;

    /** */
    private javax.swing.JButton findButton;

    /** */
    private javax.swing.JTextField offsetTextField;

    /** */
    private javax.swing.JTextField onsetTextField;

    /** */
    private javax.swing.JButton forwardButton;

    /** */
    private javax.swing.JButton goBackButton;

    /** */
    private javax.swing.JTextField goBackTextField;

    /** */
    private javax.swing.JTextField stepSizeTextField;

    private javax.swing.JLabel stepSizeLabel = new JLabel("Steps per second");

    private javax.swing.JPanel stepSizePanel;

    /** */
    private javax.swing.JPanel gridButtonPanel;

    /** */
    private javax.swing.JLabel jLabel1;

    /** */
    private javax.swing.JLabel jLabel2;

    /** */
    private javax.swing.JButton jogBackButton;

    /** */
    private javax.swing.JButton jogForwardButton;

    /** */
    private javax.swing.JLabel lblSpeed;

    /** */
    private javax.swing.JButton addDataButton;

    /** */
    private javax.swing.JButton pauseButton;

    /** */
    private javax.swing.JButton playButton;

    /** */
    private javax.swing.JButton rewindButton;

    /** */
    private javax.swing.JButton ninesetCellOffsetButton;

    /** */
    private javax.swing.JButton setCellOffsetButton;

    /** */
    private javax.swing.JButton setCellOnsetButton;

    /** */
    private javax.swing.JButton pointCellButton;

    private javax.swing.JButton showTracksSmallButton;

    /** */
    private javax.swing.JButton shuttleBackButton;

    /** */
    private javax.swing.JButton shuttleForwardButton;

    /** */
    private javax.swing.JButton stopButton;

    /** */
    private javax.swing.JLabel timestampLabel;

    /** */
    private javax.swing.JPanel tracksPanel;

    /**
     * Model containing playback information.
     */
    private PlaybackModel playbackModel;

    private boolean qtWarningShown = false;

    private org.jdesktop.application.ResourceMap resourceMap;

    private javax.swing.ActionMap actionMap;

    private String osModifier;

    private boolean highlightCells = false;

    // -------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor. Creates a new DataControllerV.
     *
     * @param parent The parent of this form.
     * @param modal  Should the dialog be modal or not?
     */
    public DataControllerV(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        clock.registerListener(this);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        resourceMap =
                org.jdesktop.application.Application.getInstance(
                        org.datavyu.Datavyu.class).getContext().getResourceMap(
                        DataControllerV.class);

        actionMap = org.jdesktop.application.Application
                .getInstance(org.datavyu.Datavyu.class).getContext()
                .getActionMap(DataControllerV.class, this);

        if (Datavyu.getPlatform() == Platform.MAC) {
            osModifier = "osx";
            setJMenuBar(((JFrame) parent).getJMenuBar());
        } else {
            osModifier = "win";
        }
        initComponents();

        setResizable(false);
        setName(this.getClass().getSimpleName());
        viewers = new LinkedHashSet<DataViewer>();

        playbackModel = new PlaybackModel();
        playbackModel.setPauseRate(0);
        playbackModel.setLastSync(0);
        playbackModel.setMaxDuration(ViewportStateImpl.MINIMUM_MAX_END);

        final int defaultEndTime = (int) MixerConstants.DEFAULT_DURATION;

        playbackModel.setWindowPlayStart(0);
        playbackModel.setWindowPlayEnd(defaultEndTime);

        mixerController = new MixerController();
        tracksPanel.add(mixerController.getTracksPanel(), "growx");
        mixerController.addTracksControllerListener(this);
        mixerController.getMixerModel().getViewportModel()
                .addPropertyChangeListener(this);
        mixerController.getMixerModel().getRegionModel()
                .addPropertyChangeListener(this);
        mixerController.getMixerModel().getNeedleModel()
                .addPropertyChangeListener(this);

        tracksPanelEnabled = true;
        showTracksPanel(tracksPanelEnabled);
        updateCurrentTimeLabel();

        visible = true;
    }

    public static String formatTime(final long time) {
        return CLOCK_FORMAT.format(new Date(time));
    }

    private static String toRGBString(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(),
                color.getBlue());
    }

    /**
     * Handles opening a data source.
     *
     * @param jd The file chooser used to open the data source.
     */
    private void openVideo(final PluginChooser chooser) {
        Plugin plugin = chooser.getSelectedPlugin();
        File f = chooser.getSelectedFile();

        new Thread(() -> {
            if (plugin != null) {

                try {
                    DataViewer dataViewer = plugin.getNewDataViewer(Datavyu
                            .getApplication().getMainFrame(), false);
                    dataViewer.setIdentifier(IDController.generateIdentifier());
                    dataViewer.setDataFeed(f);
                    dataViewer.seekTo(clock.getTime());
                    dataViewer.setDatastore(Datavyu.getProjectController()
                            .getDB());
                    addDataViewer(plugin.getTypeIcon(), dataViewer, f,
                            dataViewer.getTrackPainter());
                    mixerController.bindTrackActions(dataViewer.getIdentifier(),
                            dataViewer.getCustomActions());
                    dataViewer.addViewerStateListener(
                            mixerController.getTracksEditorController()
                                    .getViewerStateListener(dataViewer.getIdentifier()));
                } catch (Throwable t) {
                    LOGGER.error(t);
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    // stack trace as a string

                    if (plugin.getClassifier().contains("quicktime")) {
                        JLabel label = new JLabel();
                        Font font = label.getFont();

                        // create some css from the label's font
                        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
                        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
                        style.append("font-size:" + font.getSize() + "pt;");

                        // html content
                        JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" //
                                + "Error: Could not load Quicktime.  <a href=\"https://www.apple.com/quicktime/download/\">Please install Quicktime 7 from here</a><br>" +
                                "and when installing, select \"Custom Install\" and then left click on the [+] next to the red X<br>" +
                                "next to \"Legacy options\", click on the red X next to Quicktime For Java and select <br>\"Will be installed to local harddrive\". Then click \"Next\" and install.<br>" +
                                "Afterwards relaunch Datavyu to use the Quicktime plugin." //
                                + "</body></html>");

                        // handle link events
                        ep.addHyperlinkListener(new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent e) {
                                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                                    try {
                                        Desktop.getDesktop().browse(e.getURL().toURI()); // roll your own link launcher or use Desktop if J6+
                                    } catch (Exception u) {
                                        u.printStackTrace();
                                    }
                                }
                            }
                        });
                        ep.setEditable(false);
                        ep.setBackground(label.getBackground());

                        // show
                        JOptionPane.showMessageDialog(null, ep);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Could not open data source: " + t.getMessage());
                    }
                    t.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Tells the Data Controller if shift is being held or not.
     *
     * @param shift True for shift held; false otherwise.
     */
    public void setShiftMask(final boolean shift) {
        shiftMask = shift;
    }

    /**
     * Tells the Data Controller if ctrl is being held or not.
     *
     * @param ctrl True for ctrl held; false otherwise.
     */
    public void setCtrlMask(final boolean ctrl) {
        ctrlMask = ctrl;
    }

    // -------------------------------------------------------------------------
    // [interface] org.datavyu.util.ClockTimer.Listener
    //

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStart(final long time) {
        resetSync();

        long playTime = time;
        final long windowPlayStart = playbackModel.getWindowPlayStart();

        if (playTime < windowPlayStart) {
            playTime = windowPlayStart;
            clockStep(playTime);
        }

        float currentRate = clock.getRate();
        clock.stop();

        setCurrentTime(playTime);
        clock.setTime(playTime);
        clock.setRate(currentRate);

        clock.start();
    }

    /**
     * Reset the sync.
     */
    private void resetSync() {
        playbackModel.setLastSync(0);
    }

    /**
     * Keep track of whether this should be visible or not
     */
    public boolean shouldBeVisible() {
        return visible;
    }

    public void setShouldBeVisible(boolean v) {
        visible = v;
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockTick(final long time) {

        try {
            setCurrentTime(time);

            // We are playing back at a rate which is too fast and probably
            // won't allow us to stream all the information at the file. We fake
            // playback by doing a bunch of seekTo's.
            if (playbackModel.isFakePlayback()) {

                for (DataViewer v : viewers) {

                    if ((time > v.getOffset()) && isWithinPlayRange(time, v)) {
                        v.seekTo(time - v.getOffset());
                    }
                }

                // DataViewer is responsible for playing video.
            } else {

                // Synchronise viewers only if we have exceeded our pulse time.
                if ((time - playbackModel.getLastSync())
                        > (SYNC_PULSE * clock.getRate()) || playbackModel.getLastSync() == 0) {
                    long thresh = (long) (SYNC_THRESH
                            * Math.abs(clock.getRate()));
                    playbackModel.setLastSync(time);

                    for (DataViewer v : viewers) {

                        /*
                         * Use offsets to determine if the video file should
                         * start playing.
                         */

                        if (!v.isPlaying() && isWithinPlayRange(time, v)) {
                            //v.seekTo(time - v.getOffset());
                            v.play();
                        }

                        // BugzID:1797 - Viewers who are "playing" outside their
                        // timeframe should be asked to stop.
                        if (v.isPlaying() && !isWithinPlayRange(time, v)) {
                            v.stop();
                        }

                        // For plugins with low data rate, use frame rate
                        // to determine threshold.
                        if ((0 < v.getFrameRate())
                                && (v.getFrameRate() <= LOW_RATE)) {
                            thresh = (long) (ONE_SECOND / v.getFrameRate()
                                    / clock.getRate());
                        }

                        /*
                         * Only synchronise the data viewers if we have a
                         * noticable drift.
                         */
//                        System.out.println(v.getCurrentTime());
//                        System.out.println(time);
                        if (v.isPlaying()
                                && (Math.abs(
                                v.getCurrentTime()
                                        - (time - v.getOffset())) > thresh)) {
//                            v.seekTo(time - v.getOffset());
                        }
                    }
                }
            }

            // BugzID:466 - Prevent rewind wrapping the clock past the start
            // point of the view window.
            final long windowPlayStart = playbackModel.getWindowPlayStart();

            if (time < windowPlayStart) {
                setCurrentTime(windowPlayStart);
                clock.stop();
                clock.setTime(windowPlayStart);
                clockStop(windowPlayStart);
            }

            // BugzID:756 - don't play video once past the max duration.
            final long windowPlayEnd = playbackModel.getWindowPlayEnd();

            if ((time >= windowPlayEnd) && (clock.getRate() >= 0)) {
                setCurrentTime(windowPlayEnd);
                clock.stop();
                clock.setTime(windowPlayEnd);
                clockStop(windowPlayEnd);

                return;
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Sync viewers", e);
        }
    }

    /**
     * Determines whether a DataViewer has data for the desired time.
     *
     * @param time The time we wish to play at or seek to.
     * @param view The DataViewer to check.
     * @return True if data exists at this time, and false otherwise.
     */
    private boolean isWithinPlayRange(final long time, final DataViewer view) {
        return (time >= view.getOffset())
                && (time < (view.getOffset() + view.getDuration()));
    }

    public void adjustClock(final long time) {
        if (viewers.size() == 1 && (time < playbackModel.getWindowPlayEnd() && time > playbackModel.getWindowPlayStart())) {
            // Using an iterator because viewers is a set
            for (DataViewer viewer : viewers) {
                try {
                    long viewerTime = viewer.getCurrentTime();

                    long stepSize = ((ONE_SECOND) / (long) playbackModel.getCurrentFPS());

                     /* BugzID:1544 - Preserve precision - force jog to frame markers. */
                    if (!clock.isStopped()) {
                        long mod = (viewerTime % stepSize);

                        if (mod != 0) {
                            viewerTime = viewerTime + stepSize - mod;
                        }
                    }

                    clock.setTimeDontNotify(viewerTime);
                    resetSync();
                    updateCurrentTimeLabel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStop(final long time) {
        clock.stop();
        resetSync();

        /*
            This is the new style time reckoning where the timer gets updated from the video
            This gets rid of the
         */
        if (viewers.size() == 1 && (time < playbackModel.getWindowPlayEnd() && time > playbackModel.getWindowPlayStart())) {
            // Using an iterator because viewers is a set
            for (DataViewer viewer : viewers) {
                if (viewer.isPlaying()) {
                    viewer.stop();
                    try {
                        long viewerTime = viewer.getCurrentTime();

                        long stepSize = ((ONE_SECOND) / (long) playbackModel.getCurrentFPS());

                     /* BugzID:1544 - Preserve precision - force jog to frame markers. */
                        long mod = (viewerTime % stepSize);

                        if (mod != 0) {
                            viewerTime = viewerTime + stepSize - mod;
                        }

                        viewer.seekTo(viewerTime);

                        clock.setTimeDontNotify(viewerTime);
                        resetSync();
                        updateCurrentTimeLabel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            setCurrentTime(time);
            for (DataViewer viewer : viewers) {
                viewer.stop();

                if (isWithinPlayRange(time, viewer)) {
                    viewer.seekTo(time - viewer.getOffset());
                }
            }
        }
    }

    /**
     * @param rate Current (updated) clock rate.
     */
    public void clockRate(final float rate) {
        resetSync();
        lblSpeed.setText(FloatUtils.doubleToFractionStr(new Double(rate)));

        long time = getCurrentTime();

        // If rate is faster than two times - we need to fake playback to give
        // the illusion of 'smooth'. We do this by stopping the dataviewer and
        // doing many seekTo's to grab individual frames.
        if (Math.abs(rate) > 2.0 || rate < 0) {
            playbackModel.setFakePlayback(true);

            for (DataViewer viewer : viewers) {
                viewer.stop();

                if (isWithinPlayRange(time, viewer)) {
                    viewer.setPlaybackSpeed(rate);
                }
            }

            // Rate is less than two times - use the data viewer internal code
            // to draw every frame.
        } else {
            playbackModel.setFakePlayback(false);

            for (DataViewer viewer : viewers) {
                viewer.setPlaybackSpeed(rate);

                if (!clock.isStopped()) {
                    viewer.play();
                }
            }
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStep(final long time) {
        resetSync();
        setCurrentTime(time);

        for (DataViewer viewer : viewers) {

            System.out.println(time);
            try {
                if (isWithinPlayRange(time, viewer) && time != viewer.getCurrentTime()) {
                    viewer.seekTo(time - viewer.getOffset());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the mixer controller.
     */
    public MixerController getMixerController() {
        return mixerController;
    }

    private void updateCurrentTimeLabel() {
        timestampLabel.setText(tracksPanelEnabled
                ? CLOCK_FORMAT_HTML.format(getCurrentTime())
                : CLOCK_FORMAT_HTML.format(getCurrentTime()));
    }

    /**
     * Get the current master clock time for the controller.
     *
     * @return Time in milliseconds.
     */
    public long getCurrentTime() {
        return clock.getTime();
    }

    /**
     * Set time location for data streams.
     *
     * @param milliseconds The millisecond time.
     */
    public void setCurrentTime(final long milliseconds) {
        resetSync();
        updateCurrentTimeLabel();
        mixerController.getMixerModel().getNeedleModel().setCurrentTime(
                milliseconds);
    }

    /**
     * Recalculates the maximum viewer duration.
     */
    public void updateMaxViewerDuration() {
        long maxDuration = ViewportStateImpl.MINIMUM_MAX_END;
        Iterator<DataViewer> it = viewers.iterator();

        while (it.hasNext()) {
            DataViewer dv = it.next();

            if ((dv.getDuration() + dv.getOffset()) > maxDuration) {
                maxDuration = dv.getDuration() + dv.getOffset();
            }
        }

        mixerController.getMixerModel().getViewportModel().setViewportMaxEnd(
                maxDuration, true);

        if (viewers.isEmpty()) {
            mixerController.getNeedleController().resetNeedlePosition();
            mixerController.getMixerModel().getRegionModel()
                    .resetPlaybackRegion();
        }
    }

    /**
     * Remove the specified viewer from the controller.
     *
     * @param viewer The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    public boolean shutdown(final DataViewer viewer) {

        // Was the viewer removed.
        boolean removed = viewers.remove(viewer);

        if (removed) {

            viewer.clearDataFeed();

            // BugzID:2000
            viewer.removeViewerStateListener(
                    mixerController.getTracksEditorController()
                            .getViewerStateListener(viewer.getIdentifier()));

            // Recalculate the maximum playback duration.
            updateMaxViewerDuration();

            // Remove the data viewer from the tracks panel.
            mixerController.deregisterTrack(viewer.getIdentifier());

            // Data viewer removed, mark project as changed.
            Datavyu.getProjectController().projectChanged();
        }

        return removed;
    }

    /**
     * Remove the specified viewer from the controller.
     *
     * @param id The identifier of the viewer to shutdown.
     */
    public void shutdown(final Identifier id) {
        DataViewer viewer = null;

        for (DataViewer v : viewers) {

            if (v.getIdentifier().equals(id)) {
                viewer = v;

                break;
            }
        }

        if ((viewer == null) || !shouldRemove()) {
            return;
        }

        viewers.remove(viewer);

        viewer.stop();
        viewer.clearDataFeed();

        JDialog viewDialog = viewer.getParentJDialog();

        if (viewDialog != null) {
            viewDialog.dispose();
        }

        //DO SOMETHING CONCERNING STEP_SIZE
        if (viewers.isEmpty()) {
            playbackModel.setCurrentFPS(0f);
            updateStepSizeTextField();
        }

        // BugzID:2000
        viewer.removeViewerStateListener(
                mixerController.getTracksEditorController().getViewerStateListener(
                        viewer.getIdentifier()));

        // Recalculate the maximum playback duration.
        updateMaxViewerDuration();

        // Remove the data viewer from the tracks panel.
        mixerController.deregisterTrack(viewer.getIdentifier());

        // Data viewer removed, mark project as changed.
        Datavyu.getProjectController().projectChanged();

    }

    /**
     * Binds a window event listener to a data viewer.
     *
     * @param id The identifier of the viewer to bind to.
     */
    public void bindWindowListenerToDataViewer(final Identifier id,
                                               final WindowListener wl) {

        DataViewer viewer = null;

        for (DataViewer v : viewers) {

            if (v.getIdentifier().equals(id)) {
                viewer = v;

                break;
            }
        }

        if (viewer != null && viewer.getParentJDialog() != null) {
            viewer.getParentJDialog().addWindowListener(wl);
        }
    }

    /**
     * Binds a window event listener to a data viewer.
     *
     * @param id The identifier of the viewer to bind to.
     */
    public void setDataViewerVisibility(final Identifier id,
                                        final boolean visible) {

        DataViewer viewer = null;

        for (DataViewer v : viewers) {

            if (v.getIdentifier().equals(id)) {
                viewer = v;

                break;
            }
        }

        if (viewer != null) {
            viewer.setDataViewerVisible(visible);
        }
    }


    /**
     * Presents a confirmation dialog when removing a plugin from the project.
     *
     * @return True if the plugin should be removed, false otherwise.
     */
    private boolean shouldRemove() {
        ResourceMap rMap = Application.getInstance(Datavyu.class).getContext()
                .getResourceMap(Datavyu.class);

        String cancel = "Cancel";
        String ok = "OK";

        String[] options = new String[2];

        if (Datavyu.getPlatform() == Platform.MAC) {
            options[0] = cancel;
            options[1] = ok;
        } else {
            options[0] = ok;
            options[1] = cancel;
        }

        int selection = JOptionPane.showOptionDialog(this,
                rMap.getString("ClosePluginDialog.message"),
                rMap.getString("ClosePluginDialog.title"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, cancel);

        // Button behaviour is platform dependent.
        return (Datavyu.getPlatform() == Platform.MAC) ? (selection == 1)
                : (selection == 0);
    }

    /**
     * Helper method for Building a button for the data controller - sets the
     * icon, selected icon, action map and name.
     *
     * @param name     The prefix to use when looking for actions and buttons.
     * @param modifier The modifier (if any) to apply to the prefix. Maybe null.
     * @return A configured button.
     */
    private JButton buildButton(final String name,
                                final String modifier) {
        String dotModifier;
        if (!(modifier == null) && !modifier.isEmpty()) dotModifier = "." + modifier;
        else dotModifier = "";

        JButton result = new JButton();
        result.setAction(actionMap.get(name + "Action"));
        result.setIcon(resourceMap.getIcon(name + "Button.icon" + dotModifier));
        result.setPressedIcon(resourceMap.getIcon(name + "SelectedButton.icon" + dotModifier));
        result.setFocusPainted(false);
        result.setName(name + "Button");

        return result;
    }

    private JButton buildButton(final String name) {
        return buildButton(name, null);
    }

    private JPanel makeLabelAndTextfieldPanel(JLabel l, JTextField tf) {
        JPanel jp = new JPanel();
        l.setFont(TEXT_LABEL_FONT);
        tf.setFont(TEXT_FIELD_FONT);
        jp.add(l);
        jp.add(tf);
        return jp;
    }

    /**
     * Helper method for creating placeholder buttons
     */
    private JButton makePlaceholderButton(boolean vis) {
        JButton jb = new JButton();
        jb.setEnabled(false);
        jb.setFocusPainted(false);
        jb.setVisible(vis);
        return jb;
    }

    /**
     * Helper method for creating placeholder buttons
     */
    private JButton makePlaceholderButton() {
        return makePlaceholderButton(true);
    }

    /**
     * Initialize the view for OS other than Macs.
     */
    private void initComponents() {
        gridButtonPanel = new javax.swing.JPanel();
        goBackTextField = new javax.swing.JTextField();
        stepSizeTextField = new javax.swing.JTextField();
        onsetTextField = new javax.swing.JTextField();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        lblSpeed = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        offsetTextField = new javax.swing.JTextField();
        tracksPanel = new javax.swing.JPanel(new MigLayout("fill"));

        final int fontSize = 11;
        final int timeStampFontSize = 15;

        setTitle(resourceMap.getString("title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        gridButtonPanel.setBackground(Color.WHITE);
        gridButtonPanel.setLayout(new MigLayout("wrap 5"));

        // Add data button
        addDataButton.setText(resourceMap.getString("addDataButton.text"));
        addDataButton.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        addDataButton.setFocusPainted(false);
        addDataButton.setName("addDataButton");
        addDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {

                if (!qtWarningShown && !Datavyu.quicktimeLibrariesFound()) {

//                    JOptionPane.showMessageDialog(null, makeEditorPaneWithLinks(QT_WARNING_MSG), "Quicktime Warning", JOptionPane.WARNING_MESSAGE);
                    qtWarningShown = true;
                }
                openVideoButtonActionPerformed(evt);
            }
        });
        gridButtonPanel.add(addDataButton, "span 2, w 90!, h 25!");

        // Timestamp panel
        JPanel timestampPanel = new JPanel(new MigLayout("",
                "push[][][]0![]push"));
        timestampPanel.setOpaque(false);

        // Timestamp label
        timestampLabel.setFont(new Font("Tahoma", Font.BOLD, timeStampFontSize));
        timestampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timestampLabel.setName("timestampLabel");
        timestampLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // you can open a new frame here as
                // i have assumed you have declared "frame" as instance variable
                if (e.getClickCount() >= 3) {
                    int newTime = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new time in ms", getCurrentTime()));
                    setCurrentTime(newTime);
                    clock.setTime(newTime);
                }
            }
        });

        timestampPanel.add(timestampLabel);

        jLabel1.setText("@");
        timestampPanel.add(jLabel1);

        lblSpeed.setFont(new Font("Tahoma", Font.BOLD, timeStampFontSize));
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
                2));
        lblSpeed.setName("lblSpeed");
        lblSpeed.setText("0");
        timestampPanel.add(lblSpeed);

        jLabel2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        jLabel2.setText("x");
        timestampPanel.add(jLabel2);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        //placeholder at topleft: 'clear' or 'numlock' position
        gridButtonPanel.add(makePlaceholderButton(), NUMPAD_KEY_SIZE);

        //Point cell: Mac equal sign, windows forward slash
        pointCellButton = buildButton("pointCell", osModifier);
        gridButtonPanel.add(pointCellButton, NUMPAD_KEY_SIZE);

        //Show/hide: Mac forward flash, windows asterisk
        showTracksSmallButton = new JButton();
        showTracksSmallButton.setIcon(resourceMap.getIcon(
                "showTracksSmallButton.hide.icon." + osModifier));
        showTracksSmallButton.setName("showTracksSmallButton");
        showTracksSmallButton.getAccessibleContext().setAccessibleName(
                "Show Tracks");
        showTracksSmallButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                showTracksButtonActionPerformed(evt);
            }
        });
        showTracksSmallButton.setFocusPainted(false);
        gridButtonPanel.add(showTracksSmallButton, NUMPAD_KEY_SIZE);

        //MAC and WINDOWS DIFFER HERE (1)
        if (osModifier.equals("osx")) {
            //Placeholder at asterisk location
            gridButtonPanel.add(makePlaceholderButton(), NUMPAD_KEY_SIZE);

            //Placeholder - perhaps eventually the sync video button
            gridButtonPanel.add(makePlaceholderButton(false), WIDE_TEXT_FIELD_SIZE);
        } else {
            addGoBackPair();
        }


        // Set cell onset button with 7
        setCellOnsetButton = buildButton("setCellOnset");
        gridButtonPanel.add(setCellOnsetButton, NUMPAD_KEY_SIZE);

        // Play video button with 8
        playButton = buildButton("play");
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, NUMPAD_KEY_SIZE);

        // Set cell offset button with 9
        ninesetCellOffsetButton = buildButton("setCellOffset", "nine");
        gridButtonPanel.add(ninesetCellOffsetButton, NUMPAD_KEY_SIZE);

        //MAC and WINDOWS DIFFER HERE (2)
        if (osModifier.equals("osx")) {
            addGoBackPair();
        } else {
            //Find button (big plus)
            findButton = buildButton("find", "win");
            gridButtonPanel.add(findButton, TALL_NUMPAD_KEY_SIZE);

            //Placeholder - perhaps eventually the sync video button
            gridButtonPanel.add(makePlaceholderButton(false), WIDE_TEXT_FIELD_SIZE);
        }

        // Shuttle back button with 4
        shuttleBackButton = buildButton("shuttleBack");
        gridButtonPanel.add(shuttleBackButton, NUMPAD_KEY_SIZE);

        // Stop button with 5
        stopButton = buildButton("stop");
        gridButtonPanel.add(stopButton, NUMPAD_KEY_SIZE);

        // Shuttle forward button with 6
        shuttleForwardButton = buildButton("shuttleForward");
        gridButtonPanel.add(shuttleForwardButton, NUMPAD_KEY_SIZE);


        //MAC and WINDOWS DIFFER HERE (3)
        if (osModifier.equals("osx")) {
            //Find button (small plus)
            findButton = buildButton("find", "osx");
            gridButtonPanel.add(findButton, NUMPAD_KEY_SIZE);


        }


        addStepSizePanel();

        // Jog back button with 1
        jogBackButton = buildButton("jogBack");
        gridButtonPanel.add(jogBackButton, NUMPAD_KEY_SIZE);

        // Pause button with 2
        pauseButton = buildButton("pause");
        gridButtonPanel.add(pauseButton, NUMPAD_KEY_SIZE);

        // Jog forward button with 3
        jogForwardButton = buildButton("jogForward");
        gridButtonPanel.add(jogForwardButton, NUMPAD_KEY_SIZE);

        // Create new cell button with enter
        createNewCell = buildButton("createNewCell");
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridButtonPanel.add(createNewCell, TALL_NUMPAD_KEY_SIZE);

        // Onset text field
        onsetTextField.setHorizontalAlignment(SwingConstants.CENTER);
        onsetTextField.setText("00:00:00:000");
        onsetTextField.setToolTipText(resourceMap.getString(
                "onsetTextField.toolTipText"));
        onsetTextField.setName("findOnsetLabel");
        gridButtonPanel.add(makeLabelAndTextfieldPanel(new JLabel("Onset"), onsetTextField), WIDE_TEXT_FIELD_SIZE);

        // Create new cell setting offset button with zero
        createNewCellSettingOffset = buildButton("createNewCellAndSetOffset");
        gridButtonPanel.add(createNewCellSettingOffset, "span 2, w 95!, h 45!");

        // Set cell offset button
        setCellOffsetButton = buildButton("setCellOffset", "period");
        gridButtonPanel.add(setCellOffsetButton, NUMPAD_KEY_SIZE);

        // Offset text field
        offsetTextField.setHorizontalAlignment(SwingConstants.CENTER);
        offsetTextField.setText("00:00:00:000");
        offsetTextField.setToolTipText(resourceMap.getString(
                "offsetTextField.toolTipText"));
        offsetTextField.setEnabled(false); //do we really want this? i don't see what makes it different from onset
        offsetTextField.setName("findOffsetLabel");
        gridButtonPanel.add(makeLabelAndTextfieldPanel(new JLabel("Offset"), offsetTextField), WIDE_TEXT_FIELD_SIZE);


        getContentPane().setLayout(new MigLayout("ins 0, hidemode 3, fillx",
                "[growprio 0]0[]", ""));
        getContentPane().add(gridButtonPanel, "");
        getContentPane().setBackground(Color.WHITE);

        tracksPanel.setBackground(Color.WHITE);
        tracksPanel.setVisible(false);
        getContentPane().add(tracksPanel, "growx");

        pack();
    }

    private void addGoBackPair() {
        //Go back button - minus key
        goBackButton = buildButton("goBack", null);
        gridButtonPanel.add(goBackButton, NUMPAD_KEY_SIZE);

        //Go back text field
        goBackTextField.setHorizontalAlignment(SwingConstants.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setName("goBackTextField");
        gridButtonPanel.add(makeLabelAndTextfieldPanel(new JLabel("Jump back by"), goBackTextField), WIDE_TEXT_FIELD_SIZE);

    }

    private void addStepSizePanel() {
        //Go back text field
        stepSizeTextField.setHorizontalAlignment(SwingConstants.CENTER);
        stepSizeTextField.setName("stepSizeTextField");
        stepSizeTextField.setToolTipText("Double click to change");
        stepSizeTextField.setPreferredSize(new Dimension(WIDE_TEXT_FIELD_WIDTH - 10, 18));
        stepSizeTextField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2 && !viewers.isEmpty()) {
                    stepSizeTextField.setEnabled(true);
                    pressStop();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        stepSizeTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    float newfps = Float.parseFloat(stepSizeTextField.getText());
                    playbackModel.setCurrentFPS(newfps);
                    for (DataViewer dv : viewers) {
                        dv.setFrameRate(newfps);
                    }
                    stepSizeTextField.setEnabled(false);
                    updateStepSizePanelColor();

                }
            }

            public void keyTyped(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });


        stepSizePanel = makeLabelAndTextfieldPanel(stepSizeLabel, stepSizeTextField);
        gridButtonPanel.add(stepSizePanel, WIDE_TEXT_FIELD_SIZE);
        updateStepSizeTextField();

    }

    private void updateStepSizeTextField() {
        if (playbackModel == null) {
            stepSizeTextField.setEnabled(false);
        } else if (playbackModel.getCurrentFPS() == 0f) {
            stepSizeTextField.setEnabled(false);
            stepSizeTextField.setText("");
        } else {
            stepSizeTextField.setText(Float.toString(playbackModel.getCurrentFPS()));
        }

    }

    private void updateStepSizePanelColor() {
        boolean assumed = false;
        if (viewers != null) {
            for (DataViewer dv : viewers) {
                if (dv.usingAssumedFPS()) {
                    assumed = true;
                }
            }
        }
        if (assumed) stepSizePanel.setBackground(Color.RED);
        else stepSizePanel.setBackground(Color.LIGHT_GRAY);
    }


    //Create a JEditorPane with working hyperlinks
    private JEditorPane makeEditorPaneWithLinks(String s) {
        JEditorPane out = new JEditorPane("text/html", s);
        out.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                    try {
                        System.out.println("Attempting hyperlink");
                        java.awt.Desktop.getDesktop().browse(java.net.URI.create(e.getURL().toString()));
                    } catch (Exception ex) {
                        System.out.println("hyperlink failed");
                    }
            }
        });
        out.setEditable(false);
        out.setBackground((new JLabel()).getBackground());
        return out;
    }

    /**
     * Action to invoke when the user closes the data controller.
     *
     * @param evt The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) {
        setVisible(false);
        visible = false;
    }

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(
            final java.awt.event.ActionEvent evt) {
        LOGGER.info("Add data");

        PluginChooser chooser = null;

        switch (Datavyu.getPlatform()) {

            case WINDOWS:
                chooser = new WindowsJFC();

                break;

            case MAC:
                chooser = new MacOSJFC();

                break;

            case LINUX:
                chooser = new LinuxJFC();

                break;

            default:
                throw new NotImplementedException("Plugin chooser unimplemented.");
        }

        PluginManager pm = PluginManager.getInstance();
        chooser.addPlugin(pm.getPlugins());

        for (FileFilter ff : pm.getFileFilters()) {
            chooser.addChoosableFileFilter(ff);
            chooser.setFileFilter(ff);
        }

        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            openVideo(chooser);
        }
    }

    /**
     * Action to invoke when the user clicks the show tracks button.
     *
     * @param evt The event that triggered this action.
     */
    private void showTracksButtonActionPerformed(
            final java.awt.event.ActionEvent evt) {
        assert (evt.getSource() instanceof JButton);

        JButton button = (JButton) evt.getSource();
        ResourceMap resourceMap = Application.getInstance(
                org.datavyu.Datavyu.class).getContext().getResourceMap(
                DataControllerV.class);

        if (tracksPanelEnabled) {
            LOGGER.info("Show tracks (" + button.getName() + ")");

            // Panel is being displayed, hide it
            button.setIcon(resourceMap.getIcon(button.getName() + ".show.icon." + osModifier));
            button.setPressedIcon(resourceMap.getIcon(button.getName() + "Selected.show.icon." + osModifier));

        } else {
            LOGGER.info("Hide tracks (" + button.getName() + ")");

            // Panel is hidden, show it
            button.setIcon(resourceMap.getIcon(button.getName() + ".hide.icon." + osModifier));
            button.setPressedIcon(resourceMap.getIcon(button.getName() + "Selected.hide.icon." + osModifier));
        }

        tracksPanelEnabled = !tracksPanelEnabled;
        showTracksPanel(tracksPanelEnabled);
        updateCurrentTimeLabel();
    }

    /**
     * Adds a data viewer to this data controller.
     *
     * @param icon   The icon associated with the data viewer.
     * @param viewer The new viewer that we are adding to the data controller.
     * @param f      The parent file that the viewer represents.
     */
    private void addDataViewer(final ImageIcon icon, final DataViewer viewer,
                               final File f, final TrackPainter trackPainter) {
        assert viewer.getIdentifier() != null;

        addViewer(viewer, 0);

        // Add the file to the tracks information panel
        addTrack(viewer.getIdentifier(), icon, f.getAbsolutePath(), f.getName(),
                viewer.getDuration(), viewer.getOffset(), trackPainter);

        Datavyu.getProjectController().projectChanged();
    }

    /**
     * Returns set of dataviewers.
     *
     * @return set of dataviewers.
     */
    public Set<DataViewer> getDataViewers() {
        return viewers;
    }

    /**
     * Adds a track to the tracks panel.
     *
     * @param icon         Icon associated with the track
     * @param mediaPath    Absolute file path to the media file.
     * @param name         The name of the track to add.
     * @param duration     The duration of the data feed in milliseconds.
     * @param offset       The time offset of the data feed in milliseconds.
     * @param trackPainter Track painter to use.
     */
    public void addTrack(final Identifier id, final ImageIcon icon,
                         final String mediaPath, final String name, final long duration,
                         final long offset, final TrackPainter trackPainter) {
        mixerController.addNewTrack(id, icon, mediaPath, name, duration, offset,
                trackPainter);
    }

    /**
     * Add a viewer to the data controller with the given offset.
     *
     * @param viewer The data viewer to add.
     * @param offset The offset value in milliseconds.
     */
    public void addViewer(final DataViewer viewer, final long offset) {

        // Add the QTDataViewer to the list of viewers we are controlling.
        viewers.add(viewer);
        viewer.setParentController(this);
        viewer.setOffset(offset);

        // It is possible that the viewer will be handling its own window. In that case
        // dont worry about it.
        if (viewer.getParentJDialog() != null) {
            boolean visible = viewer.getParentJDialog().isVisible();
            Datavyu.getApplication().show(viewer.getParentJDialog());

            if (!visible) {
                viewer.getParentJDialog().setVisible(false);
            }
        }

        // adjust the overall frame rate.
        float fps = viewer.getFrameRate();

        if (fps > playbackModel.getCurrentFPS()) {
            playbackModel.setCurrentFPS(fps);
        }
        updateStepSizeTextField();
        updateStepSizePanelColor();

        // Update track viewer.
        long maxDuration = playbackModel.getMaxDuration();

        if ((viewer.getOffset() + viewer.getDuration()) > maxDuration) {
            maxDuration = viewer.getOffset() + viewer.getDuration();
        }

        // BugzID:2114 - If this is the first viewer we are adding, always reset
        // max duration.
        if (viewers.size() == 1) {
            maxDuration = viewer.getOffset() + viewer.getDuration();
        }

        mixerController.getMixerModel().getViewportModel().setViewportMaxEnd(
                maxDuration, true);
    }

    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    @Action
    public void setCellOnsetAction() {
        LOGGER.info("Set cell onset");
//        adjustClock(getCurrentTime());
        new SetSelectedCellStartTimeC(getCurrentTime());
        setOnsetField(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    @Action
    public void setCellOffsetAction() {
        LOGGER.info("Set cell offset");
//        adjustClock(getCurrentTime());
        new SetSelectedCellStopTimeC(getCurrentTime());
        setOffsetField(getCurrentTime());
    }

    /**
     * @param show true to show the tracks layout, false otherwise.
     */
    public void showTracksPanel(final boolean show) {
        tracksPanel.setVisible(show);
        tracksPanel.repaint();
        pack();
        validate();
    }

    /**
     * Handler for a TracksControllerEvent.
     *
     * @param e event
     */
    public void tracksControllerChanged(final TracksControllerEvent e) {

        switch (e.getTracksEvent()) {

            case CARRIAGE_EVENT:
                handleCarriageEvent((CarriageEvent) e.getEventObject());

                break;

            case TIMESCALE_EVENT:
                handleTimescaleEvent((TimescaleEvent) e.getEventObject());

                break;

            default:
                break;
        }
    }

    /**
     * Handles a TimescaleEvent.
     *
     * @param e The timescale event that triggered this action.
     */
    private void handleTimescaleEvent(final TimescaleEvent e) {
        final boolean wasClockRunning = !clock.isStopped();
        final boolean togglePlaybackMode = e.getTogglePlaybackMode();

        if (!wasClockRunning && togglePlaybackMode) {
            playAt(PLAY_RATE);
            clockStart(e.getTime());
        } else {
            gotoTime(e.getTime());
        }
    }

    private void gotoTime(final long time) {
        long newTime = time;

        if (newTime < playbackModel.getWindowPlayStart()) {
            newTime = playbackModel.getWindowPlayStart();
        }

        if (newTime > playbackModel.getWindowPlayEnd()) {
            newTime = playbackModel.getWindowPlayEnd();
        }

        clockStop(newTime);
        setCurrentTime(newTime);
        clock.setTime(newTime);
    }

    /**
     * Handles a CarriageEvent (when the carriage moves due to user
     * interaction).
     *
     * @param e The carriage event that triggered this action.
     */
    private void handleCarriageEvent(final CarriageEvent e) {

        switch (e.getEventType()) {

            case OFFSET_CHANGE:
                handleCarriageOffsetChangeEvent(e);

                break;

            case CARRIAGE_LOCK:
            case BOOKMARK_CHANGED:
            case BOOKMARK_SAVE:
                Datavyu.getProjectController().projectChanged();

                break;

            default:
                throw new IllegalArgumentException("Unknown carriage event.");
        }
    }

    /**
     * @param e
     */
    private void handleCarriageOffsetChangeEvent(final CarriageEvent e) {

        // Look through our data viewers and update the offset
        for (DataViewer viewer : viewers) {

            /*
             * Found our data viewer, update the DV offset and the settings in
             * the project file.
             */
            if (viewer.getIdentifier().equals(e.getTrackId())) {
                viewer.setOffset(e.getOffset());
            }
        }

        Datavyu.getProjectController().projectChanged();

        // Recalculate the maximum playback duration.
        long maxDuration = ViewportStateImpl.MINIMUM_MAX_END;

        for (DataViewer viewer : viewers) {

            if ((viewer.getDuration() + viewer.getOffset()) > maxDuration) {
                maxDuration = viewer.getDuration() + viewer.getOffset();
            }
        }

        mixerController.getMixerModel().getViewportModel().setViewportMaxEnd(
                maxDuration, false);
    }

    private void handleNeedleChanged(final PropertyChangeEvent e) {

        if (clock.isStopped()) {
            final long newTime = mixerController.getMixerModel()
                    .getNeedleModel().getCurrentTime();
            clock.setTime(newTime);
            clockStep(newTime);
        }

        updateCurrentTimeLabel();
    }

    private void handleRegionChanged(final PropertyChangeEvent e) {
        final RegionState region = mixerController.getMixerModel()
                .getRegionModel().getRegion();
        playbackModel.setWindowPlayStart(region.getRegionStart());
        playbackModel.setWindowPlayEnd(region.getRegionEnd());
    }

    private void handleViewportChanged(final PropertyChangeEvent e) {
        final ViewportState viewport = mixerController.getMixerModel()
                .getViewportModel().getViewport();
        playbackModel.setMaxDuration(viewport.getMaxEnd());
    }

    // -------------------------------------------------------------------------
    // Simulated clicks (for numpad calls)
    //

    /**
     * Simulates play button clicked.
     */
    public void pressPlay() {
        playButton.doClick();
    }

    public void pressShowTracksSmall() {
        showTracksSmallButton.doClick();
    }

    /**
     * Simulates forward button clicked.
     */
    public void pressForward() {
        forwardButton.doClick();
    }

    /**
     * Simulates rewind button clicked.
     */
    public void pressRewind() {
        rewindButton.doClick();
    }

    /**
     * Simulates pause button clicked.
     */
    public void pressPause() {
        pauseButton.doClick();
    }

    /**
     * Simulates stop button clicked.
     */
    public void pressStop() {
        stopButton.doClick();
    }

    /**
     * Simulates shuttle forward button clicked.
     */
    public void pressShuttleForward() {
        shuttleForwardButton.doClick();
    }

    /**
     * Simulates shuttle back button clicked.
     */
    public void pressShuttleBack() {
        shuttleBackButton.doClick();
    }

    /**
     * Simulates find button clicked.
     */
    public void pressFind() {
        findButton.doClick();
    }

    /**
     * Simulates set cell onset button clicked.
     */
    public void pressSetCellOnset() {
        setCellOnsetButton.doClick();
    }

    /**
     * Simulates set cell offset button clicked.
     */
    public void pressSetCellOffsetPeriod() {
        setCellOffsetButton.doClick();
    }

    /**
     * Simulates set cell offset button clicked.
     */
    public void pressSetCellOffsetNine() {
        ninesetCellOffsetButton.doClick();
    }

    /**
     * Simulates set new cell onset button clicked.
     */
    public void pressPointCell() {
        pointCellButton.doClick();
    }

    /**
     * Simulates go back button clicked.
     */
    public void pressGoBack() {
        goBackButton.doClick();
    }

    /**
     * Simulates create new cell button clicked.
     */
    public void pressCreateNewCell() {
        createNewCell.doClick();
    }

    /**
     * Simulates create new cell setting offset button clicked.
     */
    public void pressCreateNewCellSettingOffset() {
        createNewCellSettingOffset.doClick();
    }

    // ------------------------------------------------------------------------
    // Playback actions
    //

    /**
     * Action to invoke when the user clicks on the play button.
     */
    @Action
    public void playAction() {
        LOGGER.info("Play");
        System.out.println("Play button...");

        // BugzID:464 - When stopped at the end of the region of interest.
        // pressing play jumps the stream back to the start of the video before
        // starting to play again.
        if ((getCurrentTime() >= playbackModel.getWindowPlayEnd())
                && clock.isStopped()) {
            jumpTo(playbackModel.getWindowPlayStart());
        }

        playAt(PLAY_RATE);
    }

    /**
     * Action to invoke when the user clicks on the fast forward button.
     */
    @Action
    public void forwardAction() {
        LOGGER.info("Fast forward");
        playAt(FFORWARD_RATE);
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    @Action
    public void rewindAction() {
        LOGGER.info("Rewind");
        playAt(REWIND_RATE);
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     */
    @Action
    public void pauseAction() {
        LOGGER.info("Pause");
        System.out.println("Pause button...");
        System.out.println(System.currentTimeMillis());

        // Resume from pause at playback rate prior to pause.
        if (clock.isStopped()) {
            shuttleAt(playbackModel.getPauseRate());

            // Pause views - store current playback rate.
        } else {
            playbackModel.setPauseRate(clock.getRate());
            clock.stop();
            lblSpeed.setText("["
                    + FloatUtils.doubleToFractionStr(
                    Double.valueOf(playbackModel.getPauseRate())) + "]");
        }
    }

    /**
     * Action to invoke when the user clicks on the stop button.
     */
    @Action
    public void stopAction() {
        LOGGER.info("Stop event");
        System.out.println("Stop button");
        System.out.println(System.currentTimeMillis());
        clock.stop();
        playbackModel.setPauseRate(0);
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behavior for reversing shuttle direction?
     */
    @Action
    public void shuttleForwardAction() {
        LOGGER.info("Shuttle forward");
        shuttle(1);
    }

    /**
     * Action to invoke when the user clicks on the shuttle back button.
     */
    @Action
    public void shuttleBackAction() {
        LOGGER.info("Shuttle back");
        shuttle(-1);
    }

    /**
     * Searches the shuttle rates array for the given rate, and returns the
     * index.
     *
     * @param pRate The rate to search for.
     * @return The index of the rate, or -100 if not found.
     */
    private int findShuttleIndex(final float pRate) {
        for (int i = 0; i < SHUTTLE_RATES.length; i++) {

            if (SHUTTLE_RATES[i] == pRate) {
                return i;
            }
        }

        return -100;
    }

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds The time to use when populating the find field.
     */
    public void setOnsetField(final long milliseconds) {
        onsetTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Populates the find offset time in the controller.
     *
     * @param milliseconds The time to use when populating the find field.
     */
    public void setOffsetField(final long milliseconds) {
        offsetTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Action to invoke when the user clicks on the find button.
     */
    @Action
    public void findAction() {
        LOGGER.info("Find");

        if (shiftMask) {
            findOffsetAction();
        } else {

            try {
                jumpTo(CLOCK_FORMAT.parse(onsetTextField.getText()).getTime());
            } catch (ParseException e) {
                LOGGER.error("unable to find within video", e);
            }
        }
    }

    /**
     * Action to invoke when the user holds shift down.
     */
    public void findOffsetAction() {

        try {
            jumpTo(CLOCK_FORMAT.parse(offsetTextField.getText()).getTime());
        } catch (ParseException e) {
            LOGGER.error("unable to find within video", e);
        }
    }

    public void toggleCellHighlighting() {
        highlightCells = !highlightCells;
    }

    public boolean getCellHighlighting() {
        return highlightCells;
    }

    public void clearRegionOfInterestAction() {
        mixerController.clearRegionOfInterest();
    }

    /**
     * Sets the playback region of interest to lie from the find time to offset
     * time.
     */
    public void setRegionOfInterestAction() {

        if (this.getDataViewers().size() > 0) {
            try {
                final long findTextTime = CLOCK_FORMAT.parse(
                        onsetTextField.getText()).getTime();
                final long findOffsetTime = CLOCK_FORMAT.parse(
                        offsetTextField.getText()).getTime();

                final long newWindowPlayStart = findTextTime;
                final long newWindowPlayEnd = (findOffsetTime > newWindowPlayStart)
                        ? findOffsetTime : newWindowPlayStart;
                mixerController.getMixerModel().getRegionModel().setPlaybackRegion(
                        newWindowPlayStart, newWindowPlayEnd);
                mixerController.getMixerModel().getNeedleModel().setCurrentTime(
                        newWindowPlayStart);
            } catch (ParseException e) {
                LOGGER.error("Unable to set playback region of interest", e);
            }
        }
    }

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    @Action
    public void goBackAction() {

        try {
            LOGGER.info("Go back");

            long j = -CLOCK_FORMAT.parse(goBackTextField.getText()).getTime();
            jump(j);
            System.out.println("JUMP BACK!");

            //Bugzilla bug #179 - undoes the behavior from FogBugz BugzID:721
            // BugzID:721 - After going back - start playing again.
            //playAt(PLAY_RATE);

        } catch (ParseException e) {
            LOGGER.error("unable to find within video", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    @Action
    public void jogBackAction() {
        LOGGER.info("Jog back");

        if (!clock.isStopped()) {
            clockStop(clock.getTime());
        } else {

            int mul = 1;

            if (shiftMask) {
                mul = SHIFTJOG;
            }

            if (ctrlMask) {
                mul = CTRLJOG;
            }

            long stepSize = ((-ONE_SECOND) / (long) playbackModel.getCurrentFPS());
            long nextTime = (long) (mul * stepSize);

            long mod = clock.getTime() % stepSize;

            if (mod != 0) {
                nextTime = -mod;
            }

        /* BugzID:1361 - Disallow jog to skip past the region boundaries. */
            if ((clock.getTime() + nextTime) > playbackModel.getWindowPlayStart()) {
                stopAction();
                jump(nextTime);
            } else {
                jumpTo(playbackModel.getWindowPlayStart());
            }
        }
    }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    @Action
    public void jogForwardAction() {
        LOGGER.info("Jog forward");

        if (!clock.isStopped()) {
            clockStop(clock.getTime());
        } else {

            int mul = 1;

            if (shiftMask) {
                mul = SHIFTJOG;
            }

            if (ctrlMask) {
                mul = CTRLJOG;
            }

            long stepSize = ((ONE_SECOND) / (long) playbackModel.getCurrentFPS());
            long nextTime = (long) (mul * stepSize);

        /* BugzID:1544 - Preserve precision - force jog to frame markers. */
            long mod = (clock.getTime() % stepSize);

            if (mod != 0) {
                nextTime = nextTime + stepSize - mod;
            }

        /* BugzID:1361 - Disallow jog to skip past the region boundaries. */
            if ((clock.getTime() + nextTime) < playbackModel.getWindowPlayEnd()) {
                stopAction();
                jump(nextTime);
            } else {
                jumpTo(playbackModel.getWindowPlayEnd());
            }
        }
    }

    // ------------------------------------------------------------------------
    // [private] play back action helper functions
    //

    /**
     * @param rate Rate of play.
     */
    private void playAt(final float rate) {
        playbackModel.setPauseRate(0);
        shuttleAt(rate);
    }

    /**
     * @param direction The required direction of the shuttle.
     */
    private void shuttle(final int shuttlejump) {
        float currentRate = clock.getRate();

        if (currentRate == 0) {
            currentRate = playbackModel.getPauseRate();
        }

        try {
            shuttleAt(SHUTTLE_RATES[findShuttleIndex(currentRate) + shuttlejump]);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("Error finding shuttle index given current rate: " + currentRate);
        }
    }

    /**
     * @param rate Rate of shuttle.
     */
    private void shuttleAt(final float rate) {
        clock.setRate(rate);
        clock.start();
    }

    /**
     * @param step Milliseconds to jump.
     */
    private void jump(final long step) {
        if ((clock.getTime() + step) > playbackModel.getWindowPlayStart()) {
            stopAction();
            clock.stepTime(step);
        } else {
            jumpTo(playbackModel.getWindowPlayStart());
        }
    }

    /**
     * @param time Absolute time to jump to.
     */
    private void jumpTo(final long time) {
        synchronized (clock) {
            clock.stop();
            clock.setTime(time);
        }
    }

    // -------------------------------------------------------------------------
    //
    //

    /**
     * Action to invoke when the user clicks on the create new cell button.
     */
    @Action
    public void createNewCellAction() {
        LOGGER.info("New cell");
        adjustClock(getCurrentTime());
        CreateNewCellC controller = new CreateNewCellC();
        controller.createDefaultCell(true);
    }

    /**
     * Action to invoke when the user clicks on the new cell button.
     */
    @Action
    public void createNewCellAndSetOffsetAction() {
        LOGGER.info("New cell set offset");
        adjustClock(getCurrentTime());
        new CreateNewCellC(getCurrentTime(), true);
    }

    /**
     * Action to invoke when the user clicks on the new cell offset button.
     */
    @Action
    public void pointCellAction() {
        LOGGER.info("Set new cell offset");

        adjustClock(getCurrentTime());
        long time = getCurrentTime();
        new CreateNewCellC(time, false);
        new SetNewCellStopTimeC(time);
        setOffsetField(time);
    }

    /**
     * Action to invoke when the user clicks on the sync video button.
     */
    @Action
    public void syncVideoAction() {
        //not yet implemented
    }

    @Override
    public void propertyChange(final PropertyChangeEvent e) {

        if (e.getSource()
                == mixerController.getNeedleController().getNeedleModel()) {
            handleNeedleChanged(e);
        } else if (e.getSource()
                == mixerController.getMixerModel().getViewportModel()) {
            handleViewportChanged(e);
        } else if (e.getSource()
                == mixerController.getRegionController().getModel()) {
            handleRegionChanged(e);
        }
    }

    public float getCurrentFPS() {
        return playbackModel.getCurrentFPS();
    }

    public ClockTimer getClock() {
        return clock;
    }
}
