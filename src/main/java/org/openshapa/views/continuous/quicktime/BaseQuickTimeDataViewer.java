package org.openshapa.views.continuous.quicktime;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.openshapa.models.db.SimpleDatabase;
import org.openshapa.models.id.Identifier;
import org.openshapa.views.OpenSHAPADialog;
import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.CustomActions;
import org.openshapa.views.continuous.CustomActionsAdapter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.ViewerStateListener;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

public abstract class BaseQuickTimeDataViewer extends OpenSHAPADialog implements DataViewer {
    /** The logger for this class. */
    private Logger logger = UserMetrix.getLogger(getClass());

    /** Rate for playback. */
    private float playRate;

    /** Frames per second. */
    private float fps;

    /** parent controller. */
    private DataController parent;

    /** The playback offset of the movie in milliseconds. */
    private long offset;

    /** Is the movie currently playing? */
    private boolean playing;

    /** The current video file that this viewer is representing. */
    private File videoFile;

    /** Volume slider. */
    private JSlider volumeSlider;

    /** Dialog containing volume slider. */
    private JDialog volumeDialog;

    /** Volume button. */
    private JButton volumeButton;

    /** Visibility button. */
    private JButton visibleButton;

    /** Resize button. */
    private JButton resizeButton;

    /** Stores the desired volume the plugin should play at. */
    private float volume = 1f;

    /** Is the plugin visible? */
    private boolean isVisible = true;

    /** The original size of the movie when first loaded. */
    private Dimension nativeVideoSize;

    /** A context menu for resizing the video. */
    private JPopupMenu menuContext = new JPopupMenu();

    /** Menu item for quarter size. */
    private JMenuItem menuItemQuarter;

    /** Menu item for half size. */
    private JMenuItem menuItemHalf;

    /** Menu item for three quarters size. */
    private JMenuItem menuItemThreeQuarters;

    /** Menu item for full size. */
    private JMenuItem menuItemFull;

    /** Icon for displaying volume slider. */
    private final ImageIcon volumeIcon = new ImageIcon(getClass().getResource(
                "/icons/audio-volume.png"));

    /** Volume slider icon for when the video is hidden (volume is muted). */
    private final ImageIcon mutedIcon = new ImageIcon(getClass().getResource(
                "/icons/volume-muted.png"));

    /** Icon for hiding the video. */
    private final ImageIcon eyeIcon = new ImageIcon(getClass().getResource(
                "/icons/eye.png"));

    /** Icon for showing the video. */
    private final ImageIcon hiddenIcon = new ImageIcon(getClass().getResource(
                "/icons/eye-shut.png"));

    /** Icon for resizing the video. */
    private final ImageIcon resizeIcon = new ImageIcon(getClass().getResource(
                "/icons/resize.png"));

    /** The list of listeners interested in changes made to the project. */
    private final List<ViewerStateListener> viewerListeners =
        new LinkedList<ViewerStateListener>();

    /** ID of this data viewer. */
    private Identifier id;

    /** Custom actions handler. */
    private CustomActions actions = new CustomActionsAdapter() {
            @Override public AbstractButton getActionButton1() {
                return volumeButton;
            }

            @Override public AbstractButton getActionButton2() {
                return visibleButton;
            }

            @Override public AbstractButton getActionButton3() {
                return resizeButton;
            }
        };


    // ------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public BaseQuickTimeDataViewer(final java.awt.Frame parent, final boolean modal) {

        super(parent, modal);

        offset = 0;
        playing = false;
        
