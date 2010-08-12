package org.openshapa.plugins.spectrum.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.Viewport;

import org.openshapa.plugins.spectrum.engine.AmplitudeProcessor;
import org.openshapa.plugins.spectrum.engine.AmplitudeProcessor.Strategy;
import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import org.openshapa.views.component.TrackPainter;

import com.google.common.collect.ImmutableList;


/**
 * Used to plot amplitude data over the time domain.
 */
public final class AmplitudeTrack extends TrackPainter implements Amplitude,
    Progress, PropertyChangeListener {

    /** Color used to paint the amplitude data. */
    private static final Color DATA_COLOR = new Color(0, 0, 0, 200);

    /** Excluded properties. */
    private static final List<String> EXCLUDED_PROPS = ImmutableList.of(
            "locked", "trackId", "erroneous", "bookmark", "trackName",
            "selected", "state");

    /**
     * Number of samples to pick when displaying amplitude data. 5000 was picked
     * because we want around 7 to 8 points per pixel. Number of pixels is the
     * pixel width of the track.
     */
    private static final int NUM_SAMPLES = 5000;

    private static final int PROG_OUTER_PAD = 7;

    private static final int PROG_INNER_PAD = 2;

    private static final float PROG_OUTLINE_WIDTH = 1.5F;

    private static final int PROG_WIDTH = 150;

    private static final int PROG_HEIGHT = 20;

    private static final Color PROG_COLOR = Color.WHITE;

    /** Contains the amplitude data to visualize. */
    private StereoAmplitudeData data;

    /** Have we already registered for property changes. */
    private boolean registered;

    /** Cached amplitude image for entire file. */
    private BufferedImage cachedAmps;

    /** Cached image of the last zoomed segment. */
    private BufferedImage localAmps;

    /** Viewable model associated with the last zoomed segment. */
    private Viewport localVM;

    private TrackModel localTM;

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

    private CacheHandler cacheHandler;

    private double progress;

    private final Executor executor;

    private volatile boolean dirty;

    public AmplitudeTrack() {
        super();
        registered = false;
        executor = Executors.newCachedThreadPool();
        dirty = true;
    }

    @Override public void deregister() {
        trackModel.removePropertyChangeListener(this);
        super.deregister();
    }

    public void setMedia(final File mediaFile, final int channels) {
        this.mediaFile = mediaFile;
        this.channels = channels;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (EXCLUDED_PROPS.contains(evt.getPropertyName())) {
            return;
        }

        execProcessor();

        dirty = true;
        leftAmp = null;
        rightAmp = null;

        super.propertyChange(evt);
    }

    /**
     * Set the amplitude data to visualize.
     *
     * @param data
     *            amplitude data to visualize.
     */
    @Override public void setData(final StereoAmplitudeData data) {
        this.data = data;

        if (worker != null) {

            // If a worker is already in progress, cancel it.
            worker.cancel(true);
        }

        // Make a worker thread to compute paths.
        worker = new PathWorker(new Dimension(getSize()), mixer.getViewport(),
                data);

        executor.execute(worker);

        if (cacheHandler == null) {
            cacheHandler = new CacheHandler();
        }
    }

    @Override protected void paintCustom(final Graphics g) {

        if (!registered) {
            trackModel.addPropertyChangeListener(this);
            registered = true;
        }

        Viewport viewport = mixer.getViewport();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate carriage start and end pixel positions.
        final int startXPos = (int) viewport.computePixelXOffset(
                trackModel.getOffset());
        final int endXPos = (int) viewport.computePixelXOffset(
                trackModel.getDuration() + trackModel.getOffset());

        // Carriage height.
        final int carriageHeight = (int) (getHeight() * 7D / 10D);

        // Carriage offset from top of panel.
        final int carriageYOffset = (int) (getHeight() * 2D / 10D);

        // Y-coordinate for left channel.
        final int midYLeftPos = carriageYOffset + (carriageHeight / 4);

        // Y-coordinate for right channel.
        final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

        if (data == null) {
            execProcessor();
        }

        // Draw left channel data.
        if ((localAmps != null) && !dirty) {
            g2d.drawImage(localAmps, 0, 0, null);
        } else if (leftAmp != null) {
            localTM = trackModel.copy();
            localVM = viewport;

            // Buffer the drawn data.
            localAmps = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D imgG = (Graphics2D) localAmps.getGraphics();
            imgG.setColor(DATA_COLOR);
            imgG.draw(leftAmp);
            imgG.dispose();
        } else if (cachedAmps != null) {
            BufferedImage image2 = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g3 = (Graphics2D) image2.getGraphics();

            g3.setBackground(new Color(0, 0, 0, 0));

            g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g3.drawImage(cachedAmps, startXPos, 0, endXPos, getHeight(), 0, 0,
                cachedAmps.getWidth(), cachedAmps.getHeight(), null);

            final int x1 = (int) viewport.computePixelXOffset(
                    localVM.getViewStart() + trackModel.getOffset()
                    - localTM.getOffset());
            final int y1 = 0;
            final int x2 = (int) viewport.computePixelXOffset(
                    localVM.getViewEnd() + trackModel.getOffset()
                    - localTM.getOffset());
            final int y2 = getHeight();

            g3.clearRect(x1, y1, x2 - x1, y2 - y1);

            g3.drawImage(localAmps, x1, y1, x2, y2, 0, 0, localAmps.getWidth(),
                localAmps.getHeight(), null);

            g3.dispose();

            g2d.drawImage(image2, 0, 0, getWidth(), getHeight(), 0, 0,
                image2.getWidth(), image2.getHeight(), null);
        } else if (localAmps != null) {
            // If we don't yet have a global cached image, make sure the local
            // cache is painted.

            final int x1 = (int) viewport.computePixelXOffset(
                    localVM.getViewStart() + trackModel.getOffset()
                    - localTM.getOffset());
            final int y1 = 0;
            final int x2 = (int) viewport.computePixelXOffset(
                    localVM.getViewEnd() + trackModel.getOffset()
                    - localTM.getOffset());
            final int y2 = getHeight();

            g2d.drawImage(localAmps, x1, y1, x2, y2, 0, 0, localAmps.getWidth(),
                localAmps.getHeight(), null);
        } else {

            // Baseline zero amplitude.
            g2d.setColor(DATA_COLOR);
            g2d.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
        }

        // Draw right channel data.
        if ((localAmps != null) && !dirty) {
            // Do nothing, already drawn. Do not remove this conditional.
        } else if (rightAmp != null) {
            Graphics2D imgG = (Graphics2D) localAmps.getGraphics();
            imgG.setColor(DATA_COLOR);
            imgG.draw(rightAmp);
            imgG.dispose();

            g2d.drawImage(localAmps, 0, 0, null);

            dirty = false;
        } else if (cachedAmps != null) {
            // Do nothing, already drawn. Do not remove this conditional or
            // the baseline will be drawn.
        } else if (localAmps != null) {
            // Do nothing, already drawn. Do not remove this conditional or
            // the baseline will be drawn.
        } else {

            // Baseline zero amplitude.
            g2d.setColor(DATA_COLOR);
            g2d.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);
        }

        // Draw progress bar.
        if (progress < 1F) {
            g2d.setColor(PROG_COLOR);

            // set line width to 6, use bevel for line joins
            BasicStroke bs = new BasicStroke(PROG_OUTLINE_WIDTH,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

            // Bottom right hand corner coords
            int x2 = getWidth() - PROG_OUTER_PAD;
            int y2 = carriageYOffset + carriageHeight - PROG_OUTER_PAD;

            // Top left corner.
            int x1 = x2 - PROG_WIDTH;
            int y1 = y2 - PROG_HEIGHT;

            // The outline
            Path2D progOutline = new Path2D.Double();
            progOutline.moveTo(x2, y2);
            progOutline.lineTo(x2, y1);
            progOutline.lineTo(x1, y1);
            progOutline.lineTo(x1, y2);
            progOutline.closePath();

            g2d.fill(bs.createStrokedShape(progOutline));

            // The progress bar.
            double progWidth = (PROG_WIDTH - (2 * PROG_INNER_PAD)) * progress;

            Path2D progBar = new Path2D.Double();
            progBar.moveTo(x1 + PROG_INNER_PAD, y1 + PROG_INNER_PAD);
            progBar.lineTo(x1 + progWidth, y1 + PROG_INNER_PAD);
            progBar.lineTo(x1 + progWidth, y2 - PROG_INNER_PAD);
            progBar.lineTo(x1 + PROG_INNER_PAD, y2 - PROG_INNER_PAD);
            progBar.closePath();

            g2d.fill(progBar);
        }
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

        Viewport viewport = mixer.getViewport();

        if (viewport == null) {
            return;
        }

        // Track is past the end window, so it is not visible.
        if (viewport.getViewEnd() < trackModel.getOffset()) {
            return;
        }

        // Track is before the start window, so it is not visible.
        if ((trackModel.getDuration() + trackModel.getOffset())
                < viewport.getViewStart()) {
            return;
        }

        // 1. Calculate the times to sample.
        long start = Math.max(viewport.getViewStart(), trackModel.getOffset())
            - trackModel.getOffset();
        long end = Math.min(trackModel.getOffset() + trackModel.getDuration(),
                viewport.getViewEnd()) - trackModel.getOffset();

        // 2. Make the worker thread.
        processor = new AmplitudeProcessor(mediaFile, this, channels);
        processor.setDataTimeSegment(start, end, MILLISECONDS);
        processor.setStrategy(Strategy.FIXED_HIGH_LOW, NUM_SAMPLES);
        processor.setProgressHandler(this);

        executor.execute(processor);
    }

    @Override public void setProgress(final double p) {
        progress = p;

        if (progress < 0) {
            progress = 0;
        }

        if (progress > 1) {
            progress = 1;
        }

        repaint();
    }

    /**
     * Inner worker for calculating paths.
     */
    private final class PathWorker extends SwingWorker<Path2D[], Double> {
        private Dimension dim;
        private StereoAmplitudeData data;
        private Viewport viewport;

        public PathWorker(final Dimension d, final Viewport viewport,
            final StereoAmplitudeData data) {
            dim = d;
            this.viewport = viewport;
            this.data = data;
        }

        @Override protected Path2D[] doInBackground() throws Exception {

            if (data == null) {
                return null;
            }

            if (viewport == null) {
                return null;
            }

            Path2D[] amps = new Path2D[] {
                    new Path2D.Double(), new Path2D.Double()
                };

            // Carriage height.
            final int carriageHeight = (int) (dim.getHeight() * 7D / 10D);

            // Calculate carriage start pixel position.
            final double startXPos = viewport.computePixelXOffset(MILLISECONDS
                    .convert(data.getDataTimeStart(), data.getDataTimeUnit())
                    + trackModel.getOffset());

            // Carriage offset from top of panel.
            final int carriageYOffset = (int) (dim.getHeight() * 2D / 10D);

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
                double offset = viewport.computePixelXOffset((long) (interval
                            + data.getDataTimeStart()
                            + trackModel.getOffset()));

                offsetCounter++;

                amps[0].lineTo(offset, midYLeftPos + (-amp * ampHeight));

                publish(offsetCounter / (double) (data.sizeL() + data.sizeR()));
            }

            // Calculate right amplitude data.
            amps[1].moveTo(startXPos, midYRightPos);
            offsetCounter = 1;

            for (Double amp : data.getDataR()) {

                if (isCancelled()) {
                    return null;
                }

                double interval = data.getTimeInterval() * offsetCounter;
                double offset = viewport.computePixelXOffset((long) (interval
                            + data.getDataTimeStart()
                            + trackModel.getOffset()));
                offsetCounter++;

                amps[1].lineTo(offset, midYRightPos + (-amp * ampHeight));

                publish((offsetCounter + data.sizeL())
                    / (double) (data.sizeL() + data.sizeR()));
            }

            return amps;
        }

        @Override protected void process(final List<Double> dbls) {
            AmplitudeTrack.this.setProgress(dbls.get(dbls.size() - 1));
        }

        @Override protected void done() {
            AmplitudeTrack.this.setProgress(1F);

            try {
                Path2D[] result = get();

                if (result != null) {
                    leftAmp = result[0];
                    rightAmp = result[1];
                }

                worker = null;

                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inner class for handling cached data.
     */
    private final class CacheHandler implements Amplitude {

        private StereoAmplitudeData cache;

        public CacheHandler() {
            AmplitudeProcessor p = new AmplitudeProcessor(mediaFile, this,
                    channels);
            p.setDataTimeSegment(0, trackModel.getDuration(), MILLISECONDS);
            p.setStrategy(Strategy.FIXED_HIGH_LOW, NUM_SAMPLES);

            executor.execute(p);
        }

        @Override public void setData(final StereoAmplitudeData data) {
            cache = data;

            // Generate cached data.
            executor.execute(new CacheWorker(cache, new Dimension(getSize())));
        }
    }

    /**
     * Inner class for generating cached image.
     */
    private final class CacheWorker extends SwingWorker<BufferedImage, Void> {
        private StereoAmplitudeData cache;
        private Dimension dim;

        public CacheWorker(final StereoAmplitudeData data, final Dimension d) {
            cache = data;
            dim = d;
        }

        @Override protected BufferedImage doInBackground() throws Exception {

            // Pixel spacing between data points.
            final double spacing = 0.5;

            BufferedImage img = new BufferedImage((int) (cache.sizeL()
                        * spacing), (int) dim.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);

            if (data == null) {
                return null;
            }

            // Carriage height.
            final int carriageHeight = (int) (dim.getHeight() * 7D / 10D);

            // Calculate carriage start pixel position.
            final double startXPos = 0;

            // Carriage offset from top of panel.
            final int carriageYOffset = (int) (dim.getHeight() * 2D / 10D);

            // Y-coordinate for left channel.
            final int midYLeftPos = carriageYOffset + (carriageHeight / 4);

            // Y-coordinate for right channel.
            final int midYRightPos = carriageYOffset + (3 * carriageHeight / 4);

            // Height for 100% amplitude.
            final int ampHeight = carriageHeight / 4;

            Graphics2D g2d = (Graphics2D) img.getGraphics();

            g2d.setColor(DATA_COLOR);

            // Draw left channel data.
            {
                Path2D left = new Path2D.Double();

                left.moveTo(startXPos, midYLeftPos);

                int offsetCounter = 1;

                for (Double amp : cache.getDataL()) {
                    double offset = offsetCounter * spacing;
                    left.lineTo(offset, midYLeftPos + (-amp * ampHeight));
                    offsetCounter++;
                }

                g2d.draw(left);
            }

            // Draw right channel data.
            {
                Path2D right = new Path2D.Double();

                right.moveTo(startXPos, midYRightPos);

                int offsetCounter = 1;

                for (Double amp : cache.getDataR()) {
                    double offset = offsetCounter * spacing;
                    right.lineTo(offset, midYRightPos + (-amp * ampHeight));
                    offsetCounter++;
                }

                g2d.draw(right);
            }

            g2d.dispose();

            return img;
        }

        @Override protected void done() {

            try {
                BufferedImage result = get();

                if (result != null) {
                    cachedAmps = result;
                }

                repaint();
            } catch (Exception e) {
                // Do nothing.
            }
        }
    }

}
