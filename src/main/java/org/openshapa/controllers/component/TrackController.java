package org.openshapa.controllers.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import net.miginfocom.swing.MigLayout;

import org.openshapa.event.CarriageEvent;
import org.openshapa.event.CarriageEventListener;
import org.openshapa.event.TrackMouseEventListener;
import org.openshapa.event.CarriageEvent.EventType;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewableModel;
import org.openshapa.models.component.TrackModel.TrackState;
import org.openshapa.views.component.TrackPainter;

/**
 * TrackPainterController is responsible for managing a TrackPainter.
 */
public class TrackController {

    /** View components */
    private transient final JPanel view;
    private transient final JLabel trackLabel;
    private transient final JLabel iconLabel;
    private transient final TrackPainter trackPainter;
    private transient final JPopupMenu menu;
    private transient final JButton lockUnlockButton;
    private transient final ImageIcon unlockIcon =
            new ImageIcon(getClass().getResource("/icons/track-unlock.png"));
    private transient final ImageIcon lockIcon =
            new ImageIcon(getClass().getResource("/icons/track-lock.png"));

    /** Models */
    private final ViewableModel viewableModel;
    private transient final TrackModel trackModel;

    /**
     * Listeners interested in custom playback region events and mouse events on
     * the track
     */
    private transient final EventListenerList listenerList;

    /** States */
    // can the carriage be moved using the mouse when snap is switched on
    private transient boolean isMoveable;

