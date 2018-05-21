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

    private MediaPlayer mediaPlayer;
    private Frame frame;
    private static final int TIME_OUT_SEC = 10;

    private MeasureFrameRate(String movieFileName, float speed) {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
        frame = new Frame();
        mediaPlayer = MediaPlayer.newBuilder()
                .setFileName(movieFileName)
                .setAudioFormat(audioFormat)
                .setColorSpace(colorSpace)
                .addAudioStreamListener(new AudioSoundStreamListener(audioFormat))
                .addImageStreamListener(new ImageStreamListenerFrame(frame, colorSpace))
                .build();
        if (mediaPlayer.hasError()) {
            throw mediaPlayer.getError();
        }
        // Open the movie stream provider
        mediaPlayer.setSpeed(speed);
        int width = mediaPlayer.getWidth();
        int height = mediaPlayer.getHeight();
        frame.setBounds(0, 0, width, height);
        frame.setVisible(true);
    }

    private void start() {
        mediaPlayer.play();
    }

    private void stop() {
        mediaPlayer.stop();
    }

    private void close() throws IOException {
        frame.setVisible(false);
        mediaPlayer.close();
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
                double timeDifference = measureFrameRate.mediaPlayer.getCurrentTime()
                                      - measureFrameRate.mediaPlayer.getStartTime();
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
