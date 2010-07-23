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
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

import org.gstreamer.ClockTime;
import org.gstreamer.Gst;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.swing.VideoComponent;
import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.ViewerStateListener;

public class GStreamerDataViewer implements DataViewer {
    static {
        Gst.init();
        //DAVETODO add unload hooks
    }

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

    private long offset;
    private final TrackPainter trackPainter;
    private File dataFeed;
    private DataController parentDataController;
    private PlayBin playBin;
    private long duration = 0;
    private boolean isPlaying = false;


    public GStreamerDataViewer(Frame parent, boolean modal) {
        System.out.println("GStreamerDataViewer.GStreamerDataViewer()");
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
        if (isVisible) {
            playBin.setVolume(volume);
        } else {
            playBin.setVolume(0.0F);
        }
    }
	
	@Override
	public long getCurrentTime() throws Exception {
//		System.out.println("GStreamerDataViewer.getCurrentTime() = " + (playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0));
		return 0;
//		return playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0;
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
		return 25;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public JDialog getParentJDialog() {
		System.out.println("GStreamerDataViewer.getParentJDialog()");
		return new JDialog();
	}

	@Override
	public TrackPainter getTrackPainter() {
		System.out.println("GStreamerDataViewer.getTrackPainter()");
		return trackPainter;
	}

	@Override
	public synchronized boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public synchronized void play() {
		System.out.println("GStreamerDataViewer.play()");
		if (playBin != null) {
			playBin.setState(State.PLAYING);
			isPlaying = true;
			playBin.getState(); // wait for the state change to take effect
		}
	}

	@Override
	public synchronized void seekTo(long position) {
//		System.out.println("GStreamerDataViewer.seekTo(" + position + ")");
		if (playBin != null) {
			if (!isPlaying) {
//		        playBin.setState(State.PAUSED);
//				System.out.println("seeking...");
//				playBin.seek(1.0f, Format.TIME, SeekFlags.FLUSH | SeekFlags.SEGMENT, SeekType.SET, playBin.queryPosition(TimeUnit.NANOSECONDS), SeekType.SET, playBin.queryDuration(TimeUnit.NANOSECONDS));
				playBin.seek(ClockTime.fromMillis(position));
//		        playBin.setState(State.PLAYING);
//				lastSeekTime = System.currentTimeMillis();
			} else {
//				System.out.println("ignoring seek.");
			}
		}
	}
	
	@Override
	public synchronized void setDataFeed(final File dataFeed) {
		System.out.println("GStreamerDataViewer.setDataFeed(\"" + dataFeed.getAbsolutePath() + "\")");

		if (this.dataFeed != null) {
			return;
		}
		
		this.dataFeed = dataFeed;
		
		playBin = new PlayBin("OpenSHAPA");
        playBin.setInputFile(dataFeed);

        final VideoComponent videoComponent = new VideoComponent();
        playBin.setVideoSink(videoComponent.getElement());

		playBin.setState(State.PAUSED);
		isPlaying = false;
		playBin.getState(); // wait for the state change to take effect
        
		System.out.println("**** Duration = " + getDuration());
		duration = playBin.queryDuration(TimeUnit.MILLISECONDS);
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(dataFeed.getName());
                frame.getContentPane().add(videoComponent, BorderLayout.CENTER);
                frame.setPreferredSize(new Dimension(640, 480));
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
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
		if (playBin != null) {
			if (rate > 0) {
				playBin.setState(State.PAUSED);
//				playBin.seek(rate, Format.TIME, 0, SeekType.SET, playBin.queryPosition(TimeUnit.NANOSECONDS), SeekType.SET, playBin.queryDuration(TimeUnit.NANOSECONDS));
				playBin.setState(State.PLAYING);
				isPlaying = true;
			} else {
				playBin.setState(State.PAUSED);
				isPlaying = false;
			}
			playBin.getState(); // wait for the state change to take effect
		}
	}

	@Override
	public synchronized void stop() {
		System.out.println("GStreamerDataViewer.stop()");
		if (playBin != null) {
			playBin.setState(State.PAUSED);
			isPlaying = false;
			playBin.getState(); // wait for the state change to take effect
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
                volumeSlider.setValue((int) (volume * 100));
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
            volumeDialog.setVisible(true);
            volumeDialog.setLocation(button.getLocationOnScreen());
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
