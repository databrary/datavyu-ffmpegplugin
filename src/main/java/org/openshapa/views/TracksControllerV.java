/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import org.openshapa.graphics.TimescalePainter;

/**
 * This class manages the tracks information interface
 */
public class TracksControllerV {

    private static final int TRACKS_SCROLL_PANEL_WIDTH = 600;
    private static final int TRACKS_SCROLL_PANEL_HEIGHT = 270;

    // Root interface panel
    private JPanel tracksPanel;
    // Panel that holds individual tracks
    private JPanel tracksInfoPanel;
    // Panel that holds the time scale
    private JPanel tracksTimePanel;
    // Panel that holds the menu controls
    private JPanel tracksMenuPanel;
    // Component that is responsible for rendering the time scale
    private TimescalePainter scale;

    private int zoomSetting = 1;

    private long maxEnd;
    private long minStart;

    public TracksControllerV() {
        // Defaults
        maxEnd = 60000;
        minStart = 0;

        // Set up the tracks information panel
        tracksInfoPanel = new JPanel();
        tracksInfoPanel.setLayout(new BorderLayout());
        Box infoPanelBox = Box.createVerticalBox();
        tracksInfoPanel.add(infoPanelBox, BorderLayout.NORTH);

        JScrollPane tracksScrollPane = new JScrollPane(tracksInfoPanel);
        tracksScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tracksScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension tracksScrollPaneSize = new Dimension();
        tracksScrollPaneSize.height = TRACKS_SCROLL_PANEL_HEIGHT;
        tracksScrollPaneSize.width = TRACKS_SCROLL_PANEL_WIDTH;
        tracksScrollPane.setPreferredSize(tracksScrollPaneSize);

        // Set up the time scale panel
        tracksTimePanel = new JPanel();
        tracksTimePanel.setLayout(new BorderLayout());

        // Demonstrates how to configure the time scale for display
        scale = new TimescalePainter();
        scale.setStart(minStart);
        scale.setEnd(maxEnd);

        tracksTimePanel.add(scale);

        // Set up the menu panel
        tracksMenuPanel = new JPanel();
        tracksMenuPanel.setLayout(new BorderLayout());

        JButton lockButton = new JButton("Lock");
        JButton bookmarkButton = new JButton("Add Bookmark");
        JButton snapButton = new JButton("Snap");

        lockButton.setEnabled(false);
        bookmarkButton.setEnabled(false);
        snapButton.setEnabled(false);

        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomIn(e);
            }
        });

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOut(e);
            }
        });

        JPanel leftAlignedButtonsPanel = new JPanel();
        leftAlignedButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftAlignedButtonsPanel.add(lockButton);
        leftAlignedButtonsPanel.add(bookmarkButton);
        leftAlignedButtonsPanel.add(snapButton);

        JPanel rightAlignedButtonsPanel = new JPanel();
        rightAlignedButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        rightAlignedButtonsPanel.add(zoomInButton);
        rightAlignedButtonsPanel.add(zoomOutButton);

        tracksMenuPanel.add(leftAlignedButtonsPanel, BorderLayout.WEST);
        tracksMenuPanel.add(rightAlignedButtonsPanel, BorderLayout.EAST);

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new BorderLayout());

        Dimension tracksPanelSize = new Dimension(600, 278);
        tracksPanel.setPreferredSize(tracksPanelSize);

        Box tracksPanelBox = Box.createVerticalBox();
        tracksPanelBox.add(tracksMenuPanel);
        tracksPanelBox.add(tracksTimePanel);
        tracksPanelBox.add(tracksInfoPanel);

        tracksPanel.add(tracksPanelBox, BorderLayout.NORTH);
    }

    public JPanel getTracksPanel() {
        return tracksPanel;
    }

    public void addNewTrack(String trackName) {
        JLabel trackLabel = new JLabel(trackName);
        Box box = (Box) tracksInfoPanel.getComponent(0);
        box.add(trackLabel);
        box.revalidate();
    }

    public void zoomIn(ActionEvent evt) {
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

    public void zoomOut(ActionEvent evt) {
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
    

}
