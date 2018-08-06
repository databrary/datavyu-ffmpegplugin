package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;

public class SimpleMediaPlayerExample {
    public static void main(String[] args) {

        // Create the  media player and attach any listeners
        String movieFileName = "Nature_30fps_1080p.mp4"; //"NIAGARA_FALLS_60fps_1080p.mp4"; //"DatavyuSampleVideo.mp4"; //"Nature_30fps_1080p.mp4";

        // Open a Jframe for data streaming through Java
        MediaPlayer mediaPlayer = new FfmpegMediaPlayer(URI.create(movieFileName), new JFrame());

        // Stream through SDL
        //MediaPlayer mediaPlayer = new FfmpegMediaPlayer(URI.create(movieFileName));

        mediaPlayer.addMediaErrorListener(new MediaErrorListener() {
            @Override
            public void onError(Object source, int errorCode, String message) {
                System.err.println("Error " + errorCode + ": " + message);
            }
        });

        // Define the audio format and color space that we would like to have
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB); // This is the only one we support
        AudioFormat audioFormat = AudioPlayerThread.getMonoFormat(); // There are only two audio formats we support

        // Initialize and start playing
        mediaPlayer.init(audioFormat, colorSpace);
        mediaPlayer.play();

        // Open a Jframe for debugging purposes
        JFrame frame = new JFrame();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                double currentTime, nextTime;
                float currentVolume, nextVolume;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD5:
                        System.out.println("Stop");
                        mediaPlayer.stop();
                        break;
                    case KeyEvent.VK_NUMPAD8:
                        System.out.println("Play");
                        mediaPlayer.play();
                        break;
                    case KeyEvent.VK_S:
                        System.out.println("Step Forward");
                        mediaPlayer.stepForward();
                        break;
                    case KeyEvent.VK_NUMPAD6:
                        System.out.println("Speed +1");
                        mediaPlayer.setRate(1);
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        System.out.println("Speed -1");
                        mediaPlayer.setRate(-1);
                        break;
                    case KeyEvent.VK_NUMPAD2:
                        System.out.println("Pause");
                        mediaPlayer.pause();
                        break;
                    case KeyEvent.VK_LEFT:
                        currentTime = mediaPlayer.getPresentationTime();
                        nextTime = currentTime - 1;
                        System.out.println("Seek from " + currentTime + " sec to " + nextTime + "sec");
                        mediaPlayer.seek(nextTime);
                        break;
                    case KeyEvent.VK_RIGHT:
                        currentTime = mediaPlayer.getPresentationTime();
                        nextTime = currentTime + 1;
                        System.out.println("Seek from " + currentTime + " sec to " + nextTime + "sec");
                        mediaPlayer.seek(nextTime);
                        break;
                    case KeyEvent.VK_UP:
                        currentTime = mediaPlayer.getPresentationTime();
                        nextTime = currentTime + 5;
                        System.out.println("Seek from " + currentTime + " sec to " + nextTime + "sec");
                        mediaPlayer.seek(nextTime);
                        break;
                    case KeyEvent.VK_DOWN:
                        currentTime = mediaPlayer.getPresentationTime();
                        nextTime = currentTime - 5;
                        System.out.println("Seek from " + currentTime + " sec to " + nextTime + "sec");
                        mediaPlayer.seek(nextTime);
                        break;
                    case KeyEvent.VK_0:
                        currentVolume = mediaPlayer.getVolume();
                        nextVolume = currentVolume - 1;
                        System.out.println("Change volume from " + currentVolume + " dB to " + nextVolume + " dB");
                        mediaPlayer.setVolume(nextVolume);
                        break;
                    case KeyEvent.VK_9:
                        currentVolume = mediaPlayer.getVolume();
                        nextVolume = currentVolume + 1;
                        System.out.println("Change volume from " + currentVolume + " dB to " + nextVolume + " dB");
                        mediaPlayer.setVolume(nextVolume);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        mediaPlayer.dispose();
                        System.out.println("Dispose the media player");
                    default:
                        System.err.println("Unrecognized event " + e.paramString());
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
}
