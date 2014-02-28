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
package org.datavyu.util;

import java.awt.*;
import java.io.Serializable;

/**
 * The properties to use for configuration. Access to the configuration
 * properties should be done via org.datavyu.Configuration. This class is a
 * JavaBean so that it can be easily dumped to disk via the XMLEncoder.
 */
public final class ConfigProperties implements Serializable {

    /**
     * The unique ID for this serial version.
     */
    private static final long serialVersionUID = 4L;

    /**
     * The spreadsheet data font.
     */
    private Font spreadsheetDataFont;

    /**
     * The spreadsheet label font.
     */
    private Font spreadsheetLabelFont;

    /**
     * The spreadsheet background colour.
     */
    private Color spreadsheetBackgroundColour;

    /**
     * The spreadsheet foreground colour.
     */
    private Color spreadsheetForegroundColour;

    /**
     * The foreground colour of the spreadsheet ordinal.
     */
    private Color spreadsheetOrdinalForeground;

    /**
     * The foreground colour of the spreadsheet ordinal.
     */
    private Color spreadsheetTimestampForeground;

    /**
     * The spreadsheet selection colour.
     */
    private Color spreadsheetSelectedColour;

    /**
     * The spreadsheet selection colour.
     */
    private Color spreadsheetOverlapColour;

    /**
     * The last location the user navigated too using a file chooser.
     */
    private String lastChooserDirectory;

    /**
     * User has given permission to send usage logs to UserMetrix.
     */
    private Boolean canSendLogs;

    /**
     * Fill colour of a carriage in the unselected/normal state
     */
    private Color mixerInterfaceNormalCarriageColour;

    /**
     * Outline colour of a carriage in the unselected/normal state
     */
    private Color mixerInterfaceNormalOutlineColour;

    /**
     * Fill colour of a carriage in the selected state
     */
    private Color mixerInterfaceSelectedCarriageColour;

    /**
     * Outline colour of a carriage in the selected state
     */
    private Color mixerInterfaceSelectedOutlineColour;

    /**
     * Version number to ignore for update reminders
     */
    private String ignoreVersion;
    
    /**
     * true if column name warnings should be displayed
     */
    private boolean columnNameWarnings = true;

    /**
     * true if prereleases are preferred
     */
    private boolean prereleasePreference;

    /**
     * Default constructor.
     */
    public ConfigProperties() {
    }

    /**
     * Sets the spreadsheet data font.
     *
     * @param font The new font to use for spreadsheet data.
     */
    public void setSSDataFont(final Font font) {
        spreadsheetDataFont = font;
    }

    /**
     * @return The spreadsheet data font.
     */
    public Font getSSDataFont() {
        return spreadsheetDataFont;
    }

    /**
     * Sets the spreadsheet data font.
     *
     * @param font The new font to use for spreadsheet data.
     */
    public void setSSLabelFont(final Font font) {
        spreadsheetLabelFont = font;
    }

    /**
     * @return The spreadsheet data font.
     */
    public Font getSSLabelFont() {
        return spreadsheetLabelFont;
    }

    /**
     * Sets the spreadsheet background colour.
     *
     * @param newColour The new colour to use for the spreadsheet background.
     */
    public void setSSBackgroundColour(final Color newColour) {
        spreadsheetBackgroundColour = newColour;
    }

    /**
     * @return The spreadsheet background colour.
     */
    public Color getSSBackgroundColour() {
        return spreadsheetBackgroundColour;
    }

    /**
     * Sets the spreadsheet foreground colour.
     *
     * @param newColour The new colour to use for the spreadsheet foreground.
     */
    public void setSSForegroundColour(final Color newColour) {
        spreadsheetForegroundColour = newColour;
    }

    /**
     * @return The spreadsheet foreground colour.
     */
    public Color getSSForegroundColour() {
        return spreadsheetForegroundColour;
    }

    /**
     * Sets the spreadsheet ordinal foreground colour.
     *
     * @param newColour The new colour to use for the spreadsheet foreground.
     */
    public void setSSOrdinalColour(final Color newColour) {
        spreadsheetOrdinalForeground = newColour;
    }

    /**
     * @return The spreadsheet ordinal foreground colour.
     */
    public Color getSSOrdinalColour() {
        return spreadsheetOrdinalForeground;
    }

    /**
     * Sets the spreadsheet timestamp foreground colour.
     *
     * @param newColour The new colour to use for the spreadsheet foreground.
     */
    public void setSSTimestampColour(final Color newColour) {
        spreadsheetTimestampForeground = newColour;
    }

