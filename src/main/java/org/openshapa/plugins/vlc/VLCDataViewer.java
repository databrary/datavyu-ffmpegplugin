package org.openshapa.plugins.vlc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openshapa.models.db.Datastore;
import org.openshapa.models.id.Identifier;

import org.openshapa.plugins.CustomActions;
import org.openshapa.plugins.CustomActionsAdapter;
import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.ViewerStateListener;

import org.openshapa.util.DataViewerUtils;

import org.openshapa.views.DataController;
import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;


public class VLCDataViewer implements DataViewer {

    /** Data viewer ID. */
    private Identifier id;

    /** Dialog for showing our visualizations. */
    private JDialog vlcDialog;

    /** Data viewer offset. */
    private long offset;

    /** Data to visualize. */
    private File data;
    
    /** Boolean to keep track of whether or not we are playing */
    private boolean playing;

    /** Data viewer state listeners. */
    private List<ViewerStateListener> stateListeners;

    /** Action button for demo purposes. */
    private JButton sampleButton;
    
    /** Surface on which we will display video */
    private Canvas videoSurface;
    
    /** Factory for building our mediaPlayer */
    private MediaPlayerFactory mediaPlayerFactory;

    /** The VLC mediaPlayer */
    private EmbeddedMediaPlayer mediaPlayer;
	
    /** How we will handle fullscreen (i.e., not) */
    private FullScreenStrategy fullScreenStrategy;
	
    /** FPS of the video, calculated on launch */
    private float fps;
    
    /** Length of the video, calculated on launch */
    private long length;
	
    /** The last jog position, making sure we are only calling jog once 
        VLC has issues when trying to go to the same spot multiple times */
    private long last_position;
    
    private Thread vlcThread;
    
    static {
	// Try to load VLC libraries.
	// This discovery function is platform independent
	new NativeDiscovery().discover();
    }

    /** Supported custom actions. */
    private CustomActions actions = new CustomActionsAdapter() {
            @Override public AbstractButton getActionButton1() {
                return sampleButton;
            }
    };

    public VLCDataViewer(final Frame parent, final boolean modal) {
	
	playing = false;
	vlcDialog = new JDialog(parent, modal);
	vlcDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	vlcDialog.setName("VLCDataViewer");
	vlcDialog.setResizable(true);
	
	// Set an initial size
	vlcDialog.setSize(800, 600);

	videoSurface = new Canvas();
	videoSurface.setBackground(Color.black);

	// Set some options for libvlc
	String[] libvlcArgs = {};

	// Create a factory instance (once), you can keep a reference to this
	mediaPlayerFactory = new MediaPlayerFactory(libvlcArgs);

	// Create a full-screen strategy
	fullScreenStrategy = new FullScreenStrategy() {

		@Override
		public void enterFullScreenMode() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					vlcDialog.toFront();
					vlcDialog.setVisible(true);
				}
			});
		}

		@Override
		public void exitFullScreenMode() {
		}

		@Override
		public boolean isFullScreenMode() {
			return false;
		}
	};


	// Create a media player instance
	mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

	// Add it to the dialog and place the video onto the surface
	vlcDialog.setLayout(new BorderLayout());
	vlcDialog.add(videoSurface, BorderLayout.CENTER);
	mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
	mediaPlayer.setFullScreen(false);


        stateListeners = new ArrayList<ViewerStateListener>();
		
    }
	
