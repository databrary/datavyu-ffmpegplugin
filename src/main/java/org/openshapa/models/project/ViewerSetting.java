package org.openshapa.models.project;

/**
 * Stores user settings for a data viewer
 */
public class ViewerSetting {

    /** Fully qualified name of the plugin */
    private String pluginName;
    /** Absolute file path to the data source */
    private String filePath;
    /** Playback offset in milliseconds */
    private long offset;
    /** Bookmark position in milliseconds */
    private long bookmark;

    public ViewerSetting() {
        bookmark = -1;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * @return the bookmark
     */
    public long getBookmark() {
        return bookmark;
    }

    /**
     * @param bookmark
     *            the bookmark to set
     */
    public void setBookmark(long bookmark) {
        this.bookmark = bookmark;
    }

}
