package org.openshapa.views.continuous.quicktime;

import javax.swing.ImageIcon;

import org.openshapa.plugins.quicktime.QTPlugin;
import org.openshapa.views.continuous.Plugin;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QTPluginTests {

    @Test
    public void testGetTypeIcon() {
        Plugin plugin = new QTPlugin();
        ImageIcon icon = plugin.getTypeIcon();
        Assert.assertNotNull(icon, "Expecting icon to exist.");
        Assert.assertTrue(icon.getIconHeight() == 32,
                "Expecting icon height to be 32 pixels.");
        Assert.assertTrue(icon.getIconWidth() == 32,
                "Expecting icon width to be 32 pixels.");
    }

}
