package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Hashtable;

final class FfmpegMediaPlayer extends NativeMediaPlayer implements MediaPlayerData {
    private float mutedVolume = 1.0f;  // last volume before mute
    private boolean muteEnabled = false;
    private AudioPlayerThread audioPlayerThread;
    private ImagePlayerThread imagePlayerThread;
    private JFrame frame;

    /**
     * Create an ffmpeg media player instance
     *
     * @param source The source
     * @param frame The frame to display (optional), if null then use SDL
     */
    public FfmpegMediaPlayer(URI source, JFrame frame) {
        super(source);
        this.frame = frame;
    }

    @Override
    public void init(AudioFormat audioFormat, ColorSpace colorSpace) {
        initNative(); // start the event queue, make sure to register all state/error listeners before
        ffmpegInitPlayer(getNativeMediaRef(), source, audioFormat, colorSpace);
        // If we have audio data consume it
        if (hasAudioData()) {
            audioPlayerThread = new AudioPlayerThread();
            try {
                audioPlayerThread.init(getAudioFormat());
                audioPlayerThread.start();
            } catch (LineUnavailableException lu) {
                // TODO: Add media error
                throwMediaErrorException(MediaError.ERROR_GSTREAMER_ERROR.code(), lu.getMessage());
            }
        }
        // If we have image data consume it
        if (hasImageData()) {
            imagePlayerThread = new ImagePlayerThread();
            imagePlayerThread.init(getColorSpace(), getImageWidth(), getImageHeight(), frame);
            imagePlayerThread.start();
        }
    }

    private void throwMediaErrorException(int code, String message)
            throws MediaException {
        MediaError me = MediaError.getFromCode(code);
        throw new MediaException(message, null, me);
    }

