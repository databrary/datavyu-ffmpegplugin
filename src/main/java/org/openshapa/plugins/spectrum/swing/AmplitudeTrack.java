package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.List;

import javax.swing.SwingWorker;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.plugins.spectrum.engine.AmplitudeProcessor;
import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;


/**
 * Used to plot amplitude data over the time domain.
 */
public final class AmplitudeTrack extends TrackPainter
    implements PropertyChangeListener {

    /** Color used to paint the amplitude data. */
    private static final Color DATA_COLOR = new Color(0, 0, 0, 200);

    /** Excluded properties. */
    private static final List<String> EXCLUDED_PROPS = ImmutableList.of(
            "locked", "trackId", "erroneous", "bookmark", "trackName",
            "selected", "state");

    /** Contains the amplitude data to visualize. */
    private StereoAmplitudeData data;

    /** Have we already registered for property changes. */
    private boolean registered;

    /** Path for left channel amplitude data. */
    private Path2D leftAmp;

    /** Path for right channel amplitude data. */
    private Path2D rightAmp;

    /** Handles path pre-computing. */
    private PathWorker worker;

    /** Media file used to compute amplitude data. */
    private File mediaFile;

    /** Number of audio channels in the amplitude data. */
    private int channels;

    /** Handles amplitude processing. */
    private AmplitudeProcessor processor;

    public AmplitudeTrack() {
        registered = false;
    }

    public void deregister() {
        viewableModel.removePropertyChangeListener(this);
        trackModel.removePropertyChangeListener(this);
    }

    public void setMedia(final File mediaFile, final int channels) {
        this.mediaFile = mediaFile;
        this.channels = channels;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (Iterables.contains(EXCLUDED_PROPS, evt.getPropertyName())) {
            return;
        }

        execProcessor();

        leftAmp = null;
        rightAmp = null;
    }

    /**
     * Set the amplitude data to visualize.
     *
     * @param data
     *            amplitude data to visualize.
     */
    public void setData(final StereoAmplitudeData data) {
        this.data = data;

        if (worker != null) {

            // If a worker is already in progress, cancel it.
            worker.cancel(true);
        }

        // Make a worker thread to compute paths.
        worker = new PathWorker();
        worker.execute();
    }

    @Override protected void paintCustom(final Graphics g) {

        if (!registered) {
            trackModel.addPropertyChangeListener(this);
            viewableModel.addPropertyChangeListener(this);
            registered = true;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate carriage start and end pixel positions.
        final int startXPos = computePixelXCoord(trackModel.getOffset());
        final int endXPos = computePixelXCoord(trackModel.getDuration()
                + trackModel.getOffset());

        // Carriage height.
        final int carriageHeight = (int) (getHeight() * 7D / 10D);

        // Carriage offset from top of panel.
        final int carriageYOffset = (int) (getHeight() * 2D / 10D);

        // Y-coordinate for left channel.
        final int midYLeftPos = carriageYOffset + (carriageHeight / 4);

        // Y-coordinate for right channel.
        final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

        // Draw the baseline zero amplitude.
        g2d.setColor(DATA_COLOR);
        g2d.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
        g2d.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

        if (data == null) {
            execProcessor();

            return;
        }

        // Draw left channel data.
        if (leftAmp != null) {
            g2d.draw(leftAmp);
        }

        // Draw right channel data.
        if (rightAmp != null) {
            g2d.draw(rightAmp);
        }

    }

    /**
     * Used to compute pixel x-coordinates based on a time value.
     *
     * @param time
     *            Time in milliseconds.
     * @return Pixel coordinate.
     */
    private double computeXCoord(final double time) {
        final double ratio = viewableModel.getIntervalWidth()
            / viewableModel.getIntervalTime();

        return (time * ratio) - (viewableModel.getZoomWindowStart() * ratio);
    }

    /**
     * Helper function to process amplitude data.
     */
    private void execProcessor() {

        // No media file; nothing to process.
        if (mediaFile == null) {
            return;
        }

        // If the first processing run is already underway, do not cancel it.
        if ((processor != null) && (data == null)) {
            return;
        }

        // If some processing run is underway, cancel it because something
        // changed.
        if (processor != null) {
            processor.cancel(true);
        }

        // 1. Find the resolution of a single pixel, ms/pixel.
        double resolution = viewableModel.getIntervalTime()
            / (double) viewableModel.getIntervalWidth();

        // TODO refactor magic constants.
        resolution = Math.min(resolution, 200); // Min resample = 5Hz
        resolution = Math.max(1, resolution); // Max resample = 1kHz

        // 2. Calculate the resampling rate.
        int rate = (int) (1000000 / resolution);
        rate = Math.max(5, rate);
        rate = Math.min(rate, 1000000);

        // 3. Calculate the times to sample.
        long start = Math.max(viewableModel.getZoomWindowStart(),
                trackModel.getOffset());
        long end = Math.min(trackModel.getOffset() + trackModel.getDuration(),
                viewableModel.getZoomWindowEnd());

        // 4. Make the worker thread.
        processor = new AmplitudeProcessor(mediaFile, this, channels);
        processor.setDataTimeSegment(start, end, MILLISECONDS);
        processor.setSampleRate(rate);
        processor.execute();
    }

    /**
     * Inner worker for calculating paths.
     */
    private final class PathWorker extends SwingWorker<Path2D[], Void> {
        @Override protected Path2D[] doInBackground() throws Exception {

            if (data == null) {
                return null;
            }

            Path2D[] amps = new Path2D[] {
                    new Path2D.Double(), new Path2D.Double()
                };

            // Carriage height.
            final int carriageHeight = (int) (getHeight() * 7D / 10D);

            // Calculate carriage start pixel position.
            final double startXPos = computeXCoord(MILLISECONDS.convert(
                        data.getDataTimeStart(), data.getDataTimeUnit()));

            // Carriage offset from top of panel.
            final int carriageYOffset = (int) (getHeight() * 2D / 10D);

            // Y-coordinate for left channel.
            final int midYLeftPos = carriageYOffset + (carriageHeight / 4);

            // Y-coordinate for right channel.
            final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

            // Height for 100% amplitude.
            final int ampHeight = carriageHeight / 4;

            // Calculate left amplitude data.
            amps[0].moveTo(startXPos, midYLeftPos);

            int offsetCounter = 1;

            for (Double amp : data.getDataL()) {

                if (isCancelled()) {
                    return null;
                }

                double interval = data.getTimeInterval() * offsetCounter;
                double offset = computeXCoord(interval
                        + data.getDataTimeStart());

                offsetCounter++;

                amps[0].lineTo(offset, midYLeftPos + (-amp * ampHeight));
            }

            // Calculate right amplitude data.
            amps[1].moveTo(startXPos, midYRightPos);
            offsetCounter = 1;

            for (Double amp : data.getDataR()) {

                if (isCancelled()) {
                    return null;
                }

                double interval = data.getTimeInterval() * offsetCounter;
                double offset = computeXCoord(interval
                        + data.getDataTimeStart());
                offsetCounter++;

                if (amp != 0) {
                    amps[1].lineTo(offset, midYRightPos + (-amp * ampHeight));
                } else {
                    amps[1].lineTo(offset, midYRightPos);
                }

            }

            return amps;
        }

        @Override protected void done() {

            try {
                Path2D[] result = get();

                if (result != null) {
                    leftAmp = result[0];
                    rightAmp = result[1];
                }

                repaint();

            } catch (Exception e) {
                // Do nothing.
            }
        }
    }

}
