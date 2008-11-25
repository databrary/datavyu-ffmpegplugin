package au.com.nicta.openshapa.views;

import au.com.nicta.openshapa.cont.ContinuousDataController;
import au.com.nicta.openshapa.cont.ContinuousDataViewer;
import java.io.File;
import org.apache.log4j.Logger;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.movies.Movie;

/**
 * The viewer for a QTVideo file.
 *
 * @author cfreeman
 */
public class QTVideoViewer extends java.awt.Frame
implements ContinuousDataViewer {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTVideoViewer.class);

    /** The quicktime movie this viewer is displaying. */
    private Movie movie;

    /** The controller used to perform actions on this viewer. */
    private ContinuousDataController parentController;

    /** The "normal" playback speed. */
    private static final float NORMAL_SPEED = 1.0f;

    /** Fastforward playback speed. */
    private static final float FFORWARD_SPEED = 32.0f;

    /** Rewind playback speed. */
    private static final float RWIND_SPEED = -32.0f;

    /** The current shuttle speed. */
    private float shuttleSpeed;

    /**
     * Constructor - creates new video viewer.
     *
     * @param controller The controller invoking actions on this continous
     * data viewer.
     */
    public QTVideoViewer(final ContinuousDataController controller) {
        try {
            movie = null;
            shuttleSpeed = 0.0f;
            parentController = controller;

            // Initalise QTJava.
            QTSession.open();
        } catch (QTException e) {
            logger.error("Unable to create QTVideoViewer", e);
        }
        initComponents();
    }

    /**
     * Method to open a video file for playback.
     *
     * @param videoFile The video file that this viewer is going to display to
     * the user.
     */
    public void setVideoFile(final File videoFile) {
        try {
            OpenMovieFile omf = OpenMovieFile.asRead(new QTFile(videoFile));
            movie = Movie.fromFile(omf);
            this.add(QTFactory.makeQTComponent(movie).asComponent());
            this.pack();
        } catch (QTException e) {
            logger.error("Unable to setVideoFile", e);
        }
    }

    @Override
    public void createNewCell() {
    }

    @Override
    public void jogBack() {
    }

    /**
     * Stops the playback of the quicktime video.
     */
    @Override
    public void stop() {
        try {
            shuttleSpeed = 0.0f;
            movie.stop();
        } catch (QTException e) {
            logger.error("Unable to stop", e);
        }
    }

    @Override
    public void jogForward() {
    }

    /**
     * Shuttles the quicktime video backwards.
     */
    @Override
    public void shuttleBack() {
        try {
            if (movie != null) {
                if (shuttleSpeed == 0.0f) {
                    shuttleSpeed = 1.0f / RWIND_SPEED;
                } else {
                    shuttleSpeed = shuttleSpeed * 2;
                }
                movie.setRate(shuttleSpeed);
            }
        } catch (QTException e) {
            logger.error("Unable to shuttleBack", e);
        }
    }

    /**
     * Pauses the playback of the quicktime video.
     */
    @Override
    public void pause() {
        try {
            shuttleSpeed = 0.0f;
            movie.stop();
        } catch (QTException e) {
            logger.error("pause", e);
        }
    }

    /**
     * Shuttles the quicktime video forwards.
     */
    @Override
    public void shuttleForward() {
        try {
            if (movie != null) {
                if (shuttleSpeed == 0.0f) {
                    shuttleSpeed = 1.0f / FFORWARD_SPEED;
                } else {
                    shuttleSpeed = shuttleSpeed * 2;
                }
                movie.setRate(shuttleSpeed);
            }
        } catch (QTException e) {
            logger.error("Unable to shuttleForward", e);
        }
    }

    /**
     * Rewinds the quicktime video.
     */
    @Override
    public void rewind() {
        try {
            if (movie != null) {
                shuttleSpeed = 0.0f;
                movie.setRate(RWIND_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to rewind", e);
        }
    }

    /**
     * Plays the quicktime video.
     */
    @Override
    public void play() {
        try {
            if (movie != null) {
                shuttleSpeed = 0.0f;
                movie.setRate(NORMAL_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to play", e);
        }
    }

    /**
     * Fast forwards the quicktime video.
     */
    @Override
    public void forward() {
        try {
            if (movie != null) {
                shuttleSpeed = 0.0f;
                movie.setRate(FFORWARD_SPEED);
            }
        } catch (QTException e) {
            logger.error("Unable to forward", e);
        }
    }

    @Override
    public void setCellOffset() {
    }

    @Override
    public void find() {
    }

    @Override
    public void goBack() {
    }

    @Override
    public void setNewCellOnset() {
    }

    @Override
    public void syncCtrl() {
    }

    @Override
    public void sync() {
    }

    @Override
    public void setCellOnset() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user closes the viewer.
     *
     * @param evt The event that triggered this action.
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        parentController.shutdown(this);        
    }//GEN-LAST:event_exitForm

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
