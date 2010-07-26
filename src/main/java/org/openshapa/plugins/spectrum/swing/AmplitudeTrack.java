package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;


/**
 * Used to plot amplitude data over the time domain.
 */
public final class AmplitudeTrack extends TrackPainter
    implements PropertyChangeListener {

    /** Color used to paint the amplitude data. */
    private static final Color DATA_COLOR = new Color(0, 0, 0, 200);

    /** Contains the amplitude data to visualize. */
    private StereoAmplitudeData data;

    private boolean registered;

    private Path2D leftAmp;

    private Path2D rightAmp;

    private SwingWorker worker;

    public AmplitudeTrack() {
        registered = false;
    }

    public void deregister() {
        viewableModel.removePropertyChangeListener(this);
        trackModel.removePropertyChangeListener(this);
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (worker != null) {
            worker.cancel(true);
        }

        worker = new SwingWorker<Path2D[], Void>() {
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
                    final double startXPos = computeXCoord(
                            trackModel.getOffset());

                    // Carriage offset from top of panel.
                    final int carriageYOffset = (int) (getHeight() * 2D / 10D);

                    // Y-coordinate for left channel.
                    final int midYLeftPos = carriageYOffset
                        + (carriageHeight / 4);

                    // Y-coordinate for right channel.
                    final int midYRightPos = carriageYOffset
                        + (3 * carriageHeight / 4);

                    // Height for 100% amplitude.
                    final int ampHeight = carriageHeight / 4;

                    // Calculate left amplitude data.
                    amps[0].moveTo(startXPos, midYLeftPos);

                    int offsetCounter = 1;

                    for (Double amp : data.getDataL()) {

                        if (isCancelled()) {
                            return null;
                        }

                        long interval = data.getTimeInterval() * offsetCounter;
                        double offset = computeXCoord(MILLISECONDS.convert(
                                    interval, data.getTimeUnit())
                                + trackModel.getOffset());

                        offsetCounter++;

                        amps[0].lineTo(offset,
                            midYLeftPos + (-amp * ampHeight));
                    }

                    // Calculate right amplitude data.
                    amps[1].moveTo(startXPos, midYRightPos);
                    offsetCounter = 1;

                    for (Double amp : data.getDataR()) {

                        if (isCancelled()) {
                            return null;
                        }

                        long interval = data.getTimeInterval() * offsetCounter;
                        double offset = computeXCoord(MILLISECONDS.convert(
                                    interval, data.getTimeUnit())
                                + trackModel.getOffset());
                        offsetCounter++;

                        amps[1].lineTo(offset,
                            midYRightPos + (-amp * ampHeight));
                    }

                    return amps;
                }

                protected void done() {

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

            };
        worker.execute();
    }

    /**
     * Set the amplitude data to visualize.
     *
     * @param data
     *            amplitude data to visualize.
     */
    public void setData(final StereoAmplitudeData data) {

        if (!registered) {
            trackModel.addPropertyChangeListener(this);
            viewableModel.addPropertyChangeListener(this);
            registered = true;
        }

        this.data = data;
        leftAmp = null;
        rightAmp = null;
    }

    @Override protected void paintCustom(final Graphics g) {
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

        // Height for 100% amplitude.
        final int ampHeight = carriageHeight / 4;

        // Draw the baseline zero amplitude.
        g2d.setColor(DATA_COLOR);
        g2d.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
        g2d.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

        g2d.drawOval((int) computeXCoord(trackModel.getOffset()),
            carriageYOffset, 30, 30);

        if (data == null) {
            return;
        }

        // Draw left channel data.
        if (leftAmp == null) {
            leftAmp = new Path2D.Double();
            leftAmp.moveTo(startXPos, midYLeftPos);

            int offsetCounter = 1;

            for (Double amp : data.getDataL()) {
                long interval = data.getTimeInterval() * offsetCounter;
                double offset = computeXCoord(MILLISECONDS.convert(interval,
                            data.getTimeUnit()) + trackModel.getOffset());
                offsetCounter++;

                leftAmp.lineTo(offset, midYLeftPos + (-amp * ampHeight));
            }
        }

        g2d.draw(leftAmp);

        // Draw right channel data.
        if (rightAmp == null) {
            rightAmp = new Path2D.Double();
            rightAmp.moveTo(startXPos, midYRightPos);

            int offsetCounter = 1;

            for (Double amp : data.getDataR()) {
                long interval = data.getTimeInterval() * offsetCounter;
                double offset = computeXCoord(MILLISECONDS.convert(interval,
                            data.getTimeUnit()) + trackModel.getOffset());
                offsetCounter++;

                rightAmp.lineTo(offset, midYRightPos + (-amp * ampHeight));
            }
        }

        g2d.draw(rightAmp);
    }

    private double computeXCoord(final long time) {
        final double ratio = viewableModel.getIntervalWidth()
            / viewableModel.getIntervalTime();

        return (time * ratio) - (viewableModel.getZoomWindowStart() * ratio);
    }

}
