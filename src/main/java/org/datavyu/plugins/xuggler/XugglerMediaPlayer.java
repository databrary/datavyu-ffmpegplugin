package org.datavyu.plugins.xuggler;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.demos.VideoImage;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by jmlin_000 on 7/5/2014.
 */
public class XugglerMediaPlayer {

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
    private File data;
    private boolean playing = false;

    private long lastFrameTime = 0;

    private SourceDataLine mLine;

    public XugglerMediaPlayer() {
        // Let's make sure that we can actually convert video pixel formats.
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
            throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");

        // Create a Xuggler container object
        container = IContainer.make();
    }

    private IVideoPicture readVideoFrame() {
        IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                videoCoder.getWidth(), videoCoder.getHeight());

        int bytesDecoded = videoCoder.decodeVideo(picture, packet, 0);
        if (bytesDecoded < 0)
            throw new RuntimeException("got error decoding audio ");

    /*
     * Some decoders will consume data in a packet, but will not be able to construct
     * a full video picture yet.  Therefore you should always check if you
     * got a complete picture from the decoder
     */
        if (picture.isComplete()) {
            IVideoPicture newPic = picture;
//                    final IVideoPicture newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
      /*
       * If the resampler is not null, that means we didn't get the video in BGR24 format and
       * need to convert it into BGR24 format.
       */
            if (resampler != null) {
                // we must resample
//                picture = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
//                if (resampler.resample(newPic, picture) < 0)
//                    throw new RuntimeException("could not resample video ");
            }
//            if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
//                    throw new RuntimeException("could not decode video as BGR 24 bit data ");

            return newPic;
        } else {
            return picture;
        }
    }

    private void displayVideoFrame(IVideoPicture frame) {
        final BufferedImage bi = Utils.videoPictureToImage(frame);

        long delay = millisecondsUntilTimeToDisplay(frame);
        // if there is no audio stream; go ahead and hold up the main thread.  We'll end
        // up caching fewer video pictures in memory that way.
        try {
            if (delay > 0)
                Thread.sleep(delay);
        } catch (InterruptedException e) {
            return;
        }

        launchEdtTaskLater(new Runnable() {
            public void run() {
                mScreen.setImage(bi);
            }
        });
    }

    private IAudioSamples readAudioFrame() {
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
//                playJavaSound(samples);
            }
        }

        return samples;
    }

    private long readFrame() {
        return readFrame(true);
    }

    private long readFrame(boolean display) {
        if (container.readNextPacket(packet) < 0) {
            System.err.println("Error reading packet");
        }

        if (packet.getStreamIndex() == videoStreamId) {
            IVideoPicture vp = readVideoFrame();
            System.out.println("COMPLETE?: " + vp.isComplete());
            lastFrameTime = vp.getTimeStamp() / 1000;
            if (vp.isComplete() && display) {
                displayVideoFrame(vp);

            }
            return vp.getTimeStamp() / 1000;
        } else if (packet.getStreamIndex() == audioStreamId) {
            IAudioSamples ap = readAudioFrame();
            lastFrameTime = ap.getTimeStamp() / 1000;

            if (ap.isComplete() && display) {
                // note: this call will block if Java's sound buffers fill up, and we're
                // okay with that.  That's why we have the video "sleeping" occur
                // on another thread.
                playJavaSound(ap);
            }
            return ap.getTimeStamp() / 1000;
        } else {
            // Frame isn't part of video, it is something else
            return lastFrameTime;
        }
    }

    private void playLoop() {
        while (true) {
            if (playing) {
                try {
                    long timeStamp = readFrame();
                    if (timeStamp != -1) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void playLoop(boolean old) {
        while (true) {
            System.out.println(playing);

            if (playing) {
                while (playing && container.readNextPacket(packet) >= 0) {
                    System.out.println(playing);
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
                            IVideoPicture newPic = picture;
//                    final IVideoPicture newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
          /*
           * If the resampler is not null, that means we didn't get the video in BGR24 format and
           * need to convert it into BGR24 format.
           */
                            if (resampler != null) {
                                // we must resample
                                newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                                if (resampler.resample(newPic, picture) < 0)
                                    throw new RuntimeException("could not resample video ");
                            }
                            if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
                                throw new RuntimeException("could not decode video as BGR 24 bit data ");

                            final long delay = millisecondsUntilTimeToDisplay(newPic);
                            // if there is no audio stream; go ahead and hold up the main thread.  We'll end
                            // up caching fewer video pictures in memory that way.
                            try {
                                if (delay > 0)
                                    Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                return;
                            }

                            // And finally, convert the picture to an image and display it


                            final BufferedImage bi = Utils.videoPictureToImage(newPic);
//                            System.out.println(lastFrameTime);

//                            launchEdtTaskLater(new Runnable() {
//                                public void run() {
                            mScreen.setImage(bi);
//                                }
//                            });


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
                        do {
                        } while (false);
                    }


                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void play() {
        // Reset display synchronization
        mFirstVideoTimestampInStream = Global.NO_PTS;

        // Set the playLoop to continue doing its thing
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void seekTo(long position) {
        boolean wasPlaying = playing;
        stop();

        System.out.println(videoCoder.getTimeBase());

        final double timebase = videoCoder.getTimeBase().getDouble();
        position = (long) (position / timebase);
        final long min = Math.max(0, position - 100);
        final long max = position;

        final double frameTime = 1000.0 / getFps();

        container.seekKeyFrame(videoStreamId, min, position, max, IContainer.SEEK_FLAG_ANY);

        while (lastFrameTime * 1000 <= position - frameTime) {
            System.out.println(position);
            System.out.println(lastFrameTime);
            readFrame(false);
        }

        if (wasPlaying) {
            play();
        }
    }

    public void setVisible(boolean visible) {
        mScreen.setVisible(visible);
    }

    public long getCurrentTime() throws Exception {
        return lastFrameTime;
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
        launchEdtTaskLater(new Runnable() {
            @Override
            public void run() {
                mScreen = new VideoImage();
            }
        });
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
        videoStreamId = -1;
        videoCoder = null;
        audioStreamId = -1;
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


        float fps = (float) videoCoder.getFrameRate().getValue();
        long length = container.getDuration();

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
//            fps = FALLBACK_FRAME_RATE;
            /*
            JOptionPane.showMessageDialog(vlcDialog,
                    "Warning: Unable to detect framerate in video.\n"
                            + "This video may not behave properly. "
                            + "Please try converting to H.264.\n\n"
                            + "This can be done under Controller->Convert Videos.\n"
                            + "Setting framerate to " + FALLBACK_FRAME_RATE);
                    */
        }

        playing = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                playLoop();
            }
        }).start();
    }

    public float getFps() {
        return (float) videoCoder.getFrameRate().getValue();
    }

    public long getLength() {
        System.out.println(container.getDuration());
        return container.getDuration() / 1000;
    }

    public boolean isPlaying() {
        return playing;
    }
}
