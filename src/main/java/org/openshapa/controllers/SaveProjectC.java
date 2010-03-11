package org.openshapa.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.OpenSHAPAProjectRepresenter;
import org.openshapa.controllers.project.ProjectController;
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

    public void save(final String outFile) {
        ProjectController projectController = OpenSHAPA.getProjectController();
        projectController.updateProject();
        Project project = projectController.getProject();

        Dumper dumper =
                new Dumper(new OpenSHAPAProjectRepresenter(),
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

            projectController.saveProject();

            OpenSHAPA.getApplication().updateTitle();
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

}
