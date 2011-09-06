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
package org.openshapa.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SimpleTimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.NotImplementedException;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.OpenSHAPA;

import org.openshapa.OpenSHAPA.Platform;

import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.SetNewCellStopTimeC;
import org.openshapa.controllers.SetSelectedCellStartTimeC;
import org.openshapa.controllers.SetSelectedCellStopTimeC;
import org.openshapa.controllers.component.MixerController;
import org.openshapa.controllers.id.IDController;
import org.openshapa.controllers.layout.SingleWindowTiler;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.TimescaleEvent;
import org.openshapa.event.component.TracksControllerEvent;
import org.openshapa.event.component.TracksControllerListener;

import org.openshapa.models.PlaybackModel;
import org.openshapa.models.component.MixerConstants;
import org.openshapa.models.component.RegionState;
import org.openshapa.models.component.TimescaleConstants;
import org.openshapa.models.component.ViewportStateImpl;
import org.openshapa.models.component.ViewportState;
import org.openshapa.models.id.Identifier;

import org.openshapa.plugins.PluginManager;

import org.openshapa.util.ClockTimer;
import org.openshapa.util.ClockTimer.ClockListener;
import org.openshapa.util.FloatUtils;

import org.openshapa.views.component.TrackPainter;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.awt.event.WindowListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.Plugin;


/**
 * Quicktime video controller.
 */
