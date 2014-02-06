/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.plugins.quicktime;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import net.miginfocom.swing.MigLayout;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.id.Identifier;
import org.datavyu.plugins.CustomActions;
import org.datavyu.plugins.CustomActionsAdapter;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.ViewerStateListener;
import org.datavyu.views.DataController;
import org.datavyu.views.DatavyuDialog;
import org.datavyu.views.component.DefaultTrackPainter;
import org.datavyu.views.component.TrackPainter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public abstract class BaseQuickTimeDataViewer extends DatavyuDialog
        implements DataViewer {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(BaseQuickTimeDataViewer.class);

    /**
     * Rate for playback.
     */
    private float playRate;

    /**
     * Frames per second.
     */
    private float fps = -1;

    /**
     * parent controller.
     */
    private DataController parent;

    /**
     * The playback offset of the movie in milliseconds.
     */
    private long offset;

    /**
     * Is the movie currently playing?
     */
    private boolean playing;

    /**
     * The current video file that this viewer is representing.
     */
    private File mediaFile;

    /**
     * Volume slider.
     */
    private JSlider volumeSlider;

    /**
     * Dialog containing volume slider.
     */
    private JDialog volumeDialog;

    /**
     * Volume button.
     */
    private JButton volumeButton;

    /**
     * Resize button.
     */
    private JButton resizeButton;

    /**
     * Stores the desired volume the plugin should play at.
     */
    private float volume = 1f;

    /**
     * Is the plugin visible?
     */
    private boolean isVisible = true;

    /**
     * The original size of the movie when first loaded.
     */
    private Dimension nativeVideoSize;

    /**
     * A context menu for resizing the video.
     */
    private JPopupMenu menuContext = new JPopupMenu();

    /**
     * Menu item for quarter size.
     */
    private JMenuItem menuItemQuarter;

    /**
     * Menu item for half size.
     */
    private JMenuItem menuItemHalf;

    /**
     * Menu item for three quarters size.
     */
    private JMenuItem menuItemThreeQuarters;

    /**
     * Menu item for full size.
     */
    private JMenuItem menuItemFull;

    /**
     * Icon for displaying volume slider.
     */
    private final ImageIcon volumeIcon = new ImageIcon(getClass().getResource(
            "/icons/audio-volume.png"));

    /**
     * Volume slider icon for when the video is hidden (volume is muted).
     */
    private final ImageIcon mutedIcon = new ImageIcon(getClass().getResource(
            "/icons/volume-muted.png"));

    /**
     * Icon for resizing the video.
     */
    private final ImageIcon resizeIcon = new ImageIcon(getClass().getResource(
            "/icons/resize.png"));

    /**
     * The list of listeners interested in changes made to the project.
     */
    private final List<ViewerStateListener> viewerListeners =
            new LinkedList<ViewerStateListener>();

    /**
     * ID of this data viewer.
     */
    private Identifier id;

    /**
     * Custom actions handler.
     */
    private CustomActions actions = new CustomActionsAdapter() {
        @Override
        public AbstractButton getActionButton1() {
            return volumeButton;
        }

        @Override
        public AbstractButton getActionButton2() {
            return resizeButton;
        }

        @Override
        public AbstractButton getActionButton3() {
            return null;
        }
    };


    // ------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public BaseQuickTimeDataViewer(final java.awt.Frame parent,
                                   final boolean modal) {

        super(parent, modal);

        offset = 0;
        playing = false;

        volumeButton = new JButton();
        volumeButton.setIcon(getVolumeButtonIcon());
        volumeButton.setBorderPainted(false);
        volumeButton.setContentAreaFilled(false);
        volumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleActionButtonEvent1(e);
            }
        });

        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 70);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setName("volumeSlider");
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                handleVolumeSliderEvent(e);
            }
        });

        volumeDialog = new JDialog(parent, false);
        volumeDialog.setUndecorated(true);
        volumeDialog.setVisible(false);
        volumeDialog.setLayout(new MigLayout("", "[center]", ""));
        volumeDialog.setSize(50, 125);
        volumeDialog.setName("volumeDialog");
        volumeDialog.getContentPane().add(volumeSlider, "pushx, pushy");
        volumeDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                volumeDialog.setVisible(false);
            }
        });
        volumeDialog.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(final WindowEvent e) {
                volumeDialog.setVisible(false);
            }
        });

        resizeButton = new JButton();
        resizeButton.setIcon(resizeIcon);
        resizeButton.setBorderPainted(false);
        resizeButton.setContentAreaFilled(false);
        resizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                handleActionButtonEvent2(e);
            }
        });

        menuItemQuarter = new JMenuItem("25% size");
        menuItemQuarter.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaleVideo(0.25f);
            }
        });
        menuItemHalf = new JMenuItem("50% size");
        menuItemHalf.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaleVideo(0.5f);
            }
        });
        menuItemThreeQuarters = new JMenuItem("75% size");
        menuItemThreeQuarters.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaleVideo(0.75f);
            }
        });
        menuItemFull = new JMenuItem("100% size");
        menuItemFull.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                scaleVideo(1);
            }
        });
        menuContext.add(menuItemQuarter);
        menuContext.add(menuItemHalf);
        menuContext.add(menuItemThreeQuarters);
        menuContext.add(menuItemFull);
        menuContext.setName("menuContext");

        initComponents();
    }

    private void handleVolumeSliderEvent(final ChangeEvent e) {
        volume = volumeSlider.getValue() / 100F;
        setVolume();
        notifyChange();
    }

    /**
     * Sets the volume of the movie to the level of the slider bar, or to 0
     * if the track is hidden from view (this means hiding the track mutes
     * the volume).
     */
    private void setVolume() {
        setQTVolume(isVisible ? volume : 0F);
        volumeButton.setIcon(getVolumeButtonIcon());
    }

    protected abstract void setQTVolume(float volume);

    // ------------------------------------------------------------------------
    // [interface] org.datavyu.views.continuous.DataViewer
    //

    /**
     * @return The duration of the movie in milliseconds. If -1 is returned, the
     * movie's duration cannot be determined.
     */
    public abstract long getDuration();

    private double getAspectRatio() {
        return (nativeVideoSize != null)
                ? (nativeVideoSize.getWidth() / nativeVideoSize.getHeight()) : 1;
    }

    @Override
    public void validate() {

        // BugzID:753 - Locks the window to the videos aspect ratio.
        int newHeight = getHeight();
        int newWidth = (int) (getVideoHeight() * getAspectRatio())
                + getInsets().left + getInsets().right;
        setSize(newWidth, newHeight);

        super.validate();
    }

    /**
     * Scales the video to the desired ratio.
     *
     * @param scale The new ratio to scale to, where 1.0 = original size, 2.0 = 200% zoom, etc.
     */
    private void scaleVideo(final float scale) {
        int scaleHeight = (int) (nativeVideoSize.getHeight() * scale);

        // lock the aspect ratio
        if (getAspectRatio() > 0.0) {
            int newWidth = (int) (scaleHeight * getAspectRatio())
                    + getInsets().left + getInsets().right;
            int newHeight = scaleHeight + getInsets().bottom + getInsets().top;

            setSize(newWidth, newHeight);
            validate();
        }

        notifyChange();
    }

    public int getVideoHeight() {
        return getHeight() - getInsets().bottom - getInsets().top;
    }

    public int getVideoWidth() {
        return getWidth() - getInsets().left - getInsets().right;
    }

    private void setVideoHeight(final int height) {

        if (!(getAspectRatio() > 0)) {
            return;
        }

        int newWidth = (int) (height * getAspectRatio()) + getInsets().left
                + getInsets().right;
        int newHeight = height + getInsets().bottom + getInsets().top;

        setSize(newWidth, newHeight);
        validate();
    }

    /**
     * @return The playback offset of the movie in milliseconds.
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @param offset The playback offset of the movie in milliseconds.
     */
    public void setOffset(final long offset) {
        this.offset = offset;
    }

    /**
     * @return The parent JDialog that this data viewer resides within.
     */
    public JDialog getParentJDialog() {
        return this;
    }

    @Override
    public void setDataFeed(final File mediaFile) {
        this.mediaFile = mediaFile;
        setTitle(mediaFile.getName());
        setName(getClass().getSimpleName() + "-" + mediaFile.getName());
        pack();
        invalidate();

        // BugzID:679 + 2407: Need to make the window visible before we know the
        // dimensions because of a QTJava bug
        setDataViewerVisible(true);
        setQTDataFeed(mediaFile);

        nativeVideoSize = getQTVideoSize();
        setBounds(getX(), getY(), (int) nativeVideoSize.getWidth(),
                (int) nativeVideoSize.getHeight());
        pack();

        if (fps == -1) {
            fps = getQTFPS();
        } // otherwise we loaded it from the settings

        System.out.println("FPS:");
        System.out.println(fps);
    }

    protected abstract void setQTDataFeed(final File videoFile);

    protected abstract Dimension getQTVideoSize();

    protected abstract float getQTFPS();

    /**
     * @return The file used to display this data feed.
     */
    public File getDataFeed() {
        return mediaFile;
    }

    /**
     * Sets parent data controller.
     *
     * @param dataController The data controller to be set as parent.
     */
    public void setParentController(final DataController dataController) {
        parent = dataController;
    }

    /**
     * @return The frames per second.
     */
    public float getFrameRate() {
        return fps;
    }

    /**
     * {@inheritDoc}
     */
    public void setPlaybackSpeed(final float rate) {
        playRate = rate;
    }

    public float getPlaybackSpeed() {
        return playRate;
    }

    /**
     * {@inheritDoc}
     */
    public void play() {
        playing = true;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        playing = false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void seekTo(final long position);

    /**
     * {@inheritDoc}
     */
    public abstract long getCurrentTime();

    /**
     * {@inheritDoc}
     */
    public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    /**
     * Shows an interface for toggling the playback volume.
     *
     * @see org.datavyu.views.continuous.CustomActionListener
     * #handleActionButtonEvent1(java.awt.event.ActionEvent)
     */
    private void handleActionButtonEvent1(final ActionEvent event) {

        // BugzID:1400 - We don't allow volume changes while the track is
        // hidden from view.
        if (isVisible) {
            volumeDialog.setLocation(volumeButton.getLocationOnScreen());
            volumeDialog.setVisible(true);
        }
    }

    private void handleActionButtonEvent2(final ActionEvent event) {

        if (isVisible) {
            menuContext.show(resizeButton.getParent(), resizeButton.getX(),
                    resizeButton.getY());
        }
    }

    /**
     * Notifies listeners that a change to the project has occurred.
     */
    private void notifyChange() {

        for (ViewerStateListener listener : viewerListeners) {
            listener.notifyStateChanged(null, null);
        }
    }

    public void loadSettings(final InputStream is) {
        Properties settings = new Properties();

        try {
            settings.load(is);

            String property = settings.getProperty("offset");

            if ((property != null) && !property.equals("")) {
                setOffset(Long.parseLong(property));
            }

            property = settings.getProperty("volume");

            if ((property != null) && !property.equals("")) {
                volume = Float.parseFloat(property);
                volumeSlider.setValue((int) (volume * 100));
            }

            property = settings.getProperty("visible");

            if ((property != null) && !property.equals("")) {
                isVisible = Boolean.parseBoolean(property);
                this.setVisible(isVisible);
                setVolume();
            }

            property = settings.getProperty("height");

            if ((property != null) && !property.equals("")) {
                setVideoHeight(Integer.parseInt(property));
            }

            property = settings.getProperty("fps");
            System.out.println(property);
            if ((property != null) && !property.equals("")) {
                System.out.println("LOADING FPS");
                fps = Float.parseFloat(property);
            }


        } catch (IOException e) {
            LOGGER.error("Error loading settings", e);
        }
    }

    public void storeSettings(final OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Float.toString(volume));
        settings.setProperty("visible", Boolean.toString(isVisible));
        settings.setProperty("height", Integer.toString(getVideoHeight()));
        settings.setProperty("fps", Float.toString(fps));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            LOGGER.error("Error saving settings", e);
        }
    }

    @Override
    public void addViewerStateListener(
            final ViewerStateListener vsl) {
        viewerListeners.add(vsl);
    }

    @Override
    public void removeViewerStateListener(
            final ViewerStateListener vsl) {
        viewerListeners.remove(vsl);
    }

    private ImageIcon getVolumeButtonIcon() {

        if (isVisible && (volume > 0)) {
            return volumeIcon;
        } else {
            return mutedIcon;
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pack();
    }

    /**
     * Action to invoke when the QTDataViewer window is being hidden.
     *
     * @param evt The event that triggered this action.
     */
    private void formWindowClosing(final WindowEvent evt) {
        stop();
        volumeDialog.setVisible(false);
        isVisible = false;
        setVolume();
    }

    protected abstract void cleanUp();

    @Override
    public CustomActions getCustomActions() {
        return actions;
    }

    @Override
    public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setDatastore(final Datastore sDB) {
        // not currently needed
    }

    @Override
    public void clearDataFeed() {
        cleanUp();
    }

    @Override
    public void setDataViewerVisible(final boolean isVisible) {
        setVisible(isVisible);
        this.isVisible = isVisible;
        setVolume();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
