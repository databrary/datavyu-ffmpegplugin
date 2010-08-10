package org.openshapa.models.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;


/**
 * Stores user settings for a data viewer
 */
public final class ViewerSetting {

    /** ViewerSetting specification version. */
    public static final int VERSION = 3;

    /** Track settings associated with this data viewer. */
    private TrackSettings trackSettings;

    /** Fully qualified name of the plugin */
    private String pluginName;

    /** Absolute file path to the data source */
    private String filePath;

    /** Playback offset in milliseconds */
    private long offset;

    /** ID of settings file. */
    private String settingsId;

    private byte[] settingsData;

    private ByteArrayOutputStream settingsOutput;

    public ViewerSetting() {
    }

    /**
     * Private copy constructor.
     *
     * @param other
     */
    private ViewerSetting(final ViewerSetting other) {
        trackSettings = other.trackSettings.copy();
        pluginName = other.pluginName;
        filePath = other.filePath;
        offset = other.offset;
        settingsId = other.settingsId;
    }

    /**
     * @return track settings associated with this data viewer.
     */
    public TrackSettings getTrackSettings() {
        return trackSettings;
    }

    /**
     * @param trackSettings
     *            track settings used by this data viewer.
     */
    public void setTrackSettings(final TrackSettings trackSettings) {
        this.trackSettings = trackSettings;
    }

    /**
     * @return Absolute file path to the data source.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath
     *            Absolute file path to the data source.
     */
    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Retained for backwards compatibility.
     * @return
     */
    @Deprecated public long getOffset() {
        return offset;
    }

    /**
     * Retained for backwards compatibility.
     * @param offset
     */
    @Deprecated public void setOffset(final long offset) {
        this.offset = offset;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(final String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * @return String identifier for these settings.
     */
    public String getSettingsId() {
        return settingsId;
    }

    /**
     * Set the identifier for these settings.
     * @param settingsId Identifier to use.
     */
    public void setSettingsId(final String settingsId) {
        this.settingsId = settingsId;
    }

    /**
     * Copy viewer settings from the given input stream into an internal buffer.
     * The settings can be read using {@link #getSettingsInputStream()}.
     * @param is InputStream to copy from.
     */
    public void copySettings(final InputStream is) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            IOUtils.copy(is, os);

            settingsData = os.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        IOUtils.closeQuietly(os);
    }

    /**
     * Write the viewer settings to the given output stream.
     * @param os OutputStream to write to.
     * @throws IOException If there are problems writing to the given output
     * stream.
     */
    public void writeSettings(final OutputStream os) throws IOException {
        settingsOutput.writeTo(os);
    }

    /**
     * @return InputStream to use for reading settings.
     */
    public InputStream getSettingsInputStream() {
        return new ByteArrayInputStream(settingsData);
    }

    /**
     * @return OutputStream to use for writing settings.
     */
    public OutputStream getSettingsOutputStream() {

        // Can't re-use the stream, need empty buffer.
        settingsOutput = new ByteArrayOutputStream();

        return settingsOutput;
    }

    public ViewerSetting copy() {
        return new ViewerSetting(this);
    }

}
