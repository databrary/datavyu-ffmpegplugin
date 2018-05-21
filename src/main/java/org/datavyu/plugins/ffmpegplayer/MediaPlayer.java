package org.datavyu.plugins.ffmpegplayer;

import com.sun.media.jfxmedia.MediaException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.color.ColorSpace;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sound.sampled.AudioFormat;

/**
 * The movie stream provider allows feeding an audio stream to multiple listeners and feeding an image stream to
 * multiple listeners.
 * 
 * @author Florian Raudies, Mountain View, CA.
 *
 */
public class MediaPlayer extends MediaPlayer0 {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(MediaPlayer.class);

	/** The list of audio listeners */
	private List<StreamListener> audioListeners;
	
	/** The list of video listeners */
	private List<ImageStreamListener> imageListeners;

    public enum Status {
        UNKNOWN,
        READY,
        PAUSED,
        PLAYING,
        STOPPED,
        STALLED,
        HALTED,
        DISPOSED
    }

    private Status status = Status.UNKNOWN;

    /** The stream id for this movie stream */
    private int streamId;

    /** The size of the audio buffer */
    private final static int AUDIO_BUFFER_SIZE = 64 * 1024; // 64 kB

    /** The duration of the video/audio. Initialized at opening */
    protected double duration = 0;

    /** The number of channels. Initialized at opening */
    private int nColorChannels = 0;

    /** The play time of the streams. Initialized at opening */
    private double startTime = 0;

    /** The end time of the streams. Initialized at opening */
    private double endTime = 0;

    /** The byte buffer for the audio. Initialized at opening */
    private ByteBuffer audioBuffer = null;

    /** The audio format of the audio stream. Initialized at opening */
    private AudioFormat audioFormat;

    /** The color space of the image stream. Initialized at opening */
    private ColorSpace colorSpace;

    private String fileName;

    private String version;

    private MediaException error;

    private final Object lockForStatus = new Object();

    /** Runnables for the audio and video playback */
    private List<RunnableWithStopHook> playback = new ArrayList<>();

    /** Thread pool for the audio and video playback */
    private ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public Status getStatus() {
        synchronized (lockForStatus) {
            return status;
        }
    }

    private synchronized void setStatus(Status status) {
        synchronized (lockForStatus) {
            this.status = status;
        }
    }

    @Override
    public ColorSpace getColorSpace() { return colorSpace; }

    @Override
    public double getStartTime() { return startTime; }

    @Override
    public double getEndTime() { return endTime; }

    @Override
    public double getDuration() { return duration; }

    @Override
    public double getCurrentTime() { return getCurrentTime0(streamId); }

    @Override
    public void seek(double time) {
        // TODO: Need to display the current frame
        seek0(streamId, time);
    }

    @Override
    public AudioFormat getAudioFormat() { return audioFormat; }

    @Override
    public int getAudioBufferSize() { return AUDIO_BUFFER_SIZE; }

    @Override
    public int getNumberOfColorChannels() { return nColorChannels; }

    @Override
    public int getHeight() { return getHeight0(streamId); }

    @Override
    public int getWidth() { return getWidth0(streamId); }

    /**
     * Set the amount of time to delay the audio. A positive value makes audio
     * render later, and a negative value makes audio render earlier.
     *
     * @param delay time in milliseconds
     */
    public void setAudioSyncDelay(long delay) { setAudioSyncDelay0(streamId, delay); }

    public MediaException getError() { return error; }

    public boolean hasError() { return error != null; }

    @Override
    public int readImageData(byte[] buffer) {
        int nFrame;
        // Check if we loaded at least one image frame
        if ((nFrame = loadNextImageFrame0(streamId)) > 0) {
            // Load the image frame into the buffer
            ByteBuffer imageBuffer = getImageBuffer0(streamId);
            logger.info("Stream %d: Read %d frames at %3.3f sec.", streamId , nFrame, getCurrentTime());
            imageBuffer.get(buffer, 0, imageBuffer.capacity());
        }
        // Return the number of loaded image frames
        return nFrame;
    }

    @Override
    public int readAudioData(byte[] buffer) {
        if (loadNextAudioData0(streamId)) {
            audioBuffer.get(buffer, 0, AUDIO_BUFFER_SIZE);
            audioBuffer.rewind();
            logger.info("Stream " + streamId + ": Read audio data.");
            return 1;
        }
        return 0;
    }