public final class DataControllerV extends OpenSHAPADialog
    implements ClockListener, TracksControllerListener, DataController,
        PropertyChangeListener {

    private static final double LOW_RATE = 5D;

    /** One second in milliseconds. */
    private static final long ONE_SECOND = 1000L;

    /** Rate of playback for rewinding. */
    private static final float REWIND_RATE = -32F;

    /** Rate of normal playback. */
    private static final float PLAY_RATE = 1F;

    /** Rate of playback for fast forwarding. */
    private static final float FFORWARD_RATE = 32F;

    /** Sequence of allowable shuttle rates. */
    private static final float[] SHUTTLE_RATES;

    /**
     * The threshold to use while synchronising viewers (augmented by rate).
     */
    private static final long SYNC_THRESH = 200;

    /**
     * How often to synchronise the viewers with the master clock.
     */
    private static final long SYNC_PULSE = 500;

    // Initialize SHUTTLE_RATES
    // values: [ (2^-5), ..., (2^0), ..., (2^5) ]

    /** The max power used for playback rates; i.e. 2^POWER = max. */
    private static final int POWER = 5;

    static {
        SHUTTLE_RATES = new float[(2 * POWER) + 1];

        float value = 1;
        SHUTTLE_RATES[POWER] = value;

        for (int i = 1; i <= POWER; ++i) {
            value *= 2;
            SHUTTLE_RATES[POWER + i] = value;
            SHUTTLE_RATES[POWER - i] = 1F / value;
        }
    }

    /** The jump multiplier for shift-jogging. */
    private static final int SHIFTJOG = 5;

    /** The jump multiplier for ctrl-shift-jogging. */
    private static final int CTRLSHIFTJOG = 10;

    /** Format for representing time. */
    private static final DateFormat CLOCK_FORMAT;
    private static final DateFormat CLOCK_FORMAT_HTML;

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

    /**
     * Enumeration of shuttle directions.
     */
    enum ShuttleDirection {

        /** The backwards playrate. */
        BACKWARDS(-1),

        /** Playrate for undefined shuttle speeds. */
        UNDEFINED(0),

        /** The playrate for forwards (normal) playrate. */
        FORWARDS(1);

        /** Stores the shuttle direction. */
        private int parameter;

        /**
         * Sets the shuttle direction.
         *
         * @param p
         *            The new shuttle direction.
         */
        ShuttleDirection(final int p) {
            parameter = p;
        }

        /** @return The shuttle direction. */
        public int getParameter() {
            return parameter;
        }
    }

    // -------------------------------------------------------------------------
    // [static]
    //
    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DataControllerV.class);

    /** Determines whether or not Shift is being held. */
    private boolean shiftMask = false;

    /** Determines whether or not Control is being held. */
    private boolean ctrlMask = false;

    // -------------------------------------------------------------------------
    //
    //
    /** The list of viewers associated with this controller. */
    private Set<DataViewer> viewers;

    /** Shuttle status flag. */
    private ShuttleDirection shuttleDirection = ShuttleDirection.UNDEFINED;

    /** Clock timer. */
    private ClockTimer clock = new ClockTimer();

    /** Is the tracks panel currently shown? */
    private boolean tracksPanelEnabled = true;

    /** The controller for manipulating tracks. */
    private MixerController mixerController;

    /** */
    private javax.swing.JButton createNewCell;

    /** */
    private javax.swing.JButton createNewCellSettingOffset;

    /** */
    private javax.swing.JButton findButton;

    /** */
    private javax.swing.JTextField findOffsetField;

    /** */
    private javax.swing.JTextField findTextField;

    /** */
    private javax.swing.JButton forwardButton;

    /** */
    private javax.swing.JButton goBackButton;

    /** */
    private javax.swing.JTextField goBackTextField;

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
    private javax.swing.JButton osxsetCellOffsetButton;

    /** */
    private javax.swing.JButton setCellOffsetButton;

    /** */
    private javax.swing.JButton setCellOnsetButton;

    /** */
    private javax.swing.JButton pointCellButton;

    /** */
    private javax.swing.JButton showTracksButton;

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

    /** Model containing playback information. */
    private PlaybackModel playbackModel;

    // -------------------------------------------------------------------------
    // [initialization]
    //
    /**
     * Constructor. Creates a new DataControllerV.
     *
     * @param parent
     *            The parent of this form.
     * @param modal
     *            Should the dialog be modal or not?
     */
    public DataControllerV(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        clock.registerListener(this);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        if (OpenSHAPA.getPlatform() == Platform.MAC) {
            initComponentsMac();
        } else {
            initComponents();
        }

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
     * @param jd
     *            The file chooser used to open the data source.
     */
    private void openVideo(final PluginChooser chooser) {
        Plugin plugin = chooser.getSelectedPlugin();
        File f = chooser.getSelectedFile();

        if (plugin != null) {

            try {
                DataViewer dataViewer = plugin.getNewDataViewer(OpenSHAPA
                        .getApplication().getMainFrame(), false);
                dataViewer.setIdentifier(IDController.generateIdentifier());
                dataViewer.setDataFeed(f);
                dataViewer.seekTo(clock.getTime());
                dataViewer.setDatastore(OpenSHAPA.getProjectController()
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
                JOptionPane.showMessageDialog(null,
                    "Could not open data source: " + t.getMessage());
            }
        }
    }

    /**
     * Tells the Data Controller if shift is being held or not.
     *
     * @param shift
     *            True for shift held; false otherwise.
     */
    public void setShiftMask(final boolean shift) {
        shiftMask = shift;
    }

    /**
     * Tells the Data Controller if ctrl is being held or not.
     *
     * @param ctrl
     *            True for ctrl held; false otherwise.
     */
    public void setCtrlMask(final boolean ctrl) {
        ctrlMask = ctrl;
    }

    // -------------------------------------------------------------------------
    // [interface] org.openshapa.util.ClockTimer.Listener
    //
    /**
     * @param time
     *            Current clock time in milliseconds.
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
     * @param time
     *            Current clock time in milliseconds.
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
                        > (SYNC_PULSE * clock.getRate())) {
                    long thresh = (long) (SYNC_THRESH
                            * Math.abs(clock.getRate()));
                    playbackModel.setLastSync(time);

                    for (DataViewer v : viewers) {

                        /*
                         * Use offsets to determine if the video file should
                         * start playing.
                         */

                        if (!v.isPlaying() && isWithinPlayRange(time, v)) {
                            v.seekTo(time - v.getOffset());
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
                        if (v.isPlaying()
                                && (Math.abs(
                                        v.getCurrentTime()
                                        - (time - v.getOffset())) > thresh)) {
                            v.seekTo(time - v.getOffset());
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
     * @param time
     *            The time we wish to play at or seek to.
     * @param view
     *            The DataViewer to check.
     * @return True if data exists at this time, and false otherwise.
     */
    private boolean isWithinPlayRange(final long time, final DataViewer view) {
        return (time >= view.getOffset())
            && (time < (view.getOffset() + view.getDuration()));
    }

    /**
     * @param time
     *            Current clock time in milliseconds.
     */
    public void clockStop(final long time) {
        clock.stop();
        resetSync();
        setCurrentTime(time);

        for (DataViewer viewer : viewers) {
            viewer.stop();

            if (isWithinPlayRange(time, viewer)) {
                viewer.seekTo(time - viewer.getOffset());
            }
        }
    }

    /**
     * @param rate
     *            Current (updated) clock rate.
     */
    public void clockRate(final float rate) {
        resetSync();
        lblSpeed.setText(FloatUtils.doubleToFractionStr(new Double(rate)));

        long time = getCurrentTime();

        // If rate is faster than two times - we need to fake playback to give
        // the illusion of 'smooth'. We do this by stopping the dataviewer and
        // doing many seekTo's to grab individual frames.
        if (Math.abs(rate) > 2.0) {
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
     * @param time
     *            Current clock time in milliseconds.
     */
    public void clockStep(final long time) {
        resetSync();
        setCurrentTime(time);

        for (DataViewer viewer : viewers) {

            if (isWithinPlayRange(time, viewer)) {
                viewer.seekTo(time - viewer.getOffset());
            }
        }
    }

    /**
     * @return the mixer controller.
     */
    public MixerController getMixerController() {
        return mixerController;
    }

    /**
     * Set time location for data streams.
     *
     * @param milliseconds
     *            The millisecond time.
     */
    public void setCurrentTime(final long milliseconds) {
        resetSync();
        updateCurrentTimeLabel();
        mixerController.getMixerModel().getNeedleModel().setCurrentTime(
            milliseconds);
    }

    private void updateCurrentTimeLabel() {
        timestampLabel.setText(tracksPanelEnabled
                ? CLOCK_FORMAT_HTML.format(getCurrentTime())
                : CLOCK_FORMAT.format(getCurrentTime()));
    }

    /**
     * Get the current master clock time for the controller.
     *
     * @return Time in milliseconds.
     */
    private long getCurrentTime() {
        return clock.getTime();
    }

    /** Recalculates the maximum viewer duration. */
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
     * @param viewer
     *            The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    public boolean shutdown(final DataViewer viewer) {

        // Was the viewer removed.
        boolean removed = viewers.remove(viewer);

        if (removed) {

            // BugzID:2000
            viewer.removeViewerStateListener(
                mixerController.getTracksEditorController()
                    .getViewerStateListener(viewer.getIdentifier()));

            // Recalculate the maximum playback duration.
            updateMaxViewerDuration();

            // Remove the data viewer from the tracks panel.
            mixerController.deregisterTrack(viewer.getIdentifier());

            // Data viewer removed, mark project as changed.
            OpenSHAPA.getProjectController().projectChanged();
        }

        return removed;
    }

    /**
     * Remove the specified viewer from the controller.
     *
     * @param id
     *            The identifier of the viewer to shutdown.
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

        // BugzID:2000
        viewer.removeViewerStateListener(
            mixerController.getTracksEditorController().getViewerStateListener(
                viewer.getIdentifier()));

        // Recalculate the maximum playback duration.
        updateMaxViewerDuration();

        // Remove the data viewer from the tracks panel.
        mixerController.deregisterTrack(viewer.getIdentifier());

        // Data viewer removed, mark project as changed.
        OpenSHAPA.getProjectController().projectChanged();

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

        if (viewer != null) {
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
     * @return True if the plugin should be removed, false otherwise.
     */
    private boolean shouldRemove() {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
            .getResourceMap(OpenSHAPA.class);

        String cancel = "Cancel";
        String ok = "OK";

        String[] options = new String[2];

        if (OpenSHAPA.getPlatform() == Platform.MAC) {
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
        return (OpenSHAPA.getPlatform() == Platform.MAC) ? (selection == 1)
                                                         : (selection == 0);
    }

    /**
     * Helper method for Building a button for the data controller - sets the
     * icon, selected icon, action map and name.
     *
     * @param rMap The resource map that holds the icons for this button.
     * @param aMap The action map holding the action that this button invokes.
     * @param name The prefix to use when looking for actions and buttons.
     * @param modifier The modifier (if any) to apply to the prefix. Maybe null.
     *
     * @return A configured button.
     */
    private JButton buildButton(final ResourceMap rMap,
                                final ActionMap aMap,
                                final String name,
                                final String modifier) {

        JButton result = new JButton();
        result.setAction(aMap.get(name + "Action"));
        if (modifier == null) {
            result.setIcon(rMap.getIcon(name + "Button.icon"));
            result.setPressedIcon(rMap.getIcon(name + "SelectedButton.icon"));
        } else {
            result.setIcon(rMap.getIcon(modifier + name + "Button.icon"));
            result.setPressedIcon(rMap.getIcon(modifier + name + "SelectedButton.icon"));
        }
        result.setFocusPainted(false);
        result.setName(name + "Button");

        return result;
    }

    /**
     * Initialize the view for Macs.
     */
    private void initComponentsMac() {
        gridButtonPanel = new javax.swing.JPanel();

        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        lblSpeed = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel(new MigLayout("fill"));

        final int fontSize = 11;

        org.jdesktop.application.ResourceMap resourceMap =
            org.jdesktop.application.Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);
        setTitle(resourceMap.getString("title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        gridButtonPanel.setBackground(Color.WHITE);
        gridButtonPanel.setLayout(new MigLayout("wrap 5, ins 15 2 15 2"));

        // Add data button
        addDataButton.setText(resourceMap.getString("addDataButton.text"));
        addDataButton.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        addDataButton.setFocusPainted(false);
        addDataButton.setName("addDataButton");
        addDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    openVideoButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(addDataButton, "span 2, w 90!, h 25!");

        // Timestamp panel
        JPanel timestampPanel = new JPanel(new MigLayout("",
                    "push[][][]0![]push"));
        timestampPanel.setOpaque(false);

        // Timestamp label
        timestampLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        timestampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timestampLabel.setName("timestampLabel");
        timestampPanel.add(timestampLabel);

        jLabel1.setText("@");
        timestampPanel.add(jLabel1);

        lblSpeed.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
                2));
        lblSpeed.setName("lblSpeed");
        lblSpeed.setText("0");
        timestampPanel.add(lblSpeed);

        jLabel2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        jLabel2.setText("x");
        timestampPanel.add(jLabel2);

        // Set cell onset button
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application
            .getInstance(org.openshapa.OpenSHAPA.class).getContext()
            .getActionMap(DataControllerV.class, this);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        // placeholder
        setCellOnsetButton = buildButton(resourceMap, actionMap,
                                         "setCellOnset", null);
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Sync button
        pointCellButton = buildButton(resourceMap, actionMap,
                                 "pointCell", null);
        gridButtonPanel.add(pointCellButton, "w 45!, h 45!");

        // Set cell onset button.
        osxsetCellOffsetButton = buildButton(resourceMap, actionMap,
                                         "setCellOffset", "osx");
        gridButtonPanel.add(osxsetCellOffsetButton, "w 45!, h 45!");

        // Instant cell button.
        JButton placeholder2 = new JButton();
        placeholder2.setEnabled(false);
        placeholder2.setFocusPainted(false);
        gridButtonPanel.add(placeholder2, "w 45!, h 45!");

        // Sync video button
        JButton placeholder3 = new JButton();
        placeholder3.setEnabled(false);
        placeholder3.setFocusPainted(false);
        gridButtonPanel.add(placeholder3, "w 80!, h 45!");

        // Rewind video button
        rewindButton = buildButton(resourceMap, actionMap,
                                   "rewind", null);
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton = buildButton(resourceMap, actionMap,
                                 "play", null);
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton = buildButton(resourceMap, actionMap,
                                    "forward", null);
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Go back button
        goBackButton = buildButton(resourceMap, actionMap,
                                   "goBack", null);
        gridButtonPanel.add(goBackButton, "w 45!, h 45!");

        // Go back text field
        goBackTextField.setHorizontalAlignment(SwingConstants.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setName("goBackTextField");
        gridButtonPanel.add(goBackTextField, "w 80!, h 45!");

        // Shuttle back button
        shuttleBackButton = buildButton(resourceMap, actionMap,
                                        "shuttleBack", null);
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton = buildButton(resourceMap, actionMap,
                                 "stop", null);
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton = buildButton(resourceMap, actionMap,
                                           "shuttleForward", null);
        gridButtonPanel.add(shuttleForwardButton, "w 45!, h 45!");

        // Find button
        findButton = buildButton(resourceMap, actionMap,
                                 "find", null);
        gridButtonPanel.add(findButton, "w 45!, h 45!");

        // Find text field
        findTextField.setHorizontalAlignment(SwingConstants.CENTER);
        findTextField.setText("00:00:00:000");
        findTextField.setName("findOnsetLabel");
        gridButtonPanel.add(findTextField, "w 80!, h 45!");

        // Jog back button
        jogBackButton = buildButton(resourceMap, actionMap,
                                    "jogBack", null);
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton = buildButton(resourceMap, actionMap,
                                  "pause", null);
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton = buildButton(resourceMap, actionMap,
                                       "jogForward", null);
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell = buildButton(resourceMap, actionMap, "createNewCell", null);
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setHorizontalTextPosition(
            javax.swing.SwingConstants.CENTER);
        gridButtonPanel.add(createNewCell, "span 1 2, w 45!, h 92!");

        // Find offset field
        findOffsetField.setHorizontalAlignment(SwingConstants.CENTER);
        findOffsetField.setText("00:00:00:000");
        findOffsetField.setToolTipText(resourceMap.getString(
                "findOffsetField.toolTipText"));
        findOffsetField.setEnabled(false);
        findOffsetField.setName("findOffsetLabel");
        gridButtonPanel.add(findOffsetField, "w 80!, h 45!");

        // Create new cell setting offset button
        createNewCellSettingOffset = buildButton(resourceMap, actionMap,
                                                 "createNewCellAndSetOnset", null);
        gridButtonPanel.add(createNewCellSettingOffset, "span 2, w 92!, h 45!");

        // Set cell offset button
        setCellOffsetButton = buildButton(resourceMap, actionMap,
                                          "setCellOffset", null);
        gridButtonPanel.add(setCellOffsetButton, "w 45!, h 45!");

        // Show tracks button
        showTracksButton.setIcon(resourceMap.getIcon(
                "showTracksButton.hide.icon"));
        showTracksButton.setName("showTracksButton");
        showTracksButton.getAccessibleContext().setAccessibleName(
            "Show Tracks");
        showTracksButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    showTracksButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(showTracksButton, "w 80!, h 45!");

        getContentPane().setLayout(new MigLayout("hidemode 3, fillx",
                "[growprio 0]0[]", ""));
        getContentPane().add(gridButtonPanel, "");
        getContentPane().setBackground(Color.WHITE);

        tracksPanel.setBackground(Color.WHITE);
        tracksPanel.setVisible(false);
        getContentPane().add(tracksPanel, "growx");

        pack();
    }

    /**
     * Initialize the view for OS other than Macs.
     */
    private void initComponents() {
        gridButtonPanel = new javax.swing.JPanel();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        addDataButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        lblSpeed = new javax.swing.JLabel();
        createNewCell = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        findOffsetField = new javax.swing.JTextField();
        showTracksButton = new javax.swing.JButton();
        tracksPanel = new javax.swing.JPanel(new MigLayout("fill"));

        final int fontSize = 11;

        org.jdesktop.application.ResourceMap resourceMap =
            org.jdesktop.application.Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);
        setTitle(resourceMap.getString("title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(
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
                    openVideoButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(addDataButton, "span 2, w 90!, h 25!");

        // Timestamp panel
        JPanel timestampPanel = new JPanel(new MigLayout("",
                    "push[][][]0![]push"));
        timestampPanel.setOpaque(false);

        // Timestamp label
        timestampLabel.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        timestampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timestampLabel.setName("timestampLabel");
        timestampPanel.add(timestampLabel);

        jLabel1.setText("@");
        timestampPanel.add(jLabel1);

        lblSpeed.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
                2));
        lblSpeed.setName("lblSpeed");
        lblSpeed.setText("0");
        timestampPanel.add(lblSpeed);

        jLabel2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        jLabel2.setText("x");
        timestampPanel.add(jLabel2);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application
            .getInstance(org.openshapa.OpenSHAPA.class).getContext()
            .getActionMap(DataControllerV.class, this);

        gridButtonPanel.add(timestampPanel, "span 3, pushx, growx");

        JButton placeholder = new JButton();
        placeholder.setEnabled(false);
        placeholder.setFocusPainted(false);
        gridButtonPanel.add(placeholder, "w 45!, h 45!");

        // Set cell onset button
        setCellOnsetButton = buildButton(resourceMap, actionMap,
                                         "setCellOnset", "win");
        gridButtonPanel.add(setCellOnsetButton, "w 45!, h 45!");

        // Set new cell offset button
        pointCellButton = buildButton(resourceMap, actionMap,
                                      "pointCell", "win");
        gridButtonPanel.add(pointCellButton, "w 45!, h 45!");

        // Go back button
        goBackButton = buildButton(resourceMap, actionMap, "goBack", null);
        gridButtonPanel.add(goBackButton, "w 45!, h 45!");

        // Sync video button
        JButton placeholder2 = new JButton();
        placeholder2.setEnabled(false);
        placeholder2.setFocusPainted(false);
        gridButtonPanel.add(placeholder2, "w 80!, h 45!");

        // Rewind video button
        rewindButton = buildButton(resourceMap, actionMap, "rewind", null);
        gridButtonPanel.add(rewindButton, "w 45!, h 45!");

        // Play video button
        playButton = buildButton(resourceMap, actionMap, "play", null);
        playButton.setRequestFocusEnabled(false);
        gridButtonPanel.add(playButton, "w 45!, h 45!");

        // Fast forward button
        forwardButton = buildButton(resourceMap, actionMap, "forward", null);
        gridButtonPanel.add(forwardButton, "w 45!, h 45!");

        // Find button
        findButton = buildButton(resourceMap, actionMap, "find", "win");
        gridButtonPanel.add(findButton, "span 1 2, w 45!, h 95!");

        // Go back text field
        goBackTextField.setHorizontalAlignment(SwingConstants.CENTER);
        goBackTextField.setText("00:00:05:000");
        goBackTextField.setName("goBackTextField");
        gridButtonPanel.add(goBackTextField, "w 80!, h 45!");

        // Shuttle back button
        shuttleBackButton = buildButton(resourceMap, actionMap,
                                        "shuttleBack", null);
        gridButtonPanel.add(shuttleBackButton, "w 45!, h 45!");

        // Stop button
        stopButton = buildButton(resourceMap, actionMap, "stop", null);
        gridButtonPanel.add(stopButton, "w 45!, h 45!");

        // Shuttle forward button
        shuttleForwardButton = buildButton(resourceMap, actionMap,
                                           "shuttleForward", null);
        gridButtonPanel.add(shuttleForwardButton, "w 45!, h 45!");

        // Find text field
        findTextField.setHorizontalAlignment(SwingConstants.CENTER);
        findTextField.setText("00:00:00:000");
        findTextField.setName("findOnsetLabel");
        gridButtonPanel.add(findTextField, "w 80!, h 45!");

        // Jog back button
        jogBackButton = buildButton(resourceMap, actionMap, "jogBack", null);
        gridButtonPanel.add(jogBackButton, "w 45!, h 45!");

        // Pause button
        pauseButton = buildButton(resourceMap, actionMap, "pause", null);
        gridButtonPanel.add(pauseButton, "w 45!, h 45!");

        // Jog forward button
        jogForwardButton = buildButton(resourceMap, actionMap,
                                       "jogForward", null);
        gridButtonPanel.add(jogForwardButton, "w 45!, h 45!");

        // Create new cell button
        createNewCell = buildButton(resourceMap, actionMap,
                                    "createNewCell", null);
        createNewCell.setAlignmentY(0.0F);
        createNewCell.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridButtonPanel.add(createNewCell, "span 1 2, w 45!, h 95!");

        // Find offset field
        findOffsetField.setHorizontalAlignment(SwingConstants.CENTER);
        findOffsetField.setText("00:00:00:000");
        findOffsetField.setToolTipText(resourceMap.getString(
                "findOffsetField.toolTipText"));
        findOffsetField.setEnabled(false);
        findOffsetField.setName("findOffsetLabel");
        gridButtonPanel.add(findOffsetField, "w 80!, h 45!");

        // Create new cell setting offset button
        createNewCellSettingOffset = buildButton(resourceMap, actionMap,
                                                 "createNewCellAndSetOnset", null);
        gridButtonPanel.add(createNewCellSettingOffset, "span 2, w 95!, h 45!");

        // Set cell offset button
        setCellOffsetButton = buildButton(resourceMap, actionMap,
                                          "setCellOffset", null);
        gridButtonPanel.add(setCellOffsetButton, "w 45!, h 45!");

        // Show tracks button
        showTracksButton.setIcon(resourceMap.getIcon(
                "showTracksButton.hide.icon"));
        showTracksButton.setName("showTracksButton");
        showTracksButton.getAccessibleContext().setAccessibleName(
            "Show Tracks");
        showTracksButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    showTracksButtonActionPerformed(evt);
                }
            });
        gridButtonPanel.add(showTracksButton, "w 80!, h 45!");

        getContentPane().setLayout(new MigLayout("ins 0, hidemode 3, fillx",
                "[growprio 0]0[]", ""));
        getContentPane().add(gridButtonPanel, "");
        getContentPane().setBackground(Color.WHITE);

        tracksPanel.setBackground(Color.WHITE);
        tracksPanel.setVisible(false);
        getContentPane().add(tracksPanel, "growx");

        pack();
    }

    /**
     * Action to invoke when the user closes the data controller.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(
        final java.awt.event.ActionEvent evt) {
        LOGGER.event("Add data");

        PluginChooser chooser = null;

        // TODO finish this
        switch (OpenSHAPA.getPlatform()) {

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
        }

        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            openVideo(chooser);
        }
    }

    /**
     * Action to invoke when the user clicks the show tracks button.
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void showTracksButtonActionPerformed(
        final java.awt.event.ActionEvent evt) {
        assert (evt.getSource() instanceof JButton);

        JButton button = (JButton) evt.getSource();
        ResourceMap resourceMap = Application.getInstance(
                org.openshapa.OpenSHAPA.class).getContext().getResourceMap(
                DataControllerV.class);

        if (tracksPanelEnabled) {
            LOGGER.event("Show tracks");

            // Panel is being displayed, hide it
            button.setIcon(resourceMap.getIcon("showTracksButton.show.icon"));
        } else {
            LOGGER.event("Hide tracks");

            // Panel is hidden, show it
            button.setIcon(resourceMap.getIcon("showTracksButton.hide.icon"));
        }

        tracksPanelEnabled = !tracksPanelEnabled;
        showTracksPanel(tracksPanelEnabled);
        updateCurrentTimeLabel();
    }

    /**
     * Adds a data viewer to this data controller.
     *
     * @param icon
     *            The icon associated with the data viewer.
     * @param viewer
     *            The new viewer that we are adding to the data controller.
     * @param f
     *            The parent file that the viewer represents.
     */
    private void addDataViewer(final ImageIcon icon, final DataViewer viewer,
        final File f, final TrackPainter trackPainter) {
        assert viewer.getIdentifier() != null;

        addViewer(viewer, 0);

        // Add the file to the tracks information panel
        addTrack(viewer.getIdentifier(), icon, f.getAbsolutePath(), f.getName(),
            viewer.getDuration(), viewer.getOffset(), trackPainter);

        OpenSHAPA.getProjectController().projectChanged();
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
     * @param icon
     *            Icon associated with the track
     * @param mediaPath
     *            Absolute file path to the media file.
     * @param name
     *            The name of the track to add.
     * @param duration
     *            The duration of the data feed in milliseconds.
     * @param offset
     *            The time offset of the data feed in milliseconds.
     * @param trackPainter
     *            Track painter to use.
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
     * @param viewer
     *            The data viewer to add.
     * @param offset
     *            The offset value in milliseconds.
     */
    public void addViewer(final DataViewer viewer, final long offset) {

        // Add the QTDataViewer to the list of viewers we are controlling.
        viewers.add(viewer);
        viewer.setParentController(this);
        viewer.setOffset(offset);

        boolean visible = viewer.getParentJDialog().isVisible();
        OpenSHAPA.getApplication().show(viewer.getParentJDialog());

        if (!visible) {
            viewer.getParentJDialog().setVisible(false);
        }

        // adjust the overall frame rate.
        float fps = viewer.getFrameRate();

        if (fps > playbackModel.getCurrentFPS()) {
            playbackModel.setCurrentFPS(fps);
        }

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

        SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    SingleWindowTiler swt = new SingleWindowTiler();
                    swt.tile(viewer.getParentJDialog());
                }
            });
    }

    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    @Action public void setCellOnsetAction() {
        LOGGER.event("Set cell onset");
        new SetSelectedCellStartTimeC(getCurrentTime());
        setFindTime(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    @Action public void setCellOffsetAction() {
        LOGGER.event("Set cell offset");
        new SetSelectedCellStopTimeC(getCurrentTime());
        setFindOffsetField(getCurrentTime());
    }

    /**
     * @param show
     *            true to show the tracks layout, false otherwise.
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
     * @param e
     *            event
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
     * @param e
     *            The timescale event that triggered this action.
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
     * @param e
     *            The carriage event that triggered this action.
     */
    private void handleCarriageEvent(final CarriageEvent e) {

        switch (e.getEventType()) {

        case OFFSET_CHANGE:
            handleCarriageOffsetChangeEvent(e);

            break;

        case CARRIAGE_LOCK:
        case BOOKMARK_CHANGED:
        case BOOKMARK_SAVE:
            OpenSHAPA.getProjectController().projectChanged();

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

        OpenSHAPA.getProjectController().projectChanged();

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

    /** Simulates play button clicked. */
    public void pressPlay() {
        playButton.doClick();
    }

    /** Simulates forward button clicked. */
    public void pressForward() {
        forwardButton.doClick();
    }

    /** Simulates rewind button clicked. */
    public void pressRewind() {
        rewindButton.doClick();
    }

    /** Simulates pause button clicked. */
    public void pressPause() {
        pauseButton.doClick();
    }

    /** Simulates stop button clicked. */
    public void pressStop() {
        stopButton.doClick();
    }

    /** Simulates shuttle forward button clicked. */
    public void pressShuttleForward() {
        shuttleForwardButton.doClick();
    }

    /** Simulates shuttle back button clicked. */
    public void pressShuttleBack() {
        shuttleBackButton.doClick();
    }

    /** Simulates find button clicked. */
    public void pressFind() {
        findButton.doClick();
    }

    /** Simulates set cell onset button clicked. */
    public void pressSetCellOnset() {
        setCellOnsetButton.doClick();
    }

    /** Simulates set cell offset button clicked. */
    public void pressSetCellOffset() {
        setCellOffsetButton.doClick();
    }

    /** Simulates set cell offset button clicked. */
    public void pressSetCellOffsetOSX() {
        osxsetCellOffsetButton.doClick();
    }

    /** Simulates set new cell onset button clicked. */
    public void pressPointCell() {
        pointCellButton.doClick();
    }

    /** Simulates go back button clicked. */
    public void pressGoBack() {
        goBackButton.doClick();
    }

    /** Simulates create new cell button clicked. */
    public void pressCreateNewCell() {
        createNewCell.doClick();
    }

    /** Simulates create new cell setting offset button clicked. */
    public void pressCreateNewCellSettingOffset() {
        createNewCellSettingOffset.doClick();
    }

    // ------------------------------------------------------------------------
    // Playback actions
    //
    /**
     * Action to invoke when the user clicks on the play button.
     */
    @Action public void playAction() {
        LOGGER.event("Play");

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
    @Action public void forwardAction() {
        LOGGER.event("Fast forward");
        playAt(FFORWARD_RATE);
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    @Action public void rewindAction() {
        LOGGER.event("Rewind");
        playAt(REWIND_RATE);
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     */
    @Action public void pauseAction() {
        LOGGER.event("Pause");

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
    @Action public void stopAction() {
        LOGGER.event("Stop event");
        clock.stop();
        clock.setRate(0);
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleDirection = ShuttleDirection.UNDEFINED;
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behaviour for reversing shuttle direction?
     */
    @Action public void shuttleForwardAction() {
        LOGGER.event("Shuttle forward");

        if ((clock.getTime() <= 0)
                && ((playbackModel.getShuttleRate() != 0)
                    || (shuttleDirection != ShuttleDirection.UNDEFINED))) {
            playbackModel.setShuttleRate(0);
            playbackModel.setPauseRate(0);
            shuttleDirection = ShuttleDirection.UNDEFINED;
            shuttle(ShuttleDirection.FORWARDS);
        } else {

            // BugzID:794 - Previously ignored pauseRate if paused
            if (clock.isStopped()) {
                playbackModel.setShuttleRate(findShuttleIndex(
                        playbackModel.getPauseRate()));
                shuttle(ShuttleDirection.FORWARDS);
                // shuttle(ShuttleDirection.BACKWARDS);
                // This makes current tests fail, but may be the desired
                // functionality.
            } else {
                shuttle(ShuttleDirection.FORWARDS);
            }
        }
    }

    /**
     * Action to invoke when the user clicks on the shuttle back button.
     */
    @Action public void shuttleBackAction() {
        LOGGER.event("Shuttle back");

        if ((clock.getTime() <= 0)
                && ((playbackModel.getShuttleRate() != 0)
                    || (shuttleDirection != ShuttleDirection.UNDEFINED))) {
            playbackModel.setShuttleRate(0);
            playbackModel.setPauseRate(0);
            shuttleDirection = ShuttleDirection.UNDEFINED;
        } else {

            // BugzID:794 - Previously ignored pauseRate if paused
            if (clock.isStopped()) {
                playbackModel.setShuttleRate(findShuttleIndex(
                        playbackModel.getPauseRate()));
                shuttle(ShuttleDirection.BACKWARDS);
                // shuttle(ShuttleDirection.FORWARDS);
                // This makes current tests fail, but may be the desired
                // functionality.
            } else {
                shuttle(ShuttleDirection.BACKWARDS);
            }
        }
    }

    /**
     * Searches the shuttle rates array for the given rate, and returns the
     * index.
     *
     * @param pRate
     *            The rate to search for.
     * @return The index of the rate, or -1 if not found.
     */
    private int findShuttleIndex(final float pRate) {

        if (pRate == 0) {
            return 0;
        }

        for (int i = 0; i < SHUTTLE_RATES.length; i++) {

            if ((SHUTTLE_RATES[i] == pRate)
                    || (SHUTTLE_RATES[i] == (pRate * (-1)))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindTime(final long milliseconds) {
        findTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Populates the find offset time in the controller.
     *
     * @param milliseconds
     *            The time to use when populating the find field.
     */
    public void setFindOffsetField(final long milliseconds) {
        findOffsetField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Action to invoke when the user clicks on the find button.
     */
    @Action public void findAction() {
        LOGGER.event("Find");

        if (shiftMask) {
            findOffsetAction();
        } else {

            try {
                jumpTo(CLOCK_FORMAT.parse(findTextField.getText()).getTime());
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
            jumpTo(CLOCK_FORMAT.parse(findOffsetField.getText()).getTime());
        } catch (ParseException e) {
            LOGGER.error("unable to find within video", e);
        }
    }

    public void clearRegionOfInterestAction() {
        mixerController.clearRegionOfInterest();
    }

    /**
     * Sets the playback region of interest to lie from the find time to offset
     * time.
     */
    public void setRegionOfInterestAction() {

        try {
            final long findTextTime = CLOCK_FORMAT.parse(
                    findTextField.getText()).getTime();
            final long findOffsetTime = CLOCK_FORMAT.parse(
                    findOffsetField.getText()).getTime();

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

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    @Action public void goBackAction() {

        try {
            LOGGER.event("Go back");

            long j = -CLOCK_FORMAT.parse(goBackTextField.getText()).getTime();
            jump(j);

            // BugzID:721 - After going back - start playing again.
            playAt(PLAY_RATE);

        } catch (ParseException e) {
            LOGGER.error("unable to find within video", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    @Action public void jogBackAction() {
        LOGGER.event("Jog back");

        int mul = 1;

        if (shiftMask) {
            mul = SHIFTJOG;
        }

        if (ctrlMask) {
            mul = CTRLSHIFTJOG;
        }

        long stepSize = ((-ONE_SECOND) / (long) playbackModel.getCurrentFPS());
        long nextTime = (long) (mul * stepSize);

        /* BugzID:1544 - Preserve precision - force jog to frame markers. */
        nextTime = nextTime - (clock.getTime() % stepSize);

        /* BugzID:1361 - Disallow jog to skip past the region boundaries. */
        if ((clock.getTime() + nextTime) > playbackModel.getWindowPlayStart()) {
            jump(nextTime);
        } else {
            jumpTo(playbackModel.getWindowPlayStart());
        }
    }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    @Action public void jogForwardAction() {
        LOGGER.event("Jog forward");

        int mul = 1;

        if (shiftMask) {
            mul = SHIFTJOG;
        }

        if (ctrlMask) {
            mul = CTRLSHIFTJOG;
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
            jump(nextTime);
        } else {
            jumpTo(playbackModel.getWindowPlayEnd());
        }
    }

    // ------------------------------------------------------------------------
    // [private] play back action helper functions
    //
    /**
     * @param rate
     *            Rate of play.
     */
    private void playAt(final float rate) {
        shuttleDirection = ShuttleDirection.UNDEFINED;
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleAt(rate);
    }

    /**
     * @param direction
     *            The required direction of the shuttle.
     */
    private void shuttle(final ShuttleDirection direction) {
        int shuttleRate = playbackModel.getShuttleRate();
        float rate = SHUTTLE_RATES[shuttleRate];

        if (ShuttleDirection.UNDEFINED == shuttleDirection) {
            shuttleDirection = direction;
            rate = SHUTTLE_RATES[0];

        } else if (direction == shuttleDirection) {

            if (shuttleRate < (SHUTTLE_RATES.length - 1)) {
                rate = SHUTTLE_RATES[++shuttleRate];
                playbackModel.setShuttleRate(shuttleRate);
            }

        } else {

            if (shuttleRate > 0) {
                rate = SHUTTLE_RATES[--shuttleRate];
                playbackModel.setShuttleRate(shuttleRate);

                // BugzID: 676 - Shuttle speed transitions between zero.
            } else {
                rate = 0;
                shuttleDirection = ShuttleDirection.UNDEFINED;
            }
        }

        shuttleAt(shuttleDirection.getParameter() * rate);
    }

    /**
     * @param rate
     *            Rate of shuttle.
     */
    private void shuttleAt(final float rate) {
        clock.setRate(rate);
        clock.start();
    }

    /**
     * @param step
     *            Milliseconds to jump.
     */
    private void jump(final long step) {
        clock.stop();
        clock.setRate(0);
        playbackModel.setShuttleRate(0);
        playbackModel.setPauseRate(0);
        shuttleDirection = ShuttleDirection.UNDEFINED;

        clock.stepTime(step);
    }

    /**
     * @param time
     *            Absolute time to jump to.
     */
    private void jumpTo(final long time) {
        clock.stop();
        clock.setTime(time);
    }

    // -------------------------------------------------------------------------
    //
    //
    /**
     * Action to invoke when the user clicks on the create new cell button.
     */
    @Action public void createNewCellAction() {
        LOGGER.event("New cell");
        CreateNewCellC controller = new CreateNewCellC();
        controller.createDefaultCell();
    }

    /**
     * Action to invoke when the user clicks on the new cell button.
     */
    @Action public void createNewCellAndSetOnsetAction() {
        LOGGER.event("New cell set onset");
        new CreateNewCellC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the new cell offset button.
     */
    @Action public void pointCellAction() {
        LOGGER.event("Set new cell offset");
        new CreateNewCellC(getCurrentTime());
        new SetNewCellStopTimeC(getCurrentTime());
        setFindOffsetField(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the sync video button.
     */
    @Action public void syncVideoAction() {
    }

    @Override public void propertyChange(final PropertyChangeEvent e) {

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
}
