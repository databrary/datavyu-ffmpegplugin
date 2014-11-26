package org.datavyu.plugins.vlcfx;

import com.sun.jna.Memory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by jesse on 10/21/14.
 */
public class VLCApplication extends Application {

    /**
     * Target width, unless {@link #useSourceSize} is set.
     */
    private static final int WIDTH = 1920;
    /**
     * Target height, unless {@link #useSourceSize} is set.
     */
    private static final int HEIGHT = 1080;
    /**
     * Set this to <code>true</code> to resize the display to the dimensions of the
     * video, otherwise it will use {@link #WIDTH} and {@link #HEIGHT}.
     */
    private static final boolean useSourceSize = true;

    /**
     * Pixel format.
     */
    private final WritablePixelFormat<ByteBuffer> pixelFormat;
    /**
     *
     */
    private final BorderPane borderPane;
    /**
     * Lightweight JavaFX canvas, the video is rendered here.
     */
    private final Canvas canvas;
    File dataFile;
    boolean init = false;
    /**
     * The vlcj direct rendering media player component.
     */
    private DirectMediaPlayerComponent mediaPlayerComponent;
    private DirectMediaPlayer mp;
    /**
     *
     */
    private Stage stage;
    /**
     *
     */
    private Scene scene;
    /**
     * Pixel writer to update the canvas.
     */
    private PixelWriter pixelWriter;

    private long duration = -1;

    private long lastVlcUpdateTime = -1;
    private long lastTimeSinceVlcUpdate = -1;

    private float fps;


    private boolean assumedFps = false;

    static {
//        String tempDir = System.getProperty("java.io.tmpdir");
//        new NativeLibraryManager(tempDir);
//        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), tempDir);
//        new NativeDiscovery().discover();
    }

    public VLCApplication(File file) {
        dataFile = file;
        canvas = new Canvas();

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelFormat = PixelFormat.getByteBgraPreInstance();

        borderPane = new BorderPane();
        borderPane.setCenter(canvas);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void seek(long time) {

        System.out.println("SEEKING TO " + time);

        mp.setTime(time);
    }

    public void pause() {
        System.out.println(mp.isPlaying());
        if (mp.isPlaying())
            mp.pause();
        System.out.println(mp.isPlaying());
    }

    public void play() {
        mp.play();
    }

    public void stop() {
        mp.stop();
    }

    public long getCurrentTime() {
//        System.out.println("CURRENT TIME " + mp.getTime());
//        System.out.println("DV TIME " + Datavyu.getDataController().getCurrentTime());
//        System.out.println("POSITION " + mp.getPosition());

        long vlcTime = mp.getTime();
        if (vlcTime == lastVlcUpdateTime) {
            long currentTime = System.currentTimeMillis();
            long timeSinceVlcUpdate = lastTimeSinceVlcUpdate - currentTime;
            lastTimeSinceVlcUpdate = currentTime;
            return vlcTime + timeSinceVlcUpdate;
        } else {
            return mp.getTime();
        }
    }

    public float getFrameRate() {
        if (fps == 0.0f) {
            return 30.0f;
        }
        return fps;
    }

    public long getDuration() {
        if (duration < 0) {
            duration = mp.getLength();
        }
        return duration;
    }

    public float getRate() {
        return (float) mp.getRate();
    }

    public void setRate(float rate) {
        mp.setRate(rate);
    }

    public void setVisible(final boolean visible) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!visible) {
                    stage.hide();
                } else {
                    stage.show();
                }
            }
        });

    }

    public boolean isAssumedFps() {
        return assumedFps;
    }

    public void setVolume(double volume) {
        mp.setVolume((int) (volume * 200));
    }

    public boolean isInit() {
        return init;
    }

    public void closeAndDestroy() {

        mp.release();
        mediaPlayerComponent.release();

    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public void start(Stage primaryStage) {

        this.stage = primaryStage;

        stage.setTitle("Datavyu: " + dataFile.getName());

        scene = new Scene(borderPane);


        mediaPlayerComponent = new TestMediaPlayerComponent();
        mp = mediaPlayerComponent.getMediaPlayer();
        mp.prepareMedia(dataFile.getAbsolutePath());

        mp.play();

        // Wait for it to spin up so we can grab metadata
        while (!mp.isPlaying()) {
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        fps = mp.getFps();
        if (fps == 0) {
            assumedFps = true;
        }

        pause();

        mp.setTime(0);

        init = true;

    }

    private class TestMediaPlayerComponent extends DirectMediaPlayerComponent {

        public TestMediaPlayerComponent() {
            super(new TestBufferFormatCallback());
        }

        @Override
        public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
            Memory nativeBuffer = nativeBuffers[0];
//            ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
//            pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);

            ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
            pixelWriter.setPixels(0, 0, WIDTH, HEIGHT, pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
        }
    }

    /**
     * Callback to get the buffer format to use for video playback.
     */
    private class TestBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            int width;
            int height;
            if (useSourceSize) {
                width = sourceWidth;
                height = sourceHeight;
            } else {
                width = WIDTH;
                height = HEIGHT;
            }
            canvas.setWidth(width);
            canvas.setHeight(height);
            stage.setWidth(width);
            stage.setHeight(height);
            return new RV32BufferFormat(width, height);
        }
    }

//    private final EventHandler<ActionEvent> nextFrame = new EventHandler<ActionEvent>() {
//        @Override
//        public void handle(ActionEvent t) {
//            Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
//            if (nativeBuffers != null) {
//                // FIXME there may be more efficient ways to do this...
//                Memory nativeBuffer = nativeBuffers[0];
//                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
//                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
//                pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
//            }
////            mediaPlayerComponent.getMediaPlayer().unlock();
//        };
//    };

}
