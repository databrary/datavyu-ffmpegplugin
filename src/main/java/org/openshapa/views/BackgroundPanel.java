package org.openshapa.views;

import com.usermetrix.jclient.UserMetrix;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {
    /** The image to use as the background. */
    Image image;

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(BackgroundPanel.class);

    public BackgroundPanel(String resource) {
        try {
            URL typeIconURL = getClass().getResource(resource);
            this.image = ImageIO.read(typeIconURL);
            this.setSize(this.image.getWidth(null), this.image.getHeight(null));
        } catch (IOException e) {
            logger.error("Unable to load resource", e);
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
