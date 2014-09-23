package org.datavyu.plugins.xuggler;

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

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class XugglerDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    /**
     * Data viewer ID.
     */
    private Identifier id;
    /**
     * Data viewer offset.
     */
    private long offset = 0;
    /**
     * Data to visualize.
     */
    private File data;
    /**
     * Boolean to keep track of whether or not we are playing
     */
    private boolean playing;
    /**
     * Data viewer state listeners.
     */
    private List<ViewerStateListener> stateListeners;
    /**
     * Action button for demo purposes.
     */
    private JButton sampleButton;
    /**
     * Supported custom actions.
     */
    private CustomActions actions = new CustomActionsAdapter() {
        @Override
        public AbstractButton getActionButton1() {
            return sampleButton;
        }
    };
    /**
     * FPS of the video, calculated on launch
     */
    private float fps;
    /**
     * Length of the video, calculated on launch
     */
    private long length;
    /**
     * The last jog position, making sure we are only calling jog once
     * VLC has issues when trying to go to the same spot multiple times
     */
    private long last_position;
    private Thread xugglerThread;
    /**
     * The audio line we'll output sound to; it'll be the default audio device on your system if available
     */

    /**
     * The window we'll draw the video on.
     */
    private XugglerMediaPlayer mediaPlayer;


    public XugglerDataViewer(final Frame parent, final boolean modal) {

//        Runnable r = new Runnable() {
//            public void run() {
        mediaPlayer = new XugglerMediaPlayer();
//            }
//        };

//        xugglerThread = new Thread(r);

//        xugglerThread.start();

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

    @Override
    public JDialog getParentJDialog() {
        return new JDialog();
    }

    @Override
    public float getFrameRate() {
        return mediaPlayer.getFps();
    }

    @Override
    public void setFrameRate(float fpsIn) {
        fps = fpsIn;
    }

    @Override
    public float getDetectedFrameRate() {
        return mediaPlayer.getFps();
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public void setOffset(final long offset) {
        this.offset = offset;
    }

    @Override
    public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    @Override
    public void setDataViewerVisible(final boolean isVisible) {
        mediaPlayer.setVisible(isVisible);
    }

    @Override
    public File getDataFeed() {
        return data;
    }

    @Override
    public void setDataFeed(final File dataFeed) {
//        String filename = "F:\\Movies\\Ascenseur pour l'échafaud (1958) Louis Malle\\Ascenseur pour lechafaud (1958) Louis Malle.avi";

        data = dataFeed;
        mediaPlayer.setDataFeed(dataFeed);
        setDataViewerVisible(true);
    }

    @Override
    public long getDuration() {
        return mediaPlayer.getLength();
    }

    @Override
    public long getCurrentTime() throws Exception {
        return mediaPlayer.getCurrentTime();
    }

    @Override
    public void seekTo(final long position) {
        mediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void setPlaybackSpeed(final float rate) {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
//                if (rate < 0) {
//                    // VLC cannot play in reverse, so we're going to rely
//                    // on the clock to do fake jumping
//                    mediaPlayer.setRate(0);
//                    if (playing) {
//                        mediaPlayer.pause();
//                        playing = false;
//                    }
//                }
//                mediaPlayer.setRate(rate);
//                mediaPlayer.setTime(mediaPlayer.getTime());
            }
        };
        launchEdtTaskLater(edtTask);
    }

    @Override
    public void play() {
        mediaPlayer.play();
    }

    @Override
    public void storeSettings(final OutputStream os) {
        try {
            DataViewerUtils.storeDefaults(this, os);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void loadSettings(final InputStream is) {

        try {
            DataViewerUtils.loadDefaults(this, is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addViewerStateListener(
            final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.add(vsl);
        }
    }

    @Override
    public void removeViewerStateListener(
            final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.remove(vsl);
        }
    }

    @Override
    public CustomActions getCustomActions() {
        return actions;
    }

    @Override
    public void clearDataFeed() {
        stop();
//        videoSurface.setVisible(false);
//        vlcDialog.setVisible(false);
//        mediaPlayerFactory.release();
    }

    @Override
    public void setDatastore(final Datastore sDB) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setParentController(
            final DataController dataController) {
        // TODO Auto-generated method stub
    }

    public boolean usingAssumedFPS() {
        return false;
    }

}
