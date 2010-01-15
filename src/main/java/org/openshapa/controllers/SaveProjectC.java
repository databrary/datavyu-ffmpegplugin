package org.openshapa.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.Project;
import org.openshapa.project.OpenSHAPAProjectRepresenter;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Controller for saving the OpenSHAPA project to disk.
 */
public final class SaveProjectC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveProjectC.class);

    public void save(final String outFile) {
        Project project = OpenSHAPA.getProject();

        Dumper dumper = new Dumper(new OpenSHAPAProjectRepresenter(), new DumperOptions());
        Yaml yaml = new Yaml(dumper);

        String fileName = outFile;
        if (! fileName.endsWith(".shapa")) {
            fileName = fileName.concat(".shapa");
        }

        try {
            File outputProjectFile = new File(fileName);

            FileWriter fileWriter = new FileWriter(outputProjectFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            yaml.dump(project, out);
            out.close();
            fileWriter.close();
            project.saveProject();
            OpenSHAPA.getApplication().updateTitle();
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

}