        volumeButton = new JButton();
        volumeButton.setIcon(getActionButtonIcon1());
        volumeButton.setBorderPainted(false);
        volumeButton.setContentAreaFilled(false);
        volumeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(final ActionEvent e) {
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
                @Override public void mouseClicked(final MouseEvent e) {
                    volumeDialog.setVisible(false);
                }
            });
        volumeDialog.addWindowFocusListener(new WindowAdapter() {
                @Override public void windowLostFocus(final WindowEvent e) {
                    volumeDialog.setVisible(false);
                }
            });

        visibleButton = new JButton();
        visibleButton.setIcon(eyeIcon);
        visibleButton.setBorderPainted(false);
        visibleButton.setContentAreaFilled(false);
        visibleButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(final ActionEvent e) {
                    handleActionButtonEvent2(e);
                }
            });

        resizeButton = new JButton();
        resizeButton.setIcon(resizeIcon);
        resizeButton.setBorderPainted(false);
        resizeButton.setContentAreaFilled(false);
        resizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(final ActionEvent e) {
                    handleActionButtonEvent3(e);
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
    }

    protected abstract void setQTVolume(float volume);
    
    // ------------------------------------------------------------------------
    // [interface] org.openshapa.views.continuous.DataViewer
    //

    /**
     * @return The duration of the movie in milliseconds. If -1 is returned, the
     *         movie's duration cannot be determined.
     */
    public abstract long getDuration();
    
    private double getAspectRatio() {
    	return nativeVideoSize != null ? (nativeVideoSize.getWidth() / nativeVideoSize.getHeight()) : 1;
    }
    
    @Override public void validate() {

        // BugzID:753 - Locks the window to the videos aspect ratio.
        int newHeight = getHeight();
        int newWidth = (int) (getVideoHeight() * getAspectRatio()) + getInsets().left
            + getInsets().right;
        setSize(newWidth, newHeight);

        super.validate();
    }

    /**
     * Scales the video to the desired ratio.
     * @param scale The new ratio to scale to, where 1.0 = original size, 2.0 = 200% zoom, etc.
     */
    private void scaleVideo(final float scale) {
        int scaleHeight = (int) (nativeVideoSize.getHeight() * scale);

        // lock the aspect ratio
        if (getAspectRatio() > 0.0) {
            int newWidth = (int) (scaleHeight * getAspectRatio()) + getInsets().left
                + getInsets().right;
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
    	
        int newWidth = (int) (height * getAspectRatio()) + getInsets().left + getInsets().right;
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
     * @param offset
     *            The playback offset of the movie in milliseconds.
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

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile
     *            The video file that this viewer is going to display to the
     *            user.
     */
    public void setDataFeed(final File videoFile) {
        this.videoFile = videoFile;
        setQTDataFeed(videoFile);
        nativeVideoSize = getQTVideoSize();
        fps = getQTFPS();
        
        setTitle(videoFile.getName());
        setName(getClass().getSimpleName() + "-" + videoFile.getName());
        pack();
        setVisible(true);
        setBounds(getX(), getY(), (int) nativeVideoSize.getWidth(), (int) nativeVideoSize.getHeight());
    }
    
    protected abstract void setQTDataFeed(final File videoFile);
    protected abstract Dimension getQTVideoSize();
    protected abstract float getQTFPS();

    /**
     * @return The file used to display this data feed.
     */
    public File getDataFeed() {
        return videoFile;
    }

    /**
     * Sets parent data controller.
     *
     * @param dataController
     *            The data controller to be set as parent.
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
     * @see org.openshapa.views.continuous.CustomActionListener
     *      #handleActionButtonEvent1(java.awt.event.ActionEvent)
     */
    private void handleActionButtonEvent1(final ActionEvent event) {

        // BugzID:1400 - We don't allow volume changes while the track is
        // hidden from view.
        if (isVisible) {

            // Show the volume frame.
            volumeDialog.setVisible(true);
            volumeDialog.setLocation(volumeButton.getLocationOnScreen());
        }
    }

    private void handleActionButtonEvent2(final ActionEvent event) {
        isVisible = !isVisible;
        this.setVisible(isVisible);

        visibleButton.setIcon(getActionButtonIcon2());

        notifyChange();

    }

    private void handleActionButtonEvent3(final ActionEvent event) {

        if (isVisible) {
            menuContext.show(resizeButton.getParent(), resizeButton.getX(),
                resizeButton.getY());
        }
    }

    /** Notifies listeners that a change to the project has occurred. */
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


        } catch (IOException e) {
            logger.error("Error loading settings", e);
        }
    }

    public void storeSettings(final OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Float.toString(volume));
        settings.setProperty("visible", Boolean.toString(isVisible));
        settings.setProperty("height", Integer.toString(getVideoHeight()));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            logger.error("Error saving settings", e);
        }
    }

    @Override public void addViewerStateListener(
        final ViewerStateListener vsl) {
        viewerListeners.add(vsl);
    }

    @Override public void removeViewerStateListener(
        final ViewerStateListener vsl) {
        viewerListeners.remove(vsl);
    }

    private ImageIcon getActionButtonIcon1() {
        if (isVisible && (volume > 0)) {
            return volumeIcon;
        } else {
            return mutedIcon;
        }
    }

    private ImageIcon getActionButtonIcon2() {
        if (isVisible) {
            return eyeIcon;
        } else {
            return hiddenIcon;
        }
    }

    // ------------------------------------------------------------------------
    // [generated]
    //

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(
                    final java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
            });

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the QTDataViewer window is closing (clean itself
     * up).
     *
     * @param evt
     *            The event that triggered this action.
     */
    private void formWindowClosing(final java.awt.event.WindowEvent evt) { // GEN-FIRST:event_formWindowClosing
        stop();
        cleanUp();
        volumeDialog.setVisible(false);
        parent.shutdown(this);
    }

    protected abstract void cleanUp();
    
    @Override public CustomActions getCustomActions() {
        return actions;
    }

    @Override public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setSimpleDatabase(SimpleDatabase sDB) {
    	// not currently needed
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
