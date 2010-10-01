package org.openshapa.views;

import java.awt.Component;
import java.awt.Dimension;

import java.io.File;

import java.util.List;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.common.collect.Lists;
import org.openshapa.plugins.Filter;
import org.openshapa.plugins.Plugin;


public class WindowsJFC extends PluginChooser {

    private JComboBox pluginsBox;
    private List<PluginCallback> plugins = Lists.newArrayList();

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
