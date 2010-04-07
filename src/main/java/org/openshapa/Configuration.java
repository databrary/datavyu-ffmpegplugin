package org.openshapa;

import java.awt.Color;
import java.awt.Font;

import java.io.File;
import java.io.IOException;

import org.jdesktop.application.LocalStorage;

import org.openshapa.util.ConfigProperties;

import com.usermetrix.jclient.UserMetrix;


/**
 * Singleton object containing global configuration definitions for the user
 * interface.
 */
public final class Configuration {

    /** The name of the configuration file. */
    private static final String CONFIG_FILE = "settings.xml";

    /** The single instance of the configuration object for OpenSHAPA. */
    private static Configuration instance = null;

    /** The default font to be used by OpenSHAPA. */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 14);

    /** The default font to be used by OpenSHAPA labels. */
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);

    /** The default data font size to be used by OpenSHAPA labels. */
    private static final float DATA_FONT_SIZE = 14;

    /** The default label font size to be used by OpenSHAPA labels. */
    private static final float LABEL_FONT_SIZE = 12;

    /** The default spreadsheet background colour. */
    private static final Color DEFAULT_BACKGROUND = Color.WHITE;

    /** The default spreadsheet foreground colour. */
    private static final Color DEFAULT_FOREGROUND = Color.BLACK;

    /** The default spreadsheet selected colour. */
    private static final Color DEFAULT_SELECTED = new Color(176, 197, 227);

    /** The default spreadsheet overlap colour. */
    private static final Color DEFAULT_OVERLAP = Color.RED;

    /** Fill colour of a carriage in the unselected/normal state. */
    private static final Color DEFAULT_NORMAL_CARRIAGE_COLOR = new Color(169,
            218, 248);

    /** Outline colour of a carriage in the unselected/normal state. */
    private static final Color DEFAULT_NORMAL_OUTLINE_COLOR = new Color(129,
            167, 188);

    /** Fill colour of a carriage in the selected state. */
    private static final Color DEFAULT_SELECTED_CARRIAGE_COLOR = new Color(138,
            223, 162);

    /** Outline colour of a carriage in the selected state. */
    private static final Color DEFAULT_SELECTED_OUTLINE_COLOR = new Color(105,
            186, 128);

    /** The configuration properties. */
    private ConfigProperties properties;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(Configuration.class);

    /** Default font type. */
    private Font newFont = null;

    /**
     * Default constructor.
     */
    private Configuration() {
        super();

        // Try to load the configuration properties from disk.
        try {
            LocalStorage ls = OpenSHAPA.getApplication().getContext()
                .getLocalStorage();
            properties = (ConfigProperties) ls.load(CONFIG_FILE);

            // Oh-noes, can't load configuration file from disk.
        } catch (IOException e) {
            logger.error("Unable to load configuration file from dis", e);
        }

        // Set custom font
        String fontFileName = "/fonts/DejaVuSansCondensed.ttf";

        try {
            newFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream(fontFileName));
            properties.setSSDataFont(newFont.deriveFont(DATA_FONT_SIZE));
            properties.setSSLabelFont(newFont.deriveFont(LABEL_FONT_SIZE));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(fontFileName
                + " can't be loaded. Using default font");
        }

        // Properties not loaded from disk - initalise to default and save.
        if (properties == null) {
            properties = new ConfigProperties();
            properties.setSSDataFont(DEFAULT_FONT);
            properties.setSSLabelFont(LABEL_FONT);
            properties.setSSBackgroundColour(DEFAULT_BACKGROUND);
            properties.setSSForegroundColour(DEFAULT_FOREGROUND);
            properties.setSSSelectedColour(DEFAULT_SELECTED);
            properties.setSSOverlapColour(DEFAULT_OVERLAP);
            properties.setMixerInterfaceNormalCarriageColour(
                DEFAULT_NORMAL_CARRIAGE_COLOR);
            properties.setMixerInterfaceNormalOutlineColour(
                DEFAULT_NORMAL_OUTLINE_COLOR);
            properties.setMixerInterfaceSelectedCarriageColour(
                DEFAULT_SELECTED_CARRIAGE_COLOR);
            properties.setMixerInterfaceSelectedOutlineColour(
                DEFAULT_SELECTED_OUTLINE_COLOR);

            save();
        }

        if (properties.getLCDirectory() == null) {
            properties.setLCDirectory(System.getProperty("user.home"));
            save();
        }

        if (properties.getSSOverlapColour() == null) {

            // Assume that user wants their selected colour overridden too.
            properties.setSSSelectedColour(DEFAULT_SELECTED);
            properties.setSSOverlapColour(DEFAULT_OVERLAP);
            save();
        }

        // If one property is null, just reset all.
        if (properties.getMixerInterfaceNormalCarriageColour() == null) {
            properties.setMixerInterfaceNormalCarriageColour(
                DEFAULT_NORMAL_CARRIAGE_COLOR);
            properties.setMixerInterfaceNormalOutlineColour(
                DEFAULT_NORMAL_OUTLINE_COLOR);
            properties.setMixerInterfaceSelectedCarriageColour(
                DEFAULT_SELECTED_CARRIAGE_COLOR);
            properties.setMixerInterfaceSelectedOutlineColour(
                DEFAULT_SELECTED_OUTLINE_COLOR);
            save();
        }
    }

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
     * @param font
     *            The new data font to use on the spreadsheet.
     */
    public void setSSDataFont(final Font font) {
        properties.setSSDataFont(font);
        save();
    }

    /**
     * @return The data font to use for the spreadsheet.
     */
    public Font getSSDataFont() {
        return properties.getSSDataFont();
    }

    /**
     * Changes the data font size.
     * @param size new font size
     */
    public void setSSDataFontSize(final float size) {
        properties.setSSDataFont(newFont.deriveFont(size));
        save();
    }

    /**
     * Sets and saves (to the config file) the data font to use on the
     * spreadsheet.
     *
     * @param font
     *            The new data font to use on the spreadsheet.
     */
    public void setSSLabelFont(final Font font) {
        properties.setSSLabelFont(font);
        save();
    }

    /**
     * @return The data font to use for the spreadsheet.
     */
    public Font getSSLabelFont() {
        return properties.getSSLabelFont();
    }

    /**
     * Sets and saves (to the config file) the background colour of the
     * spreadsheet.
     *
     * @param colour
     *            The new colour to use for the spreadsheet background.
     */
    public void setSSBackgroundColour(final Color colour) {
        properties.setSSBackgroundColour(colour);
        save();
    }

    /**
     * @return The background colour for the spreadsheet.
     */
    public Color getSSBackgroundColour() {
        return properties.getSSBackgroundColour();
    }

    /**
     * Sets and saves (to the config file) the foreground colour of the
     * spreadsheet.
     *
     * @param colour
     *            The new colour to use for the spreadsheet foreground.
     */
    public void setSSForegroundColour(final Color colour) {
        properties.setSSForegroundColour(colour);
        save();
    }

    /**
     * @return The foreground colour of the spreadsheet.
     */
    public Color getSSForegroundColour() {
        return properties.getSSForegroundColour();
    }

    /**
     * Sets and saves (to the config file) the selected colour of the
     * spreadsheet.
     *
     * @param colour
     *            The new colour to use for spreadsheet selections.
     */
    public void setSSSelectedColour(final Color colour) {
        properties.setSSSelectedColour(colour);
        save();
    }

    /**
     * @return The selected colour of the spreadsheet.
     */
    public Color getSSSelectedColour() {
        return properties.getSSSelectedColour();
    }

    /**
     * Sets and saves (to the config file) the overlap colour of the
     * spreadsheet.
     *
     * @param colour
     *            The new colour to use for spreadsheet overlaps.
     */
    public void setSSOverlapColour(final Color colour) {
        properties.setSSOverlapColour(colour);
        save();
    }

    /**
     * @return The overlap colour of the spreadsheet.
     */
    public Color getSSOverlapColour() {
        return properties.getSSOverlapColour();
    }

    /**
     * Sets and saves (to the config file) the last directory the user navigated
     * too in a chooser.
     *
     * @param location
     *            The last location that the user navigated too.
     */
    public void setLCDirectory(final File location) {
        properties.setLCDirectory(location.toString());
        save();
    }

    /**
     * @return The last directory the user navigated too in a file chooser.
     */
    public File getLCDirectory() {
        return new File(properties.getLCDirectory());
    }

    public void setCanSendLogs(final Boolean send) {
        properties.setCanSendLogs(send);
        save();
    }

    public Boolean getCanSendLogs() {
        return properties.getCanSendLogs();
    }

    /**
     * @return the mixerInterfaceSelectedCarriageColour
     */
    public Color getMixerInterfaceSelectedCarriageColour() {
        return properties.getMixerInterfaceSelectedCarriageColour();
    }

    /**
     * @param newColour
     *            the mixerInterfaceSelectedCarriageColour to set
     */
    public void setMixerInterfaceSelectedCarriageColour(final Color newColour) {
        properties.setMixerInterfaceSelectedCarriageColour(newColour);
        save();
    }

    /**
     * @return the mixerInterfaceSelectedOutlineColour
     */
    public Color getMixerInterfaceSelectedOutlineColour() {
        return properties.getMixerInterfaceSelectedOutlineColour();
    }

    /**
     * @param newColour
     *            the mixerInterfaceSelectedOutlineColour to set
     */
    public void setMixerInterfaceSelectedOutlineColour(final Color newColour) {
        properties.setMixerInterfaceSelectedOutlineColour(newColour);
        save();
    }

    /**
     * @return the mixerInterfaceNormalCarriageColour
     */
    public Color getMixerInterfaceNormalCarriageColour() {
        return properties.getMixerInterfaceNormalCarriageColour();
    }

    /**
     * @param newColour
     *            the mixerInterfaceNormalCarriageColour to set
     */
    public void setMixerInterfaceNormalCarriageColour(final Color newColour) {
        properties.setMixerInterfaceNormalCarriageColour(newColour);
        save();
    }

    /**
     * @return the mixerInterfaceNormalOutlineColour
     */
    public Color getMixerInterfaceNormalOutlineColour() {
        return properties.getMixerInterfaceNormalOutlineColour();
    }

    /**
     * @param newColour
     *            the mixerInterfaceNormalOutlineColour to set
     */
    public void setMixerInterfaceNormalOutlineColour(final Color newColour) {
        properties.setMixerInterfaceNormalOutlineColour(newColour);
        save();
    }

    /**
     * Saves the configuration properties do disk. This is stored in local
     * storage of the swing application framework.
     */
    private void save() {

        // Try to save the configuration properties to disk.
        try {
            LocalStorage ls = OpenSHAPA.getApplication().getContext()
                .getLocalStorage();
            ls.save(properties, CONFIG_FILE);

            // Oh-noes, can't save configuration file to disk.
        } catch (IOException e) {
            logger.error("Unable to save configuration to disk", e);
        }
    }

}
