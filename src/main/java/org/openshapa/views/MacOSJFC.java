package org.openshapa.views;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MacOSJFC extends BaseJFC {
    @Override protected JDialog createDialog(final Component parent) {
        JDialog dialog = super.createDialog(parent);

        JLabel pluginSelect = new JLabel();
        pluginSelect.setText("Plugin:");

        pluginsBox = new JComboBox(plugins.toArray());
        pluginsBox.setEditable(false);
        pluginsBox.setLightWeightPopupEnabled(true);
        pluginsBox.setSize(220, 27);

        JPanel bottomPanel = (JPanel) getComponent(4);
        JPanel inputPanel = (JPanel) bottomPanel.getComponent(0);

        Component[] origs = inputPanel.getComponents();
        inputPanel.add(pluginSelect);
        inputPanel.add(pluginsBox);
        inputPanel.add(origs[0]);
        inputPanel.add(origs[1]);

        dialog.setSize(getWidth() + 100, getHeight() + 50);

        return dialog;
    }
}
