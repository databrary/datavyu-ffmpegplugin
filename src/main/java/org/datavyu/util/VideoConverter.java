/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datavyu.util;

import com.sun.jna.Memory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.swing.*;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * @author jesse
 */
public class VideoConverter extends Application {

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
    static {
        // Try to load VLC libraries.
        // This discovery function is platform independent
        new NativeDiscovery().discover();
    }

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

    public VideoConverter() {
        canvas = new Canvas();

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelFormat = PixelFormat.getByteBgraInstance();

        borderPane = new BorderPane();
        borderPane.setCenter(canvas);

        mediaPlayerComponent = new TestMediaPlayerComponent();
    }

    public void ConvertVideo(File infile, File outfile, final JProgressBar progressBar) {
        String[] libvlcArgs = {":sout=#transcode{vcodec=h264,acodec=mp4"
                + "}:standard{mux=mp4,dst="
                + outfile.getAbsolutePath() + ",access=file}"};

        canvas = new Canvas();
//        canvas.setBackground(Color.black);


        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void positionChanged(MediaPlayer mediaPlayer, final float newPosition) {
                if (SwingUtilities.isEventDispatchThread()) {
                    int value = Math.min(100, Math.round(newPosition * 100.0f));
                    progressBar.setValue(value);
                } else
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            int value = Math.min(100, Math.round(newPosition * 100.0f));
                            progressBar.setValue(value);
                        }
                    });
            }

            @Override
            public void finished(final MediaPlayer mediaPlayer) {
                if (SwingUtilities.isEventDispatchThread()) {
                    progressBar.setValue(progressBar.getMaximum());
                    mediaPlayer.stop();
                } else
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setValue(progressBar.getMaximum());
                            mediaPlayer.stop();
                        }
                    });
            }
        });

        mediaPlayerComponent.getMediaPlayer().playMedia(infile.getAbsolutePath(), libvlcArgs);


    }

    public void StopConversion() {
        mediaPlayerComponent.getMediaPlayer().stop();
    }

    public void DetectIfNeedsConvert(File infile) {

    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        stage.setTitle("vlcj JavaFX Direct Rendering Test");

        scene = new Scene(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();

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
