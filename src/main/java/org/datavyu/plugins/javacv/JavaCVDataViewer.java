package org.datavyu.plugins.javacv;


import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
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
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class JavaCVDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
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
     * How we will handle fullscreen (i.e., not)
     */
    private FullScreenStrategy fullScreenStrategy;
    /**
     * FPS of the video, calculated on launch
     */
    private float fps;
    /**
     * Length of the video, calculated on launch
     */
    private long length;
    private OpenCVFrameGrabber player;
    private CanvasFrame canvasFrame;
    private boolean assumedFPS = false;

    public JavaCVDataViewer(final Frame parent, final boolean modal) {

        playing = false;


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
        return vlcDialog;
    }

    @Override
    public float getFrameRate() {
        return fps;
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


        System.out.println(String.format("FPS: %f", fps));
        System.out.println(String.format("Length: %d", length));

        // Test to see if we should prompt user to convert the video to
        // the ideal format
        playing = false;

        player = new OpenCVFrameGrabber(dataFeed);
        try {
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        canvasFrame = new CanvasFrame("Extracted Frame", 1);

        // Read frame by frame, stop early if the display window is closed
        for (int i = 0; i < player.getLengthInFrames(); i++) {
            try {
                player.setFrameNumber(i);
                canvasFrame.showImage(player.grab());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public long getDuration() {
        return length;
    }

    @Override
    public long getCurrentTime() throws Exception {
        return 0;
    }

    @Override
    public void seekTo(final long position) {

    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {

    }

    @Override
    public void setPlaybackSpeed(final float rate) {

    }

    @Override
    public void play() {

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
