package org.openshapa.plugins.gstvideo;

import com.sun.jna.Platform;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.Message;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.State;
import org.gstreamer.Structure;

import org.gstreamer.elements.OSXVideoSink;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.RGBDataSink;

import org.gstreamer.event.BusSyncHandler;

import org.gstreamer.interfaces.XOverlay;

import org.gstreamer.swing.OSXVideoComponent;
import org.gstreamer.swing.VideoComponent;
import org.openshapa.models.db.Datastore;

import org.openshapa.models.id.Identifier;
import org.openshapa.plugins.CustomActions;
import org.openshapa.plugins.CustomActionsAdapter;
import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.ViewerStateListener;
import org.openshapa.views.DataController;

import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;


public class GStreamerDataViewer implements DataViewer {

    @Override
    public void setDatastore(Datastore sDB) {

    }

    @Override
    public void clearDataFeed() {
    	unloadGStreamer();
    }

    private enum VideoSinkType {
        swingRenderer, osxRenderer, xWindowsRenderer,
    }

    /** ID of this data viewer. */
    private Identifier id;

    /** Icon for displaying volume slider. */
    private final ImageIcon volumeIcon = new ImageIcon(getClass().getResource(
                "/icons/audio-volume.png"));

    /** Volume slider icon for when the video is hidden (volume is muted). */
    private final ImageIcon mutedIcon = new ImageIcon(getClass().getResource(
                "/icons/volume-muted.png"));

    /** Volume slider. */
    private JSlider volumeSlider;

    /** Dialog containing volume slider. */
    private JDialog volumeDialog;

    /** Volume button. */
    private JButton volumeButton;

    /** Stores the desired volume the plugin should play at. */
    private float volume = 1f;

    /** Is the plugin visible? */
    private boolean isVisible = true;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(GStreamerDataViewer.class);

    /** The list of listeners interested in changes made to the project. */
    private final List<ViewerStateListener> viewerListeners =
        new LinkedList<ViewerStateListener>();

    /** Custom actions handler. */
    private CustomActions actions = new CustomActionsAdapter() {
            @Override public AbstractButton getActionButton1() {
                return volumeButton;
            }
        };

    private boolean isGStreamerLoaded = false;
    private JDialog videoDialog;
    private long offset;
    private final TrackPainter trackPainter;
    private File dataFeed;
    private DataController parentDataController;
    private PlayBin playBin;
    private long duration = 0;
    private double videoFrameRate = 0;
    private Dimension videoSize;
    private boolean isPlaying = false;
    private float playbackRate = 1;
    private VideoComponent videoComponent;
    private OSXVideoSink osxvideosink;

    /** GStreamer Playbin mute volume */
    private final double MUTE_VOLUME = 0.0;

    /** Duration of time (seconds) to wait for before aborting an attempt to load a video. */
    private final long VIDEO_LOADING_TIMEOUT_SECONDS = 60;

    private long lastPlayingSeekTime = System.currentTimeMillis();
    private final long minimumIntervalBetweenPlayingSeeks = 5000;

    /** Whether debug messages should be printed */
    private final boolean DEBUG = false;
    
