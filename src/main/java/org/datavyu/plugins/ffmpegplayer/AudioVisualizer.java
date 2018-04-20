package org.datavyu.plugins.ffmpegplayer;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AudioVisualizer extends JPanel implements StreamListener {

    final private static int WIDTH = 600;
    final private static int HEIGHT = 400;

    private BarPanel barPanel = new BarPanel(WIDTH, HEIGHT);
    private AudioStream audioStream;
    private float bps; // bytes per second
    private long nBytes;

    public AudioVisualizer(AudioStream audioStream) {
        this.audioStream = audioStream;
    }

    @Override
    public void streamOpened() {
        AudioFormat audioFormat = audioStream.getAudioFormat();
        bps = audioFormat.getFrameSize() * audioFormat.getFrameRate();
        nBytes = 0;
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(barPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void streamData(byte[] data) {

        // Set range
        float startTime = bps * nBytes;
        nBytes += data.length;
        float endTime = bps * nBytes;

        // Pull the data convert into bars
        barPanel.update(IntStream
                .range(0, data.length)
                .map(i -> data[i])
                .mapToObj(datum -> new BarPanel.Bar((int) bps, datum)).collect(Collectors.toList()),
                new BarPanel.Range(startTime, endTime),
                new BarPanel.Range(0f, 255f));
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