    public TrackController() {
        isMoveable = true;

        view = new JPanel();
        view.setLayout(new MigLayout("ins 0", "[]0[]"));
        view
                .setBorder(BorderFactory.createLineBorder(
                        new Color(73, 73, 73), 1));

        trackPainter = new TrackPainter();

        viewableModel = new ViewableModel();
        trackModel = new TrackModel();
        trackModel.setState(TrackState.NORMAL);
        trackModel.setBookmark(-1);
        trackModel.setLocked(false);

        trackPainter.setViewableModel(viewableModel);
        trackPainter.setTrackModel(trackModel);

        listenerList = new EventListenerList();

        final TrackPainterListener painterListener = new TrackPainterListener();
        trackPainter.addMouseListener(painterListener);
        trackPainter.addMouseMotionListener(painterListener);

        menu = new JPopupMenu();
        JMenuItem setBookmarkMenuItem = new JMenuItem("Set bookmark");
        setBookmarkMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                TrackController.this.setBookmarkAction();
            }
        });
        JMenuItem clearBookmarkMenuItem = new JMenuItem("Clear bookmark");
        clearBookmarkMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                TrackController.this.clearBookmarkAction();
            }
        });
        menu.add(setBookmarkMenuItem);
        menu.add(clearBookmarkMenuItem);

        menu.setName("trackPopUpMenu");

        trackPainter.add(menu);

        // Create the Header panel and its components
        trackLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel = new JLabel("", SwingConstants.CENTER);

        trackLabel.setName("trackLabel");

        final JPanel header = new JPanel(new MigLayout("ins 0, wrap 3"));
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(0, 0, 0, 1, new Color(73, 73, 73)),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        header.setBackground(Color.LIGHT_GRAY);
        header.add(trackLabel, "w 96!, span 3");
        header.add(iconLabel, "span 3, w 96!, h 32!");

        // Set up the button used for locking/unlocking track movement
        lockUnlockButton = new JButton(unlockIcon);
        lockUnlockButton.setContentAreaFilled(false);
        lockUnlockButton.setBorderPainted(false);
        lockUnlockButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                handleLockUnlockButtonEvent(e);
            }
        });
        header.add(lockUnlockButton, "w 20!, h 20!");
        lockUnlockButton.setName("lockUnlockButton");

        view.add(header, "w 100!, h 75!");

        // Create the Carriage panel
        view.add(trackPainter, "w 662!, h 75!");
    }

    /**
     * Sets the track information to use.
     * @param icon
     *            Icon to use with this track. {@code null} if no icon.
     * @param trackName
     *            Name of this track
     * @param trackId
     *            Absolute path to the track's data feed
     * @param duration
     *            Duration of the data feed in milliseconds
     * @param offset
     *            Offset of the data feed in milliseconds
     */
    public void setTrackInformation(final ImageIcon icon,
            final String trackName, final String trackId, final long duration,
            final long offset) {
        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        trackModel.setTrackName(trackName);
        trackModel.setTrackId(trackId);
        trackModel.setDuration(duration);
        trackModel.setOffset(offset);
        trackModel.setErroneous(false);
        trackLabel.setText(trackName);
        trackLabel.setToolTipText(trackName);
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
    public void setErroneous(final boolean erroneous) {
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

    private void setState(final TrackState state) {
        trackModel.setState(state);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * @return True if the track is selected, false otherwise.
     */
    public boolean isSelected() {
        return trackModel.isSelected();
    }

    /**
     * @return True if the track is locked, false otherwise.
     */
    public boolean isLocked() {
        return trackModel.isLocked();
    }

    /**
     * @return Offset in milliseconds.
     */
    public long getOffset() {
        return trackModel.getOffset();
    }

    /**
     * @return Returns the duration of the track in milliseconds. Does not take
     *         into account any offsets.
     */
    public long getDuration() {
        return trackModel.getDuration();
    }

    /**
     * @return Bookmarked position in milliseconds. Does not take into account
     *         any offsets.
     */
    public long getBookmark() {
        return trackModel.getBookmark();
    }

    /**
     * @return track name, i.e. file name.
     */
    public String getTrackName() {
        return trackLabel.getText();
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
        return viewableModel.clone();
    }

    /**
     * Copies the given viewable model
     * 
     * @param viewableModel
     */
    public void setViewableModel(final ViewableModel viewableModel) {
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
        view.repaint();
    }

    /**
     * @return a clone of the track model used by the controller
     */
    public TrackModel getTrackModel() {
        return trackModel.clone();
    }

    /**
     * Set if the track carriage can be moved while the snap functionality is
     * switched on
     * 
     * @param canMove
     */
    public void setMoveable(final boolean canMove) {
        isMoveable = canMove;
    }

    /**
     * Set if the track carriage can be moved
     * 
     * @param lock
     */
    public void setLocked(final boolean lock) {
        trackModel.setLocked(lock);
        if (lock) {
            lockUnlockButton.setIcon(lockIcon);
        } else {
            lockUnlockButton.setIcon(unlockIcon);
        }
    }

    /**
     * Used to request bookmark saving
     */
    public void saveBookmark() {
        fireCarriageBookmarkSaveEvent();
    }

    /**
     * Request a bookmark
     */
    private void setBookmarkAction() {
        fireCarriageBookmarkRequestEvent();
        /*
         * invert the selected state because the menu event generates a click
         * event.
         */
        changeSelected();
    }

    /**
     * Remove the track's bookmark
     */
    private void clearBookmarkAction() {
        trackModel.setBookmark(-1);
        trackPainter.setTrackModel(trackModel);
        /*
         * invert the selected state because the menu event generates a click
         * event.
         */
        changeSelected();
    }

    /**
     * Invert selection state.
     */
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
     * Handles the event for locking and unlocking the track's movement.
     * 
     * @param e
     */
    private void handleLockUnlockButtonEvent(final ActionEvent e) {
        boolean isLocked = trackModel.isLocked();
        isLocked ^= true;
        trackModel.setLocked(isLocked);
        if (isLocked) {
            lockUnlockButton.setIcon(lockIcon);
        } else {
            lockUnlockButton.setIcon(unlockIcon);
        }
    }

    /**
     * Register a mouse listener.
     * 
     * @param listener
     */
    public void addMouseListener(final MouseListener listener) {
        view.addMouseListener(listener);
    }

    /**
     * Remove the mouse listener.
     * 
     * @param listener
     */
    public void removeMouseListener(final MouseListener listener) {
        view.removeMouseListener(listener);
    }

    /**
     * Register the listener to be notified of carriage events
     * 
     * @param listener
     */
    public void addCarriageEventListener(final CarriageEventListener listener) {
        synchronized (this) {
            listenerList.add(CarriageEventListener.class, listener);
        }
    }

    /**
     * Remove the listener from being notified of carriage events
     * 
     * @param listener
     */
    public void removeCarriageEventListener(final CarriageEventListener listener) {
        synchronized (this) {
            listenerList.remove(CarriageEventListener.class, listener);
        }
    }

    /**
     * Register the listener interested in mouse events on the track's carriage.
     * 
     * @param listener
     */
    public void addTrackMouseEventListener(
            final TrackMouseEventListener listener) {
        synchronized (this) {
            listenerList.add(TrackMouseEventListener.class, listener);
        }
    }

    /**
     * Remove the listener from being notified of mouse events on the track's
     * carriage.
     * 
     * @param listener
     */
    public void removeTrackMouseEventListener(
            final TrackMouseEventListener listener) {
        synchronized (this) {
            listenerList.remove(TrackMouseEventListener.class, listener);
        }
    }

    /**
     * Used to inform listeners about a new carriage event
     * 
     * @param newOffset
     * @param temporalPosition
     *            the temporal position of the mouse when the new offset is
     *            triggered
     */
    private void fireCarriageOffsetChangeEvent(final long newOffset,
            final long temporalPosition) {
        synchronized (this) {
            final CarriageEvent e =
                    new CarriageEvent(this, trackModel.getTrackId(), newOffset,
                            trackModel.getBookmark(), trackModel.getDuration(),
                            temporalPosition, EventType.OFFSET_CHANGE);
            final Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1]).offsetChanged(e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about a bookmark request event
     * 
     * @param offset
     */
    private void fireCarriageBookmarkRequestEvent() {
        synchronized (this) {
            final CarriageEvent e =
                    new CarriageEvent(this, trackModel.getTrackId(), trackModel
                            .getOffset(), trackModel.getBookmark(), trackModel
                            .getDuration(), 0, EventType.BOOKMARK_REQUEST);
            final Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1])
                            .requestBookmark(e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about a bookmark request event
     * 
     * @param offset
     */
    private void fireCarriageBookmarkSaveEvent() {
        synchronized (this) {
            final CarriageEvent e =
                    new CarriageEvent(this, trackModel.getTrackId(), trackModel
                            .getOffset(), trackModel.getBookmark(), trackModel
                            .getDuration(), 0, EventType.BOOKMARK_SAVE);
            final Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1]).saveBookmark(e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about track selection event.
     */
    private void fireCarriageSelectionChangeEvent() {
        synchronized (this) {
            final CarriageEvent e =
                    new CarriageEvent(this, trackModel.getTrackId(), trackModel
                            .getOffset(), trackModel.getBookmark(), trackModel
                            .getDuration(), 0, EventType.CARRIAGE_SELECTION);
            final Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1])
                            .selectionChanged(e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about the mouse release event on the track's
     * carriage.
     * 
     * @param e
     */
    private void fireMouseReleasedEvent(final MouseEvent e) {
        synchronized (this) {
            final Object[] listeners = listenerList.getListenerList();
            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == TrackMouseEventListener.class) {
                    ((TrackMouseEventListener) listeners[i + 1])
                            .mouseReleased(e);
                }
            }
        }
    }

    /**
     * Inner listener used to handle mouse events.
     */
    private class TrackPainterListener extends MouseInputAdapter {

        private transient long offsetInit;
        private transient boolean inCarriage;
        private transient int xInit;
        private transient TrackState initialState;

        private transient final Cursor moveCursor =
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        private transient final Cursor defaultCursor =
                Cursor.getDefaultCursor();

        @Override
        public void mouseClicked(final MouseEvent e) {
            if (trackPainter.getCarriagePolygon().contains(e.getPoint())) {
                changeSelected();
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            if (trackPainter.getCarriagePolygon().contains(e.getPoint())) {
                inCarriage = true;
                xInit = e.getX();
                offsetInit = trackModel.getOffset();
                trackPainter.setCursor(moveCursor);
                initialState = trackModel.getState();
            }
            if (e.isPopupTrigger()) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (trackModel.isLocked()) {
                return;
            }
            if (inCarriage) {
                final int xNet = e.getX() - xInit;
                // Calculate the total amount of time we offset by
                final float newOffset =
                        (xNet * 1F) / viewableModel.getIntervalWidth()
                                * viewableModel.getIntervalTime() + offsetInit;
                final float temporalPosition =
                        (e.getX() * 1F) / viewableModel.getIntervalWidth()
                                * viewableModel.getIntervalTime();
                if (isMoveable) {
                    fireCarriageOffsetChangeEvent((long) newOffset,
                            (long) temporalPosition);
                } else {
                    final long threshold =
                            (long) (0.05F * (viewableModel.getZoomWindowEnd() - viewableModel
                                    .getZoomWindowStart()));
                    if (Math.abs(newOffset - offsetInit) >= threshold) {
                        isMoveable = true;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            isMoveable = true;
            inCarriage = false;
            final Component source = (Component) e.getSource();
            source.setCursor(defaultCursor);
            setState(initialState);
            if (e.isPopupTrigger()) {
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
            fireMouseReleasedEvent(e);
        }
    }

}
