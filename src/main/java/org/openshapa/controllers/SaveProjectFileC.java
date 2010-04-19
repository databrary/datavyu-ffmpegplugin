package org.openshapa.controllers;

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
    private UserMetrix logger = UserMetrix.getInstance(SaveProjectFileC.class);

    /**
     * Saves the OpenSHAPA project to disk.
     *
     * @param outFile The output file to use for the project.
     * @param project The project you wish to save to disk.
     */
    public void save(final String outFile, final Project project) {
        logger.usage("saving to file");
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

    /**
     * Serialize the OpenSHAPA project to a stream. The caller is responsible
     * for closing the output stream.
     *
     * @param outStream The output stream to use for the project.
     * @param project The project you wish to serialize.
     */
    public void save(final OutputStream outStream, final Project project) {
        logger.usage("saving to stream");
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
