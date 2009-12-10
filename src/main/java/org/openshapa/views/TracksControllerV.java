package org.openshapa.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import org.openshapa.event.TracksControllerEvent;
import org.openshapa.event.TracksControllerListener;
import org.openshapa.graphics.NeedlePainter;
import org.openshapa.graphics.TimescalePainter;
import org.openshapa.graphics.TrackPainter;
import org.openshapa.graphics.event.NeedleEvent;
import org.openshapa.graphics.event.NeedleEventListener;

/**
 * This class manages the tracks information interface
 */
public class TracksControllerV implements NeedleEventListener {

    // Root interface panel
    private JPanel tracksPanel;
    // Panel that holds individual tracks
    private JPanel tracksInfoPanel;
    // Component that is responsible for rendering the time scale
    private TimescalePainter scale;
    // Scroll pane that holds track information
    private JScrollPane tracksScrollPane;
    // Component responsible for painting the timing needle
    private NeedlePainter needle;
    // This layered pane holds the needle painter
    private JLayeredPane layeredPane;
    // This box holds the header and carriage box
    private Box tracksInfoBox;
    // This box holds the header
    private Box headerBox;
    // This box holds the carriage
    private Box carriageBox;

    /** Zoomed into the display by how much.
     * Values should only be 1, 2, 4, 8, 16, 32
     */
    private int zoomSetting = 1;
    /**
     * The value of the longest video's time length in milliseconds
     */
    private long maxEnd;
    /**
     * The value of the earliest video's start time in milliseconds
     */
    private long minStart;
    /**
     * Holds each component used to paint a carriage
     * Key: Track name
     */
    private Map<String, Track> trackPainterMap;
    /**
     * 
     */
    private EventListenerList listenerList;

