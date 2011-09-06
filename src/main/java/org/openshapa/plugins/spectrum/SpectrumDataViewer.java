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
package org.openshapa.plugins.spectrum;

import java.awt.Container;
import java.awt.Frame;
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

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.openshapa.models.id.Identifier;

import org.openshapa.plugins.spectrum.engine.PlaybackEngine;
import org.openshapa.plugins.spectrum.swing.AmplitudeTrack;
import org.openshapa.plugins.spectrum.swing.SpectrumDialog;

import org.openshapa.views.component.TrackPainter;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import org.openshapa.models.db.Datastore;

import org.openshapa.plugins.CustomActions;
import org.openshapa.plugins.CustomActionsAdapter;
import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.ViewerStateListener;

import org.openshapa.views.DataController;


/**
 * Data viewer for audio spectrum.
 */
public final class SpectrumDataViewer implements DataViewer {

    private static final Logger LOGGER = UserMetrix.getLogger(
            SpectrumDataViewer.class);

    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 100;

    private static final Icon VOL_NORMAL = new ImageIcon(
            SpectrumDataViewer.class.getResource("/icons/audio-volume.png"));
    private static final Icon VOL_MUTED = new ImageIcon(SpectrumDataViewer.class
            .getResource("/icons/volume-muted.png"));

    /** ID of the data viewer. */
    private Identifier id;

    /** Main data viewer dialog. */
    private SpectrumDialog dialog;

    /** Volume dialog. */
    private JDialog volDialog;

    /** Volume slider. */
    private JSlider volSlider;

    /** Volume button. */
    private JButton volButton;

    /** Track. */
    private AmplitudeTrack track;

    /** Media file being visualized. */
    private File mediaFile;

    /** Playback offset. */
    private long playbackOffset;

    /** Duration of media file in milliseconds. */
    private long duration;

    /** Viewer volume before a mute. */
    private int preMuteVol;

    /** Playback engine. */
    private PlaybackEngine engine;

    /** Custom actions handler. */
    private CustomActions actions = new CustomActionsAdapter() {
            @Override public AbstractButton getActionButton1() {
                return volButton;
            }
        };

    private List<ViewerStateListener> viewerListeners;

