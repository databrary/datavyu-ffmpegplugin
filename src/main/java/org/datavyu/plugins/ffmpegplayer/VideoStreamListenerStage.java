package org.datavyu.plugins.ffmpegplayer;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

public class VideoStreamListenerStage implements StreamListener {

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(VideoStreamListenerStage.class);

    /** The properties */
    private Hashtable<String, String> properties = new Hashtable<>();

    private MovieStream movieStream;

    private ColorSpace colorSpace;

    private ComponentColorModel cm;

    private WritableImage writableImage;

    private Stage stage;

    private ImageView imageView;

    public VideoStreamListenerStage(MovieStream movieStream, Stage stage, ColorSpace colorSpace) {
        this.movieStream = movieStream;
        this.colorSpace = colorSpace;
        this.stage = stage;
        this.imageView = new ImageView();
        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().add(imageView);
        stage.setScene(scene);
    }

    @Override
    public void streamOpened() {
        int width = movieStream.getWidthOfView();
        int height = movieStream.getHeightOfView();
        int nChannel = movieStream.getNumberOfColorChannels();
        cm = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Initialize an empty originalImage
        DataBufferByte dataBuffer = new DataBufferByte(new byte[width*height*nChannel], width*height);
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0,0));
        writableImage = SwingFXUtils.toFXImage(new BufferedImage(cm, raster, false, properties), null);
        imageView.setImage(writableImage);
        stage.show();
    }

    @Override
    public void streamData(byte[] data) {
        // Width and height could have changed due to the view
        int width = movieStream.getWidthOfView();
        int height = movieStream.getHeightOfView();
        logger.debug("Received " + data.length + " By for originalImage: " + width + " x " + height + " pixels.");
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        // Create data buffer
        DataBufferByte dataBuffer = new DataBufferByte(data, width*height);
        // Create writable raster
        WritableRaster raster = WritableRaster.createWritableRaster(sm, dataBuffer, new Point(0, 0));
        // Create the original image
        imageView.setImage(SwingFXUtils.toFXImage(new BufferedImage(cm, raster, false, properties),
                writableImage));
        stage.show();
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
