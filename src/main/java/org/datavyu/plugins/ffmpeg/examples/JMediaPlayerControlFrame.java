package org.datavyu.plugins.ffmpeg.examples;

import org.datavyu.plugins.ffmpeg.MediaPlayer;
import org.datavyu.plugins.ffmpeg.PlaybackRateController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JMediaPlayerControlFrame extends JFrame implements KeyListener, ChangeListener {

    private MediaPlayer mediaPlayer;
    private JSlider jSlider;

    JMediaPlayerControlFrame(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        jSlider = new JSlider(0, (int) mediaPlayer.getDuration(),0);
        jSlider.setFocusable(false);
        jSlider.addChangeListener(this);
        add(jSlider, BorderLayout.SOUTH);
        addKeyListener(this);
        pack();
        setSize(640, 480);
        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        double currentTime, nextTime;
        float currentVolume, nextVolume;
        float currentRate, nextRate;
        currentRate = mediaPlayer.getRate();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_NUMPAD5:
                System.out.println("Stop");
                mediaPlayer.stop();
                break;
            case KeyEvent.VK_NUMPAD8:
                System.out.println("Play");
                mediaPlayer.play();
                break;
            case KeyEvent.VK_NUMPAD2:
                System.out.println("Pause");
                mediaPlayer.pause();
                break;
            case KeyEvent.VK_S:
                System.out.println("Step Forward");
                mediaPlayer.stepForward();
                break;
            case KeyEvent.VK_D:
                System.out.println("Step Backward");
                mediaPlayer.stepBackward();
                break;
            case KeyEvent.VK_I:
                System.out.println("Stream Information : Fps = " + mediaPlayer.getFps() +
                                    " Image Width = " + mediaPlayer.getImageWidth() +
                                    " Image Height = " + mediaPlayer.getImageHeight() +
                                    " Stream Duration = " + mediaPlayer.getDuration());
                break;
            case KeyEvent.VK_NUMPAD6:
                nextRate = PlaybackRateController.stepToFaster(currentRate);
                System.out.println("Change rate from " + currentRate + " to " + nextRate);
                mediaPlayer.setRate(nextRate);
                break;
            case KeyEvent.VK_NUMPAD4:
                nextRate = PlaybackRateController.stepToSlower(currentRate);
                System.out.println("Change rate from " + currentRate + " to " + nextRate);
                mediaPlayer.setRate(nextRate);
                break;
            case KeyEvent.VK_SPACE:
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
                nextVolume = currentVolume - 0.1F;
                System.out.println("Change volume from " + currentVolume + " dB to " + nextVolume + " dB");
                mediaPlayer.setVolume(nextVolume);
                break;
            case KeyEvent.VK_9:
                currentVolume = mediaPlayer.getVolume();
                nextVolume = currentVolume + 0.1F;
                System.out.println("Change volume from " + currentVolume + " dB to " + nextVolume + " dB");
                mediaPlayer.setVolume(nextVolume);
                break;
            case KeyEvent.VK_M:
                System.out.println("Mute");
                mediaPlayer.setMute(true);
                break;
            case KeyEvent.VK_N:
                System.out.println("Un-mute");
                mediaPlayer.setMute(false);
                break;
            case KeyEvent.VK_ESCAPE:
                mediaPlayer.dispose();
                System.exit(0);
                System.out.println("Dispose the media player");
            default:
                System.err.println("Unrecognized event " + e.paramString());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void stateChanged(ChangeEvent e) {
        int newTime = jSlider.getValue();
        mediaPlayer.seek(newTime);
    }
}
