package org.openshapa.views.continuous.gstreamer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.elements.RGBDataSink;
import org.gstreamer.swing.VideoComponent;
import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.ViewerStateListener;

public class GStreamerDataViewer implements DataViewer {
    /** Icon for displaying volume slider. */
    private final ImageIcon volumeIcon =
            new ImageIcon(getClass().getResource("/icons/audio-volume.png"));

    /** Volume slider icon for when the video is hidden (volume is muted). */
    private final ImageIcon mutedIcon =
            new ImageIcon(getClass().getResource("/icons/volume-muted.png"));

    /** Volume slider. */
    private JSlider volumeSlider;

    /** Dialog containing volume slider. */
    private JDialog volumeDialog;

    /** Stores the desired volume the plugin should play at. */
    private float volume = 1f;

    /** Is the plugin visible? */
    private boolean isVisible = true;

    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(GStreamerDataViewer.class);

    /** The list of listeners interested in changes made to the project. */
    private final List<ViewerStateListener> viewerListeners =
            new LinkedList<ViewerStateListener>();

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

    /** GStreamer Playbin mute volume */
    private final double MUTE_VOLUME = 0.0;
    /** Duration of time (seconds) to wait for before aborting an attempt to load a video. */
    private final long VIDEO_LOADING_TIMEOUT_SECONDS = 30;
    