private void launchEdtTaskNow(Runnable edtTask) {
	if (SwingUtilities.isEventDispatchThread()) {
		edtTask.run();
	} else {
		try {
			SwingUtilities.invokeAndWait(edtTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

    private void launchEdtTaskLater(Runnable edtTask) {
	if (SwingUtilities.isEventDispatchThread()) {
		edtTask.run();
	} else {
		try {
			SwingUtilities.invokeLater(edtTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    }

    @Override public JDialog getParentJDialog() {
        return vlcDialog;
    }

    @Override public float getFrameRate() {
	return fps;
    }

    @Override public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override public Identifier getIdentifier() {
        return id;
    }

    @Override public void setOffset(final long offset) {
        this.offset = offset;
    }

    @Override public long getOffset() {
        return offset;
    }

    @Override public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    @Override public void setDataViewerVisible(final boolean isVisible) {
        vlcDialog.setVisible(isVisible);
    }

    @Override public void setDataFeed(final File dataFeed) {
	vlcDialog.setVisible(true);
	vlcDialog.setName(vlcDialog.getName() + "-" + dataFeed.getName());
	mediaPlayer.startMedia(dataFeed.getAbsolutePath());

	// Grab FPS and length
	
	// Because of the way VLC works, we have to wait for the metadata to become
	// available a short time after we start playing.
	// TODO: reimplement this using the video output event
	try {
	    int i = 0;
	    while(mediaPlayer.getVideoDimension() == null) {
		    if(i > 100)
			break;
		    Thread.sleep(5); 
		    i++; }
	} catch (Exception e) {
		
	}
	
	fps = mediaPlayer.getFps();
	length = mediaPlayer.getLength();
	Dimension d = mediaPlayer.getVideoDimension();
	
	System.out.println(String.format("FPS: %f", fps));
	System.out.println(String.format("Length: %d", length));
	
	// Stop the player. This will rewind whatever
	// frames we just played to get the FPS and length
	mediaPlayer.pause();
	mediaPlayer.setTime(1);
	
	playing = false;
	
	vlcDialog.setSize(d);
	
	// Test to make sure we got the framerate.
	// If we didn't, alert the user that this
	// may not work right.
	if(fps < 1.0) {
		// VLC can't read the framerate for this video for some reason.
		// Set it to 29.97fps so it is still usable for coding.
		fps = 29.97f;
		JOptionPane.showMessageDialog(vlcDialog, 
				"Warning: Unable to detect framerate in video.\n"
				+ "This video may not behave properly. "
				+ "Please try converting to H.264.\n\nSetting "
				+ "framerate to 29.97.");
	}
    }

    @Override public File getDataFeed() {
        return data;
    }

    @Override public long getDuration() {
		return length;
    }

    @Override public long getCurrentTime() throws Exception {
        return mediaPlayer.getTime();
    }

    @Override public void seekTo(final long position) {
	Runnable edtTask = new Runnable() {
		@Override public void run() {

			if(!playing) {
				if(position > 0)
					mediaPlayer.setTime(position);
				else
					mediaPlayer.setTime(1);
			}
		}
	};

	launchEdtTaskLater(edtTask);
    }

    @Override public boolean isPlaying() {
		return playing;
    }

    @Override public void stop() {
	Runnable edtTask = new Runnable() {
		@Override public void run() {
			if(playing) {
				mediaPlayer.pause();
				playing = false;
			}
		}
	};

	launchEdtTaskLater(edtTask);
    }

    @Override public void setPlaybackSpeed(final float rate) {
	if(rate < 0) {
		// VLC cannot play in reverse, so we're going to rely
		// on the clock to do fake jumping
		mediaPlayer.setRate(0);
		if(playing) {
			mediaPlayer.pause();
			playing = false;
		}
	}
	mediaPlayer.setRate(rate);
    }

    @Override public void play() {
	Runnable edtTask = new Runnable() {
		@Override public void run() {
			if(!playing && mediaPlayer.getRate() > 0) {
				mediaPlayer.play();
				playing = true;
			}
		}
	};
		
	launchEdtTaskLater(edtTask);
    }

    @Override public void storeSettings(final OutputStream os) {
        try {
            DataViewerUtils.storeDefaults(this, os);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override public void loadSettings(final InputStream is) {

        try {
            DataViewerUtils.loadDefaults(this, is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override public void addViewerStateListener(
        final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.add(vsl);
        }
    }

    @Override public void removeViewerStateListener(
        final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.remove(vsl);
        }
    }

    @Override public CustomActions getCustomActions() {
        return actions;
    }

    @Override public void clearDataFeed() {
        stop();
	videoSurface.setVisible(false);
	vlcDialog.setVisible(false);
	mediaPlayerFactory.release();
    }

    @Override public void setDatastore(final Datastore sDB) {
        // TODO Auto-generated method stub
    }

    @Override public void setParentController(
        final DataController dataController) {
        // TODO Auto-generated method stub
    }

}