    private class InitMoviePlayer implements Runnable {

        @Override
        public void run() {
            //String fileName, String version, ColorSpace reqColorSpace, AudioFormat reqAudioFormat

            if (!isSupported(colorSpace)) {
                error = new MediaException("Color space " + colorSpace + " is not supported!");
                return;
            }
            if (!isSupported(audioFormat)) {
                error = new MediaException("Requested audio format " + audioFormat + " is not supported!");
                return;
            }

            int[] errNoAndStreamId = open0(fileName, version, audioFormat);
            int errNo = errNoAndStreamId[0];

            if (errNo != 0) {
                error = new MediaException("Error " + errNo + " occurred while opening " + fileName + ".");
                return;
            }

            streamId = errNoAndStreamId[1];
            startTime = getStartTime0(streamId);
            endTime = getEndTime0(streamId);
            duration = getDuration0(streamId);

            if (hasVideoStream0(streamId)) {
                nColorChannels = getNumberOfColorChannels0(streamId);

                for (StreamListener listener : imageListeners) {
                    listener.streamOpened();
                }

                // Add the playback
                playback.add(new VideoPlayback());
            }

            if (hasAudioStream0(streamId)) {
                audioBuffer = getAudioBuffer0(streamId, AUDIO_BUFFER_SIZE);
                // When using stereo need to multiply the frameSize by number of channels
                audioFormat = new AudioFormat(
                        getEncoding(),
                        getSampleRate0(streamId),
                        getSampleSizeInBits0(streamId),
                        getNumberOfSoundChannels0(streamId),
                        getFrameSize0(streamId) * getNumberOfSoundChannels0(streamId),
                        (int) getFrameRate0(streamId),
                        false);

                for (StreamListener listener : audioListeners) {
                    listener.streamOpened();
                }

                // Add the playback
                playback.add(new AudioPlayback());
            }

            setStatus(Status.READY);

            // Do we need to play
            checkPlay();
        }
    }

    private boolean isReady() {
        return status == Status.READY;
    }

    private abstract class RunnableWithStopHook implements Runnable {
        boolean doRun;
        void stop() {
            doRun = false;
        }
    }

	private class AudioPlayback extends RunnableWithStopHook {

        byte[] buffer = new byte[getAudioBufferSize()]; // Allocate the buffer for the audio data

		@Override
		public void run() {
		    doRun = true;

            // Introduce an "onReady event from player"

            logger.info("Stream %d: Starting audio thread.", getStreamId());

		    // Inform stream listeners about play
            synchronized (audioListeners) {
                for (StreamListener listener : audioListeners) {
                    listener.streamStarted();
                }
            }

			// Start the play back loop
			while (doRun) {

			    boolean hasData = readAudioData(buffer) > 0;

			    if (hasData) {
                    // Fulfill all listeners with a lock that allows to add listeners concurrently
                    synchronized (audioListeners) {
                        // For a listeners forward this data
                        for (StreamListener listener : audioListeners) {
                            listener.streamData(buffer);
                        }
                    }
                    setStatus(Status.PLAYING);

                } else {
			        setStatus(Status.STALLED);
                }
			}

			// Inform stream listeners about stop
            synchronized (audioListeners) {
                for (StreamListener listener : audioListeners) {
                    listener.streamStopped();
                }
            }

            logger.info("Stream %d: Stopped audio thread.", getStreamId());
		}
	}

	private class VideoPlayback extends RunnableWithStopHook {

        @Override
        public void run() {
            doRun = true;

            logger.info("Stream %d: Starting video thread.", getStreamId());

            // Inform the stream listeners about the play
            synchronized (imageListeners) {
                for (StreamListener listener : imageListeners) {
                    listener.streamStarted();
                }
            }

            while (doRun) {

                // The size of the buffer may change per frame
                byte[] buffer = new byte[getWidth()*getHeight()*getNumberOfColorChannels()];
                boolean hasData = readImageData(buffer) > 0;

                if (hasData) {
                    // Fulfill all listeners
                    synchronized (imageListeners) {
                        for (ImageStreamListener listener : imageListeners) {
                            listener.streamNewImageSize(getWidth(), getHeight());
                            listener.streamData(buffer);
                        }
                    }
                    setStatus(Status.PLAYING);

                } else {
                    setStatus(Status.STALLED);
                }
            }

            // Inform stream listeners about stop
            synchronized (imageListeners) {
                for (StreamListener listener : imageListeners) {
                    listener.streamStopped();
                }
            }

            logger.info("Stream %d: Stopped video thread.", getStreamId());
        }
    }