    public GStreamerDataViewer(final Frame parent, final boolean modal) {

        if (DEBUG) System.out.println("GStreamerDataViewer.GStreamerDataViewer()");

        isGStreamerLoaded = true;
        Gst.init();

        trackPainter = new DefaultTrackPainter();
        setOffset(0);

        volumeButton = new JButton();

        {
            volumeButton.setIcon(getActionButtonIcon1());
            volumeButton.setBorderPainted(false);
            volumeButton.setContentAreaFilled(false);
            volumeButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent e) {
                        handleActionButtonEvent1(e);
                    }
                });
        }

        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 70);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setName("volumeSlider");
        volumeSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {
                    handleVolumeSliderEvent(e);
                }
            });

        volumeDialog = new JDialog(parent, false);
        volumeDialog.setUndecorated(true);
        volumeDialog.setVisible(false);
        volumeDialog.setLayout(new MigLayout("", "[center]", ""));
        volumeDialog.setSize(50, 125);
        volumeDialog.setName("volumeDialog");
        volumeDialog.getContentPane().add(volumeSlider, "pushx, pushy");
        volumeDialog.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(final MouseEvent e) {
                    volumeDialog.setVisible(false);
                }
            });

        volumeDialog.addWindowFocusListener(new WindowAdapter() {
                @Override public void windowLostFocus(final WindowEvent e) {
                    volumeDialog.setVisible(false);
                }
            });

        final boolean useFixedAspectRatioDialog = false;

        if (useFixedAspectRatioDialog) {
            videoDialog = new FixedAspectRatioDialog(parent, false);
        } else {
            videoDialog = new JDialog(parent, false);
        }

        videoDialog.setVisible(false);
        videoDialog.setName("videoDialog");
        videoDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        videoDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent evt) {
                    GStreamerDataViewer.this.windowClosing(evt);
                }
            });
    }
    
    private void unloadGStreamer() {
        if (playBin != null) {
            playBin.setVolume(MUTE_VOLUME);
            playBin.setState(State.NULL);
            waitForPlaybinStateChange();
            playBin.dispose();
            playBin = null;
        }
        
        if (videoComponent != null) {
        	videoComponent = null;
        }
        
        if (osxvideosink != null) {
        	osxvideosink.dispose();
        	osxvideosink = null;
        }

        if (isGStreamerLoaded) {
        	Gst.deinit();
        	isGStreamerLoaded = false;
        }
        
        System.gc();
    }

    private void waitForPlaybinStateChange() {
        playBin.getState(TimeUnit.NANOSECONDS.convert(500,
                TimeUnit.MILLISECONDS));
    }

    private void handleVolumeSliderEvent(final ChangeEvent e) {

        if (playBin != null) {

            // Calculate volume as a percentage.
            volume = volumeSlider.getValue() / 100F;
            setVolume();
            notifyChange();
        }
    }

    /**
     * Sets the volume of the movie to the level of the slider bar, or to 0
     * if the track is hidden from view (this means hiding the track mutes
     * the volume).
     */
    private void setVolume() {
        playBin.setVolume(isVisible ? volume : MUTE_VOLUME);
        volumeButton.setIcon(getActionButtonIcon1());
    }

    @Override public long getCurrentTime() {
//      if (DEBUG) System.out.println("GStreamerDataViewer.getCurrentTime() = " + (playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0));
        return (playBin != null) ? playBin.queryPosition(TimeUnit.MILLISECONDS)
                                 : 0;
    }

    @Override public File getDataFeed() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.getDataFeed()");

        return dataFeed;
    }

    @Override public long getDuration() {
        return duration;
    }

    @Override public float getFrameRate() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.getFrameRate()");

        return (float) videoFrameRate;
    }

    @Override public long getOffset() {
        return offset;
    }

    @Override public JDialog getParentJDialog() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.getParentJDialog()");

        return videoDialog;
    }

    @Override public TrackPainter getTrackPainter() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.getTrackPainter()");

        return trackPainter;
    }

    @Override public boolean isPlaying() {
        return isPlaying;
    }

    @Override public synchronized void play() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.play()");

        if ((playBin != null) && !isPlaying) {
            final int seekFlags = SeekFlags.FLUSH | SeekFlags.ACCURATE
            /* | SeekFlags.SKIP -- doesn't work very well in gstreamer */ ;
            playBin.seek(playbackRate, Format.TIME, seekFlags, SeekType.NONE, 0,
                SeekType.NONE, 0);
            playBin.setVolume(volume);
            playBin.setState(State.PLAYING);
            waitForPlaybinStateChange();
            isPlaying = true;
        }
    }

    private void updatePlaybackVolume() {
        final double minimumPlaybackRateForAudio = 0.9;
        final double maximumPlaybackRateForAudio = 1.1;
        playBin.setVolume(
            (isPlaying
                && ((playbackRate >= minimumPlaybackRateForAudio)
                    && (playbackRate <= maximumPlaybackRateForAudio)))
                ? volume : MUTE_VOLUME);
    }

    private void gstreamerSeek(final long positionNanoseconds) {
        assert playBin != null;

        final double seekPlaybackRate = (playbackRate != 0.0) ? playbackRate
                                                              : 1.0;
        final int seekFlags = SeekFlags.FLUSH
            | SeekFlags.ACCURATE /* | SeekFlags.SKIP -- doesn't work very well in gstreamer */;
        final SeekType startSeekType;
        final long startSeekTime;
        final SeekType endSeekType;
        final long endSeekTime;

        if (playbackRate >= 0) {
            startSeekType = SeekType.SET;
            startSeekTime = positionNanoseconds;
            endSeekType = SeekType.END;
            endSeekTime = 0;
        } else {
            startSeekType = SeekType.SET;
            startSeekTime = 0;
            endSeekType = SeekType.SET;
            endSeekTime = positionNanoseconds;
        }

        playBin.seek(seekPlaybackRate, Format.TIME, seekFlags, startSeekType,
            startSeekTime, endSeekType, endSeekTime);
    }

    @Override public synchronized void seekTo(long position) {
    	if (DEBUG) System.out.println("GStreamerDataViewer.seekTo(" + position
            + "), currentTime=" + getCurrentTime() + ", difference="
            + (position - getCurrentTime()) + ", playbackSpeed=" + playbackRate
            + ", isPlaying=" + isPlaying);

        if ((playBin != null) && (position != getCurrentTime())) {
            final boolean isTrickModePlayback = playbackRate >= 2.0;

            if (isPlaying && !isTrickModePlayback) {

                if (isPlaying) {
                    return;
                }

                // limit the frequency of seeks performed to prevent skipping during normal playback
                final long currentTimeMillis = System.currentTimeMillis();

                if ((lastPlayingSeekTime + minimumIntervalBetweenPlayingSeeks)
                        > currentTimeMillis) {

                    // ignore this playing seek request
                	if (DEBUG) System.err.println("ignoring seek request");

                    return;
                } else if ((position - getCurrentTime()) < 250) {

                    //TODO temporary HACK - adjust seek latencies by compensating for the seek call
                	if (DEBUG) System.err.println("patching seeks by "
                        + ((position - getCurrentTime()) / 2) + " ms");
                    position += (position - getCurrentTime()) / 2;
                }

                lastPlayingSeekTime = currentTimeMillis;
            }

            updatePlaybackVolume();
            gstreamerSeek(TimeUnit.NANOSECONDS.convert(position,
                    TimeUnit.MILLISECONDS));
            waitForPlaybinStateChange();
        }
    }

    @Override public synchronized void setDataFeed(final File dataFeed) {
        if (this.dataFeed != null) {
            return;
        }

        this.dataFeed = dataFeed;

        playBin = new PlayBin("OpenSHAPA");
        playBin.setInputFile(dataFeed);

        final VideoSinkType renderer;

        if (Platform.isMac()) {
            renderer = VideoSinkType.osxRenderer;
        } else if (Platform.isLinux()) {
            renderer = VideoSinkType.xWindowsRenderer;
        } else {
            renderer = VideoSinkType.swingRenderer;
        }

        switch (renderer) {

        case swingRenderer: {
            videoComponent = new VideoComponent();
            ((RGBDataSink) videoComponent.getElement()).getSinkElement()
                .setMaximumLateness(-1, TimeUnit.MILLISECONDS); //TODO ugly hack!
            playBin.setVideoSink(videoComponent.getElement());

            break;
        }

        case xWindowsRenderer: {
            final Canvas canvas = new Canvas();
            videoDialog.add(canvas);

            final Element xvimagesink = ElementFactory.make("xvimagesink",
                    "xvimagesink");
            xvimagesink.set("force-aspect-ratio", true);
            playBin.setVideoSink(xvimagesink);

            playBin.getBus().setSyncHandler(new BusSyncHandler() {
                    public BusSyncReply syncMessage(final Message msg) {
                        Structure s = msg.getStructure();

                        if ((s == null) || !s.hasName("prepare-xwindow-id")) {
                            return BusSyncReply.PASS;
                        }

                        XOverlay.wrap(xvimagesink).setWindowID(canvas);

                        return BusSyncReply.DROP;
                    }
                });

            break;
        }

        case osxRenderer: {
            osxvideosink = new OSXVideoSink("osxvideosink");
            osxvideosink.setMaximumLateness(50, TimeUnit.MILLISECONDS);
            playBin.setVideoSink(osxvideosink);
            osxvideosink.listenForNewViews(playBin.getBus());

            osxvideosink.addListener(new OSXVideoSink.Listener() {
                    @Override public void newVideoComponent(final Object source,
                        final OSXVideoComponent osxVideoComponent) {
                        videoDialog.getContentPane().add(osxVideoComponent,
                            BorderLayout.CENTER);
                        osxVideoComponent.setPreferredSize(
                            playBin.getVideoSize());
                        osxVideoComponent.setEnabled(false);
                        osxVideoComponent.setFocusable(false);
                        videoDialog.pack();
                    }
                });

            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        videoDialog.setTitle(dataFeed.getName());
                        videoDialog.setVisible(true);
                    }
                });

            break;
        }
        }

        playBin.setState(State.PAUSED);

        State state = playBin.getState(TimeUnit.NANOSECONDS.convert(
                    VIDEO_LOADING_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        if (state != State.PAUSED) {
            this.dataFeed = null;
            throw new RuntimeException("Couldn't add video file "
                + dataFeed.getAbsolutePath());
        }

        isPlaying = false;
        playBin.seek(1.0, Format.TIME,
            SeekFlags.FLUSH
            | SeekFlags.ACCURATE /* | SeekFlags.SKIP -- doesn't work very well in gstreamer */,
            SeekType.SET, 0, SeekType.END, 0);
        waitForPlaybinStateChange();

        duration = playBin.queryDuration(TimeUnit.MILLISECONDS);

        final double defaultVideoFrameRate = 25;
        final Dimension defaultVideoSize = new Dimension(320, 240);

        videoFrameRate = playBin.getVideoSinkFrameRate();

        if (videoFrameRate <= 0) {
            videoFrameRate = defaultVideoFrameRate;
        }

        videoSize = playBin.getVideoSize();

        if (videoSize == null) {
            videoSize = defaultVideoSize;
        }

        if (renderer == VideoSinkType.swingRenderer) {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        videoDialog.setTitle(dataFeed.getName());
                        videoDialog.getContentPane().add(videoComponent,
                            BorderLayout.CENTER);
                        videoComponent.setPreferredSize(videoSize);
                        videoDialog.pack();
                        videoDialog.setVisible(true);
                    }
                });
        }
    }

    public void windowClosing(final WindowEvent evt) {
    }

    @Override public void setOffset(final long offset) {
    	if (DEBUG) System.out.println("GStreamerDataViewer.setOffset(" + offset + ")");
        this.offset = offset;
    }

    @Override public synchronized void setParentController(
        final DataController dataController) {
    	if (DEBUG) System.out.println("GStreamerDataViewer.setParentController()");
        parentDataController = dataController;
    }

    @Override public void setPlaybackSpeed(final float rate) {
    	if (DEBUG) System.out.println("GStreamerDataViewer.setPlaybackSpeed(" + rate
            + ")");
        this.playbackRate = rate;

        if (playBin != null) {

            if (rate != 0) {
                isPlaying = true;
                updatePlaybackVolume();
                gstreamerSeek(playBin.queryPosition(TimeUnit.NANOSECONDS));
                playBin.setState(State.PLAYING);
            } else {
                playBin.setVolume(MUTE_VOLUME);
                playBin.setState(State.PAUSED);
                isPlaying = false;
            }

            waitForPlaybinStateChange();
        }
    }

    @Override public synchronized void stop() {
    	if (DEBUG) System.out.println("GStreamerDataViewer.stop()");

        if ((playBin != null) && isPlaying) {
            playBin.setVolume(MUTE_VOLUME);
            playBin.setState(State.PAUSED);
            waitForPlaybinStateChange();
            isPlaying = false;
        }
    }

    @Override public void loadSettings(final InputStream is) {
        Properties settings = new Properties();

        try {
            settings.load(is);

            String property = settings.getProperty("offset");

            if ((property != null) && !"".equals(property)) {
                setOffset(Long.parseLong(property));
            }

            property = settings.getProperty("volume");

            if ((property != null) && !"".equals(property)) {
                volume = Float.parseFloat(property);
                volumeSlider.setValue((int) Math.round(volume * 100));
            }

            property = settings.getProperty("visible");

            if ((property != null) && !"".equals(property)) {
                isVisible = Boolean.parseBoolean(property);

                // BugzID:2032 - Need to update when visbility is back again.
                //this.setVisible(isVisible);
                setVolume();
            }

            property = settings.getProperty("height");

            if ((property != null) && !"".equals(property)) {
                // BugzID:2057 - Need to update when resize is back again.
                //setVideoHeight(Integer.parseInt(property));
            }

        } catch (IOException e) {
            LOGGER.error("Error loading settings", e);
        }
    }

    @Override public void storeSettings(final OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Float.toString(volume));
        settings.setProperty("visible", Boolean.toString(isVisible));

        // BugzID:2057 - Need to update when resize is back again. - not zero.
        settings.setProperty("height", Integer.toString(0));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            LOGGER.error("Error saving settings", e);
        }
    }


    /** Notifies listeners that a change to the project has occurred. */
    private void notifyChange() {

        for (ViewerStateListener listener : viewerListeners) {
            listener.notifyStateChanged(null, null);
        }
    }

    private ImageIcon getActionButtonIcon1() {

        if (isVisible && (volume > 0)) {
            return volumeIcon;
        } else {
            return mutedIcon;
        }
    }

    private void handleActionButtonEvent1(final ActionEvent event) {

        // BugzID:1400 - We don't allow volume changes while the track is
        // hidden from view.
        if (isVisible) {

            // Show the volume frame.
            volumeDialog.setLocation(volumeButton.getLocationOnScreen());
            volumeDialog.setVisible(true);
        }
    }

    @Override public Identifier getIdentifier() {
        return id;
    }

    @Override public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override public CustomActions getCustomActions() {
        return actions;
    }

    @Override public void addViewerStateListener(
        final ViewerStateListener vsl) {
        viewerListeners.add(vsl);
    }

    @Override public void removeViewerStateListener(
        final ViewerStateListener vsl) {
        viewerListeners.remove(vsl);
    }
    
    @Override public void setDataViewerVisible(boolean isVisible) {
    	videoDialog.setVisible(isVisible);
    	this.isVisible = isVisible;
    	setVolume();
    }
}
