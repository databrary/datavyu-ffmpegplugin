package org.datavyu.plugins.vlcoop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.datavyu.models.db.Datastore;
import org.datavyu.models.id.Identifier;

import org.datavyu.plugins.CustomActions;
import org.datavyu.plugins.CustomActionsAdapter;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.ViewerStateListener;

import org.datavyu.util.DataViewerUtils;

import org.datavyu.views.DataController;
import org.datavyu.views.component.DefaultTrackPainter;
import org.datavyu.views.component.TrackPainter;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.oop.component.OutOfProcessMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.events.MediaPlayerEventType;


public class VLCOOPDataViewer implements DataViewer {

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
    
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private List<MediaPlayerEventListener> listeners = new ArrayList<MediaPlayerEventListener>(); 
    PlayerPanel playerPanel;
	
    /** FPS of the video, calculated on launch */
    private float fps;
    
    /** Length of the video, calculated on launch */
    private long length;
	
    /** The last jog position, making sure we are only calling jog once 
        VLC has issues when trying to go to the same spot multiple times */
    private long last_position;
    
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

    public VLCOOPDataViewer(final Frame parent, final boolean modal) {
	

	playing = false;
	vlcDialog = new JDialog(parent, modal);
	vlcDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	vlcDialog.setName("VLCOOPDataViewer");
	vlcDialog.setResizable(true);
	
	stateListeners = new ArrayList<ViewerStateListener>();
	
	// Set an initial size
	vlcDialog.setSize(800, 600);

//	executorService.submit(new Runnable() {
//            @Override
//            public void run() {
	playerPanel = new PlayerPanel();
	
	playerPanel.setWrapper(new OutOfProcessMediaPlayerComponent());	
	
//	RemoteListener l = new RemoteListener();
//        listeners.add(l);
	
	// Lots of traffic will be generated when sending position- and time-changed
	// events, maybe not a problem but you have the option of inhibiting them
	
//	playerPanel.wrapper().mediaPlayer().enableEvents(MediaPlayerEventType.notEvents(MediaPlayerEventType.POSITION_CHANGED, MediaPlayerEventType.TIME_CHANGED));
//        playerPanel.wrapper().mediaPlayer().addMediaPlayerEventListener(l);


	// Add it to the dialog and place the video onto the surface
	vlcDialog.setLayout(new BorderLayout());
	vlcDialog.add(playerPanel.videoSurface(), BorderLayout.CENTER);


	
//	    }});
	        
		
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
//	executorService.submit(new Runnable() {
//            @Override
//            public void run() {
	vlcDialog.setVisible(true);
	System.out.println("MAKING VISIBLE");
	playerPanel.attachVideoSurface();
		System.out.println("ATTACHED SURFACE");

	vlcDialog.setName(vlcDialog.getName() + "-" + dataFeed.getName());
	playerPanel.wrapper().mediaPlayer().playMedia(dataFeed.getAbsolutePath());
		System.out.println("PLAYED");


	// Grab FPS and length
	
	// Because of the way VLC works, we have to wait for the metadata to become
	// available a short time after we start playing.
	// TODO: reimplement this using the video output event
//	try {
//	    int i = 0;
//	    while(playerPanel.wrapper().mediaPlayer().getVideoDimension() == null) {
//		    if(i > 100)
//			break;
//		    Thread.sleep(5);
//		    i++; }
//	} catch (Exception e) {
//		
//	}
//	
//	fps = playerPanel.wrapper().mediaPlayer().getFps();
//	length = playerPanel.wrapper().mediaPlayer().getLength();
//	Dimension d = playerPanel.wrapper().mediaPlayer().getVideoDimension();
//	
//	System.out.println(String.format("FPS: %f", fps));
//	System.out.println(String.format("Length: %d", length));
//	
//	// Stop the player. This will rewind whatever
//	// frames we just played to get the FPS and length
//	playerPanel.wrapper().mediaPlayer().pause();
//	playerPanel.wrapper().mediaPlayer().setTime(1);
//	
//	playing = false;
//	
//	vlcDialog.setSize(d);
//	
//	// Test to make sure we got the framerate.
//	// If we didn't, alert the user that this
//	// may not work right.
//	if(fps < 1.0) {
//		// VLC can't read the framerate for this video for some reason.
//		// Set it to 29.97fps so it is still usable for coding.
//		fps = 29.97f;
//		JOptionPane.showMessageDialog(vlcDialog, 
//				"Warning: Unable to detect framerate in video.\n"
//				+ "This video may not behave properly. "
//				+ "Please try converting to H.264.\n\nSetting "
//				+ "framerate to 29.97.");
//	}
	
//	    }});
		
    }

    @Override public File getDataFeed() {
        return data;
    }

    @Override public long getDuration() {
		return length;
    }

    @Override public long getCurrentTime() throws Exception {
        return playerPanel.wrapper().mediaPlayer().getTime();
    }

    @Override public void seekTo(final long position) {
		executorService.submit(new Runnable() {
            @Override
            public void run() {

			if(!playing) {
				if(position > 0)
					playerPanel.wrapper().mediaPlayer().setTime(position);
				else
					playerPanel.wrapper().mediaPlayer().setTime(1);
			}
		}
	});
    }

    @Override public boolean isPlaying() {
		return playing;
    }

    @Override public void stop() {
	Runnable edtTask = new Runnable() {
		@Override public void run() {
			if(playing) {
				playerPanel.wrapper().mediaPlayer().pause();
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
		playerPanel.wrapper().mediaPlayer().setRate(0);
		if(playing) {
			playerPanel.wrapper().mediaPlayer().pause();
			playing = false;
		}
	}
	playerPanel.wrapper().mediaPlayer().setRate(rate);
    }

    @Override public void play() {
	Runnable edtTask = new Runnable() {
		@Override public void run() {
			if(!playing && playerPanel.wrapper().mediaPlayer().getRate() > 0) {
				playerPanel.wrapper().mediaPlayer().play();
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
	playerPanel.videoSurface().setVisible(false);
	vlcDialog.setVisible(false);
	playerPanel.wrapper().mediaPlayer().release();
    }

    @Override public void setDatastore(final Datastore sDB) {
        // TODO Auto-generated method stub
    }

    @Override public void setParentController(
        final DataController dataController) {
        // TODO Auto-generated method stub
    }
    
    private class RemoteListener extends MediaPlayerEventAdapter {

        public RemoteListener() {
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            log("playing");
            playerPanel.showVideo(true);
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            log("paused");
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            log("stopped");
            playerPanel.showVideo(false);
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            log("finished");
            playerPanel.showVideo(false);
        }

        private void log(String s) {
            Logger.debug("Listener {}: {}", s);
        }
    }

}
