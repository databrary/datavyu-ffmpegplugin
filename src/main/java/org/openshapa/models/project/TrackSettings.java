package org.openshapa.models.project;

/**
 * This class is used to store user interface settings relating to a given
 * track.
 * 
 * @author Douglas Teoh
 */
public class TrackSettings implements Cloneable {

    /** Absolute file path to the data source */
    private String filePath;
    /** Is the track's movement locked on the interface */
    private boolean isLocked;
    /** The track's bookmark position */
    private long bookmarkPosition;

    public TrackSettings() {
        bookmarkPosition = -1;
    }

    /**
     * Private copy constructor.
     * 
     * @param other
     */
    private TrackSettings(final TrackSettings other) {
        filePath = other.filePath;
        isLocked = other.isLocked;
        bookmarkPosition = other.bookmarkPosition;
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
     * @return the bookmark position
     */
    public long getBookmarkPosition() {
        return bookmarkPosition;
    }

    /**
     * @param bookmarkPosition
     *            the bookmark position to set
     */
    public void setBookmarkPosition(final long bookmarkPosition) {
        this.bookmarkPosition = bookmarkPosition;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath
     *            the filePath to set
     */
    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    @Override
    public TrackSettings clone() {
        return new TrackSettings(this);
    }
}
