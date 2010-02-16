package org.openshapa.controllers.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import net.miginfocom.swing.MigLayout;

import org.openshapa.event.CarriageEvent;
import org.openshapa.event.CarriageEventListener;
import org.openshapa.event.CarriageEvent.EventType;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewableModel;
import org.openshapa.views.component.TrackPainter;

/**
 * TrackPainterController is responsible for managing a TrackPainter.
 */
public class TrackController {
    /** View */
    private TrackPainter trackPainter;
    private PopupMenu menu;
    /** Models */
    private ViewableModel viewableModel;
    private TrackModel trackModel;
    /** Listens to mouse events */
    private TrackPainterListener trackPainterListener;
    /** Listeners interested in custom playback region events */
    private EventListenerList listenerList;

    private JPanel view;

    private JLabel trackLabel;

    public TrackController() {
        view = new JPanel();
        view.setLayout(new MigLayout("ins 0", "[]0[]"));
        // view.setBackground(new Color(65, 65, 65));
        view
                .setBorder(BorderFactory.createLineBorder(
                        new Color(73, 73, 73), 1));

        trackPainter = new TrackPainter();

        viewableModel = new ViewableModel();
        trackModel = new TrackModel();
        trackModel.setBookmark(-1);

        trackPainter.setViewableModel(viewableModel);
        trackPainter.setTrackModel(trackModel);

        listenerList = new EventListenerList();

        trackPainterListener = new TrackPainterListener();
        trackPainter.addMouseListener(trackPainterListener);
        trackPainter.addMouseMotionListener(trackPainterListener);

        menu = new PopupMenu();
        MenuItem setBookmarkMenuItem = new MenuItem("Set bookmark");
        setBookmarkMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TrackController.this.setBookmarkAction();
            }
        });
        MenuItem clearBookmarkMenuItem = new MenuItem("Clear bookmark");
        clearBookmarkMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TrackController.this.clearBookmarkAction();
            }
        });
        menu.add(setBookmarkMenuItem);
        menu.add(clearBookmarkMenuItem);

        trackPainter.add(menu);

        // Create the Header panel
        trackLabel = new JLabel("", SwingConstants.CENTER);
        trackLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                new Color(73, 73, 73)));

        view.add(trackLabel, "w 100!, h 75!");

        // Create the Carriage panel
        view.add(trackPainter, "w 662!, h 75!");
    }

    /**
     * Sets the track information to use.
     * 
     * @param trackId
     *            Absolute path to the track's data feed
     * @param duration
     *            Duration of the data feed in milliseconds
     * @param offset
     *            Offset of the data feed in milliseconds
     */
    public void setTrackInformation(final String trackName,
            final String trackId, final long duration, final long offset) {
        trackModel.setTrackName(trackName);
        trackModel.setTrackId(trackId);
        trackModel.setDuration(duration);
        trackModel.setOffset(offset);
        trackModel.setErroneous(false);
        trackLabel.setText(trackName);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * Sets the track offset in milliseconds.
     * 
     * @param offset
     *            Offset of the data feed in milliseconds
     */
    public void setTrackOffset(final long offset) {
        trackModel.setOffset(offset);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * Indicate that the track's information cannot be resolved.
     * 
     * @param erroneous
     */
    public void setErroneous(boolean erroneous) {
        trackModel.setErroneous(erroneous);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * Add a bookmark location to the track. Does not take track offsets into
     * account.
     * 
     * @param bookmark
     *            bookmark position in milliseconds
     */
    public void addBookmark(final long bookmark) {
        if (0 <= bookmark && bookmark <= trackModel.getDuration()) {
            trackModel.setBookmark(bookmark);
            trackPainter.setTrackModel(trackModel);
        }
    }

    /**
     * Add a bookmark location to the track. Track offsets are taken into
     * account. This call is the same as addBookmark(position - offset).
     * 
     * @param position
     *            temporal position in milliseconds to bookmark.
     */
    public void addTemporalBookmark(final long position) {
        addBookmark(position - trackModel.getOffset());
    }

    /**
     * @return True if the track is selected, false otherwise.
     */
    public boolean isSelected() {
        return trackModel.isSelected();
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    /**
     * @return a clone of the viewable model used by the controller
     */
    public ViewableModel getViewableModel() {
        // return a clone to avoid model tainting
        return (ViewableModel) viewableModel.clone();
    }

    /**
     * Copies the given viewable model
     * 
     * @param viewableModel
     */
    public void setViewableModel(ViewableModel viewableModel) {
        /*
         * Just copy the values, do not spread references all over the place to
         * avoid model tainting.
         */
        this.viewableModel.setEnd(viewableModel.getEnd());
        this.viewableModel.setIntervalTime(viewableModel.getIntervalTime());
        this.viewableModel.setIntervalWidth(viewableModel.getIntervalWidth());
        this.viewableModel.setZoomWindowEnd(viewableModel.getZoomWindowEnd());
        this.viewableModel.setZoomWindowStart(viewableModel
                .getZoomWindowStart());
        trackPainter.setViewableModel(this.viewableModel);
    }

    private void setBookmarkAction() {
        fireCarriageBookmarkRequestEvent();
        /*
         * invert the selected state because the menu event generates a click
         * event.
         */
        changeSelected();
    }

    private void clearBookmarkAction() {
        trackModel.setBookmark(-1);
        trackPainter.setTrackModel(trackModel);
        /*
         * invert the selected state because the menu event generates a click
         * event.
         */
        changeSelected();
    }

    private void changeSelected() {
        if (trackModel.isSelected()) {
            trackModel.setSelected(false);
        } else {
            trackModel.setSelected(true);
        }
        trackPainter.setTrackModel(trackModel);
        fireCarriageSelectionChangeEvent();
    }

    /**
     * Register the listener to be notified of carriage events
     * 
     * @param listener
     */
    public synchronized void addCarriageEventListener(
            CarriageEventListener listener) {
        listenerList.add(CarriageEventListener.class, listener);
    }

    /**
     * Remove the listener from being notified of carriage events
     * 
     * @param listener
     */
    public synchronized void removeCarriageEventListener(
            CarriageEventListener listener) {
        listenerList.remove(CarriageEventListener.class, listener);
    }

    /**
     * Used to inform listeners about a new carriage event
     * 
     * @param offset
     */
    private synchronized void fireCarriageOffsetChangeEvent(long offset) {
        CarriageEvent e =
                new CarriageEvent(this, trackModel.getTrackId(), offset,
                        trackModel.getDuration(), EventType.OFFSET_CHANGE);
        Object[] listeners = listenerList.getListenerList();
        /*
         * The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == CarriageEventListener.class) {
                ((CarriageEventListener) listeners[i + 1]).offsetChanged(e);
            }
        }
    }

    /**
     * Used to inform listeners about a bookmark request event
     * 
     * @param offset
     */
    private synchronized void fireCarriageBookmarkRequestEvent() {
        CarriageEvent e =
                new CarriageEvent(this, trackModel.getTrackId(), trackModel
                        .getOffset(), trackModel.getDuration(),
                        EventType.BOOKMARK_REQUEST);
        Object[] listeners = listenerList.getListenerList();
        /*
         * The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == CarriageEventListener.class) {
                ((CarriageEventListener) listeners[i + 1]).requestBookmark(e);
            }
        }
    }

    private synchronized void fireCarriageSelectionChangeEvent() {
        CarriageEvent e =
                new CarriageEvent(this, trackModel.getTrackId(), trackModel
                        .getOffset(), trackModel.getDuration(),
                        EventType.CARRIAGE_SELECTION);
        Object[] listeners = listenerList.getListenerList();
        /*
         * The listener list contains the listening class and then the listener
         * instance.
         */
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == CarriageEventListener.class) {
                ((CarriageEventListener) listeners[i + 1]).selectionChanged(e);
            }
        }
    }

    /**
     * Inner listener used to handle mouse eventss
     */
    private class TrackPainterListener extends MouseInputAdapter {

        private long offsetInit;
        private boolean inCarriage;
        private int xInit;

        private final Cursor moveCursor =
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        @Override
        public void mouseClicked(MouseEvent e) {
            changeSelected();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (trackPainter.getCarriagePolygon().contains(e.getPoint())) {
                inCarriage = true;
                xInit = e.getX();
                offsetInit = trackModel.getOffset();
                trackPainter.setCursor(moveCursor);
            }
            if (e.isPopupTrigger()) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (inCarriage) {
                int xNet = e.getX() - xInit;
                // Calculate the total amount of time we offset by
                float newOffset =
                        (xNet * 1F) / viewableModel.getIntervalWidth()
                                * viewableModel.getIntervalTime() + offsetInit;
                fireCarriageOffsetChangeEvent(Math.round(newOffset));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            inCarriage = false;
            Component source = (Component) e.getSource();
            source.setCursor(defaultCursor);
            if (e.isPopupTrigger()) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
