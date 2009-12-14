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
public class SaveProjectC {

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
            File outputProjectFile = new File(outFile);
            if ((outputProjectFile.exists()
                    && OpenSHAPA.getApplication().overwriteExisting())
                || !outputProjectFile.exists()) {

                BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
                yaml.dump(project, out);
                project.saveProject();
            }
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

}
