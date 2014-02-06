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

import com.google.common.collect.Lists;
import org.datavyu.plugins.Plugin;

import javax.swing.*;
import java.util.List;

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

    @Override
    public void addPlugin(final Plugin plugin) {
        PluginCallback pc = new PluginCallback(plugin);
        plugins.add(pc);
    }

    @Override
    public void addPlugin(final Iterable<Plugin> plugins) {

        for (Plugin plugin : plugins) {
            PluginCallback pc = new PluginCallback(plugin);
            this.plugins.add(pc);
        }
    }

    @Override
    public Plugin getSelectedPlugin() {
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
