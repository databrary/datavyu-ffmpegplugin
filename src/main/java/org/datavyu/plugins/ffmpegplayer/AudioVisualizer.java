package org.datavyu.plugins.ffmpegplayer;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AudioVisualizer implements StreamListener {
    final private static int N_SAMPLE = 128;
    final private static int WIDTH = 600;
    final private static int HEIGHT = 400;
    final private static GraphPanel.Range Y_RANGE = new GraphPanel.Range(-32768f, 32768f, 5000f);
    private JFrame frame = new JFrame("Audio Visualizer");
    private GraphPanel barPanel = new GraphPanel(WIDTH, HEIGHT);
    private float bps; // bytes per second
    private long nBytes;
    private int nSample;

    public AudioVisualizer(AudioFormat audioFormat, int nSample) {
        this.nSample = nSample;
        this.bps = audioFormat.getFrameSize() * audioFormat.getFrameRate();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(barPanel, BorderLayout.CENTER);
        frame.pack();
    }

    public AudioVisualizer(AudioFormat audioFormat) {
        this(audioFormat, N_SAMPLE);
    }

    @Override
    public void streamOpened() {
        nBytes = 0;
        frame.setVisible(true);
    }

    @Override
    public void streamData(byte[] data) {

        // Set range
        float startTime = nBytes/bps;
        nBytes += data.length;
        float endTime = nBytes/bps;
        GraphPanel.Range xRange = new GraphPanel.Range(startTime, endTime, (endTime-startTime)/10);

        // Create points
        int nSubSample = data.length/nSample;
        java.util.List<GraphPanel.Point> points = new ArrayList<>(nSubSample);
        for (int iSubSample = 0; iSubSample < nSubSample; ++iSubSample) {
            float average = 0;
            for (int iSample = 0; iSample < nSample/2; ++iSample) {
                average += ((int)data[iSubSample*nSample+2*iSample]) << 8 + data[iSubSample*nSample+2*iSample+1];
            }
            points.add(new GraphPanel.Point(startTime+iSubSample*nSample/bps, average/nSample));
        }

        // Update bar panel
        barPanel.update(points, xRange, Y_RANGE);
    }

    @Override
    public void streamClosed() {
        frame.setVisible(false);
    }

    @Override
    public void streamStopped() {

    }

    @Override
    public void streamStarted() {

    }
}
