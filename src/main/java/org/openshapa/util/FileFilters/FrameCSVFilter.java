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
 */package org.openshapa.util.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * A file filter for CSV files.
 */
public final class FrameCSVFilter extends FileFilter {

    public static final FrameCSVFilter INSTANCE = new FrameCSVFilter();

    private FrameCSVFilter() {
    }

    /**
     * @return The description of the file filter.
     */
    @Override public String getDescription() {
        return "Frame-by-frame CSV file (*.csv)";
    }

    /**
     * Determines if the file filter will accept the supplied file.
     *
     * @param file
     *            The file to check if this file will accept.
     * @return true if the file is to be accepted, false otherwise.
     */
    @Override public boolean accept(final File file) {
        return (file.getName().endsWith(".csv") || file.isDirectory());
    }
}
