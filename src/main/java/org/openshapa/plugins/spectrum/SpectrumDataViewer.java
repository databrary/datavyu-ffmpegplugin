package org.openshapa.plugins.spectrum;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.openshapa.plugins.spectrum.engine.PlaybackEngine;
import org.openshapa.plugins.spectrum.swing.AmplitudeTrack;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.ViewerStateListener;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;


public class SpectrumDataViewer implements DataViewer {

    /** Number of microseconds in one millisecond. */
    private static final long MILLISECONDS = 1000;

    /** Dialog. */
    private SpectrumDialog dialog;

    /** Track. */
    private TrackPainter track;

    /** Data controller. */
    private DataController dataC;

    /** Media file being visualized. */
    private File mediaFile;

    /** Playback offset. */
    private long playbackOffset;

    /** Duration of media file in milliseconds. */
    private long duration;

    private PlaybackEngine engine;

    public SpectrumDataViewer(final Frame parent, final boolean modal) {

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    track = new AmplitudeTrack();

                    dialog = new SpectrumDialog(parent, modal);
                    dialog.setDefaultCloseOperation(
                        WindowConstants.DISPOSE_ON_CLOSE);
                    dialog.addWindowListener(new WindowAdapter() {
                            public void windowClosing(final WindowEvent evt) {
                                dialogClosing(evt);
                            }
                        });
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }

    }

    @Override public long getCurrentTime() throws Exception {
        return engine.getCurrentTime();
    }

    @Override public File getDataFeed() {
        return mediaFile;
    }

    @Override public long getDuration() {
        return duration;
    }

    @Override public float getFrameRate() {
        return 0;
    }

    @Override public long getOffset() {
        return playbackOffset;
    }

    /**
     * Must be called from the EDT.
     */
    @Override public JDialog getParentJDialog() {

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Should be in EDT.");
        }

        return dialog;
    }

    /**
     * Must be called from the EDT.
     */
    @Override public TrackPainter getTrackPainter() {

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Should be in EDT.");
        }

        return track;
    }

    @Override public boolean isPlaying() {
        return (engine != null) && engine.isPlaying();
    }

    @Override public void play() {

        if (engine != null) {
            engine.startPlayback();
        }
    }

    @Override public void seekTo(final long time) {

        if (engine != null) {
            engine.seek(time);
        }
    }

    @Override public void setDataFeed(final File file) {
        mediaFile = file;


        engine = new PlaybackEngine(mediaFile, dialog);
        engine.start();

        IMediaReader mediaReader = ToolFactory.makeReader(
                file.getAbsolutePath());
        mediaReader.open();

        // Record media duration
        duration = mediaReader.getContainer().getDuration() / MILLISECONDS;

        mediaReader.close();

        Runnable edtTask = new Runnable() {
                @Override public void run() {

                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                }
            };

        SwingUtilities.invokeLater(edtTask);
    }

    @Override public void setOffset(final long offset) {
        playbackOffset = offset;
    }

    @Override public void setParentController(final DataController dataC) {
        this.dataC = dataC;
    }

    @Override public void setPlaybackSpeed(final float arg0) {
        // TODO Auto-generated method stub
    }

    @Override public void stop() {

        if (engine != null) {
            engine.stopPlayback();
        }
    }

    /**
     * Handles dialog window closing event.
     *
     * @param evt
     *            Event to handle.
     */
    private void dialogClosing(final WindowEvent evt) {

        engine.interrupt();

        if (dataC != null) {
            dataC.shutdown(this);
        }
    }

    @Override public void addViewerStateListener(
        final ViewerStateListener listener) {
        // Do nothing; no events to report.
    }

    @Override public void loadSettings(final InputStream is) {
        // Do nothing.
    }

    @Override public void storeSettings(final OutputStream os) {
        // Do nothing.
    }

    @Override public ImageIcon getActionButtonIcon1() {
        return null;
    }

    @Override public ImageIcon getActionButtonIcon2() {
        return null;
    }

    @Override public ImageIcon getActionButtonIcon3() {
        return null;
    }

    @Override public void handleActionButtonEvent1(final ActionEvent arg0) {
    }

    @Override public void handleActionButtonEvent2(final ActionEvent arg0) {
    }

    @Override public void handleActionButtonEvent3(final ActionEvent arg0) {
    }

}
