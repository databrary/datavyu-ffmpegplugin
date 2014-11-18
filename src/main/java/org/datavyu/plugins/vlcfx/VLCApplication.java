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
     * Pixel writer to update the canvas.
     */
    private final PixelWriter pixelWriter;
    /**
     * Pixel format.
     */
    private final WritablePixelFormat<ByteBuffer> pixelFormat;
    /**
     *
     */
    private final BorderPane borderPane;
    /**
     * The vlcj direct rendering media player component.
     */
    private final DirectMediaPlayerComponent mediaPlayerComponent;
    private final DirectMediaPlayer mp;
    File dataFile;
    boolean init = false;
    /**
     *
     */
    private Stage stage;
    /**
     *
     */
    private Scene scene;
    /**
     * Lightweight JavaFX canvas, the video is rendered here.
     */
    private Canvas canvas;


    public VLCApplication(File file) {
        dataFile = file;
        canvas = new Canvas();

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelFormat = PixelFormat.getByteBgraInstance();

        borderPane = new BorderPane();
        borderPane.setCenter(canvas);

        mediaPlayerComponent = new TestMediaPlayerComponent();
        mp = mediaPlayerComponent.getMediaPlayer();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void seek(long time) {
        System.out.println("SEEKING");
        mp.setTime(time);
    }

    public void pause() {
        mp.pause();
    }

    public void play() {
        mp.play();
    }

    public void stop() {
        mp.stop();
    }

    public long getCurrentTime() {
        return mp.getTime();
    }

    public float getFrameRate() {
        return (float) 30;
    }

    public long getDuration() {
        return mp.getLength();
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

    public void setVolume(double volume) {
    }

    public boolean isInit() {
        return init;
    }

    public void closeAndDestroy() {

    }

    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        stage.setTitle("vlcj JavaFX Direct Rendering Test");

        scene = new Scene(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();

        mp.playMedia(dataFile.getAbsolutePath());
        mp.pause();

        init = true;

    }

    private class TestMediaPlayerComponent extends DirectMediaPlayerComponent {

        public TestMediaPlayerComponent() {
            super(new TestBufferFormatCallback());
        }

        @Override
        public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
            Memory nativeBuffer = nativeBuffers[0];
            ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
            pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
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

}