    public TracksControllerV() {
        // Set default scale values
        maxEnd = 60000;
        minStart = 0;

        listenerList = new EventListenerList();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 295));
        layeredPane.setSize(layeredPane.getPreferredSize());

        // Set up the root panel
        tracksPanel = new JPanel();
        tracksPanel.setLayout(new GridBagLayout());
        tracksPanel.setBackground(Color.WHITE);

        // Menu buttons
        JButton lockButton = new JButton("Lock");
        JButton bookmarkButton = new JButton("Add Bookmark");
        JButton snapButton = new JButton("Snap");

        lockButton.setVisible(false);
        bookmarkButton.setVisible(false);
        snapButton.setVisible(false);

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

        
        final int pad = 3;
        int xOffset = pad * 3;

        lockButton.setSize(lockButton.getPreferredSize());
        lockButton.setLocation(xOffset, 0);
        layeredPane.add(lockButton, new Integer(0));
        xOffset += lockButton.getSize().width + pad;

        bookmarkButton.setSize(bookmarkButton.getPreferredSize());
        bookmarkButton.setLocation(xOffset, 0);
        layeredPane.add(bookmarkButton, new Integer(0));
        xOffset += bookmarkButton.getSize().width + pad;

        snapButton.setSize(snapButton.getPreferredSize());
        snapButton.setLocation(xOffset, 0);
        layeredPane.add(snapButton, new Integer(0));

        zoomOutButton.setSize(zoomOutButton.getPreferredSize());
        xOffset = layeredPane.getSize().width - (pad + pad + zoomOutButton.getSize().width);
        zoomOutButton.setLocation(xOffset, 0);
        layeredPane.add(zoomOutButton, new Integer(0));

        zoomInButton.setSize(zoomInButton.getPreferredSize());
        xOffset -= (pad + zoomInButton.getSize().width);
        zoomInButton.setLocation(xOffset, 0);
        layeredPane.add(zoomInButton, new Integer(0));

        int yOffset = lockButton.getSize().height + pad;

        // Add the timescale
        scale = new TimescalePainter();
        {
            Dimension size = new Dimension();
            size.setSize(785, 35);
            scale.setSize(size);
            scale.setPreferredSize(size);
            scale.setConstraints(minStart, maxEnd, zoomIntervals(1));
            scale.setLocation(10, yOffset);
        }
        layeredPane.add(scale, new Integer(0));

        yOffset += pad + scale.getSize().height;

        // Add the scroll pane

        tracksInfoBox = Box.createHorizontalBox();
        headerBox = Box.createVerticalBox();
        carriageBox = Box.createVerticalBox();
        tracksInfoBox.add(headerBox);
        tracksInfoBox.add(carriageBox);

        tracksInfoPanel = new JPanel();
        tracksInfoPanel.setLayout(new BoxLayout(tracksInfoPanel, BoxLayout.Y_AXIS));
        tracksInfoPanel.add(tracksInfoBox);
        tracksScrollPane = new JScrollPane(tracksInfoPanel);
        tracksScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tracksScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Set an explicit size of the scroll pane
        {
            Dimension size = new Dimension();
            size.setSize(785, 227);
            tracksScrollPane.setSize(size);
            tracksScrollPane.setPreferredSize(size);
            tracksScrollPane.setLocation(10, yOffset);
        }
        layeredPane.add(tracksScrollPane, new Integer(0));

        needle = new NeedlePainter();
        {
            Dimension size = new Dimension();
            size.setSize(765, 248);
            needle.setSize(size);
            needle.setPreferredSize(size);
            // Value determined through trial-and-error.
            needle.setLocation(10, 26);
//            needle.setPadding(lockButton.getSize().height + pad, 101);
            needle.setPadding(0, 101);
            needle.setWindowStart(minStart);
            needle.setWindowEnd(maxEnd);
            needle.setIntervalTime(scale.getIntervalTime());
            needle.setIntervalWidth(scale.getIntervalWidth());
        }
        needle.addNeedleEventListener(this);
        layeredPane.add(needle, new Integer(10));

        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            tracksPanel.add(layeredPane, c);
        }
        
        tracksPanel.validate();

        trackPainterMap = new HashMap<String,Track>();
    }

    /**
     * @return the panel containing the tracks interface.
     */
    public JPanel getTracksPanel() {
        return tracksPanel;
    }

    /**
     * Add a new track to the interface.
     * @param trackName name of the track
     * @param duration the total duration of the track in milliseconds
     * @param offset the amount of playback offset in milliseconds
     */
    public void addNewTrack(String mediaPath, String trackName, long duration,
            long offset) {
        // Check if the scale needs to be updated.
        if (duration + offset > maxEnd) {
            maxEnd = duration + offset;
            rescale();
        }

        // Creates the label used to identify the track
        JLabel trackLabel = new JLabel(trackName);

        // Create the header panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 100;
            infoPanel.setMinimumSize(size);
            infoPanel.setMaximumSize(size);
            infoPanel.setSize(size);
            infoPanel.setPreferredSize(size);
            infoPanel.add(trackLabel);

            headerBox.add(infoPanel);
            headerBox.add(Box.createVerticalStrut(2));
        }

        // Create the carriage panel
        JPanel carriagePanel = new JPanel();
        carriagePanel.setLayout(new BorderLayout());
        carriagePanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

        TrackPainter trackPainter = new TrackPainter();
        {
            Dimension size = new Dimension();
            size.height = 66;
            size.width = 665;
            trackPainter.setPreferredSize(size);
        }

        if (duration == -1) {
            // Case where the track duration could not be determined
            trackPainter.setError(true);
        } else {
            trackPainter.setStart(0);
            trackPainter.setEnd(duration);
            trackPainter.setOffset(offset);
            trackPainter.setIntervalTime(scale.getIntervalTime());
            trackPainter.setIntervalWidth(scale.getIntervalWidth());
            trackPainter.setZoomWindowStart(scale.getStart());
            trackPainter.setZoomWindowEnd(scale.getEnd());
        }
        
        carriagePanel.add(trackPainter, BorderLayout.PAGE_START);
        {
            Dimension size = new Dimension();
            size.height = 70;
            size.width = 665;
            carriagePanel.setMinimumSize(size);
            carriagePanel.setMaximumSize(size);
            carriagePanel.setSize(size);
            carriagePanel.setPreferredSize(size);
            
            carriageBox.add(carriagePanel);
            carriageBox.add(Box.createVerticalStrut(2));
        }

        zoomTracks(null);

        // Record this track's information
        Track track = new Track();
        track.trackPainter = trackPainter;
        track.infoPanel = infoPanel;
        track.carriagePanel = carriagePanel;

        trackPainterMap.put(mediaPath, track);

        tracksPanel.validate();
    }

    /**
     * @param time Set the current time to use.
     */
    public void setCurrentTime(long time) {
        needle.setCurrentTime(time);
        needle.repaint();
    }

    /**
     * Zooms into the displayed scale and re-adjusts the timing needle
     * accordingly.
     * @param evt
     */
    public void zoomInScale(ActionEvent evt) {
        zoomSetting = zoomSetting * 2;
        if (zoomSetting > 32) {
            zoomSetting = 32;
        }

        rescale();
        
        scale.repaint();
        needle.repaint();
    }

    /**
     * Zooms out of the displayed scale and re-adjusts the timing needle 
     * accordingly.
     * @param evt
     */
    public void zoomOutScale(ActionEvent evt) {
        zoomSetting = zoomSetting / 2;
        if (zoomSetting < 1) {
            zoomSetting = 1;
        }

        rescale();
        
        scale.repaint();
        needle.repaint();
    }

    /**
     * Update the track display after a zoom.
     * @param evt
     */
    public void zoomTracks(ActionEvent evt) {
        Iterable<Track> tracks = trackPainterMap.values();
        for (Track track : tracks) {
            TrackPainter tp = track.trackPainter;
            tp.setIntervalTime(scale.getIntervalTime());
            tp.setIntervalWidth(scale.getIntervalWidth());
            tp.setZoomWindowStart(scale.getStart());
            tp.setZoomWindowEnd(scale.getEnd());
            tp.repaint();
        }
        tracksInfoBox.validate();
    }

    /**
     * Remove a track from our tracks panel.
     * @param mediaPath the path to the media file
     */
    public void removeTrack(final String mediaPath) {
        Track removedTrack = trackPainterMap.remove(mediaPath);
        if (removedTrack == null) {
            return;
        }
        // Remove the track from the panel.
        headerBox.remove(removedTrack.infoPanel);
        carriageBox.remove(removedTrack.carriagePanel);

        // Recalculate max zoom window
        Iterable<Track> tracks = trackPainterMap.values();
        maxEnd = 0;
        for (Track track : tracks) {
            TrackPainter tp = track.trackPainter;
            long length = tp.getEnd() + tp.getOffset();
            if (length > maxEnd) {
                maxEnd = length;
            }
        }
        // If there are no more tracks, reset.
        if (maxEnd == 0) {
            maxEnd = 60000;
            zoomSetting = 1;
        }
        // Update zoom window scale
        rescale();
        // Update zoomed tracks
        zoomTracks(null);
        // Update tracks panel display
        tracksPanel.invalidate();
        tracksPanel.repaint();
    }

    /**
     * Removes all track components from this controller and resets components.
     */
    public void removeAll() {
        trackPainterMap.clear();
        headerBox.removeAll();
        carriageBox.removeAll();
        maxEnd = 60000;
        zoomSetting = 1;
        rescale();
        zoomTracks(null);
        needle.setCurrentTime(0);
        needle.setWindowStart(0);
        needle.setWindowEnd(0);
        needle.setIntervalTime(scale.getIntervalTime());
        needle.setIntervalWidth(scale.getIntervalWidth());
        tracksPanel.invalidate();
        tracksPanel.repaint();
    }

    /**
     * @param zoomValue supports 1x, 2x, 4x, 8x, 16x, 32x
     * @return the amount of intervals to show given a zoom value
     */
    private int zoomIntervals(final int zoomValue) {
        assert(zoomValue >= 1);
        assert(zoomValue <= 32);
        if (zoomValue <= 2) {
            return 20;
        }
        if (zoomValue <= 8) {
            return 10;
        }
        if (zoomValue <= 32) {
            return 5;
        }
        // Default amount of zoom intervals
        return 20;
    }

    /**
     * Recalculates timing scale and needle constraints based on the minimum
     * track start time, longest track time, and current zoom setting.
     */
    private void rescale() {
        long range = maxEnd - minStart;
        long mid = range / 2;
        long newStart = mid - (range / zoomSetting / 2);
        long newEnd = mid + (range / zoomSetting / 2);

        if (zoomSetting == 1) {
            newStart = minStart;
            newEnd = maxEnd;
        }

        scale.setConstraints(newStart, newEnd, zoomIntervals(zoomSetting));

        needle.setWindowStart(newStart);
        needle.setWindowEnd(newEnd);
        needle.setIntervalTime(scale.getIntervalTime());
        needle.setIntervalWidth(scale.getIntervalWidth());
    }

    /**
     * NeedlePainter needle was moved using the mouse
     * @param e needle event from the NeedlePainter
     */
    public void needleMoved(NeedleEvent e) {
        fireTracksControllerEvent(e);
    }

    /**
     * Register listeners who are interested in events from this class.
     * @param listener
     */
    public synchronized void addTracksControllerListener(
            TracksControllerListener listener) {
        listenerList.add(TracksControllerListener.class, listener);
    }

    /**
     * De-register listeners from receiving events from this class.
     * @param listener
     */
    public synchronized void removeTracksControllerListener(
            TracksControllerListener listener) {
        listenerList.remove(TracksControllerListener.class, listener);
    }

    /**
     * Used to fire a new event informing listeners about new child component
     * events.
     * @param needleEvent
     */
    private synchronized void fireTracksControllerEvent(NeedleEvent needleEvent) {
        TracksControllerEvent e = new TracksControllerEvent(this, needleEvent);
        Object[] listeners = listenerList.getListenerList();
        /* The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
           if (listeners[i] == TracksControllerListener.class) {
               ((TracksControllerListener)listeners[i+1]).tracksControllerChanged(e);
           }
        }
    }

    /**
     * Inner class used for tracks panel management.
     */
    private class Track {
        public JPanel infoPanel;
        public JPanel carriagePanel;
        public TrackPainter trackPainter;
    }

}
