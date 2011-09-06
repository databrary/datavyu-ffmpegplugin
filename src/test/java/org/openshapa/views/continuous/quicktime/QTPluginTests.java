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
package org.openshapa.views.continuous.quicktime;

import javax.swing.ImageIcon;

import org.openshapa.plugins.quicktime.java.QTPlugin;

import org.testng.Assert;

import org.testng.annotations.Test;


public class QTPluginTests {

    @Test public void testGetTypeIcon() {
        org.openshapa.plugins.Plugin plugin = new QTPlugin();
        ImageIcon icon = plugin.getTypeIcon();
        Assert.assertNotNull(icon, "Expecting icon to exist.");
        Assert.assertTrue(icon.getIconHeight() == 32,
            "Expecting icon height to be 32 pixels.");
        Assert.assertTrue(icon.getIconWidth() == 32,
            "Expecting icon width to be 32 pixels.");
    }

}
