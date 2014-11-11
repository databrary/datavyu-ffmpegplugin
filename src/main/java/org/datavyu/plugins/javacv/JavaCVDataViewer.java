package org.datavyu.plugins.javacv;


import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.GLCanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.datavyu.event.PlaybackEvent;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class JavaCVDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    private final LinkedBlockingQueue<PlaybackEvent> eventQueue;
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
    private FrameGrabber player;
    private CanvasFrame canvasFrame;
    private boolean assumedFPS = false;

    public JavaCVDataViewer(final Frame parent, final boolean modal) {

        playing = false;

        eventQueue = new LinkedBlockingQueue<PlaybackEvent>(1);

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

        // Test to see if we should prompt user to convert the video to
        // the ideal format
        playing = false;

        canvasFrame = new GLCanvasFrame("Extracted Frame", 1.0);
//        {
//            protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
//                try {
//                    super.initCanvas(fullScreen, displayMode, gamma);
//                }
//                catch (RuntimeException ex) {
//                    // will fail at the end of the function
//                }
//                if (!fullScreen) {
//                    canvas.setSize(10, 10);
//                    canvas.createBufferStrategy(2);
//                    // canvas.setIgnoreRepaint(true); // you may be able to do this on the mac because of how quartz works
//                }
//            }
//        };

        player = new OpenCVFrameGrabber(dataFeed);
//        player.setVideoBitrate(500);


        try {
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(player.getImageWidth());
        canvasFrame.setCanvasSize(player.getImageWidth(), player.getImageHeight());

        // Read frame by frame, stop early if the display window is closed

//        for (int i = 0; i < player.getLengthInFrames(); i++) {
//            try {
////                player.setFrameNumber(i);
//                canvasFrame.showImage(player.grab());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        length = player.getLengthInTime() / 1000;
        fps = (float) player.getFrameRate();
        System.out.println("VIDEO LENGTH IN MS: " + length / 1000);
        new Thread(new JavaCVPlayer(eventQueue)).start();


    }

    @Override
    public long getDuration() {
        return length;
    }

    @Override
    public long getCurrentTime() throws Exception {
        return player.getTimestamp() / 1000;
    }

    @Override
    public void seekTo(final long position) {
        try {
            System.out.println("BEFORE TIMESTAMP: " + player.getTimestamp() / 1000);
            if (player.getTimestamp() / 1000 != position) {
                PlaybackEvent e = new PlaybackEvent(this, PlaybackEvent.PlaybackType.SEEK,
                        position * 1000, player.getTimestamp() / 1000, position, 0);

//                eventQueue.put(e);
                player.setTimestamp(position * 1000);
                canvasFrame.showImage(player.grab());
            }
            System.out.println("AFTER TIMESTAMP: " + player.getTimestamp() / 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
        playing = false;
    }

    @Override
    public void setPlaybackSpeed(final float rate) {

    }

    @Override
    public void play() {
        playing = true;
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

    class JavaCVPlayer implements Runnable {
        private final BlockingQueue<PlaybackEvent> queue;
        private long lastSeekStamp = -1;

        public JavaCVPlayer(BlockingQueue<PlaybackEvent> q) {
            queue = q;
        }

        @Override
        public void run() {
            // Read frame by frame, stop early if the display window is closed
            while (true) {
                while (playing && player.getFrameNumber() < player.getLengthInFrames() && queue.size() == 0) {
                    try {
//                    player.setFrameNumber(i);
//                        player.setTimestamp(Datavyu.getDataController().getCurrentTime() * 1000);
                        canvasFrame.showImage(player.grab());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (queue.size() > 0) {
                    try {

                        PlaybackEvent e = queue.take();
                        if (e.getType() == PlaybackEvent.PlaybackType.SEEK && e.getGoTime() != lastSeekStamp) {
                            player.setTimestamp(e.getGoTime());
                            canvasFrame.showImage(player.grab());
                            lastSeekStamp = e.getGoTime();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (!playing) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

//    public static void main(String[] args) {
//        String data;
//        data = "/Users/jesse/Desktop/IULong_L10_01.mpg";
//        FrameGrabber player;
//        CanvasFrame canvasFrame;
//
//
//        canvasFrame = new CanvasFrame("Extracted Frame") {
//            protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
//                try {
//                    super.initCanvas(fullScreen, displayMode, gamma);
//                }
//                catch (RuntimeException ex) {
//                    // will fail at the end of the function
//                }
//                if (!fullScreen) {
//                    canvas.setSize(10, 10);
//                    canvas.createBufferStrategy(2);
//                    // canvas.setIgnoreRepaint(true); // you may be able to do this on the mac because of how quartz works
//                }
//            }
//        };
//
//        class FFmpegFrameGrabberFix extends FFmpegFrameGrabber {
//
//            private String          filename;
//            private avformat.AVFormatContext oc;
//            private avformat.AVStream video_st, audio_st;
//            private avcodec.AVCodecContext video_c, audio_c;
//            private avutil.AVFrame picture, picture_rgb;
//            private BytePointer buffer_rgb;
//            private avutil.AVFrame samples_frame;
//            private BytePointer[]   samples_ptr;
//            private Buffer[]        samples_buf;
//            private avcodec.AVPacket pkt, pkt2;
//            private int             sizeof_pkt;
//            private int[]           got_frame;
//            private swscale.SwsContext img_convert_ctx;
//            private opencv_core.IplImage return_image;
//            private boolean         frameGrabbed;
//            private org.bytedeco.javacv.Frame frame;
//
//            public FFmpegFrameGrabberFix(String data) {
//                super(data);
//            }
//
//
//
//            @Override public void setTimestamp(long timestamp) throws Exception {
//                int ret;
//                if (oc == null) {
//                    super.setTimestamp(timestamp);
//                } else {
//                    timestamp = timestamp * AV_TIME_BASE / 1000000L;
//            /* add the stream start time */
//                    if (oc.start_time() != AV_NOPTS_VALUE) {
//                        timestamp += oc.start_time();
//                    }
//                    if ((ret = avformat_seek_file(oc, -1, Long.MIN_VALUE, timestamp, Long.MAX_VALUE, AVSEEK_FLAG_BACKWARD)) < 0) {
//                        throw new Exception("avformat_seek_file() error " + ret + ": Could not seek file to timestamp " + timestamp + ".");
//                    }
//                    if (video_c != null) {
//                        avcodec_flush_buffers(video_c);
//                    }
//                    if (audio_c != null) {
//                        avcodec_flush_buffers(audio_c);
//                    }
//                    if (pkt2.size() > 0) {
//                        pkt2.size(0);
//                        av_free_packet(pkt);
//                    }
//            /* comparing to timestamp +/- 1 avoids rouding issues for framerates
//               which are no proper divisors of 1000000, e.g. where
//               av_frame_get_best_effort_timestamp in grabFrame sets this.timestamp
//               to ...666 and the given timestamp has been rounded to ...667
//               (or vice versa)
//            */
//                    while (this.timestamp > timestamp + 1 && grabFrame(false) != null) {
//                        // flush frames if seeking backwards
//                        System.out.println(this.timestamp);
//                    }
//                    while (this.timestamp < timestamp - 1 && grabFrame(false) != null) {
//                        // decode up to the desired frame
//                        System.out.println(this.timestamp);
//                    }
//                    if (video_c != null) {
//                        frameGrabbed = true;
//                    }
//                }
//            }
//        }
//
//        player = new FFmpegFrameGrabberFix(data);
//
//        try {
//            player.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(player.getImageWidth());
//        canvasFrame.setCanvasSize(player.getImageWidth(), player.getImageHeight());
//
//        // Read frame by frame, stop early if the display window is closed
//
////        for (int i = 0; i < player.getLengthInFrames(); i++) {
////            try {
//////                player.setFrameNumber(i);
////                canvasFrame.showImage(player.grab());
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//        int i = 0;
//        while(player.getFrameNumber() < player.getLengthInFrames()) {
//            try {
//                    player.setFrameNumber(i);
////                        player.setTimestamp(Datavyu.getDataController().getCurrentTime() * 1000);
//                canvasFrame.showImage(player.grab());
//                i++;
//                System.out.println(i);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}