    @Override
    protected long playerGetAudioSyncDelay() throws MediaException {
        long[] audioSyncDelay = new long[1];
        int rc = ffmpegGetAudioSyncDelay(getNativeMediaRef(), audioSyncDelay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return audioSyncDelay[0];
    }

    @Override
    protected void playerSetAudioSyncDelay(long delay) throws MediaException {
        int rc = ffmpegSetAudioSyncDelay(getNativeMediaRef(), delay);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPlay() throws MediaException {
        int rc = ffmpegPlay(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerStop() throws MediaException {
        int rc = ffmpegStop(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerPause() throws MediaException {
        int rc = ffmpegPause(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerFinish() throws MediaException {
        int rc = ffmpegFinish(getNativeMediaRef());
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected float playerGetRate() throws MediaException {
        float[] rate = new float[1];
        int rc = ffmpegGetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return rate[0];
    }

    @Override
    protected void playerSetRate(float rate) throws MediaException {
        int rc = ffmpegSetRate(getNativeMediaRef(), rate);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected double playerGetPresentationTime() throws MediaException {
        double[] presentationTime = new double[1];
        int rc = ffmpegGetPresentationTime(getNativeMediaRef(), presentationTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return presentationTime[0];
    }

    @Override
    protected boolean playerGetMute() throws MediaException {
        return muteEnabled;
    }

    @Override
    protected synchronized void playerSetMute(boolean enable) throws MediaException {
        if (enable != muteEnabled) {
            if (enable) {
                // Cache the current volume.
                float currentVolume = getVolume();

                // Set the volume to zero.
                playerSetVolume(0);

                // Set the mute flag. It is necessary to do this after
                // calling setVolume() as otherwise the volume will not
                // be set to zero.
                muteEnabled = true;

                // Save the pre-mute volume.
                mutedVolume = currentVolume;
            } else {
                // Unset the mute flag. It is necessary to do this before
                // calling setVolume() as otherwise the volume will not
                // be set to the cached value.
                muteEnabled = false;

                // Set the volume to the cached value.
                playerSetVolume(mutedVolume);
            }
        }
    }

    @Override
    protected float playerGetVolume() throws MediaException {
        synchronized(this) {
            if (muteEnabled)
                return mutedVolume;
        }
        float[] volume = new float[1];
        int rc = ffmpegGetVolume(getNativeMediaRef(), volume);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return volume[0];
    }

    @Override
    protected synchronized void playerSetVolume(float volume) throws MediaException {
        if (!muteEnabled) {
            int rc = ffmpegSetVolume(getNativeMediaRef(), volume);
            if (0 != rc) {
                throwMediaErrorException(rc, null);
            } else {
                mutedVolume = volume;
            }
        } else {
            mutedVolume = volume;
        }
    }

    @Override
    protected float playerGetBalance() throws MediaException {
        float[] balance = new float[1];
        int rc = ffmpegGetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return balance[0];
    }

    @Override
    protected void playerSetBalance(float balance) throws MediaException {
        int rc = ffmpegSetBalance(getNativeMediaRef(), balance);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected double playerGetDuration() throws MediaException {
        double[] duration = new double[1];
        int rc = ffmpegGetDuration(getNativeMediaRef(), duration);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        if (duration[0] == -1.0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return duration[0];
        }
    }

    @Override
    protected void playerSeek(double streamTime) throws MediaException {
        int rc = ffmpegSeek(getNativeMediaRef(), streamTime);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    protected void playerDispose() {
        if (imagePlayerThread != null) {
            imagePlayerThread.stopped = true;
        }
        if (audioPlayerThread != null) {
            audioPlayerThread.stopped = true;
        }
    }

    private class AudioPlayerThread extends Thread {
        private SourceDataLine soundLine = null;
        //private FloatControl gainControl = null;
        private volatile boolean stopped = false;

        AudioPlayerThread() {
            setName("FFmpeg audio player thread");
            setDaemon(false);
        }

        private void init(AudioFormat audioFormat) throws LineUnavailableException {
            // Get the data line
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            //gainControl = (FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN);
        }

        @Override
        public void run() {
            while (!stopped) {
                byte[] data = null; // data will be assigned through native call
                getAudioBuffer(data);
                // Write blocks when data can't be consumed fast enough
                soundLine.write(data, 0, data.length);
            }
        }

        public void terminate() {
            stopped = false;
            soundLine.drain();
            soundLine.stop();
            soundLine.close();
        }
    }

    // Currently this uses swing components to display the buffered image
    // TODO(fraudies): Switch this to javafx for new GUI
    private class ImagePlayerThread extends Thread {
        private SampleModel sm;
        private ComponentColorModel cm;
        private Hashtable<String, String> properties = new Hashtable<>();
        private BufferedImage image;
        private JFrame frame;
        private boolean doPaint = false;
        private BufferStrategy strategy;
        private static final int NUM_COLOR_CHANNELS = 3;
        private static final int NUM_BUFFERS = 3;
        private volatile boolean stopped = false;
        private int width;
        private int height;
        private static final double REFRESH_PERIOD = 0.01; // >= 1/fps
        private static final double TO_MILLIS = 1000.0;

        ImagePlayerThread() {
            setName("Ffmpeg image player thread");
            setDaemon(false);
        }

        private void updateDisplay() {
            do {
                do {
                    // Make sure to create the buffer strategy before using it!
                    Graphics graphics = strategy.getDrawGraphics();
                    if (doPaint) {
                        graphics.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(),  null);
                    }
                    graphics.dispose();
                } while (strategy.contentsRestored());
                strategy.show();
            } while (strategy.contentsLost());
        }

        private void init(ColorSpace colorSpace, int width, int height, JFrame frame) {

            this.frame = frame;
            this.width = width;
            this.height = height;

            cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
            // Set defaults
            sm = cm.createCompatibleSampleModel(this.width, this.height);
            // Initialize an empty image
            DataBufferByte dataBuffer = new DataBufferByte(new byte[this.width*this.height*NUM_COLOR_CHANNELS],
                    this.width*this.height);
            WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
            // Create the original image
            image = new BufferedImage(cm, raster, false, properties);

            this.frame.setBounds(0, 0, this.width, this.height);
            this.frame.setVisible(true);

            strategy = this.frame.getBufferStrategy();

            // Make sure to make the canvas visible before creating the buffer strategy
            this.frame.createBufferStrategy(NUM_BUFFERS);
            launcher(() -> updateDisplay());
        }

        public void run() {
            while (!stopped) {
                long start = System.currentTimeMillis();
                byte[] data = null;
                // Get the data from the native side that matches width & height
                getImageBuffer(data);
                // Create data buffer
                DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
                // Create writable raster
                WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
                // Create the original image
                image = new BufferedImage(cm, raster, false, properties);
                launcher(() -> updateDisplay());
                // This does not measure the time to update the display
                double waitTime = REFRESH_PERIOD - (System.currentTimeMillis() - start)/TO_MILLIS;
                // If we need to wait
                if (waitTime > 0) {
                    try {
                        Thread.sleep((long) (waitTime*TO_MILLIS));
                    } catch (InterruptedException ie) {
                        // do nothing
                    }
                }
            }
        }

        private void terminte() {
            stopped = true;
        }
    }

    private static void launcher(Runnable runnable) {
        if (EventQueue.isDispatchThread()){
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException e) {
            }
        }
    }


    // ********** From the MediaPlayerData interface
    @Override
    public boolean hasAudioData() {
        boolean[] hasData = new boolean[1];
        int rc = ffmpegHasAudioData(getNativeMediaRef(), hasData);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return hasData[0];
    }

    @Override
    public boolean hasImageData() {
        boolean[] hasData = new boolean[1];
        int rc = ffmpegHasImageData(getNativeMediaRef(), hasData);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return hasData[0];
    }

    @Override
    public int getImageWidth() {
        int[] width = new int[1];
        int rc = ffmpegGetImageWidth(getNativeMediaRef(), width);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return width[0];
    }

    @Override
    public int getImageHeight() {
        int[] height = new int[1];
        int rc = ffmpegGetImageHeight(getNativeMediaRef(), height);
        if (rc != 0) {
            throwMediaErrorException(rc, null);
        }
        return height[0];
    }

    @Override
    public AudioFormat getAudioFormat() {
        AudioFormat ref = null; //new AudioFormat(null, 0F, 0, 0, 0, 0F, false);
        int rc = ffmpegGetAudioFormat(getNativeMediaRef(), ref);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return ref;
    }

    @Override
    public ColorSpace getColorSpace() {
        ColorSpace ref = null; // filled by native side
        int rc = ffmpegGetColorSpace(getNativeMediaRef(), ref);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
        return ref;
    }

    @Override
    public void getAudioBuffer(byte[] data) {
        int rc = ffmpegGetAudioBuffer(getNativeMediaRef(), data);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    @Override
    public void getImageBuffer(byte[] data) {
        int rc = ffmpegGetImageBuffer(getNativeMediaRef(), data);
        if (0 != rc) {
            throwMediaErrorException(rc, null);
        }
    }

    // Native methods
    private native int ffmpegInitPlayer(long refNativeMedia, URI source, AudioFormat requestedAudioFormat,
                                        ColorSpace requestedColorFormat);

    private native int ffmpegGetAudioSyncDelay(long refNativeMedia, long[] syncDelay);
    private native int ffmpegSetAudioSyncDelay(long refNativeMedia, long delay);
    private native int ffmpegPlay(long refNativeMedia);
    private native int ffmpegPause(long refNativeMedia);
    private native int ffmpegStop(long refNativeMedia);
    private native int ffmpegFinish(long refNativeMedia);
    private native int ffmpegGetRate(long refNativeMedia, float[] rate);
    private native int ffmpegSetRate(long refNativeMedia, float rate);
    private native int ffmpegGetPresentationTime(long refNativeMedia, double[] time);
    private native int ffmpegGetVolume(long refNativeMedia, float[] volume);
    private native int ffmpegSetVolume(long refNativeMedia, float volume);
    private native int ffmpegGetBalance(long refNativeMedia, float[] balance);
    private native int ffmpegSetBalance(long refNativeMedia, float balance);
    private native int ffmpegGetDuration(long refNativeMedia, double[] duration);
    private native int ffmpegSeek(long refNativeMedia, double streamTime);

    private native int ffmpegHasAudioData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegHasImageData(long refNativeMedia, boolean[] hasData);
    private native int ffmpegGetImageWidth(long refNativeMedia, int[] width);
    private native int ffmpegGetImageHeight(long refNativeMedia, int [] height);
    private native int ffmpegGetAudioFormat(long refNativeMedia, AudioFormat refToAudioFormat);
    private native int ffmpegGetColorSpace(long refNativeMedia, ColorSpace refToColorSpace);
    private native int ffmpegGetImageBuffer(long refNativeMedia, byte[] refToData);
    private native int ffmpegGetAudioBuffer(long refNativeMedia, byte[] refToData);
}