    public GStreamerDataViewer(Frame parent, boolean modal) {
        
        System.out.println("GStreamerDataViewer.GStreamerDataViewer()");

        Gst.init();
        
        trackPainter = new DefaultTrackPainter();
        setOffset(0);

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
        
        videoDialog = new JDialog(parent, false);
        videoDialog.setVisible(false);
        videoDialog.setName("videoDialog");
        videoDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        videoDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                GStreamerDataViewer.this.windowClosing(evt);
            }
        });
    }

    private void waitForPlaybinStateChange() {
        playBin.getState(TimeUnit.NANOSECONDS.convert(500, TimeUnit.MILLISECONDS));
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
    }
    
    @Override
    public long getCurrentTime() {
//        System.out.println("GStreamerDataViewer.getCurrentTime() = " + (playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0));
        return playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0;
    }

    @Override
    public File getDataFeed() {
        System.out.println("GStreamerDataViewer.getDataFeed()");
        return dataFeed;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public float getFrameRate() {
        System.out.println("GStreamerDataViewer.getFrameRate()");
        return (float) videoFrameRate;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public JDialog getParentJDialog() {
        System.out.println("GStreamerDataViewer.getParentJDialog()");
        return videoDialog;
    }

    @Override
    public TrackPainter getTrackPainter() {
        System.out.println("GStreamerDataViewer.getTrackPainter()");
        return trackPainter;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public synchronized void play() {
        System.out.println("GStreamerDataViewer.play()");
        if (playBin != null && !isPlaying) {
            final int seekFlags = SeekFlags.FLUSH | SeekFlags.ACCURATE | (1 << 4) /*SeekFlags.SKIP*/;
            playBin.seek(playbackRate, Format.TIME, seekFlags, SeekType.NONE, 0, SeekType.NONE, 0);
            playBin.setVolume(volume);
            playBin.setState(State.PLAYING);
            waitForPlaybinStateChange();
            isPlaying = true;
        }
    }

    private void updatePlaybackVolume() {
        final double minimumPlaybackRateForAudio = 0.9;
        final double maximumPlaybackRateForAudio = 1.1;
        playBin.setVolume(isPlaying && ((playbackRate >= minimumPlaybackRateForAudio) && (playbackRate <= maximumPlaybackRateForAudio)) ? volume : MUTE_VOLUME);
    }

    private void gstreamerSeek(long positionNanoseconds) {
        final double seekPlaybackRate = playbackRate != 0.0 ? playbackRate : 1.0;
        final int seekFlags = SeekFlags.FLUSH | SeekFlags.ACCURATE | SeekFlags.SKIP;
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

        playBin.seek(seekPlaybackRate, Format.TIME, seekFlags, startSeekType, startSeekTime, endSeekType, endSeekTime);
    }
    
    @Override
    public synchronized void seekTo(long position) {
        System.out.println("GStreamerDataViewer.seekTo(" + position + "), currentTime=" + getCurrentTime() + ", difference=" + (position - getCurrentTime()) + ", playbackSpeed=" + playbackRate + ", isPlaying=" + isPlaying);
        if (playBin != null && position != getCurrentTime()) {
        	updatePlaybackVolume();
        	gstreamerSeek(TimeUnit.NANOSECONDS.convert(position, TimeUnit.MILLISECONDS));
            waitForPlaybinStateChange();
        }
    }
    
    @Override
    public synchronized void setDataFeed(final File dataFeed) {
        if (this.dataFeed != null) {
            return;
        }
        
        this.dataFeed = dataFeed;
        
        playBin = new PlayBin("OpenSHAPA");
        playBin.setInputFile(dataFeed);

        videoComponent = new VideoComponent();
        ((RGBDataSink) videoComponent.getElement()).getSinkElement().setMaximumLateness(-1, TimeUnit.MILLISECONDS); //TODO ugly hack!
        playBin.setVideoSink(videoComponent.getElement());

        playBin.setState(State.PAUSED);
        State state = playBin.getState(TimeUnit.NANOSECONDS.convert(VIDEO_LOADING_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (state != State.PAUSED) {
        	this.dataFeed = null;
            throw new RuntimeException("Couldn't add video file " + dataFeed.getAbsolutePath());
        }
        
        isPlaying = false;
        playBin.seek(1.0, Format.TIME, SeekFlags.FLUSH | SeekFlags.ACCURATE | SeekFlags.SKIP, SeekType.SET, 0, SeekType.END, 0);
        waitForPlaybinStateChange();
        
        duration = playBin.queryDuration(TimeUnit.MILLISECONDS);
        videoFrameRate = playBin.getVideoSinkFrameRate();
        if (videoFrameRate <= 0) {
        	final double defaultVideoFrameRate = 25;
        	videoFrameRate = defaultVideoFrameRate;
        }

        videoSize = playBin.getVideoSize();
        if (videoSize == null) {
        	videoSize = new Dimension(640, 480);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                videoDialog.setTitle(dataFeed.getName());
                videoDialog.getContentPane().add(videoComponent, BorderLayout.CENTER);
                videoDialog.setPreferredSize(videoSize);
                videoDialog.pack();
                videoDialog.setVisible(true);
            }
        });
    }
    
    public void windowClosing(final WindowEvent evt) {
        if (playBin != null) {
            playBin.setVolume(MUTE_VOLUME);
            playBin.setState(State.NULL);
            playBin = null;
            videoComponent = null;
        }
        
        Gst.deinit();
        
        if (parentDataController != null) {
            parentDataController.shutdown(this);
        }
    }
    
    @Override
    public void setOffset(long offset) {
        System.out.println("GStreamerDataViewer.setOffset(" + offset + ")");
        this.offset = offset;
    }

    @Override
    public synchronized void setParentController(DataController dataController) {
        System.out.println("GStreamerDataViewer.setParentController()");
        parentDataController = dataController;
    }

    @Override
    public void setPlaybackSpeed(float rate) {
        System.out.println("GStreamerDataViewer.setPlaybackSpeed(" + rate + ")");
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

    @Override
    public synchronized void stop() {
        System.out.println("GStreamerDataViewer.stop()");
        if (playBin != null && isPlaying) {
            playBin.setVolume(MUTE_VOLUME);
            playBin.setState(State.PAUSED);
            waitForPlaybinStateChange();
            isPlaying = false;
        }
    }

    @Override public void loadSettings(InputStream is) {
        Properties settings = new Properties();

        try {
            settings.load(is);
            String property = settings.getProperty("offset");
            if (property != null & !property.equals("")) {
                setOffset(Long.parseLong(property));
            }
            property = settings.getProperty("volume");
            if (property != null & !property.equals("")) {
                volume = Float.parseFloat(property);
                volumeSlider.setValue((int) Math.round(volume * 100));
            }
            property = settings.getProperty("visible");
            if (property != null & !property.equals("")) {
                isVisible = Boolean.parseBoolean(property);
                // BugzID:2032 - Need to update when visbility is back again.
                //this.setVisible(isVisible);
                setVolume();
            }
            property = settings.getProperty("height");
            if (property != null & !property.equals("")) {
                // BugzID:2057 - Need to update when resize is back again.
                //setVideoHeight(Integer.parseInt(property));
            }

        } catch (IOException e) {
            logger.error("Error loading settings", e);
        }
    }

    @Override public void storeSettings(OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Float.toString(volume));
        settings.setProperty("visible", Boolean.toString(isVisible));

        // BugzID:2057 - Need to update when resize is back again. - not zero.
        settings.setProperty("height", Integer.toString(0));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            logger.error("Error saving settings", e);
        }
    }

    @Override public void addViewerStateListener(ViewerStateListener vsl) {
        viewerListeners.add(vsl);
    }

    /** Notifies listeners that a change to the project has occurred. */
    private void notifyChange() {
        for (ViewerStateListener listener : viewerListeners) {
            listener.notifyStateChanged(null, null);
        }
    }

    @Override public ImageIcon getActionButtonIcon1() {
        if (isVisible && volume > 0) {
            return volumeIcon;
        } else {
            return mutedIcon;
        }
    }

    @Override public ImageIcon getActionButtonIcon2() {
        System.out.println("GStreamerDataViewer.getActionButtonIcon2()");
        // TODO Auto-generated method stub
        return null;
    }

    @Override public ImageIcon getActionButtonIcon3() {
        System.out.println("GStreamerDataViewer.getActionButtonIcon3()");
        // TODO Auto-generated method stub
        return null;
    }

    @Override public void handleActionButtonEvent1(ActionEvent event) {
        JButton button = (JButton) event.getSource();

        // BugzID:1400 - We don't allow volume changes while the track is
        // hidden from view.
        if (isVisible) {
            // Show the volume frame.
            volumeDialog.setLocation(button.getLocationOnScreen());
            volumeDialog.setVisible(true);
        }
    }

    @Override public void handleActionButtonEvent2(ActionEvent event) {
        //isVisible = !isVisible;
        //this.setVisible(isVisible);
        //setVolume();
        //notifyChange();
    }

    @Override public void handleActionButtonEvent3(ActionEvent event) {
        System.out.println("GStreamerDataViewer.handleActionButtonEvent3()");
        // TODO Auto-generated method stub
    }
}
