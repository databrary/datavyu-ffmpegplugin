package org.openshapa.views;

import java.io.File;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.openshapa.plugins.Filter;
import org.openshapa.plugins.Plugin;

import com.google.common.collect.Lists;

public abstract class BaseJFC extends PluginChooser {
    protected JComboBox pluginsBox;
    protected List<PluginCallback> plugins = Lists.newArrayList();
    
/* BugzID:2395 allow plugins to open any file type
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
*/    

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
