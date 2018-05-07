package org.datavyu.plugins.ffmpegplayer.examples;

import javafx.scene.media.MediaException;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MoviePlayer;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerContainer;

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
    org.datavyu.plugins.ffmpegplayer.MoviePlayer moviePlayer;
    final Frame frame;

    public SimpleMoviePlayerExample(String movieFileName) throws IOException {
        this(movieFileName, "0.0.0.1");
    }

    public SimpleMoviePlayerExample(String movieFileName, String version) throws MediaException {
        final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat reqAudioFormat = AudioSoundStreamListener.getNewMonoFormat();
        moviePlayer = MoviePlayer.newBuilder()
                .setFileName(movieFileName)
                .setVersion(version)
                .setAudioFormat(reqAudioFormat)
                .setColorSpace(reqColorSpace)
                .build();
        if (moviePlayer.hasError()) {
            throw moviePlayer.getError();
        }
        frame = new Frame();
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                moviePlayer.close();
            }
        } );
        // Add the audio sound listener
        moviePlayer.addAudioStreamListener(new AudioSoundStreamListener(moviePlayer));
        // Add video display
        moviePlayer.addVideoStreamListener(new VideoStreamListenerContainer(moviePlayer, frame,
                reqColorSpace));
        // Open the movie stream provider
        //moviePlayer.open(movieFileName, version, reqColorSpace, reqAudioFormat);
        moviePlayer.play();
        int width = moviePlayer.getWidth();
        int height = moviePlayer.getHeight();
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