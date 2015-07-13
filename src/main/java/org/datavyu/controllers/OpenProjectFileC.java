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
package org.datavyu.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.controllers.project.DatavyuProjectConstructor;
import org.datavyu.models.project.Project;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

/**
 * Controller for opening and loading Datavyu project files that are on disk.
 */
public final class OpenProjectFileC {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(OpenProjectFileC.class);

    /**
     * Opens and loads a project file from disk.
     *
     * @param inFile The project file to open and load, absolute path
     * @return valid project if file was opened and loaded, null otherwise.
     */
    public Project open(final File inFile) {
        Yaml yaml = new Yaml(new Loader(new DatavyuProjectConstructor()));
        try {
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            Object o = yaml.load(in);

            // Make sure the de-serialised object is a project file
            if (!(o instanceof Project)) {
                LOGGER.error("Not a Datavyu project file");
                return null;
            }

            return (Project) o;
        } catch (FileNotFoundException ex) {
            LOGGER.error("Cannot open project file: "
                    + inFile.getAbsolutePath(), ex);
            return null;
        }
    }

    /**
     * Opens and loads a project file from a stream. The caller is responsible
     * for managing the stream.
     *
     * @param inStream The stream to deserialize and load
     * @return valid project if stream was deserialized, null otherwise.
     */
    public Project open(final InputStream inStream) {
        Yaml yaml = new Yaml(new Loader(new DatavyuProjectConstructor()));
        Object o = yaml.load(inStream);

        // Make sure the de-serialised object is a project file
        if (!(o instanceof Project)) {
            LOGGER.error("Not a Datavyu project file");
            return null;
        }

        return (Project) o;
    }
}
