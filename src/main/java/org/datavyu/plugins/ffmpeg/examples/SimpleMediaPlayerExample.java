package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.*;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;

public class SimpleMediaPlayerExample {
    public static void main(String[] args) {

        // Create the  media player and attach any listeners
        String movieFileName = "Nature_30fps_1080p.mp4";

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
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();

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
                        System.out.println("Step Forward, with FPS : "  + mediaPlayer.getFps());
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
