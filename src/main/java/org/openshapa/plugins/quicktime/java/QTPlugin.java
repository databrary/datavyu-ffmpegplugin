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
package org.openshapa.plugins.quicktime.java;

import java.io.FileFilter;

import java.net.URL;

import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.google.common.collect.Lists;

import com.sun.jna.Platform;

import org.openshapa.plugins.DataViewer;
import org.openshapa.plugins.Filter;
import org.openshapa.plugins.FilterNames;
import org.openshapa.plugins.Plugin;

import org.openshapa.views.continuous.quicktime.QTDataViewer;


public final class QTPlugin implements Plugin {

    private static final Filter VIDEO_FILTER = new Filter() {
            final SuffixFileFilter ff;
            final List<String> ext;

            {
                ext = Lists.newArrayList(".avi", ".mov", ".mpg", ".mp4");
                ff = new SuffixFileFilter(ext, IOCase.INSENSITIVE);
            }

            @Override public FileFilter getFileFilter() {
                return ff;
            }

            @Override public String getName() {
                return FilterNames.VIDEO.getFilterName();
            }

            @Override public Iterable<String> getExtensions() {
                return ext;
            }
        };

    @Override public DataViewer getNewDataViewer(final java.awt.Frame parent,
        final boolean modal) {

        if (Platform.isMac() || Platform.isWindows()) {
            return new QTDataViewer(parent, modal);
        } else {
            return null;
        }
    }

    /**
     * @return icon representing this plugin.
     */
    @Override public ImageIcon getTypeIcon() {
        URL typeIconURL = getClass().getResource(
                "/icons/gstreamerplugin-icon.png");

        return new ImageIcon(typeIconURL);
    }

    @Override public String getClassifier() {
        return "openshapa.video";
    }

    @Override public Filter[] getFilters() {
        return new Filter[] { VIDEO_FILTER };
    }

    @Override public String getPluginName() {
        return "QuickTime Video";
    }

    @Override public Class<? extends DataViewer> getViewerClass() {

        if (Platform.isMac() || Platform.isWindows()) {
            return QTDataViewer.class;
        }

        return null;
    }

}
