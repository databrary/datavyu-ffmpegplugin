package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.models.project.Project;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for saving the OpenSHAPA project to disk.
 */
public final class SaveProjectFileC {

    /** The logger for this class. */
    private Logger logger = UserMetrix.getInstance(SaveProjectFileC.class);

    /**
     * Serialize the OpenSHAPA project to a stream. The caller is responsible
     * for closing the output stream.
     *
     * @param outStream The output stream to use for the project.
     * @param project The project you wish to serialize.
     */
    public void save(final OutputStream outStream, final Project project) {
        logger.usage("save to stream");
        Dumper dumper = new Dumper(new OpenSHAPAProjectRepresenter(),
                new DumperOptions());
        Yaml yaml = new Yaml(dumper);

        try {
            outStream.write(yaml.dump(project).getBytes());
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

}
