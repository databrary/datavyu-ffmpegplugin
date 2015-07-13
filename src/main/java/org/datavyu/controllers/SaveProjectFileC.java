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
import org.datavyu.controllers.project.DatavyuProjectRepresenter;
import org.datavyu.models.project.Project;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Controller for saving the Datavyu project to disk.
 */
public final class SaveProjectFileC {

    /**
     * The logger for this class.
     */
    private static Logger LOGGER = LogManager.getLogger(SaveProjectFileC.class);

    /**
     * Serialize the Datavyu project to a stream. The caller is responsible
     * for closing the output stream.
     *
     * @param outStream The output stream to use for the project.
     * @param project   The project you wish to serialize.
     */
    public void save(final OutputStream outStream, final Project project) {
        LOGGER.info("save to stream");
        Dumper dumper = new Dumper(new DatavyuProjectRepresenter(),
                new DumperOptions());
        Yaml yaml = new Yaml(dumper);

        try {
            outStream.write(yaml.dump(project).getBytes());
        } catch (IOException ex) {
            LOGGER.error("Unable to save project file", ex);
        }
    }

}
