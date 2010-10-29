package org.openshapa.plugins.spectrum.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingWorker;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.Viewport;

import org.openshapa.plugins.spectrum.engine.AmplitudeProcessor;
import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.ProcessorConstants;
import org.openshapa.plugins.spectrum.models.StereoData;

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

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    /** Excluded properties. */
    private static final List<String> EXCLUDED_PROPS = ImmutableList.of(
            "locked", "trackId", "erroneous", "bookmark", "trackName",
            "selected", "state");

    private static final int PROG_OUTER_PAD = 7;

    private static final int PROG_INNER_PAD = 2;

    private static final float PROG_OUTLINE_WIDTH = 1.5F;

    private static final int PROG_WIDTH = 150;

    private static final int PROG_HEIGHT = 20;

    private static final Color PROG_COLOR = Color.WHITE;

    /** Have we already registered for property changes. */
    private boolean registered;

    /** Cached amplitude image for entire file. */
    private BufferedImage cachedAmps;

    /** Cached image of the last zoomed segment. */
    private BufferedImage localAmps;

    /** Viewable model associated with the last zoomed segment. */
    private Viewport localVM;

    /** Track model associated with the last zoomed segment. */
    private TrackModel localTM;

    /** Media file used to compute amplitude data. */
    private File mediaFile;

    /** Number of audio channels in the amplitude data. */
    private int channels;

    /** Handles amplitude processing. */
    private AmplitudeProcessor processor;

    /** Current progress level [0,1]. */
    private double progress;

    /** Handles task execution. */
    private final Executor executor;

    /** This is the canvas that we paint on. */
    private BufferedImage localBlocks;

    /** Progress handler. */
    private Ongoing progHandler;

    /** Worker threads for individual blocks. */
    private Queue<BlockWorker> blockWorkers;

    /** Manages background cache processing. */
    private Cache cacheHandler;

    /** Do we have data that can be displayed. */
    private volatile boolean dataAvailable;

    /** Left channel value to normalize against. */
    private volatile double lValNorm;

    /** Right channel value to normalize against. */
    private volatile double rValNorm;

    public AmplitudeTrack() {
        super();
        registered = false;
        executor = Executors.newFixedThreadPool(2);
        progHandler = new Ongoing();
        blockWorkers = new ConcurrentLinkedQueue<BlockWorker>();
        dataAvailable = false;

        lValNorm = ProcessorConstants.LEVELS;
        rValNorm = ProcessorConstants.LEVELS;
    }

    @Override public void deregister() {
        trackModel.removePropertyChangeListener(this);

        // Stop amp processor.
        if (processor != null) {
            processor.cancel(true);
            processor = null;
        }

        // Stop all block worker threads.
        for (BlockWorker worker : blockWorkers) {
            worker.cancel(true);
        }

        blockWorkers.clear();

        // Stop the background cache processor.
        if (cacheHandler != null) {
            cacheHandler.stopCaching();
            cacheHandler = null;
        }

        // Kill off our bg threads.
        ((ExecutorService) executor).shutdown();

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

        super.propertyChange(evt);
    }

    private double computeStartXPos(final Viewport viewport) {
        return viewport.computePixelXOffset(trackModel.getOffset());
    }

    private double computeEndXPos(final Viewport viewport) {
        return viewport.computePixelXOffset(trackModel.getDuration()
                + trackModel.getOffset());
    }

    private int computeCarriageHeight() {
        return (int) (getHeight() * 7D / 10D);
    }

    private int computeCarriageYOffset() {
        return (int) (getHeight() * 2D / 10D);
    }

    private int computeMidYLeftPos() {
        return computeCarriageYOffset() + (computeCarriageHeight() / 4);
    }

    private int computeMidYRightPos() {
        return computeCarriageYOffset() + (3 * computeCarriageHeight() / 4);
    }

    private int computeAmpHeight() {
        return computeCarriageHeight() / 4;
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
        final int startXPos = (int) Math.rint(computeStartXPos(viewport));
        final int endXPos = (int) Math.rint(computeEndXPos(viewport));

        // Carriage height.
        final int carriageHeight = computeCarriageHeight();

        // Carriage offset from top of panel.
        final int carriageYOffset = computeCarriageYOffset();

        // Y-coordinate for left channel.
        final int midYLeftPos = computeMidYLeftPos();

        // Y-coordinate for right channel.
        final int midYRightPos = computeMidYRightPos();

        if (!dataAvailable) {
            execProcessor();
        }

        if (localAmps == null) {
            GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            localAmps = gc.createCompatibleImage(getWidth(), getHeight(),
                    Transparency.TRANSLUCENT);
        }

        // Paint amplitude data.
        if (localBlocks != null) {
            g2d.drawImage(localBlocks, 0, 0, null);
        } else {

            // Draw baseline amplitudes.
            g2d.setColor(DATA_COLOR);
            g2d.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
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

        // Generate cache if we haven't.
        if ((cachedAmps == null) && (cacheHandler == null)) {
            cacheHandler = new Cache();
            cacheHandler.start = 0;
            cacheHandler.end = trackModel.getDuration();
            cacheHandler.generateCache();
        }

        // If the first processing run is already underway, do not cancel it.
        if ((processor != null) && !dataAvailable) {
            return;
        }

        // If some processing run is underway, cancel it because something
        // changed.
        if (processor != null) {
            processor.cancel(true);

            // Stop all worker threads.
            for (BlockWorker worker : blockWorkers) {
                worker.cancel(true);
            }

            blockWorkers.clear();
        }

        // Set up image for painting blocks.
        if (localBlocks == null) {
            GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            localBlocks = gc.createCompatibleImage(getWidth(), getHeight(),
                    Transparency.TRANSLUCENT);

            Graphics2D g = localBlocks.createGraphics();
            g.setBackground(TRANSPARENT);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Draw baseline amplitudes.
            Viewport viewport = mixer.getViewport();
            int startXPos = (int) Math.rint(computeStartXPos(viewport));
            int endXPos = (int) Math.rint(computeEndXPos(viewport));
            int midYLeftPos = computeMidYLeftPos();
            int midYRightPos = computeMidYRightPos();
            g.setColor(DATA_COLOR);
            g.drawLine(startXPos, midYLeftPos, endXPos, midYLeftPos);
            g.drawLine(startXPos, midYRightPos, endXPos, midYRightPos);

            g.dispose();
        } else {
            Graphics2D g = localBlocks.createGraphics();
            g.setBackground(TRANSPARENT);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            Viewport viewport = mixer.getViewport();

            // Use the global cache as a backdrop if available.
            if (cachedAmps != null) {
                int startXPos = (int) Math.rint(computeStartXPos(viewport));
                int endXPos = (int) Math.rint(computeEndXPos(viewport));
                g.drawImage(cachedAmps, startXPos, 0, endXPos, getHeight(), 0,
                    0, cachedAmps.getWidth(), cachedAmps.getHeight(), null);
            }

            // Use the local cache as a backdrop if possible.
            if ((localAmps != null)
                    && (viewport.getZoomLevel()
                        > ProcessorConstants.ZOOM_REPROCESS_THRESHOLD)) {
                final int x1 = (int) viewport.computePixelXOffset(
                        localVM.getViewStart() + trackModel.getOffset()
                        - localTM.getOffset());
                final int y1 = 0;
                final int x2 = (int) viewport.computePixelXOffset(
                        localVM.getViewEnd() + trackModel.getOffset()
                        - localTM.getOffset());
                final int y2 = getHeight();

                g.clearRect(x1, y1, x2 - x1, y2 - y1);
                g.drawImage(localAmps, x1, y1, x2, y2, 0, 0,
                    localAmps.getWidth(), localAmps.getHeight(), null);
            }
        }

        Viewport viewport = mixer.getViewport();

        if (viewport == null) {
            return;
        }

        // Track is past the end window, so it is not visible.
        if (viewport.getViewEnd() < trackModel.getOffset()) {
            progress = 1; // Erase stray progress bars.

            return;
        }

        // Track is before the start window, so it is not visible.
        if ((trackModel.getDuration() + trackModel.getOffset())
                < viewport.getViewStart()) {
            progress = 1;

            return;
        }

        // Track isn't zoomed in enough to need processing of new data.
        if ((viewport.getZoomLevel()
                    < ProcessorConstants.ZOOM_REPROCESS_THRESHOLD)
                && (cachedAmps != null)) {
            progress = 1;

            return;
        }

        // 1. Calculate the times to sample.
        long start = Math.max(viewport.getViewStart(), trackModel.getOffset())
            - trackModel.getOffset();
        long end = Math.min(trackModel.getOffset() + trackModel.getDuration(),
                viewport.getViewEnd()) - trackModel.getOffset();

        // 2. Make the worker thread.
        processor = AmplitudeProcessor.create(mediaFile, channels, progHandler);
        processor.autoNormalizeAgainst(lValNorm, rValNorm);
        processor.setDataTimeSegment(start, end, MILLISECONDS);
        executor.execute(processor);
    }

    /** Inner class for handling ongoing background processing progress. */
    private class Ongoing
        implements org.openshapa.plugins.spectrum.engine.Progress {

        @Override public void allDone(final StereoData allData) {
            AmplitudeBlock lastBlock = Iterables.getLast(
                    allData.getDataBlocks());
            blockDone(lastBlock);

            overallProgress(1);

            dataAvailable = true;

            executor.execute(new Runnable() {
                    @Override public void run() {

                        // Copy what's being drawn into our local cache.
                        localVM = mixer.getViewport();
                        localTM = trackModel.copy();

                        Graphics2D localG = localAmps.createGraphics();
                        localG.setBackground(TRANSPARENT);
                        localG.clearRect(0, 0, getWidth(), getHeight());
                        localG.drawImage(localBlocks, 0, 0, null);
                        localG.dispose();
                    }
                });
        }

        @Override public void blockDone(final AmplitudeBlock block) {
            BlockWorker worker = new BlockWorker();
            worker.block = block;
            worker.viewport = mixer.getViewport();

            blockWorkers.add(worker);

            executor.execute(worker);
        }

        @Override public void overallProgress(final double percentage) {
            progress = percentage;

            if (progress < 0) {
                progress = 0;
            }

            if (progress > 1) {
                progress = 1;
            }

            repaint();
        }
    }

    /** Inner class for processing an {@link AmplitudeBlock}. */
    private class BlockWorker extends SwingWorker<Path2D[], Void> {
        AmplitudeBlock block; // Block to process.
        Viewport viewport; // Viewport to use.

        /** Generate paths for the block being processed. */
        @Override protected Path2D[] doInBackground() throws Exception {

            if ((block == null) || (viewport == null)) {
                return null;
            }

            Path2D[] amps = new Path2D[] {
                    new Path2D.Double(), new Path2D.Double()
                };

            // Calculate path start pixel position.
            final double startXPosL = viewport.computePixelXOffset(MILLISECONDS
                    .convert(block.getLinkLTime(), block.getTimeUnit())
                    + trackModel.getOffset());

            final double startXPosR = viewport.computePixelXOffset(MILLISECONDS
                    .convert(block.getLinkRTime(), block.getTimeUnit())
                    + trackModel.getOffset());

            // Height for 100% amplitude.
            final double ampHeight = computeAmpHeight();

            // Y-coordinate for left channel.
            final double midYLeftPos = computeMidYLeftPos();

            // Y-coordinate for right channel.
            final double midYRightPos = computeMidYRightPos();

            // Start at the last position.
            amps[0].moveTo(startXPosL,
                midYLeftPos + (-block.getLastL() * ampHeight));
            amps[1].moveTo(startXPosR,
                midYRightPos + (-block.getLastR() * ampHeight));

            double[] leftVals = block.getLArray();
            double[] rightVals = block.getRArray();

            double timeInterval = block.computeInterval();

            for (int i = 0; i < leftVals.length; i++) {

                if (isCancelled()) {
                    return null;
                }

                double interval = timeInterval * i;

                long curTime = (long) (interval + block.getStartTime()
                        + trackModel.getOffset());
                double offset = viewport.computePixelXOffset(curTime);

                amps[0].lineTo(offset,
                    midYLeftPos + (-leftVals[i] * ampHeight));
                amps[1].lineTo(offset,
                    midYRightPos + (-rightVals[i] * ampHeight));
            }

            leftVals = null;
            rightVals = null;

            return amps;
        }

        /** Draw the paths onto the image buffer. */
        @Override protected void done() {

            try {
                Path2D[] amps = get();

                if (amps != null) {
                    Graphics2D g2d = localBlocks.createGraphics();
                    g2d.setBackground(TRANSPARENT);

                    Rectangle2D lRect = amps[0].getBounds2D();
                    Rectangle2D rRect = amps[1].getBounds2D();

                    // Delete part of the backdrop that we are overwriting.
                    int x = (int) (Math.rint(
                                Math.min(lRect.getX(), rRect.getX())));
                    int width = (int) Math.ceil(Math.max(lRect.getWidth(),
                                rRect.getWidth()));
                    g2d.clearRect(x, 0, width, localBlocks.getHeight());

                    g2d.setColor(DATA_COLOR);
                    g2d.draw(amps[0]);
                    g2d.draw(amps[1]);
                    g2d.dispose();

                    amps[0] = null;
                    amps[1] = null;

                    repaint();
                }
            } catch (Exception e) {
                // Do nothing.
            } finally {
                blockWorkers.remove(this);

                block = null;
                viewport = null;
            }
        }
    }

    /** Inner class for handling cache data progress. */
    private class Cache
        implements org.openshapa.plugins.spectrum.engine.Progress {
        long start; // Cache data start time in milliseconds.
        long end; // Cache data end time in milliseconds.
        AmplitudeProcessor ap; // Process the amplitude data.
        CacheWorker cw; // Generates global cache.

        void generateCache() {
            ap = AmplitudeProcessor.create(mediaFile, channels, this);
            ap.disableAutoNormalize();
            ap.setDataTimeSegment(start, end, MILLISECONDS);
            executor.execute(ap);
        }

        void stopCaching() {

            if (ap != null) {
                ap.cancel(true);
                ap = null;
            }

            if (cw != null) {
                cw.cancel(true);
                cw = null;
            }
        }

        @Override public void allDone(final StereoData allData) {
            cw = new CacheWorker();
            cw.dim = getSize();
            cw.data = allData;
            executor.execute(cw);
        }

        @Override public void blockDone(final AmplitudeBlock block) {
            // Do nothing.
        }

        @Override public void overallProgress(final double percentage) {
            // Do nothing.
        }
    }

    /** Inner class for generating a global cached image. */
    private class CacheWorker extends SwingWorker<BufferedImage, Void> {
        StereoData data; // Cache data.
        Dimension dim; // Size of track.

        /** Draw the cache data all at once onto an image buffer. */
        @Override protected BufferedImage doInBackground() throws Exception {
            GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            BufferedImage img = gc.createCompatibleImage((int) dim.getWidth(),
                    (int) dim.getHeight(), Transparency.TRANSLUCENT);

            // Calculate carriage start pixel position.
            final double startXPos = 0;

            // Y-coordinate for left channel.
            final int midYLeftPos = computeMidYLeftPos();

            // Y-coordinate for right channel.
            final int midYRightPos = computeMidYRightPos();

            // Height for 100% amplitude.
            final int ampHeight = computeAmpHeight();

            // Left channel path
            Path2D left = new Path2D.Double();
            left.moveTo(startXPos, midYLeftPos);

            // Right channel path
            Path2D right = new Path2D.Double();
            right.moveTo(startXPos, midYRightPos);

            lValNorm = data.getMaxL();
            rValNorm = data.getMaxR();

            for (AmplitudeBlock block : data.getDataBlocks()) {
                double[] leftVals = block.getLArray();
                double[] rightVals = block.getRArray();

                double timeInterval = block.computeInterval();

                for (int i = 0; i < leftVals.length; i++) {
                    double interval = timeInterval * i;
                    long curTime = (long) (interval + block.getStartTime());
                    double offset = computeXPos(curTime);

                    // Manually normalize our block values against the global
                    // max.
                    double lVal = leftVals[i] / lValNorm;
                    double rVal = rightVals[i] / rValNorm;

                    left.lineTo(offset, midYLeftPos + (-lVal * ampHeight));
                    right.lineTo(offset, midYRightPos + (-rVal * ampHeight));
                }
            }

            Graphics2D g2d = img.createGraphics();
            g2d.setColor(DATA_COLOR);
            g2d.draw(left);
            g2d.draw(right);
            g2d.dispose();

            return img;
        }

        /** Set the cached image. */
        @Override protected void done() {

            try {
                BufferedImage result = get();

                if (result != null) {
                    cachedAmps = result;
                }

                repaint();
            } catch (Exception e) {
                // Do nothing.
            } finally {
                data = null;
                dim = null;
                cacheHandler = null;
            }
        }

        double computeXPos(final long time) {
            return time / (data.getDataTimeEnd() / dim.getWidth());
        }
    }

}
