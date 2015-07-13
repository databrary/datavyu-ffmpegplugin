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
package org.datavyu.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(BackgroundPanel.class);
    /**
     * The image to use as the background.
     */
    Image image;

    public BackgroundPanel(String resource) {
        try {
            URL typeIconURL = getClass().getResource(resource);
            this.image = ImageIO.read(typeIconURL);
            this.setSize(this.image.getWidth(null), this.image.getHeight(null));
        } catch (IOException e) {
            LOGGER.error("Unable to load resource", e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
