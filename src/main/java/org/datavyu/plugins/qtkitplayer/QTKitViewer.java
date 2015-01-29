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
package org.datavyu.plugins.qtkitplayer;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import org.datavyu.plugins.quicktime.BaseQuickTimeDataViewer;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;

import java.awt.*;
import java.io.File;


/**
 * The viewer for a quicktime video file.
 * <b>Do not move this class, this is for backward compatibility with 1.07.</b>
 */
public final class QTKitViewer extends BaseQuickTimeDataViewer {

    /**
     * How many milliseconds in a second?
     */
    private static final int MILLI = 1000;
    /**
     * How many frames to check when correcting the FPS.
     */
    private static final int CORRECTIONFRAMES = 5;
    /**
     * The logger for this class.
     */
    private static Logger LOGGER = UserMetrix.getLogger(QTKitViewer.class);
    private static float FALLBACK_FRAME_RATE = 24.0f;
    long prevSeekTime = -1;
    /**
     * The quicktime movie this viewer is displaying.
     */
    private QTKitPlayer movie;
    /**
     * The visual track for the above quicktime movie.
     */
    private Track visualTrack;
    /**
     * The visual media for the above visual track.
     */
    private Media visualMedia;

    public QTKitViewer(final Frame parent, final boolean modal) {
        super(parent, modal);

        movie = null;
    }

    @Override
    protected void setQTVolume(final float volume) {

        if (movie == null) {
            return;
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                movie.setVolume(volume);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDuration() {


        return movie.getDuration();
    }

    @Override
    protected void setQTDataFeed(final File videoFile) {

        // Ensure that the native hierarchy is set up
        this.addNotify();

        movie = new QTKitPlayer(videoFile);

        this.add(movie, BorderLayout.CENTER);

        nativeVideoSize = getQTVideoSize();
        System.out.println(nativeVideoSize);

        if (nativeVideoSize.getHeight() == 0) {
            nativeVideoSize = new Dimension(320, 240);
        }

//        setBounds(getX(), getY(), (int) nativeVideoSize.getWidth(),
//                (int) nativeVideoSize.getHeight());
//


        EventQueue.invokeLater(new Runnable() {
            public void run() {
//                System.out.println(new Dimension(movie.getWidth(), movie.getHeight()));

                movie.setVolume(0.7F);
            }
        });

    }

    @Override
    protected Dimension getQTVideoSize() {
        return new Dimension((int) movie.getMovieWidth(), (int) movie.getMovieHeight());
    }

    @Override
    protected float getQTFPS() {

        return movie.getFPS();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void play() {
        super.play();

        try {

            if (movie != null) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        movie.setRate(getPlaybackSpeed());
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Unable to play", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();

        try {

            if (movie != null) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        movie.stop();
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Unable to stop", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seekTo(final long position) {

        try {
            if (movie != null && (prevSeekTime != position || position != movie.getCurrentTime())) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        movie.setTime(position);
                    }
                });
                prevSeekTime = position;
            }
        } catch (Exception e) {
            LOGGER.error("Unable to find", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCurrentTime() {

        try {
            return movie.getCurrentTime();
        } catch (Exception e) {
            LOGGER.error("Unable to get time", e);
        }

        return 0;
    }

    @Override
    protected void cleanUp() {
        //TODO
//        movie.release();
    }
}
