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

import javax.swing.*;
import java.awt.*;

public class WindowsJFC extends BaseJFC {
    @Override
    protected JDialog createDialog(final Component parent) {
        JDialog dialog = super.createDialog(parent);

        JLabel pluginSelect = new JLabel();
        pluginSelect.setText("Plugin:");

        pluginsBox = new JComboBox(plugins.toArray());
        pluginsBox.setEditable(false);
        pluginsBox.setLightWeightPopupEnabled(true);
        pluginsBox.setSize(286, 20);

        // Got these values by printing out the component hierarchy
        JPanel filePanel = (JPanel) getComponent(2);
        JPanel inputPanel = (JPanel) filePanel.getComponent(2);

        JPanel descPanel = (JPanel) inputPanel.getComponent(0);
        descPanel.add(new Box.Filler(new Dimension(1, 12), new Dimension(1, 12),
                new Dimension(1, 12)));
        descPanel.add(pluginSelect);

        // There is a box filler in between components 0 and 2.
        JPanel fieldPanel = (JPanel) inputPanel.getComponent(2);
        fieldPanel.add(new Box.Filler(new Dimension(1, 8), new Dimension(1, 8),
                new Dimension(1, 8)));
        fieldPanel.add(pluginsBox);

        dialog.pack();

        //setFileFilter(getAcceptAllFileFilter());

        return dialog;
    }
    
}
