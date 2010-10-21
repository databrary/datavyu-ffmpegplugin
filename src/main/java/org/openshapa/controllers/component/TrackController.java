package org.openshapa.controllers.component;

import com.google.common.collect.Maps;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.text.StrSubstitutor;

import org.openshapa.OpenSHAPA;

import org.openshapa.event.component.CarriageEvent;
import org.openshapa.event.component.CarriageEventListener;
import org.openshapa.event.component.TrackMouseEventListener;
import org.openshapa.event.component.CarriageEvent.EventType;

import org.openshapa.models.component.MixerView;
import org.openshapa.models.component.TrackConstants;
import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.Viewport;
import org.openshapa.models.component.TrackModel.TrackState;
import org.openshapa.models.id.Identifier;

import org.openshapa.plugins.CustomActions;
import org.openshapa.plugins.ViewerStateListener;

import org.openshapa.views.component.TrackPainter;


/**
 * TrackPainterController is responsible for managing a TrackPainter.
 */
public final class TrackController implements ViewerStateListener,
    PropertyChangeListener {

    /** The UserMetrix logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(
            TrackController.class);

    /** Main panel holding the track UI. */
    private final JPanel view;

    /** Header block. */
    private final JPanel header;

    /** Track label. */
    private final JLabel trackLabel;

    /** Label holding the icon. */
    private final JLabel iconLabel;

    /** Component that paints the track. */
    private final TrackPainter trackPainter;

    /** Right click menu. */
    private final JPopupMenu menu;

    /** Button for (un)locking the track. */
    private final JButton lockUnlockButton;

    /** Button for unloading the track (and its associated plugin). */
    private final JButton rubbishButton;

    /** Button for hiding or showing the data viewer. */
    private final JButton visibleButton;

    /** Viewable model. */
    private final MixerView mixer;

    /** Track model. */
    private final TrackModel trackModel;

    /**
     * Listeners interested in custom playback region events and mouse events on
     * the track.
     */
    private final EventListenerList listenerList;

    /** States. */
    // can the carriage be moved using the mouse when snap is switched on
    private boolean isMoveable;

    private boolean isViewerVisible = true;


    /**
     * Creates a new TrackController.
     *
     * @param trackPainter the track painter for this controller to manage.
     */
    public TrackController(final MixerView mixer,
        final TrackPainter trackPainter) {
        isMoveable = true;

        view = new JPanel();
        view.setLayout(new MigLayout("fillx, ins 0", "[]0[]"));
        view.setBorder(BorderFactory.createLineBorder(
                TrackConstants.BORDER_COLOR, 1));

        this.trackPainter = trackPainter;

        this.mixer = mixer;
        trackModel = new TrackModel();
        trackModel.setState(TrackState.NORMAL);
        trackModel.setBookmark(-1);
        trackModel.setLocked(false);

        trackPainter.setMixerView(mixer);
        trackPainter.setTrackModel(trackModel);

        mixer.addPropertyChangeListener(this);

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
        trackLabel.setName("trackLabel");
        trackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trackLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);


        header = new JPanel(new MigLayout("ins 0, wrap 6"));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1,
                    TrackConstants.BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        header.setBackground(Color.LIGHT_GRAY);

        // Normally I would use pushx instead of defining the width, but in this
        // case I defined the width because span combined with push makes the
        // first action icon cell push out as well. 136 was calculated from
        // 140 pixels minus 2 minus 2 (from the empty border defined above).
        header.add(trackLabel, "span 6, w 136!, center, growx");
        header.add(iconLabel, "span 6, w 136!, h 32!, center, growx");

        // Set up the button used for locking/unlocking track movement
        {
            lockUnlockButton = new JButton(TrackConstants.UNLOCK_ICON);
            lockUnlockButton.setName("lockUnlockButton");
            lockUnlockButton.setContentAreaFilled(false);
            lockUnlockButton.setBorderPainted(false);
            lockUnlockButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent e) {
                        handleLockUnlockButtonEvent(e);
                    }
                });

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("width",
                Integer.toString(TrackConstants.ACTION_BUTTON_WIDTH));
            constraints.put("height",
                Integer.toString(TrackConstants.ACTION_BUTTON_HEIGHT));

            String template = "cell 0 2, w ${width}!, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            header.add(lockUnlockButton, sub.replace(template));
        }

        // Set up the button used for hiding/showing a track's data viewer
        {
            visibleButton = new JButton(TrackConstants.VIEWER_HIDE_ICON);
            visibleButton.setName("visibleButton");
            visibleButton.setContentAreaFilled(false);
            visibleButton.setBorderPainted(false);
            visibleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent e) {
                        handleVisibleButtonEvent(e);
                    }
                });

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("width",
                Integer.toString(TrackConstants.ACTION_BUTTON_WIDTH));
            constraints.put("height",
                Integer.toString(TrackConstants.ACTION_BUTTON_HEIGHT));

            String template = "cell 1 2, w ${width}!, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            header.add(visibleButton, sub.replace(template));
        }

        // Set up the button used for removing a track and its plugin
        {
            rubbishButton = new JButton(TrackConstants.DELETE_ICON);
            rubbishButton.setName("rubbishButton");
            rubbishButton.setContentAreaFilled(false);
            rubbishButton.setBorderPainted(false);
            rubbishButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent e) {
                        handleDeleteButtonEvent(e);
                    }
                });

            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("width",
                Integer.toString(TrackConstants.ACTION_BUTTON_WIDTH));
            constraints.put("height",
                Integer.toString(TrackConstants.ACTION_BUTTON_HEIGHT));

            String template = "cell 5 2, w ${width}!, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            header.add(rubbishButton, sub.replace(template));
        }

        // Add the header to our layout.
        {
            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("width",
                Integer.toString(TrackConstants.HEADER_WIDTH));
            constraints.put("height",
                Integer.toString(TrackConstants.CARRIAGE_HEIGHT));

            String template = "w ${width}!, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            view.add(header, sub.replace(template));
        }

        // Add the track carriage to our layout.
        {
            Map<String, String> constraints = Maps.newHashMap();
            constraints.put("height",
                Integer.toString(TrackConstants.CARRIAGE_HEIGHT));

            String template = "pushx, growx, h ${height}!";
            StrSubstitutor sub = new StrSubstitutor(constraints);

            view.add(trackPainter, sub.replace(template));
        }

        view.validate();
    }

    /**
     * Sets the track information to use.
     *
     *@param id
     *            Identifier to use.
     * @param icon
     *            Icon to use with this track. {@code null} if no icon.
     * @param trackName
     *            Name of this track
     * @param trackPath
     *            Absolute path to the track's data feed
     * @param duration
     *            Duration of the data feed in milliseconds
     * @param offset
     *            Offset of the data feed in milliseconds
     */
    public void setTrackInformation(final Identifier id, final ImageIcon icon,
        final String trackName, final String trackPath, final long duration,
        final long offset) {

        if (icon != null) {
            iconLabel.setIcon(icon);
        }

        trackModel.setId(id);
        trackModel.setTrackName(trackName);
        trackModel.setMediaPath(trackPath);
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

    private ImageIcon getVisibleButtonIcon() {

        if (isViewerVisible) {
            return TrackConstants.VIEWER_HIDE_ICON;
        } else {
            return TrackConstants.VIEWER_SHOW_ICON;
        }
    }

    /**
     * Indicate that the track's information cannot be resolved.
     *
     * @param erroneous true if the data is erroneous, false otherwise.
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

        if ((0 <= bookmark) && (bookmark <= trackModel.getDuration())) {
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
     * Sets the state of the track model.
     *
     * @param state the new state to set.
     */
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
     * @return a clone of the track model used by the controller
     */
    public TrackModel getTrackModel() {
        return trackModel.copy();
    }

    /**
     * Set if the track carriage can be moved while the snap functionality is
     * switched on.
     *
     * @param canMove true if the carriage can be moved, false otherwise.
     */
    public void setMoveable(final boolean canMove) {
        isMoveable = canMove;
    }

    /**
     * Set if the track carriage can be moved.
     *
     * @param lock true if the carriage is locked, false otherwise.
     */
    public void setLocked(final boolean lock) {
        trackModel.setLocked(lock);

        if (lock) {
            lockUnlockButton.setIcon(TrackConstants.LOCK_ICON);
        } else {
            lockUnlockButton.setIcon(TrackConstants.UNLOCK_ICON);
        }
    }

    /**
     * Used to request bookmark saving.
     */
    public void saveBookmark() {
        fireCarriageBookmarkSaveEvent();
    }

    public void deselect() {
        trackModel.setSelected(false);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * When the viewer tells us that the state of the project should change,
     * tell OpenSHAPA to update the projectChanged state.
     */
    @Override public void notifyStateChanged(final String propertyChanged,
        final String newValue) {

        if (propertyChanged != null) {

            // Determine if we can handle the requested change
            boolean handled = false;
            String property = propertyChanged.toLowerCase();

            // FIXME empty property names are not allowed because we can't
            // create nameless variables. null properties are allowed but used
            // to represent multiple property changes.
            if (property.equals("")) {
                handled = true;
            }

            // FIXME this is hackish and it couples the plugins directly to
            // how this method works. Maybe a new data controller interface
            // method is needed. No other plugin developers are aware that
            // we can change the duration of a media file.
            if (property.equals("duration")) {
                handled = true;

                Long val = null;

                try {
                    val = Long.parseLong(newValue);
                } catch (NumberFormatException ex) {
                    LOGGER.error("Error in format of long value: " + newValue);
                    handled = false;
                }

                if (val != null) {

                    trackModel.setDuration(val);
                    view.repaint();
                    OpenSHAPA.getDataController().updateMaxViewerDuration();
                    OpenSHAPA.getDataController().getMixerController()
                        .clearRegionAndZoomOut();
                }
            }

            // FIXME this is not an error, property change listeners should just
            // ignore properties they are not interested in.
            if (!handled) {

                // We couldn't find a way to handle the change- report this.
                LOGGER.error("Unhandled property change: notified update of "
                    + propertyChanged + " to " + newValue);
            }
        }

        // FIXME move interface method into project controller?
        OpenSHAPA.getProjectController().projectChanged();
    }

    public void bindTrackActions(final CustomActions actions) {
        Runnable edtTask = new Runnable() {
                @Override public void run() {

                    Map<String, String> constraints = Maps.newHashMap();
                    constraints.put("width",
                        Integer.toString(TrackConstants.ACTION_BUTTON_WIDTH));
                    constraints.put("height",
                        Integer.toString(TrackConstants.ACTION_BUTTON_HEIGHT));

                    String template = "w ${width}!, h ${height}!";
                    StrSubstitutor sub = new StrSubstitutor(constraints);
                    String cons = sub.replace(template);

                    if (actions.getActionButton1() != null) {
                        header.add(actions.getActionButton1(),
                            cons + ", cell 2 2");
                    }

                    if (actions.getActionButton2() != null) {
                        header.add(actions.getActionButton2(),
                            cons + ", cell 3 2");
                    }

                    if (actions.getActionButton3() != null) {
                        header.add(actions.getActionButton3(),
                            cons + ", cell 4 2");
                    }

                    header.validate();
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }
    }

    /**
     * Request a bookmark.
     */
    private void setBookmarkAction() {
        fireCarriageBookmarkRequestEvent();
    }

    /**
     * Remove the track's bookmark.
     */
    private void clearBookmarkAction() {
        trackModel.setBookmark(-1);
        trackPainter.setTrackModel(trackModel);
    }

    /**
     * Invert selection state.
     *
     * @param hasModifiers true if modifiers were held down, false otherwise.
     */
    private void changeSelected(final boolean hasModifiers) {

        if (trackModel.isSelected()) {
            trackModel.setSelected(false);
        } else {
            trackModel.setSelected(true);
        }

        trackPainter.setTrackModel(trackModel);
        fireCarriageSelectionChangeEvent(hasModifiers);
    }

    /**
     * Handles the event for locking and unlocking the track's movement.
     *
     * @param e event to handle.
     */
    private void handleLockUnlockButtonEvent(final ActionEvent e) {
        boolean isLocked = trackModel.isLocked();
        isLocked ^= true;
        trackModel.setLocked(isLocked);

        setLocked(isLocked);

        fireLockStateChangedEvent();
    }

    /**
     * Handles the event for removing a track with the rubbish bin button.
     * @param e The event to handle.
     */
    private void handleDeleteButtonEvent(final ActionEvent e) {
        OpenSHAPA.getDataController().shutdown(trackModel.getId());
    }

    /**
     * Handles the event for hiding/showing a data viewer with the eye button.
     * @param e The event to handle.
     */
    private void handleVisibleButtonEvent(final ActionEvent e) {
        isViewerVisible = !isViewerVisible;

        OpenSHAPA.getDataController().setDataViewerVisibility(trackModel
            .getId(), isViewerVisible);

        visibleButton.setIcon(getVisibleButtonIcon());
    }

    /**
     * Register a mouse listener.
     *
     * @param listener listener to register.
     */
    public void addMouseListener(final MouseListener listener) {

        synchronized (this) {
            view.addMouseListener(listener);
        }
    }

    /**
     * Remove the mouse listener.
     *
     * @param listener listener to remove.
     */
    public void removeMouseListener(final MouseListener listener) {

        synchronized (this) {
            view.removeMouseListener(listener);
        }
    }

    /**
     * Register the listener to be notified of carriage events.
     *
     * @param listener listener to register.
     */
    public void addCarriageEventListener(final CarriageEventListener listener) {

        synchronized (this) {
            listenerList.add(CarriageEventListener.class, listener);
        }
    }

    /**
     * Remove the listener from being notified of carriage events.
     *
     * @param listener listener to remove.
     */
    public void removeCarriageEventListener(
        final CarriageEventListener listener) {

        synchronized (this) {
            listenerList.remove(CarriageEventListener.class, listener);
        }
    }

    /**
     * Register the listener interested in mouse events on the track's carriage.
     *
     * @param listener listener to register.
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
     * @param listener listener to remove.
     */
    public void removeTrackMouseEventListener(
        final TrackMouseEventListener listener) {

        synchronized (this) {
            listenerList.remove(TrackMouseEventListener.class, listener);
        }
    }

    /**
     * Used to inform listeners about a new carriage event.
     *
     * @param newOffset the new offset to inform listeners about.
     * @param temporalPosition
     *            the temporal position of the mouse when the new offset is
     *            triggered
     * @param hasModifiers true if modifiers were held down, false otherwise.
     */
    private void fireCarriageOffsetChangeEvent(final long newOffset,
        final long temporalPosition, final boolean hasModifiers) {

        synchronized (this) {
            final CarriageEvent e = new CarriageEvent(this, trackModel.getId(),
                    newOffset, trackModel.getBookmark(),
                    trackModel.getDuration(), temporalPosition,
                    EventType.OFFSET_CHANGE, hasModifiers);
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
     * Used to inform listeners about a bookmark request event.
     */
    private void fireCarriageBookmarkRequestEvent() {

        synchronized (this) {
            final CarriageEvent e = new CarriageEvent(this, trackModel.getId(),
                    trackModel.getOffset(), trackModel.getBookmark(),
                    trackModel.getDuration(), 0, EventType.BOOKMARK_REQUEST,
                    false);
            final Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1]).requestBookmark(
                        e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about a bookmark request event.
     */
    private void fireCarriageBookmarkSaveEvent() {

        synchronized (this) {
            final CarriageEvent e = new CarriageEvent(this, trackModel.getId(),
                    trackModel.getOffset(), trackModel.getBookmark(),
                    trackModel.getDuration(), 0, EventType.BOOKMARK_SAVE,
                    false);
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
     *
     * @param hasModifiers true if modifiers were held down, false otherwise.
     */
    private void fireCarriageSelectionChangeEvent(final boolean hasModifiers) {

        synchronized (this) {
            final CarriageEvent e = new CarriageEvent(this, trackModel.getId(),
                    trackModel.getOffset(), trackModel.getBookmark(),
                    trackModel.getDuration(), 0, EventType.CARRIAGE_SELECTION,
                    hasModifiers);
            final Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1]).selectionChanged(
                        e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about lock state change event.
     */
    private void fireLockStateChangedEvent() {

        synchronized (this) {
            final CarriageEvent e = new CarriageEvent(this, trackModel.getId(),
                    trackModel.getOffset(), trackModel.getBookmark(),
                    trackModel.getDuration(), 0, EventType.CARRIAGE_LOCK,
                    false);

            final Object[] listeners = listenerList.getListenerList();

            /*
             * The listener list contains the listening class and then the
             * listener instance.
             */
            for (int i = 0; i < listeners.length; i += 2) {

                if (listeners[i] == CarriageEventListener.class) {
                    ((CarriageEventListener) listeners[i + 1]).lockStateChanged(
                        e);
                }
            }
        }
    }

    /**
     * Used to inform listeners about the mouse release event on the track's
     * carriage.
     *
     * @param e the event to handle.
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
                    ((TrackMouseEventListener) listeners[i + 1]).mouseReleased(
                        e);
                }
            }
        }
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {

        if (Viewport.NAME.equals(evt.getPropertyName())) {
            view.repaint();
        }
    }

    public void attachAsWindowListener() {
        OpenSHAPA.getDataController().bindWindowListenerToDataViewer(
            trackModel.getId(), new WindowAdapter() {

                @Override public void windowClosing(final WindowEvent e) {
                    isViewerVisible = false;
                    visibleButton.setIcon(getVisibleButtonIcon());
                }

            });

    }

    /**
     * Calculates the time threshold below which data tracks will snap into place. 
     *   
     * @param viewport current viewport
     * @return snapping threshold in time units (milliseconds);
     */
    public static long calculateSnappingThreshold(final Viewport viewport) {
        final long MINIMUM_THRESHOLD_MILLISECONDS = 10;
        return Math.max((long) Math.ceil(0.01F * viewport.getViewDuration()), MINIMUM_THRESHOLD_MILLISECONDS);
    }
    
    /**
     * Inner listener used to handle mouse events.
     */
    private class TrackPainterListener extends MouseInputAdapter {

        /** Initial offset value. */
        private long offsetInit;

        /** Is the mouse in the carriage. */
        private boolean inCarriage;

        /** Initial x-coord position. */
        private int xInit;

        /** Initial track state. */
        private TrackState initialState;

        /** Mouse cursor for moving. */
        private final Cursor moveCursor = Cursor.getPredefinedCursor(
                Cursor.MOVE_CURSOR);

        /** Default mouse cursor. */
        private final Cursor defaultCursor = Cursor.getDefaultCursor();

        private Viewport viewport;

        @Override public void mouseClicked(final MouseEvent e) {

            if (trackPainter.getCarriagePolygon().contains(e.getPoint())) {
                final boolean hasModifiers = e.isAltDown() || e.isAltGraphDown()
                    || e.isControlDown() || e.isMetaDown() || e.isShiftDown();
                changeSelected(hasModifiers);
            }
        }

        @Override public void mousePressed(final MouseEvent e) {
            viewport = mixer.getViewport();

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

        @Override public void mouseDragged(final MouseEvent e) {

            if (trackModel.isLocked()) {
                return;
            }

            final boolean hasModifiers = e.isAltDown() || e.isAltGraphDown()
                || e.isControlDown() || e.isMetaDown() || e.isShiftDown();

            if (inCarriage) {
                final int xNet = e.getX() - xInit;

                // Calculate the total amount of time we offset by
                final double newOffset = viewport.computeTimeFromXOffset(xNet) + offsetInit;
                final long temporalPosition = viewport.computeTimeFromXOffset(e.getX()) + viewport.getViewStart();

                if (isMoveable) {
                    fireCarriageOffsetChangeEvent((long) newOffset, temporalPosition, hasModifiers);
                } else {
                	final long threshold = calculateSnappingThreshold(viewport);
                    if (Math.abs(newOffset - offsetInit) >= threshold) {
                        isMoveable = true;
                    }
                }
            }
        }

        @Override public void mouseReleased(final MouseEvent e) {
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
