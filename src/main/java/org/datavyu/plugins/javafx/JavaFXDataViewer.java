package org.datavyu.plugins.javafx;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.datavyu.models.db.Datastore;
import org.datavyu.plugins.CustomActions;
import org.datavyu.plugins.CustomActionsAdapter;
import org.datavyu.plugins.ViewerStateListener;
import org.datavyu.plugins.quicktime.BaseQuickTimeDataViewer;
import org.datavyu.views.DataController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;


public class JavaFXDataViewer extends BaseQuickTimeDataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    private static final String VIDEO =
            "file:///Users/jesse/Desktop/country_life_butter.mp4";

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
    private JDialog dialog = new JDialog();
    private JavaFXApplication javafxapp;
    private boolean assumedFPS = false;


    public JavaFXDataViewer(final Frame parent, final boolean modal) {
        super(parent, modal);
//        new JFXPanel();
//        stateListeners = new ArrayList<ViewerStateListener>();

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
//        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } finally {
//                    doneLatch.countDown();
                }
            }
        });

//        try {
//            doneLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void setQTVolume(float volume) {
        javafxapp.setVolume(volume);
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
        return dialog;
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
        return getFrameRate();
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
    public void setDataViewerVisible(final boolean isVisible) {
        javafxapp.setVisible(isVisible);
        this.isVisible = isVisible;
    }

    @Override
    public File getDataFeed() {
        return data;
    }

    @Override
    public void setDataFeed(final File dataFeed) {
        System.out.println("Setting datafeed");
        data = dataFeed;
        Platform.setImplicitExit(false);



        // Needed to init JavaFX stuff

//        new JFXPanel();
//        runAndWait(() -> {});

        javafxapp = new JavaFXApplication(dataFeed);

        System.out.println(SwingUtilities.isEventDispatchThread());
        System.out.println(Platform.isFxApplicationThread());

//                    System.out.println("Starting JFX App in new thread...");
        runAndWait(new Runnable() {
            @Override
            public void run() {
                javafxapp.start(new Stage());
            }
        });


//        PlatformImpl.startup(() -> {});




        // Wait for javafx to initialize
        System.out.println("Waiting for JFX app to init");
        while (!javafxapp.isInit()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Inited, going");
        // Hide our fake dialog box
        dialog.setVisible(false);

        // TODO Add in function to guess framerate
    }

    /**
     * Scales the video to the desired ratio.
     *
     * @param scale The new ratio to scale to, where 1.0 = original size, 2.0 = 200% zoom, etc.
     */
    @Override
    protected void scaleVideo(final float scale) {
        javafxapp.setScale(scale);

        notifyChange();
    }

    @Override
    protected void setQTDataFeed(File videoFile) {

    }

    @Override
    protected Dimension getQTVideoSize() {
        return null;
    }

    @Override
    protected float getQTFPS() {
        return getFrameRate();
    }

    @Override
    public long getDuration() {
        return javafxapp.getDuration();
    }

    @Override
    public long getCurrentTime() {
        return javafxapp.getCurrentTime();
    }

    @Override
    public void seekTo(final long position) {

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
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
        playing = false;

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
        javafxapp.pause();
//            }
//        });
    }

    @Override
    public void setPlaybackSpeed(final float rate) {
//        javafxapp.setRate(rate);
    }

    @Override
    public void play() {
        playing = true;

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
        javafxapp.play();
//            }
//        });
    }

    @Override
    protected void cleanUp() {

    }

    @Override
    public void clearDataFeed() {
        stop();
        javafxapp.setVisible(false);
        javafxapp.closeAndDestroy();
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
