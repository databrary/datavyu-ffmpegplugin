package org.openshapa.views;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LinuxJFC extends BaseJFC {
    @Override protected JDialog createDialog(final Component parent) {
        JDialog dialog = super.createDialog(parent);

        JLabel pluginSelect = new JLabel();
        pluginSelect.setText("Plugin:");

        pluginsBox = new JComboBox(plugins.toArray());
        pluginsBox.setEditable(false);
        pluginsBox.setLightWeightPopupEnabled(true);

        // Got these values from Swing Explorer
        JPanel interior = (JPanel) getComponent(1);

        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createEmptyBorder());
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(pluginSelect);

        interior.add(labelPanel);
        interior.add(pluginsBox);

        dialog.pack();

        return dialog;
    }
}
