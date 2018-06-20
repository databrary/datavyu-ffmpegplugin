package org.datavyu.plugins.ffmpegplayer.examples;

import javafx.scene.media.MediaException;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MediaPlayer;
import org.datavyu.plugins.ffmpegplayer.ImageStreamListenerFrame;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimpleMoviePlayerExample {
    MediaPlayer mediaPlayer;
    final JFrame frame;

    public SimpleMoviePlayerExample(String movieFileName) throws IOException {
        this(movieFileName, "0.0.0.1");
    }

    public SimpleMoviePlayerExample(String movieFileName, String version) throws MediaException {
        final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();
        frame = new JFrame();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_NUMPAD5){
                    System.out.println("Stop");
                    mediaPlayer.stop();
                }
                if(e.getKeyCode() == KeyEvent.VK_NUMPAD8){
                    System.out.println("Play");
                    mediaPlayer.play();
                }
                if(e.getKeyCode() == KeyEvent.VK_NUMPAD6){
                    System.out.println("Speed +2");
                    mediaPlayer.setSpeed(2);
                }
                if(e.getKeyCode() == KeyEvent.VK_NUMPAD4){
                    System.out.println("Speed -2");
                    mediaPlayer.setSpeed(-2);
                }
                if(e.getKeyCode() == KeyEvent.VK_NUMPAD2){
                    System.out.println("Pause");
                    mediaPlayer.pause();
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    System.out.println("Seek Back Short, Current Time " + mediaPlayer.getCurrentTime() + " next Time " +(mediaPlayer.getCurrentTime() - (1)) + " Duration "+mediaPlayer.getDuration());
                    mediaPlayer.seek(mediaPlayer.getCurrentTime() - (1));
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    System.out.println("Seek Forward Short, Current Time " + mediaPlayer.getCurrentTime() + " next Time " +(mediaPlayer.getCurrentTime() + (1)) + " Duration "+mediaPlayer.getDuration());
                    mediaPlayer.seek(mediaPlayer.getCurrentTime() + (1));
                }
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    System.out.println("Seek Forward Long, Current Time " + mediaPlayer.getCurrentTime() + " next Time " +(mediaPlayer.getCurrentTime() + (1 * 5)) + " Duration "+mediaPlayer.getDuration());
                    mediaPlayer.seek(mediaPlayer.getCurrentTime() + (1 * 5));
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    System.out.println("Seek Back Long, Current Time " + mediaPlayer.getCurrentTime() + " next Time " +(mediaPlayer.getCurrentTime() - (1 * 5)) + " Duration "+mediaPlayer.getDuration());
                    mediaPlayer.seek(mediaPlayer.getCurrentTime() - (1 * 5));
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        mediaPlayer = MediaPlayer.newBuilder()
                .setFileName(movieFileName)
                .setVersion(version)
                .setAudioFormat(audioFormat)
                .setColorSpace(colorSpace)
                .addAudioStreamListener(new AudioSoundStreamListener(audioFormat))
                .addImageStreamListener(new ImageStreamListenerFrame(frame, colorSpace))
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
        frame.setBounds(0, 0, 460, 380);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
//        String folderName = "C:\\Users\\Florian";
        String folderName = "C:\\Users\\DatavyuTests\\Documents\\Resources\\Videos";
        List<String> fileNames = Arrays.asList(new String[]{"DatavyuSampleVideo.mp4"});
//        List<String> fileNames = Arrays.asList(new String[]{"Nature Makes You Happy _ BBC Earth_1080p.mp4"});
//        List<String> fileNames = Arrays.asList(new String[]{"DatavyuSampleVideo.mp4", "TurkishManGaitClip_KEATalk.mov"});
        for (String fileName : fileNames) {
            try {
                new SimpleMoviePlayerExample(new File(folderName, fileName).toString());
            } catch (IOException io) {
                System.err.println(io);
            }
        }
    }
}