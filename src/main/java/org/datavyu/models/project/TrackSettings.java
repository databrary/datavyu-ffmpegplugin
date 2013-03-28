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
package org.datavyu.models.project;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is used to store user interface settings relating to a given
 * track.
 *
 * @author Douglas Teoh
 */
public final class TrackSettings {

    /** TrackSettings specification version. */
    public static final int VERSION = 2;

    /** Absolute file path to the data source */
    @Deprecated private String filePath;

    /** Is the track's movement locked on the interface */
    private boolean isLocked;

    /** The track's bookmark positions */
    private List<Long> bookmarkPositions;
    
    /* The track's datarate */
    private float fps;

    public TrackSettings() {
        bookmarkPositions = new ArrayList<Long>();
    }

    /**
     * Private copy constructor.
     *
     * @param other
     */
    private TrackSettings(final TrackSettings other) {
        filePath = other.filePath;
        isLocked = other.isLocked;
        bookmarkPositions = other.bookmarkPositions;
        fps = other.fps;
    }

    /**
     * @return is the track's movement locked on the interface
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * @param isLocked
     *            is track's movement locked on the interface
     */
    public void setLocked(final boolean isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * @return bookmark positions
     */
    public List<Long> getBookmarkPositions() {
        return bookmarkPositions;
    }

    /**
     * Add a bookmark position to our settings.
     *
     * @param position
     */
    public void addBookmarkPosition(final long position) {
        bookmarkPositions.add(position);
    }

    /**
     * @param bookmarkPosition
     *            the bookmark position to set
     */
    public void setBookmarkPositions(final List<Long> bookmarkPositions) {
        this.bookmarkPositions = bookmarkPositions;
    }

    /**
     * @return the filePath
     */
    @Deprecated public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath
     *            the filePath to set
     */
    @Deprecated public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }
    
    public float getFps() {
        return fps;
    }
    
    public void setFps(float fps) {
        this.fps = fps;
    }

    public TrackSettings copy() {
        return new TrackSettings(this);
    }
}
