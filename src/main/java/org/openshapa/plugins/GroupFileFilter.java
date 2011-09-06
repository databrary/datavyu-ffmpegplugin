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
package org.openshapa.plugins;

import java.io.File;

import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public final class GroupFileFilter extends FileFilter {

    private final String description;
    private List<Filter> filters;

    public GroupFileFilter(final String desc) {
        description = desc;
        filters = Lists.newArrayList();
    }

    @Override public boolean accept(final File f) {

        if (f.isDirectory()) {
            return true;
        }

        for (Filter filter : filters) {

            if (filter.getFileFilter().accept(f)) {
                return true;
            }
        }

        return false;
    }

    @Override public String getDescription() {
        Set<String> extensions = Sets.newTreeSet();

        for (Filter filter : filters) {

            for (String ext : filter.getExtensions()) {
                extensions.add(ext);
            }
        }

        StringBuilder sb = new StringBuilder(description);
        sb.append(":");

        for (String ext : extensions) {
            sb.append(" ");
            sb.append(ext);
            sb.append(",");
        }

        if (!extensions.isEmpty()) {

            // Remove trailing comma.
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public void addFileFilter(final Filter filter) {
        filters.add(filter);
    }

}
