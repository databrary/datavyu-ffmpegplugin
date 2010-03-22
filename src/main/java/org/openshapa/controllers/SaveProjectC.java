package org.openshapa.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.models.project.Project;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.usermetrix.jclient.UserMetrix;

/**
 * Controller for saving the OpenSHAPA project to disk.
 */
public final class SaveProjectC {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(SaveProjectC.class);

    /**
     * Saves the OpenSHAPA project to disk.
     *
     * @param outFile The output file to use for the project.
     * @param project The project you wish to save to disk.
     */
    public void save(final String outFile, final Project project) {
        Dumper dumper = new Dumper(new OpenSHAPAProjectRepresenter(),
                                   new DumperOptions());
        Yaml yaml = new Yaml(dumper);

        String fileName = outFile;
        if (!fileName.endsWith(".shapa")) {
            fileName = fileName.concat(".shapa");
        }

        try {
            File outputProjectFile = new File(fileName);
            FileWriter fileWriter = new FileWriter(outputProjectFile);
            BufferedWriter out = new BufferedWriter(fileWriter);

            yaml.dump(project, out);

            out.close();
            fileWriter.close();
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

}
