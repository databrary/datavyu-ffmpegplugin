package org.openshapa.views;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MacOSJFC extends BaseJFC {
    @Override
    protected JDialog createDialog(final Component parent) {
        JDialog dialog = super.createDialog(parent);

        JLabel pluginSelect = new JLabel();
        pluginSelect.setText("Plugin:");
        pluginSelect.setHorizontalAlignment(SwingConstants.RIGHT);

        pluginsBox = new JComboBox(plugins.toArray());
        pluginsBox.setEditable(false);
        pluginsBox.setLightWeightPopupEnabled(true);

        JPanel pluginPanel = new JPanel(new GridBagLayout());
        pluginPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0.5D;
            c.insets = new Insets(0, 0, 0, 6);

            pluginPanel.add(pluginSelect, c);
        }
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = 0;

            pluginPanel.add(pluginsBox, c);
        }
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 0;
            c.weightx = 0.5D;
            c.insets = new Insets(0, 6, 0, 0);

            JPanel blankPanel = new JPanel();
            blankPanel.setBorder(null);
            pluginPanel.add(blankPanel, c);
        }

        JPanel inputPanel = (JPanel) getComponent(2);
        Component[] origs = inputPanel.getComponents();

        inputPanel.removeAll();
        inputPanel.add(origs[0]);
        inputPanel.add(origs[1]);
        inputPanel.add(pluginPanel);
        inputPanel.add(origs[2]);

        return dialog;
    }
}
