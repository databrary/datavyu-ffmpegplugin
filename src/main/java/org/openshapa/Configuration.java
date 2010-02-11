package org.openshapa;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import org.jdesktop.application.LocalStorage;
import org.openshapa.util.ConfigProperties;

/**
 * Singleton object containing global configuration definitions for the user
 * interface.
 */
public final class Configuration {

    /** The configuration properties. */
    private ConfigProperties properties;

    /** The name of the configuration file. */
    private static final String CONFIG_FILE = "settings.xml";

    /** The single instance of the configuration object for OpenSHAPA. */
    private static Configuration instance = null;

    /** The default font to be used by OpenSHAPA. */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);

    /** The default spreadsheet background colour. */
    private static final Color DEFAULT_BACKGROUND = Color.WHITE;

    /** The default spreadsheet foreground colour. */
    private static final Color DEFAULT_FOREGROUND = Color.BLACK;

    /** The default spreadsheet selected colour. */
    private static final Color DEFAULT_SELECTED = new Color(176, 197, 227);

    /** The default spreadsheet overlap colour. */
    private static final Color DEFAULT_OVERLAP = Color.RED;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(Configuration.class);

    /**
     * @return The single instance of the Configuration object in OpenSHAPA.
     */
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    /**
     * Sets and saves (to the config file) the data font to use on the
     * spreadsheet.
     *
     * @param font The new data font to use on the spreadsheet.
     */
    public void setSSDataFont(final Font font) {
        this.properties.setSSDataFont(font);
        this.save();
    }

    /**
     * @return The data font to use for the spreadsheet.
     */
    public Font getSSDataFont() {
        return this.properties.getSSDataFont();
    }

    /**
     * Sets and saves (to the config file) the background colour of the
     * spreadsheet.
     *
     * @param colour The new colour to use for the spreadsheet background.
     */
    public void setSSBackgroundColour(final Color colour) {
        this.properties.setSSBackgroundColour(colour);
        this.save();
    }

    /**
     * @return The background colour for the spreadsheet.
     */
    public Color getSSBackgroundColour() {
        return this.properties.getSSBackgroundColour();
    }

    /**
     * Sets and saves (to the config file) the foreground colour of the
     * spreadsheet.
     *
     * @param colour The new colour to use for the spreadsheet foreground.
     */
    public void setSSForegroundColour(final Color colour) {
        this.properties.setSSForegroundColour(colour);
        this.save();
    }

    /**
     * @return The foreground colour of the spreadsheet.
     */
    public Color getSSForegroundColour() {
        return this.properties.getSSForegroundColour();
    }

    /**
     * Sets and saves (to the config file) the selected colour of the
     * spreadsheet.
     *
     * @param colour The new colour to use for spreadsheet selections.
     */
    public void setSSSelectedColour(final Color colour) {
        this.properties.setSSSelectedColour(colour);
        this.save();
    }

    /**
     * @return The selected colour of the spreadsheet.
     */
    public Color getSSSelectedColour() {
        return this.properties.getSSSelectedColour();
    }

    /**
     * Sets and saves (to the config file) the overlap colour of the
     * spreadsheet.
     *
     * @param colour The new colour to use for spreadsheet overlaps.
     */
    public void setSSOverlapColour(final Color colour) {
        this.properties.setSSOverlapColour(colour);
        this.save();
    }

    /**
     * @return The overlap colour of the spreadsheet.
     */
    public Color getSSOverlapColour() {
        return this.properties.getSSOverlapColour();
    }

    /**
     * Sets and saves (to the config file) the last directory the user navigated
     * too in a chooser.
     *
     * @param location The last location that the user navigated too.
     */
    public void setLCDirectory(final File location) {
        this.properties.setLCDirectory(location.toString());
        this.save();
    }

    /**
     * @return The last directory the user navigated too in a file chooser.
     */
    public File getLCDirectory() {
        return new File(this.properties.getLCDirectory());
    }

    /**
     * Default constructor.
     */
    private Configuration() {
        super();

        // Try to load the configuration properties from disk.
        try {
            LocalStorage ls = OpenSHAPA.getApplication()
                                       .getContext().getLocalStorage();
            properties = (ConfigProperties) ls.load(CONFIG_FILE);

        // Oh-noes, can't load configuration file from disk.
        } catch (IOException e) {
            logger.error("Unable to load configuration file from dis", e);
        }

        // Properties not loaded from disk - initalise to default and save.
        if (properties == null) {
            properties = new ConfigProperties();
            properties.setSSDataFont(DEFAULT_FONT);
            properties.setSSBackgroundColour(DEFAULT_BACKGROUND);
            properties.setSSForegroundColour(DEFAULT_FOREGROUND);
            properties.setSSSelectedColour(DEFAULT_SELECTED);
            properties.setSSOverlapColour(DEFAULT_OVERLAP);
            this.save();
        }

        if (properties.getLCDirectory() == null) {
            properties.setLCDirectory(System.getProperty("user.home"));
            this.save();
        }

        if (properties.getSSOverlapColour() == null) {
            // Assume that user wants their selected colour overridden too.
            properties.setSSSelectedColour(DEFAULT_SELECTED);
            properties.setSSOverlapColour(DEFAULT_OVERLAP);
            this.save();
        }
    }

    /**
     * Saves the configuration properties do disk. This is stored in local
     * storage of the swing application framework.
     */
    private void save() {
        // Try to save the configuration properties to disk.
        try {
            LocalStorage ls = OpenSHAPA.getApplication()
                                       .getContext().getLocalStorage();
            ls.save(properties, CONFIG_FILE);

        // Oh-noes, can't save configuration file to disk.
        } catch (IOException e) {
            logger.error("Unable to save configuration to disk", e);
        }
    }


}
