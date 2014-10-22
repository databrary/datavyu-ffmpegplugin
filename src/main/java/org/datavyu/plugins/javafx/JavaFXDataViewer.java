package org.datavyu.plugins.javafx;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.datavyu.Datavyu;
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
import java.util.concurrent.CountDownLatch;


public class JavaFXDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    private static final String VIDEO =
            "file:///Users/jesse/Desktop/country_life_butter.mp4";
    /**
     * Data viewer ID.
     */
    private Identifier id;
    /**
     * Dialog for showing our visualizations.
     */
    private JDialog vlcDialog;
    /**
     * Data viewer offset.
     */
    private long offset;
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
     * Surface on which we will display video
     */
    private Canvas videoSurface;
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
    private Thread vlcThread;
    private JDialog dialog = new JDialog();
    private JavaFXApplication javafxapp;
    private boolean assumedFPS = false;

    public JavaFXDataViewer(final Frame parent, final boolean modal) {
        stateListeners = new ArrayList<ViewerStateListener>();

    }

    public static void runAndWait(final Runnable action) {
        if (action == null)
            throw new NullPointerException("action");

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        // queue on JavaFX thread and wait for completion
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } finally {
                    doneLatch.countDown();
                }
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            // ignore exception
        }
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
        return javafxapp.getFrameRate();
    }

    public void setFrameRate(float fpsIn) {
        fps = fpsIn;
        assumedFPS = false;
    }

    @Override
    public float getDetectedFrameRate() {
        return 30;
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
        vlcDialog.setVisible(isVisible);
    }

    @Override
    public File getDataFeed() {
        return data;
    }

    @Override
    public void setDataFeed(final File dataFeed) {
        data = dataFeed;


        // Needed to init JavaFX stuff
        new JFXPanel();
        javafxapp = new JavaFXApplication(dataFeed);

        runAndWait(new Runnable() {
            @Override
            public void run() {
                javafxapp.start(new Stage());
            }
        });


        // Wait for javafx to initialize
        while (!javafxapp.isInit()) {
        }


        // Test to make sure we got the framerate.
        // If we didn't, alert the user that this
        // may not work right.
        if (fps < 1.0) {
            // VLC can't read the framerate for this video for some reason.
            // Set it to the fallback rate so it is still usable for coding.
            fps = FALLBACK_FRAME_RATE;
            /*
            JOptionPane.showMessageDialog(vlcDialog,
                    "Warning: Unable to detect framerate in video.\n"
                            + "This video may not behave properly. "
                            + "Please try converting to H.264.\n\n"
                            + "This can be done under Controller->Convert Videos.\n"
                            + "Setting framerate to " + FALLBACK_FRAME_RATE);
                    */
        }
    }

    @Override
    public long getDuration() {
        return javafxapp.getDuration();
    }

    @Override
    public long getCurrentTime() throws Exception {
        return javafxapp.getCurrentTime();
    }

    @Override
    public void seekTo(final long position) {
        System.out.println("CURRENT TIME IN MILLIS:" + javafxapp.getCurrentTime());
        System.out.println("CURRENT CONT TIME:" + Datavyu.getDataController().getCurrentTime());

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
        javafxapp.pause();
        javafxapp.seek(position);
//            }
//        });

    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
        System.out.println("CURRENT TIME IN MILLIS:" + javafxapp.getCurrentTime());
        playing = false;
        javafxapp.pause();
//            }
//        });
    }

    @Override
    public void setPlaybackSpeed(final float rate) {

    }

    @Override
    public void play() {
        System.out.println("CURRENT TIME IN MILLIS:" + javafxapp.getCurrentTime());

        playing = true;
        javafxapp.play();
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
        videoSurface.setVisible(false);
        vlcDialog.setVisible(false);
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
        return assumedFPS;
    }

}
