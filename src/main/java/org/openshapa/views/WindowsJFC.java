package org.openshapa.views;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WindowsJFC extends BaseJFC {
    @Override protected JDialog createDialog(final Component parent) {
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

        setFileFilter(getAcceptAllFileFilter());

        return dialog;
    }
}
