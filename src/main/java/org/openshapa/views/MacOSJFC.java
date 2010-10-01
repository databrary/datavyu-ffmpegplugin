package org.openshapa.views;

import java.awt.Component;

import java.io.File;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.common.collect.Lists;
import org.openshapa.plugins.Filter;
import org.openshapa.plugins.Plugin;


public class MacOSJFC extends PluginChooser {

    private JComboBox pluginsBox;
    private List<PluginCallback> plugins = Lists.newArrayList();

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

    public void approveSelection() {
        File selected = getSelectedFile();

        boolean approved = false;

        for (Filter filter : getSelectedPlugin().getFilters()) {

            if (filter.getFileFilter().accept(selected)) {
                approved = true;
                super.approveSelection();
            }
        }

        if (!approved) {
            JOptionPane.showMessageDialog(this, "Select a different plugin.",
                "Unsupported File", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override public void addPlugin(final Plugin plugin) {
        PluginCallback pc = new PluginCallback(plugin);
        plugins.add(pc);
    }

    @Override public void addPlugin(final Iterable<Plugin> plugins) {

        for (Plugin plugin : plugins) {
            PluginCallback pc = new PluginCallback(plugin);
            this.plugins.add(pc);
        }
    }

    @Override public Plugin getSelectedPlugin() {
        Object selected = pluginsBox.getSelectedItem();

        return ((PluginCallback) selected).plugin;
    }

    private static final class PluginCallback {
        final Plugin plugin;

        PluginCallback(final Plugin plugin) {
            this.plugin = plugin;
        }

        public String toString() {
            return plugin.getPluginName();
        }
    }

}
