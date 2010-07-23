package org.openshapa.plugins.spectrum.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;


/**
 * Used to plot amplitude data over the time domain.
 */
public final class AmplitudeTrack extends TrackPainter
    implements PropertyChangeListener {

    /** Color used to paint the amplitude data. */
    private static final Color DATA_COLOR = new Color(0, 0, 0, 100);

    /** Contains the amplitude data to visualize. */
    private StereoAmplitudeData data;

    /** Amplitude data image. */
    private BufferedImage ampImage;

    private boolean registered;

    public AmplitudeTrack() {
        registered = false;
    }

    public void deregister() {
        viewableModel.removePropertyChangeListener(this);
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {
        System.out.println("Property change");

        if (data == null) {
            return;
        }

        Runnable edtTask = new Runnable() {

                @Override public void run() {
                    ampImage = new BufferedImage(getWidth(), getHeight(),
                            BufferedImage.TYPE_4BYTE_ABGR);

                    Graphics ampG = ampImage.getGraphics();

                    // Calculate carriage start and end pixel positions.
                    final int startXPos = computePixelXCoord(
                            trackModel.getOffset());
                    final int endXPos = computePixelXCoord(
                            trackModel.getDuration() + trackModel.getOffset());

                    // Carriage height.
                    final int carriageHeight = (int) (getHeight() * 7D / 10D);

                    // Carriage offset from top of panel.
                    final int carriageYOffset = (int) (getHeight() * 2D / 10D);

                    // Y-coordinate for left channel.
                    final int midYLeftPos = carriageYOffset
                        + (carriageHeight / 4);

                    // Y-coordinate for right channel.
                    final int midYRightPos = carriageYOffset
                        + (3 * carriageHeight / 4);

                    ampG.setColor(DATA_COLOR);

                    // Draw the baseline zero amplitude.
                    ampG.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
                    ampG.drawLine(startXPos, midYRightPos, endXPos,
                        midYRightPos);

                    if (data == null) {
                        return;
                    }

                    // Height for 100% amplitude.
                    final int ampHeight = carriageHeight / 4;

                    // Draw left channel data
                    int offsetCounter = 1;

                    int xLineStart = startXPos;
                    int yLineStart = midYLeftPos;

                    for (Double amp : data.getDataL()) {
                        long interval = data.getTimeInterval() * offsetCounter;
                        int offset = computePixelXCoord(MILLISECONDS.convert(
                                    interval, data.getTimeUnit()));
                        offsetCounter++;

                        ampG.drawLine(xLineStart, yLineStart,
                            startXPos + offset,
                            midYLeftPos + (int) (-amp * ampHeight));

                        xLineStart = startXPos + offset;
                        yLineStart = midYLeftPos + (int) (-amp * ampHeight);
                    }

                    // Draw right channel data
                    offsetCounter = 1;

                    xLineStart = startXPos;
                    yLineStart = midYRightPos;

                    for (Double amp : data.getDataR()) {
                        long interval = data.getTimeInterval() * offsetCounter;
                        int offset = computePixelXCoord(MILLISECONDS.convert(
                                    interval, data.getTimeUnit()));
                        offsetCounter++;

                        ampG.drawLine(xLineStart, yLineStart,
                            startXPos + offset,
                            midYRightPos + (int) (-amp * ampHeight));

                        xLineStart = startXPos + offset;
                        yLineStart = midYRightPos + (int) (-amp * ampHeight);
                    }
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }

    }

    /**
     * Set the amplitude data to visualize.
     *
     * @param data
     *            amplitude data to visualize.
     */
    public void setData(final StereoAmplitudeData data) {

        if (!registered) {
            viewableModel.addPropertyChangeListener(this);
            registered = true;
        }

        this.data = data;
    }

    @Override protected void paintCustom(final Graphics g) {

        if (ampImage == null) {


            ampImage = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);

            Graphics ampG = ampImage.getGraphics();

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

            ampG.setColor(DATA_COLOR);

            // Draw the baseline zero amplitude.
            ampG.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
            ampG.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

            if (data == null) {
                return;
            }

            // Height for 100% amplitude.
            final int ampHeight = carriageHeight / 4;

            // Draw left channel data
            int offsetCounter = 1;

            int xLineStart = startXPos;
            int yLineStart = midYLeftPos;

            for (Double amp : data.getDataL()) {
                long interval = data.getTimeInterval() * offsetCounter;
                int offset = computePixelXCoord(MILLISECONDS.convert(interval,
                            data.getTimeUnit()));
                offsetCounter++;

                ampG.drawLine(xLineStart, yLineStart, startXPos + offset,
                    midYLeftPos + (int) (-amp * ampHeight));

                xLineStart = startXPos + offset;
                yLineStart = midYLeftPos + (int) (-amp * ampHeight);
            }

            // Draw right channel data
            offsetCounter = 1;

            xLineStart = startXPos;
            yLineStart = midYRightPos;

            for (Double amp : data.getDataR()) {
                long interval = data.getTimeInterval() * offsetCounter;
                int offset = computePixelXCoord(MILLISECONDS.convert(interval,
                            data.getTimeUnit()));
                offsetCounter++;

                ampG.drawLine(xLineStart, yLineStart, startXPos + offset,
                    midYRightPos + (int) (-amp * ampHeight));

                xLineStart = startXPos + offset;
                yLineStart = midYRightPos + (int) (-amp * ampHeight);
            }
        }

        g.drawImage(ampImage, 0, 0, null);
    }

}
