/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.openshapa.graphics.TimescalePainter;
import org.openshapa.graphics.TrackPainter;

/**
 * This class manages the tracks information interface
 */
public class TracksControllerV {

    // Root interface panel
    private JPanel tracksPanel;
    // Panel that holds individual tracks
    private JPanel tracksInfoPanel;
    // Component that is responsible for rendering the time scale
    private TimescalePainter scale;

    private JScrollPane tracksScrollPane;

    private int zoomSetting = 1;

    private long maxEnd;
    private long minStart;

    private List<TrackPainter> trackPainterList;

    public TracksControllerV() {
        // Set default scale values
        maxEnd = 60000;
        minStart = 0;

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new GridBagLayout());
        tracksPanel.setBackground(Color.WHITE);
        

        // Menu buttons
        JButton lockButton = new JButton("Lock");
        JButton bookmarkButton = new JButton("Add Bookmark");
        JButton snapButton = new JButton("Snap");

        lockButton.setEnabled(false);
        bookmarkButton.setEnabled(false);
        snapButton.setEnabled(false);

        JButton zoomInButton = new JButton("( + )");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomInScale(e);
                zoomTracks(e);
            }
        });

        JButton zoomOutButton = new JButton("( - )");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutScale(e);
                zoomTracks(e);
            }
        });

        /* These constraints are re-scoped as re-using may cause problems if
         * values are not reset.
         */
        {
            // Adding lock button
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(2,2,2,2);
            tracksPanel.add(lockButton, c);
        }

        {
            // Adding bookmark button
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(2,2,2,2);
            tracksPanel.add(bookmarkButton, c);
        }

        {
            // Adding snap button
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            c.insets = new Insets(2,2,2,2);
            tracksPanel.add(snapButton, c);
        }

        {
            // Adding the zoom in and out button
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = 0;
            c.insets = new Insets(2,2,2,2);
            c.anchor = GridBagConstraints.LINE_END;

            Box hbox = Box.createHorizontalBox();
            hbox.add(zoomInButton);
            hbox.add(zoomOutButton);

            tracksPanel.add(hbox, c);
        }

        // Add the timescale
        scale = new TimescalePainter();
        scale.setStart(minStart);
        scale.setEnd(maxEnd);

        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 4;
            c.fill = GridBagConstraints.HORIZONTAL;
            tracksPanel.add(scale, c);
        }
        
        // Add the scroll pane

        tracksInfoPanel = new JPanel();
        tracksInfoPanel.setLayout(new GridBagLayout());

        tracksScrollPane = new JScrollPane(tracksInfoPanel);
        tracksScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Set an explicit size of the scroll pane
        {
            tracksScrollPane.getVerticalScrollBar().getWidth();
            Dimension size = new Dimension();
            size.setSize(785, 227);
            tracksScrollPane.setPreferredSize(size);
        }

        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 4;
            tracksPanel.add(tracksScrollPane, c);
        }
        
        trackPainterList = new ArrayList<TrackPainter>();
    }

    public JPanel getTracksPanel() {
        return tracksPanel;
    }

    public void addNewTrack(String trackName) {
        JLabel trackLabel = new JLabel(trackName);

        int newRow = tracksInfoPanel.getComponentCount();

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 100;
            infoPanel.setPreferredSize(size);
            infoPanel.add(trackLabel);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = newRow;
            tracksInfoPanel.add(infoPanel, c);
        }

        JPanel carriagePanel = new JPanel();
        carriagePanel.setLayout(new BorderLayout());
        carriagePanel.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, Color.BLACK));

        TrackPainter trackPainter = new TrackPainter();
        {
            Dimension size = new Dimension();
            size.height = 66;
            size.width = 665;
            trackPainter.setPreferredSize(size);
        }
        trackPainter.setStart(0);
        trackPainter.setEnd(30000);
        trackPainter.setOffset(6000);
        trackPainter.setIntervalTime(scale.getIntervalTime());
//        System.out.println("Initial add interval time: " + scale.getIntervalTime());
        trackPainter.setIntervalWidth(scale.getIntervalWidth());
//        System.out.println("Initial add interval width: " + scale.getIntervalWidth());
        trackPainter.setZoomWindowStart(scale.getStart());
        trackPainter.setZoomWindowEnd(scale.getEnd());

        trackPainterList.add(trackPainter);

        carriagePanel.add(trackPainter, BorderLayout.PAGE_START);

        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 667;
            carriagePanel.setPreferredSize(size);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = newRow;
            c.insets = new Insets(0,0,1,0);
            tracksInfoPanel.add(carriagePanel, c);
        }

        tracksInfoPanel.validate();
    }

    public void zoomInScale(ActionEvent evt) {
        zoomSetting = zoomSetting * 2;
        if (zoomSetting > 32) {
            zoomSetting = 32;
            return;
        }

        long range = maxEnd - minStart;
        long newStart = scale.getStart() + (range / (2*zoomSetting));
        long newEnd = newStart + (range / zoomSetting);

        int newIntervals = scale.getIntervals() - 4;
        if (newIntervals < 4) {
            newIntervals = 4;
        }

        scale.setStart(newStart);
        scale.setEnd(newEnd);
        scale.setIntervals(newIntervals);

        scale.repaint();
    }

    public void zoomOutScale(ActionEvent evt) {
        zoomSetting = zoomSetting / 2;
        if (zoomSetting < 1) {
            zoomSetting = 1;
            return;
        }
        
        long range = maxEnd - minStart;
        long newStart = scale.getStart() + (range / (2*zoomSetting));
        long newEnd = newStart + (range / zoomSetting);
        
        if (zoomSetting == 1) {
            newStart = minStart;
            newEnd = maxEnd;
        }
        
        int newIntervals = scale.getIntervals() + 4;
        if (newIntervals > 20) {
            newIntervals = 20;
        }

        scale.setStart(newStart);
        scale.setEnd(newEnd);
        scale.setIntervals(newIntervals);

        scale.repaint();
    }

    public void zoomTracks(ActionEvent evt) {
        for (TrackPainter tp : trackPainterList) {
            tp.setIntervalTime(scale.getIntervalTime());
//            System.out.println("Interval time: " + scale.getIntervalTime());
            tp.setIntervalWidth(scale.getIntervalWidth());
//            System.out.println("Interval width: " + scale.getIntervalWidth());
            tp.setZoomWindowStart(scale.getStart());
//            System.out.println("Scale start: " + scale.getStart());
            tp.setZoomWindowEnd(scale.getEnd());
//            System.out.println("Scale end: " + scale.getEnd());
            tp.repaint();
        }

        tracksInfoPanel.validate();
    }

}
