/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

    public TracksControllerV() {
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
        TimescalePainter scale = new TimescalePainter();
        scale.setStart(25000);
        scale.setEnd(60000);
        scale.setIntervals(100);
        scale.setMajor(15);

        tracksTimePanel.add(scale);

        // Set up the menu panel
        tracksMenuPanel = new JPanel();
        tracksMenuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton lockButton = new JButton("Lock");
        JButton bookmarkButton = new JButton("Add Bookmark");
        JButton snapButton = new JButton("Snap");

        lockButton.setEnabled(false);
        bookmarkButton.setEnabled(false);
        snapButton.setEnabled(false);

        tracksMenuPanel.add(lockButton);
        tracksMenuPanel.add(bookmarkButton);
        tracksMenuPanel.add(snapButton);

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

}
