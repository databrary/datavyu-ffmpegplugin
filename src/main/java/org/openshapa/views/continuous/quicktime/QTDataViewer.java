package org.openshapa.views.continuous.quicktime;

import java.awt.Component;
import java.io.File;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.openshapa.util.Constants;
import org.openshapa.views.continuous.DataViewer;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDDimension;
import quicktime.qd.QDRect;
import quicktime.qd.Region;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;

/**
 * The viewer for a quicktime video file.
 */
public final class QTDataViewer extends JFrame
        implements DataViewer {

    //--------------------------------------------------------------------------
    //
    //

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTDataViewer.class);

    //--------------------------------------------------------------------------
    //
    //

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The visual track for the above quicktime movie. */
    private Track visualTrack;

    /** The visual media for the above visual track. */
    private Media visualMedia;

    /** Rate for playback. */
    private float playRate;

    /** The visual component for the quicktime content. */
    private Component qtComponent;

    /** Frames per second. */
    private float fps;

    //--------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor - creates new video viewer.
     */
    public QTDataViewer() {
        try {
            movie = null;

            // Initalise QTJava.
            QTSession.open();

        } catch (QTException e) {
            logger.error("Unable to create QTVideoViewer", e);
        }
        initComponents();
    }


    //--------------------------------------------------------------------------
    // [interface] org.openshapa.views.continuous.DataViewer
    //

    /**
     * @return The parent JFrame that this data viewer resides within.
     */
    public JFrame getParentJFrame() {
        return this;
    }

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile The video file that this viewer is going to display to
     * the user.
     */
    public void setDataFeed(final File videoFile) {
        try {
            this.setTitle(videoFile.getName());
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);
            this.setName(this.getClass().getSimpleName() + videoFile.getName());

            // Set the time scale for our movie to milliseconds (i.e. 1000 ticks
            // per second.
            movie.setTimeScale(Constants.TICKS_PER_SECOND);
            visualTrack = movie.getIndTrackType(1,
                                       StdQTConstants.visualMediaCharacteristic,
                                       StdQTConstants.movieTrackCharacteristic);
            visualMedia = visualTrack.getMedia();

            fps = (float) visualMedia.getSampleCount()
                  / visualMedia.getDuration() * visualMedia.getTimeScale();

        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }

    /**
     * @return The frames per second.
     */
    public float getFrameRate() {
        return fps;
    }

    /**
     * @param rate The playback rate.
     */
    public void setPlaybackSpeed(final float rate) {
        this.playRate = rate;
    }

    /**
     * Plays the continous data stream at the current playback rate..
     */
    public void play() {
        try {
            if (movie != null) {
                movie.setRate(playRate);
            }
        } catch (QTException e) {
            logger.error("Unable to play", e);
        }
    }

    /**
     * Stops the playback of the continous data stream.
     */
    public void stop() {
        try {
            if (movie != null) {
                movie.stop();
            }
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    /**
     * @param position Millisecond absolute position for track.
     */
    public void seekTo(final long position) {
        try {
            if (movie != null) {
                TimeRecord time = new TimeRecord(Constants.TICKS_PER_SECOND,
                                                 position);
                movie.setTime(time);
                pack();
            }
        } catch (QTException e) {
            logger.error("Unable to find", e);
        }
    }

    /**
     * @return Current time in milliseconds.
     *
     * @throws QTException If error occurs accessing underlying implemenation.
     */
    public long getCurrentTime() throws QTException {
        return movie.getTime();
    }


    //--------------------------------------------------------------------------
    // [generated]
    //

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the user resizes the component.
     *
     * @param evt The event that triggered this action.
     */
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        try {
            if (qtComponent != null) {
                visualTrack.setSize(new QDDimension(qtComponent.getWidth(),
                                                    qtComponent.getHeight()));
            }

            qtComponent = QTFactory.makeQTComponent(movie).asComponent();
            qtComponent.setBounds(qtComponent.getX(),
                                  qtComponent.getY(),
                                  movie.getBox().getWidth(),
                                  movie.getBox().getHeight());
            this.add(qtComponent);

            QDDimension d = visualTrack.getSize();
            movie.setDisplayClipRgn(new Region(new QDRect(d.getWidth(),
                                                          d.getHeight())));
            movie.setClipRgn(new Region(new QDRect(d.getWidth(),
                                                   d.getHeight())));
            movie.setBox(new QDRect(d.getWidth(), d.getHeight()));
            movie.setBounds(new QDRect(d.getWidth(), d.getHeight()));

            this.pack();
            this.invalidate();
            this.setVisible(true);

        } catch (StdQTException e) {
            logger.error("Unable to resize video", e);
        } catch (QTException e) {
            logger.error("Unable to resize video", e);
        }
    }//GEN-LAST:event_formComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
