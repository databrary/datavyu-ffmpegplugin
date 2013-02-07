package org.datavyu.plugins.vlcrc;

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

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;


public class VLCRCDataViewer implements DataViewer {

    /** Data viewer ID. */
    private Identifier id;

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
	
    /** FPS of the video, calculated on launch */
    private float fps;
    
    /** Length of the video, calculated on launch */
    private long length;

    
    
    
    private VLCRCProcess vlcThread;
    
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

    public VLCRCDataViewer(final Frame parent, final boolean modal) {
	
	playing = false;


	     String vlcParameters = String.format(
           "-I rc --rc-fake-tty --video-on-top --disable-screensaver --no-video-title-show " +
           "--no-mouse-events --no-keyboard-events --no-fullscreen --no-video-deco " +
           "--video-x %d --video-y %d --width %d --height %d",
           200,      // X
           200,      //Y
           800,    //Width
           600     //Height
           );

	vlcThread = new VLCRCProcess();
	
	System.out.println("Starting VLC process");
	vlcThread.start();
	System.out.println("VLC process started.");
	
	while(!vlcThread.initialized) {
		try {
		   Thread.sleep(10);
		} catch (Exception e) {
		
		}
	}
	System.out.println("VLC process initialized");

        stateListeners = new ArrayList<ViewerStateListener>();
		
    }

    @Override public JDialog getParentJDialog() {
	return new JDialog();
//        return vlcThread.vlcDialog;
    }

    @Override public float getFrameRate() {
	return vlcThread.getFps();
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
        vlcThread.vlcDialog.setVisible(isVisible);
    }

    @Override public void setDataFeed(final File dataFeed) {
	vlcThread.setMedia(dataFeed);
    }

    @Override public File getDataFeed() {
        return data;
    }

    @Override public long getDuration() {
	return vlcThread.length;
    }

    @Override public long getCurrentTime() throws Exception {
        return vlcThread.getTime();
    }

    @Override public void seekTo(final long position) {
	if(!playing) {
		if(position > 0)
//			vlcThread.mediaPlayer.nextFrame();
			vlcThread.setTime(position);
		else
			vlcThread.setTime(0);
	}
    }

    @Override public boolean isPlaying() {
	return playing;
    }

    @Override public void stop() {
	if(playing) {
		vlcThread.pause();
		playing = false;
	}

    }

    @Override public void setPlaybackSpeed(final float rate) {
	if(rate < 0) {
		// VLC cannot play in reverse, so we're going to rely
		// on the clock to do fake jumping
		vlcThread.setRate(0);
		if(playing) {
			vlcThread.pause();
			playing = false;
		}
	} else {
		vlcThread.setRate(rate);
	}
    }

    @Override public void play() {
	if(!playing && vlcThread.getRate() > 0) {
		vlcThread.mediaPlayer.play();
		playing = true;
	}
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
	vlcThread.clearDataFeed();
	vlcThread.interrupt();
    }

    @Override public void setDatastore(final Datastore sDB) {
        // TODO Auto-generated method stub
    }

    @Override public void setParentController(
        final DataController dataController) {
        // TODO Auto-generated method stub
    }

}
