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
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.swing.*;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * @author jesse
 */
public class VideoConverter extends Application {

    static {
        new NativeDiscovery().discover();
    }

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
    private DirectMediaPlayer mediaPlayerComponent = null;
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

//        mediaPlayerComponent = new TestMediaPlayerComponent();
    }

    public void ConvertVideo(File infile, File outfile, final JProgressBar progressBar) {
//        :sout=#transcode{vcodec=mp4v,vb=1024,acodec=mp4a,ab=192}:standard{mux=mp4,access=file{no-overwrite},dst=/Users/jesse/Desktop/test.mp4}
//        String[] libvlcArgs = {"-vvvvv", "--no-plugins-cache", ":sout=#transcode{venc=x264{profile=baseline}," +
//                "vcodec=mp4v,vfilter=canvas{padd=true},aenc=ffmpeg{strict=-2},acodec=mp4a,ab=192,channels=2"
//                + "}:standard{access=file,mux=mp4,dst="
//                + outfile.getAbsolutePath() + "}"};
        String[] libvlcArgs = {"-vvvvv",
                "--sout-x264-preset", "baseline",
//                "--sout-x264-tune","film", "--sout-transcode-threads","8",
//                "--sout-x264-keyint","50", "--sout-x264-lookahead","100", "--sout-x264-vbv-maxrate","6000", "--sout-x264-vbv-bufsize","6000",
                ":sout=#transcode{" +
                        "vcodec=h264,vb=1200,aenc=ffmpeg{strict=-2},acodec=mp4a,ab=192,channels=2,samplerate=44100,scale=1,fps=29.97"
                + "}:standard{access=file,dst="
                + outfile.getAbsolutePath() + "}"};

        mediaPlayerComponent = new MediaPlayerFactory(libvlcArgs).newDirectMediaPlayer(new TestBufferFormatCallback(), new RenderCallback() {
            @Override
            public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
                Memory nativeBuffer = nativeBuffers[0];
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
            }
        });

        canvas = new Canvas();
//        canvas.setBackground(Color.black);


        mediaPlayerComponent.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

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

        mediaPlayerComponent.playMedia(infile.getAbsolutePath(), libvlcArgs);


    }

    public void StopConversion() {
        mediaPlayerComponent.stop();
    }

    public void DetectIfNeedsConvert(File infile) {

    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        stage.setTitle("vlcj JavaFX Direct Rendering Test");

        scene = new Scene(borderPane);

        primaryStage.setScene(scene);
//        primaryStage.show();
//        primaryStage.hide();

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
