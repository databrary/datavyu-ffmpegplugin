package org.openshapa.models.project;

/**
 * Stores user settings for a data viewer
 */
public class ViewerSetting implements Cloneable {

    /** Fully qualified name of the plugin */
    private String pluginName;
    /** Absolute file path to the data source */
    private String filePath;
    /** Playback offset in milliseconds */
    private long offset;

    public ViewerSetting() {
    }

    /**
     * Private copy constructor.
     * 
     * @param other
     */
    private ViewerSetting(final ViewerSetting other) {
        pluginName = other.pluginName;
        filePath = other.filePath;
        offset = other.offset;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(final long offset) {
        this.offset = offset;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(final String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public ViewerSetting clone() {
        return new ViewerSetting(this);
    }

}
