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
package org.datavyu.util.FileFilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * File filter for Datavyu project archive files
 */
public class OPFFilter extends FileFilter {

    public static final OPFFilter INSTANCE = new OPFFilter();

    private OPFFilter() {
    }

    @Override
    public boolean accept(final File file) {
        return (file.getName().endsWith(".opf") || file.isDirectory());
    }

    @Override
    public String getDescription() {
        return "Datavyu project file (*.opf)";
    }

}
