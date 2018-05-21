package org.datavyu.plugins.ffmpegplayer.examples;

import javafx.scene.media.MediaException;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MediaPlayer;
import org.datavyu.plugins.ffmpegplayer.ImageStreamListenerContainer;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimpleMoviePlayerExample {
    MediaPlayer mediaPlayer;
    final Frame frame;

    public SimpleMoviePlayerExample(String movieFileName) throws IOException {
        this(movieFileName, "0.0.0.1");
    }

    public SimpleMoviePlayerExample(String movieFileName, String version) throws MediaException {
        final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
        frame = new Frame();
        mediaPlayer = MediaPlayer.newBuilder()
                .setFileName(movieFileName)
                .setVersion(version)
                .setAudioFormat(audioFormat)
                .setColorSpace(colorSpace)
                .addAudioStreamListener(new AudioSoundStreamListener(audioFormat))
                .addImageStreamListener(new ImageStreamListenerContainer(frame, null, colorSpace))
                .build();
        if (mediaPlayer.hasError()) {
            throw mediaPlayer.getError();
        }
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                mediaPlayer.close();
            }
        } );
        // Open the movie stream provider
        mediaPlayer.play();
        int width = mediaPlayer.getWidth();
        int height = mediaPlayer.getHeight();
        frame.setBounds(0, 0, width, height);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String folderName = "C:\\Users\\Florian";
        List<String> fileNames = Arrays.asList(new String[]{"DatavyuSampleVideo.mp4", "TurkishManGaitClip_KEATalk.mov"});
        for (String fileName : fileNames) {
            try {
                new SimpleMoviePlayerExample(new File(folderName, fileName).toString());
            } catch (IOException io) {
                System.err.println(io);
            }
        }
    }
}