    public SpectrumDataViewer(final Frame parent, final boolean modal) {
        viewerListeners = new ArrayList<ViewerStateListener>();

        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    track = new AmplitudeTrack();

                    dialog = new SpectrumDialog(parent, modal);
                    dialog.setDefaultCloseOperation(
                        WindowConstants.HIDE_ON_CLOSE);

                    // BugzID:2431 - Mute when viewer is hidden.
                    dialog.addWindowListener(new WindowAdapter() {
                            public void windowClosing(final WindowEvent evt) {
                                muteVolume();
                            }
                        });

                    volButton = new JButton();
                    volButton.setIcon(VOL_NORMAL);
                    volButton.setBorderPainted(false);
                    volButton.setContentAreaFilled(false);
                    volButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(
                                final ActionEvent e) {
                                handleActionButtonEvent1(e);
                            }
                        });

                    volDialog = new JDialog(parent, false);
                    volDialog.setUndecorated(true);
                    volDialog.setVisible(false);
                    volDialog.addMouseListener(new MouseAdapter() {
                            @Override public void mouseClicked(
                                final MouseEvent e) {
                                volDialog.setVisible(false);
                            }
                        });
                    volDialog.addWindowFocusListener(new WindowAdapter() {
                            @Override public void windowLostFocus(
                                final WindowEvent e) {
                                volDialog.setVisible(false);
                            }
                        });

                    Container c = volDialog.getContentPane();
                    c.setLayout(new MigLayout("wrap 1", "[center]", ""));
                    c.add(new JLabel("Volume"));

                    JButton maxVol = new JButton();
                    maxVol.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(
                                final ActionEvent e) {
                                handleMaxVol(e);
                            }
                        });

                    {
                        URL iconURL = getClass().getResource(
                                "/icons/spectrum/volume-high.png");
                        maxVol.setIcon(new ImageIcon(iconURL));
                    }

                    c.add(maxVol, "w 48!, h 48!");

                    volSlider = new JSlider(JSlider.VERTICAL, MIN_VOLUME,
                            MAX_VOLUME, MAX_VOLUME);
                    volSlider.setMajorTickSpacing(10);
                    volSlider.setPaintTicks(true);
                    volSlider.setName("SpectrumVolumeSlider");
                    volSlider.addChangeListener(new ChangeListener() {
                            public void stateChanged(final ChangeEvent e) {
                                handleVolumeSliderEvent(e);
                            }
                        });
                    c.add(volSlider, "h 125!, w 48!");

                    JButton muteVol = new JButton();
                    muteVol.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(
                                final ActionEvent e) {
                                muteVolume();
                            }
                        });

                    {
                        URL iconURL = getClass().getResource(
                                "/icons/spectrum/volume-muted.png");
                        muteVol.setIcon(new ImageIcon(iconURL));
                    }

                    c.add(muteVol, "w 48!, h 48!");

                    volDialog.pack();
                    volDialog.setResizable(false);
                }
            };

        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            SwingUtilities.invokeLater(edtTask);
        }
    }

    @Override public long getCurrentTime() throws Exception {
        return engine.getCurrentTime();
    }

    @Override public File getDataFeed() {
        return mediaFile;
    }

    @Override public long getDuration() {
        return duration;
    }

    @Override public float getFrameRate() {

        // TODO review this API.
        return SpectrumConstants.FPS;
    }

    @Override public long getOffset() {
        return playbackOffset;
    }

    /**
     * Must be called from the EDT.
     */
    @Override public JDialog getParentJDialog() {

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Should be in EDT.");
        }

        return dialog;
    }

    /**
     * Must be called from the EDT.
     */
    @Override public TrackPainter getTrackPainter() {

        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Should be in EDT.");
        }

        return track;
    }

    @Override public boolean isPlaying() {
        return (engine != null) && engine.isPlaying();
    }

    @Override public void play() {

        if (engine != null) {
            setVolume();
            engine.startPlayback();
        }
    }

    @Override public void seekTo(final long time) {

        if (engine != null) {
            engine.seek(time);
        }
    }

    @Override public void setDataFeed(final File file) {
        mediaFile = file;

        // Find number of audio channels first.
        final int channels = SpectrumUtils.getNumChannels(file);

        // Record media duration and audio FPS
        duration = SpectrumUtils.getDuration(file);

        // Get the engine up and running.
        engine = new PlaybackEngine(mediaFile, dialog);
        engine.setMediaLength(duration);
        engine.start();

        // Show the dialog, set up the track.
        Runnable edtTask = new Runnable() {
                @Override public void run() {
                    setVolume();

                    if (dialog != null) {
                        dialog.setVisible(true);
                        dialog.setTitle("Spectrum - " + file.getName());
                    }

                    track.setMedia(mediaFile, channels);
                    track.repaint();
                }
            };

        SwingUtilities.invokeLater(edtTask);
    }

    @Override public void setOffset(final long offset) {
        playbackOffset = offset;
    }

    @Override public void setParentController(final DataController dataC) {
    }

    @Override public void setPlaybackSpeed(final float speed) {

        if (engine != null) {
            engine.adjustSpeed(speed);
        }
    }

    @Override public void stop() {

        if (engine != null) {
            engine.stopPlayback();
        }
    }

    @Override public void addViewerStateListener(
        final ViewerStateListener listener) {

        synchronized (this) {
            viewerListeners.add(listener);
        }
    }

    @Override public void removeViewerStateListener(
        final ViewerStateListener vsl) {

        synchronized (this) {
            viewerListeners.remove(vsl);
        }
    }

    @Override public void loadSettings(final InputStream is) {
        Properties settings = new Properties();

        try {
            settings.load(is);

            String property = settings.getProperty("offset");

            if ((property != null) && !"".equals(property)) {
                setOffset(Long.parseLong(property));
            }

            property = settings.getProperty("volume");

            if ((property != null) && !"".equals(property)) {
                final String volProp = property;

                Runnable edtTask = new Runnable() {
                        @Override public void run() {
                            volSlider.setValue(Integer.parseInt(volProp));
                        }
                    };

                if (SwingUtilities.isEventDispatchThread()) {
                    edtTask.run();
                } else {
                    SwingUtilities.invokeLater(edtTask);
                }
            }

        } catch (IOException e) {
            LOGGER.error("Error loading settings", e);
        }

    }

    @Override public void storeSettings(final OutputStream os) {
        Properties settings = new Properties();
        settings.setProperty("offset", Long.toString(getOffset()));
        settings.setProperty("volume", Integer.toString(volSlider.getValue()));

        try {
            settings.store(os, null);
        } catch (IOException e) {
            LOGGER.error("Error saving settings", e);
        }
    }

    @Override public Identifier getIdentifier() {
        return id;
    }

    @Override public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override public CustomActions getCustomActions() {
        return actions;
    }

    private void handleActionButtonEvent1(final ActionEvent event) {

        // Show the volume frame.
        volDialog.setLocation(volButton.getLocationOnScreen());
        volDialog.setVisible(true);
    }

    private void handleMaxVol(final ActionEvent e) {
        volSlider.setValue(MAX_VOLUME);
    }

    private void muteVolume() {
        preMuteVol = volSlider.getValue();
        volSlider.setValue(MIN_VOLUME);
    }

    private void handleVolumeSliderEvent(final ChangeEvent e) {
        int vol = volSlider.getValue();

        if (vol == 0) {
            volButton.setIcon(VOL_MUTED);
        } else {
            volButton.setIcon(VOL_NORMAL);
        }

        setVolume();

        if (engine != null) {

            synchronized (this) {

                for (ViewerStateListener listener : viewerListeners) {
                    listener.notifyStateChanged(null, null);
                }
            }
        }
    }

    private void setVolume() {
        boolean isVisible = (dialog != null) ? dialog.isVisible() : true;

        if (engine != null) {
            engine.setVolume(isVisible ? volSlider.getValue() : MIN_VOLUME);
        }
    }

    @Override public void setDatastore(final Datastore sDB) {
    }

    @Override public void setDataViewerVisible(final boolean isVisible) {
        dialog.setVisible(isVisible);

        if (isVisible) {
            volSlider.setValue(preMuteVol);
        } else {
            muteVolume();
        }
    }

    @Override public void clearDataFeed() {
        track.deregister();

        // Shutdown the engine
        engine.shutdown();

        // Stop the engine thread.
        engine.interrupt();
    }

}
