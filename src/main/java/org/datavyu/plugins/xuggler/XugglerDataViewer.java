package org.datavyu.plugins.xuggler;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.demos.VideoImage;
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

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class XugglerDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    /**
     * Data viewer ID.
     */
    private Identifier id;
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
    /**
     * The audio line we'll output sound to; it'll be the default audio device on your system if available
     */
    private SourceDataLine mLine;
    /**
     * The window we'll draw the video on.
     */
    private VideoImage mScreen;
    private IContainer container;
    private IMediaReader reader;
    private IMediaViewer viewer;
    private IPacket packet;
    private IStreamCoder videoCoder = null;
    private IStreamCoder audioCoder = null;
    private IVideoResampler resampler = null;
    private int videoStreamId;
    private int audioStreamId;
    private long mSystemVideoClockStartTime;
    private long mFirstVideoTimestampInStream;

    public XugglerDataViewer(final Frame parent, final boolean modal) {


        // Let's make sure that we can actually convert video pixel formats.
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
            throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");

        // Create a Xuggler container object
        container = IContainer.make();

        stateListeners = new ArrayList<ViewerStateListener>();

    }

    private void playLoop() {
        while (container.readNextPacket(packet) >= 0 && playing) {
      /*
       * Now we have a packet, let's see if it belongs to our video stream
       */
            if (packet.getStreamIndex() == videoStreamId) {
        /*
         * We allocate a new picture to get the data out of Xuggler
         */
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                        videoCoder.getWidth(), videoCoder.getHeight());


        /*
         * Now, we decode the video, checking for any errors.
         *
         */
                int bytesDecoded = videoCoder.decodeVideo(picture, packet, 0);
                if (bytesDecoded < 0)
                    throw new RuntimeException("got error decoding audio ");

        /*
         * Some decoders will consume data in a packet, but will not be able to construct
         * a full video picture yet.  Therefore you should always check if you
         * got a complete picture from the decoder
         */
                if (picture.isComplete()) {
//                    IVideoPicture newPic = picture;
                    final IVideoPicture newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
          /*
           * If the resampler is not null, that means we didn't get the video in BGR24 format and
           * need to convert it into BGR24 format.
           */
                    if (resampler != null) {
                        // we must resample
//                        final IVideoPicture newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                        if (resampler.resample(newPic, picture) < 0)
                            throw new RuntimeException("could not resample video ");
                    }
                    if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
                        throw new RuntimeException("could not decode video as BGR 24 bit data ");

                    long delay = millisecondsUntilTimeToDisplay(newPic);
                    // if there is no audio stream; go ahead and hold up the main thread.  We'll end
                    // up caching fewer video pictures in memory that way.
                    try {
                        if (delay > 0)
                            Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        return;
                    }

                    // And finally, convert the picture to an image and display it


                    BufferedImage bi = Utils.videoPictureToImage(newPic);
//                    System.out.println(bi.getWidth());
                    mScreen.setImage(bi);

                }
            } else if (packet.getStreamIndex() == audioStreamId) {
        /*
         * We allocate a set of samples with the same number of channels as the
         * coder tells us is in this buffer.
         *
         * We also pass in a buffer size (1024 in our example), although Xuggler
         * will probably allocate more space than just the 1024 (it's not important why).
         */
                IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

        /*
         * A packet can actually contain multiple sets of samples (or frames of samples
         * in audio-decoding speak).  So, we may need to call decode audio multiple
         * times at different offsets in the packet's data.  We capture that here.
         */
                int offset = 0;

        /*
         * Keep going until we've processed all data
         */
                while (offset < packet.getSize()) {
                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                    if (bytesDecoded < 0)
                        throw new RuntimeException("got error decoding audio ");
                    offset += bytesDecoded;
          /*
           * Some decoder will consume data in a packet, but will not be able to construct
           * a full set of samples yet.  Therefore you should always check if you
           * got a complete set of samples from the decoder
           */
                    if (samples.isComplete()) {
                        // note: this call will block if Java's sound buffers fill up, and we're
                        // okay with that.  That's why we have the video "sleeping" occur
                        // on another thread.
                        playJavaSound(samples);
                    }
                }
            } else {
        /*
         * This packet isn't part of our video stream, so we just silently drop it.
         */
//                do {} while(false);
            }


        }
    }

    private long millisecondsUntilTimeToDisplay(IVideoPicture picture) {
        /**
         * We could just display the images as quickly as we decode them, but it turns
         * out we can decode a lot faster than you think.
         *
         * So instead, the following code does a poor-man's version of trying to
         * match up the frame-rate requested for each IVideoPicture with the system
         * clock time on your computer.
         *
         * Remember that all Xuggler IAudioSamples and IVideoPicture objects always
         * give timestamps in Microseconds, relative to the first decoded item.  If
         * instead you used the packet timestamps, they can be in different units depending
         * on your IContainer, and IStream and things can get hairy quickly.
         */
        long millisecondsToSleep = 0;
        if (mFirstVideoTimestampInStream == Global.NO_PTS) {
            // This is our first time through
            mFirstVideoTimestampInStream = picture.getTimeStamp();
            // get the starting clock time so we can hold up frames
            // until the right time.
            mSystemVideoClockStartTime = System.currentTimeMillis();
            millisecondsToSleep = 0;
        } else {
            long systemClockCurrentTime = System.currentTimeMillis();
            long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - mSystemVideoClockStartTime;
            // compute how long for this frame since the first frame in the stream.
            // remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
            // so we divide by 1000 to get milliseconds.
            long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - mFirstVideoTimestampInStream) / 1000;
            final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
            millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo -
                    (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
        }
        return millisecondsToSleep;
    }

    /**
     * Opens a Swing window on screen.
     */
    private void openJavaVideo() {
        mScreen = new VideoImage();
    }

    /**
     * Forces the swing thread to terminate; I'm sure there is a right
     * way to do this in swing, but this works too.
     */
    private void closeJavaVideo() {
//        System.exit(0);
    }

    private void openJavaSound(IStreamCoder aAudioCoder) throws LineUnavailableException {
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                aAudioCoder.getChannels(),
                true, /* xuggler defaults to signed 16 bit samples */
                false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        mLine = (SourceDataLine) AudioSystem.getLine(info);
        /**
         * if that succeeded, try opening the line.
         */
        mLine.open(audioFormat);
        /**
         * And if that succeed, start the line.
         */
        mLine.start();


    }

    private void playJavaSound(IAudioSamples aSamples) {
        /**
         * We're just going to dump all the samples into the line.
         */
        byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
        mLine.write(rawBytes, 0, aSamples.getSize());
    }

    private void closeJavaSound() {
        if (mLine != null) {
      /*
       * Wait for the line to finish playing
       */
            mLine.drain();
      /*
       * Close the line.
       */
            mLine.close();
            mLine = null;
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
        return fps;
    }

    @Override
    public float getDetectedFrameRate() {
        return fps;
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
        mScreen.setVisible(isVisible);
    }

    @Override
    public File getDataFeed() {
        return data;
    }

    @Override
    public void setDataFeed(final File dataFeed) {
//        String filename = "F:\\Movies\\Ascenseur pour l'échafaud (1958) Louis Malle\\Ascenseur pour lechafaud (1958) Louis Malle.avi";

        data = dataFeed;

        String filename = data.getAbsolutePath();

        reader = ToolFactory.makeReader(data.getAbsolutePath());

        viewer = com.xuggle.mediatool.ToolFactory.makeViewer();

        System.out.println(data.getAbsolutePath());
        // Open up the container
        System.out.println(container);
        if (container.open(data.getAbsolutePath(), IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("could not open file: " + data.getAbsolutePath());

        int numStreams = container.getNumStreams();

        // and iterate through the streams to find the first audio stream
        int videoStreamId = -1;
        videoCoder = null;
        int audioStreamId = -1;
        audioCoder = null;
        for (int i = 0; i < numStreams; i++) {
            // Find the stream object
            IStream stream = container.getStream(i);
            // Get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();

            if (videoStreamId == -1 && coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
            } else if (audioStreamId == -1 && coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                audioStreamId = i;
                audioCoder = coder;
            }
        }
        if (videoStreamId == -1 && audioStreamId == -1)
            throw new RuntimeException("could not find audio or video stream in container: " + filename);


        /*
     * Check if we have a video stream in this file.  If so let's open up our decoder so it can
     * do work.
     */
        if (videoCoder != null) {
            if (videoCoder.open() < 0)
                throw new RuntimeException("could not open audio decoder for container: " + filename);

            if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                // if this stream is not in BGR24, we're going to need to
                // convert it.  The VideoResampler does that for us.
                resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                        videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
                if (resampler == null)
                    throw new RuntimeException("could not create color space resampler for: " + filename);
            }
      /*
       * And once we have that, we draw a window on screen
       */
            openJavaVideo();
        }

        if (audioCoder != null) {
            if (audioCoder.open() < 0)
                throw new RuntimeException("could not open audio decoder for container: " + filename);

      /*
       * And once we have that, we ask the Java Sound System to get itself ready.
       */
            try {
                openJavaSound(audioCoder);
            } catch (LineUnavailableException ex) {
                throw new RuntimeException("unable to open sound device on your system when playing back container: " + filename);
            }
        }


        // Grab FPS and length


        fps = (float) videoCoder.getFrameRate().getValue();
        length = container.getDuration();

        System.out.println(String.format("FPS: %f", fps));
        System.out.println(String.format("Length: %d", length));

        packet = IPacket.make();
        mFirstVideoTimestampInStream = Global.NO_PTS;
        mSystemVideoClockStartTime = 0;


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
        return length;
    }

    @Override
    public long getCurrentTime() throws Exception {
        return packet.getTimeStamp();
    }

    @Override
    public void seekTo(final long position) {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                stop();
                container.seekKeyFrame(videoStreamId, position, IContainer.SEEK_FLAG_ANY);
            }
        };

        launchEdtTaskLater(edtTask);
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
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
//                if (rate < 0) {
//                    // VLC cannot play in reverse, so we're going to rely
//                    // on the clock to do fake jumping
//                    mediaPlayer.setRate(0);
//                    if (playing) {
//                        mediaPlayer.pause();
//                        playing = false;
//                    }
//                }
//                mediaPlayer.setRate(rate);
//                mediaPlayer.setTime(mediaPlayer.getTime());
            }
        };
        launchEdtTaskLater(edtTask);
    }

    @Override
    public void play() {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                if (!playing) {
                    playLoop();
                    playing = true;
                }
            }
        };

        launchEdtTaskLater(edtTask);
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
//        videoSurface.setVisible(false);
//        vlcDialog.setVisible(false);
//        mediaPlayerFactory.release();
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

}