    /**
     * @return The spreadsheet timestamp foreground colour.
     */
    public Color getSSTimestampeColour() {
        return spreadsheetTimestampForeground;
    }

    /**
     * Sets the spreadsheet selected colour.
     *
     * @param newColour The new colour to use for spreadsheet selections.
     */
    public void setSSSelectedColour(final Color newColour) {
        spreadsheetSelectedColour = newColour;
    }

    /**
     * @return The spreadsheet selections colour.
     */
    public Color getSSSelectedColour() {
        return spreadsheetSelectedColour;
    }

    /**
     * Sets the spreadsheet overlap colour.
     *
     * @param newColour The new colour to use for spreadsheet overlaps.
     */
    public void setSSOverlapColour(final Color newColour) {
        spreadsheetOverlapColour = newColour;
    }

    /**
     * @return The spreadsheet overlap colour.
     */
    public Color getSSOverlapColour() {
        return spreadsheetOverlapColour;
    }

    /**
     * Sets the last chooser directory that the user nominated.
     *
     * @param location The last location the user nominated.
     */
    public void setLCDirectory(final String location) {
        lastChooserDirectory = location;
    }

    /**
     * @return The last chooser directory that the user nominated.
     */
    public String getLCDirectory() {
        return lastChooserDirectory;
    }

    /**
     * Set if we are able to transmit logs or not to UserMetrix.
     *
     * @param send Can we send logs to UserMetrix?
     */
    public void setCanSendLogs(final Boolean send) {
        canSendLogs = send;
    }

    /**
     * @return True if we can send usage logs to UserMetrix.
     */
    public Boolean getCanSendLogs() {
        return canSendLogs;
    }

    /**
     * @return the mixerInterfaceNormalCarriageColour
     */
    public Color getMixerInterfaceNormalCarriageColour() {
        return mixerInterfaceNormalCarriageColour;
    }

    /**
     * @param newColour the mixerInterfaceNormalCarriageColour to set
     */
    public void setMixerInterfaceNormalCarriageColour(final Color newColour) {
        mixerInterfaceNormalCarriageColour = newColour;
    }

    /**
     * @return the mixerInterfaceNormalOutlineColour
     */
    public Color getMixerInterfaceNormalOutlineColour() {
        return mixerInterfaceNormalOutlineColour;
    }

    /**
     * @param newColour the mixerInterfaceNormalOutlineColour to set
     */
    public void setMixerInterfaceNormalOutlineColour(final Color newColour) {
        mixerInterfaceNormalOutlineColour = newColour;
    }

    /**
     * @return the mixerInterfaceSelectedCarriageColour
     */
    public Color getMixerInterfaceSelectedCarriageColour() {
        return mixerInterfaceSelectedCarriageColour;
    }

    /**
     * @param newColour the mixerInterfaceSelectedCarriageColour to set
     */
    public void setMixerInterfaceSelectedCarriageColour(final Color newColour) {
        mixerInterfaceSelectedCarriageColour = newColour;
    }

    /**
     * @return the mixerInterfaceSelectedOutlineColour
     */
    public Color getMixerInterfaceSelectedOutlineColour() {
        return mixerInterfaceSelectedOutlineColour;
    }

    /**
     * @param newColour the mixerInterfaceSelectedOutlineColour to set
     */
    public void setMixerInterfaceSelectedOutlineColour(final Color newColour) {
        mixerInterfaceSelectedOutlineColour = newColour;
    }

    /**
     * @return the ignoreVersion
     */
    public String getIgnoreVersion() {
        return ignoreVersion;
    }

    /**
     * @param version the version to set
     */
    public void setIgnoreVersion(final String version) {
        ignoreVersion = version;
    }
    
    /**
     * @return whether or not to display warnings for illegal column names
     */    
    public boolean getColumnNameWarning()
    {
        return columnNameWarnings;
    }
    
    /**
     * @param b whether or not to display warnings for illegal column names
     */    
    public void setColumnNameWarning(final boolean b)
    {
        columnNameWarnings = b;
    }

    /**
     * @return the prerelease preference
     */
    public boolean getPrereleasePreference() {
        return prereleasePreference;
    }

    /**
     * @param preference true if prereleases are preferred
     */
    public void setPrereleasePreference(boolean preference) {
        prereleasePreference = preference;
    }
}
