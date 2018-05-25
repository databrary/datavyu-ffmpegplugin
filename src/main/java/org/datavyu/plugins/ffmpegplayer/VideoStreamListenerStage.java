package org.datavyu.plugins.ffmpegplayer;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

public class VideoStreamListenerStage extends Application implements StreamListener {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(VideoStreamListenerStage.class);

    /** The properties */
    private Hashtable<String, String> properties = new Hashtable<>();

    private MediaPlayer mediaPlayer;

    private ColorSpace colorSpace;

    private ComponentColorModel cm;

    private WritableImage writableImage;

    private ImageView imageView;

    public VideoStreamListenerStage() {

    }

    public VideoStreamListenerStage(MediaPlayer mediaPlayer, ColorSpace colorSpace) {
        this.mediaPlayer = mediaPlayer;
        this.colorSpace = colorSpace;
        this.imageView = new ImageView();
    }

    @Override
    public void streamOpened() {
        int width = mediaPlayer.getWidth();
        int height = mediaPlayer.getHeight();
        int nChannel = mediaPlayer.getNumberOfColorChannels();
        cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Initialize an empty originalImage
        DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
        writableImage = SwingFXUtils.toFXImage(new BufferedImage(cm, raster, false, properties), null);
        imageView.setImage(writableImage);
    }

    @Override
    public void streamData(byte[] data) {
        // Width and height could have changed due to the view
        int width = mediaPlayer.getWidth();
        int height = mediaPlayer.getHeight();
        logger.debug("Received " + data.length + " By for originalImage: " + width + " x " + height + " pixels.");
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Create data buffer
        DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
        // Create writable raster
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
        // Create the original image
        imageView.setImage(SwingFXUtils.toFXImage(new BufferedImage(cm, raster, false, properties),
                writableImage));
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().add(imageView);
        // Make sure the rescaling happens for the image
        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty().bind(scene.heightProperty());
        primaryStage.setScene(scene);
    }

    @Override
    public void streamClosed() {

    }

    @Override
    public void streamStopped() {

    }

    @Override
    public void streamStarted() {

    }
}