    private boolean playRequested = false;

    private void checkPlay() {
        if (playRequested) {
            play0(streamId);
            playback.parallelStream().forEach(r -> threads.submit(r));
        }
    }

	@Override
	public void play() {
        if (isReady()) {
            play0(streamId);
            playback.parallelStream().forEach(r -> threads.submit(r));
        } else {
            playRequested = true;
        }
	}

    @Override
    public void stop() {
        if (isReady()) {
            stop0(streamId);
            playback.parallelStream().forEach(r -> r.stop());
            setStatus(Status.STOPPED);
        } else {
            playRequested = false;
        }
    }

    @Override
    public void pause() {
        if (isReady()) {
            pause0(streamId);
            playback.parallelStream().forEach(r -> r.stop());
            setStatus(Status.PAUSED);
        } else {
            playRequested = false;
        }
    }

    @Override
    public void close() {
	    if (isReady()) {
            stop();
            close0(streamId);
            setStatus(Status.DISPOSED);
        } else {
            playRequested = false;
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (isCloseToZero(speed, EPS)) {
            stop();
        }
        // TODO: Handle play/stop for audio, but may just work out with empty signals
        setSpeed0(streamId, speed);
    }

    /**
     * Get the stream id for this movie stream.
     *
     * @return the stream id.
     */
    private int getStreamId() { return streamId; }

    private static boolean isSupported(ColorSpace colorSpace) {
        return colorSpace == ColorSpace.getInstance(ColorSpace.CS_sRGB);
    }

    private static boolean isSupported(AudioFormat audioFormat) {
        return (audioFormat.getChannels() == 1 && audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED)
                || (audioFormat.getChannels() == 2 && audioFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED);
    }

    /**
     * Get the encoding for the audio stream.
     *
     * @return The encoding javax.sound.sampled.AudioFormat.Encoding.
     */
    private AudioFormat.Encoding getEncoding() {
        String codecName = getCodecName0(streamId).toLowerCase();
        AudioFormat.Encoding encoding = new AudioFormat.Encoding(codecName);
        switch (codecName) {
            case "pcm_u8":
            case "pcm_u16le":
                encoding = AudioFormat.Encoding.PCM_UNSIGNED;
                break;
            case "pcm_s8":
            case "pcm_s16le":
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                break;
        }
        return encoding; // Return codec with name
    }

    private final static double EPS = Math.ulp(1.0);

    private static boolean isCloseToZero(double value, double threshold) {
        return Math.abs(value) < threshold;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final static class Builder {
        private String fileName;
        private String version;
        private AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
        private ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB); // set default
        private List<StreamListener> audioListeners = new ArrayList<>();
        private List<ImageStreamListener> imageListeners = new ArrayList<>();

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setAudioFormat(AudioFormat audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public Builder setColorSpace(ColorSpace colorSpace) {
            this.colorSpace = colorSpace;
            return this;
        }

        public Builder addAudioStreamListener(StreamListener listener) {
            this.audioListeners.add(listener);
            return this;
        }

        public Builder addImageStreamListener(ImageStreamListener listener) {
            this.imageListeners.add(listener);
            return this;
        }

        public MediaPlayer build() {
            return new MediaPlayer(
                    fileName,
                    version,
                    colorSpace,
                    audioFormat,
                    audioListeners,
                    imageListeners);
        }
    }

    private MediaPlayer(String fileName,
                        String version,
                        ColorSpace colorSpace,
                        AudioFormat audioFormat,
                        List<StreamListener> audioListeners,
                        List<ImageStreamListener> imageListeners) {
        this.fileName = fileName;
        this.version = version;
        this.audioFormat = audioFormat;
        this.colorSpace = colorSpace;
        this.audioListeners = audioListeners;
        this.imageListeners = imageListeners;
        // Initialize the player but return fast
        Thread thread = new Thread(new InitMoviePlayer());
        thread.start();
    }
}
