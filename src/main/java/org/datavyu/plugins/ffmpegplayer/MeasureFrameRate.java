package org.datavyu.plugins.ffmpegplayer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") // TODO: Performance measurement; integrate into tests
public class MeasureFrameRate {

    private MoviePlayer moviePlayer;
    private Frame frame;
    private static final int TIME_OUT_SEC = 10;

    private MeasureFrameRate(String movieFileName, float speed) {
        moviePlayer = MoviePlayer.newBuilder().setFileName(movieFileName).build();
        if (moviePlayer.hasError()) {
            throw moviePlayer.getError();
        }
        final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat reqAudioFormat = AudioSoundStreamListener.getNewMonoFormat();
        frame = new Frame();
        // Add the audio sound listener
        moviePlayer.addAudioStreamListener(new AudioSoundStreamListener(moviePlayer));
        // Add video display
        moviePlayer.addVideoStreamListener(new VideoStreamListenerContainer(moviePlayer, frame,
                reqColorSpace));
        // Open the movie stream provider
        moviePlayer.setSpeed(speed);
        int width = moviePlayer.getWidth();
        int height = moviePlayer.getHeight();
        frame.setBounds(0, 0, width, height);
        frame.setVisible(true);
    }

    private void start() {
        moviePlayer.play();
    }

    private void stop() {
        moviePlayer.stop();
    }

    private void close() throws IOException {
        frame.setVisible(false);
        moviePlayer.close();
    }

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.OFF);
        //String movieFileName = "C:\\Users\\Florian\\DatavyuSampleVideo.mp4";
        String movieFileName = "C:\\Users\\Florian\\databrary106-Majumder-Datavyu_Training-Cathy_004-004_Mother_Child.mp4";
        List<Float> speeds = new ArrayList<Float>() {{
            add(2f);
            //add(32f);
            //add(1f); add(2f); add(4f); add(8f); add(16f); add(32f);
        }};
        try {
            for (float speed : speeds) {
                MeasureFrameRate measureFrameRate = new MeasureFrameRate(movieFileName, speed);
                measureFrameRate.start();
                try {
                    Thread.sleep(TIME_OUT_SEC * 1000);
                } catch (InterruptedException ie) {
                    System.out.println("Timeout failed with " + ie);
                }
                measureFrameRate.stop();
                double timeDifference = measureFrameRate.moviePlayer.getCurrentTime()
                                      - measureFrameRate.moviePlayer.getStartTime();
                double isSpeed = timeDifference/TIME_OUT_SEC;
                System.out.println("The expected speed: " + speed);
                System.out.println("The detected speed: " + isSpeed);
                //assert Math.abs(speed - isSpeed) < Math.ulp(1f);
                measureFrameRate.close();
            }
        } catch (IOException io) {
            System.err.println("Could not open file " + movieFileName + ". Exception: " + io);
        }
    }
}
