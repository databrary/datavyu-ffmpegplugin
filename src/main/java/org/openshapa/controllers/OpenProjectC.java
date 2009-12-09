package org.openshapa.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.OpenSHAPAProjectConstructor;
import org.openshapa.project.Project;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

/**
 * Controller for opening and loading OpenSHAPA project files that are on disk.
 */
public class OpenProjectC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(OpenProjectC.class);

    /**
     * Opens and loads a project file from disk.
     *
     * @param inFile The project file to open and load, absolute path
     * @return true of the file was opened and loaded, false otherwise.
     */
    public boolean open(final String inFile) {
        Yaml yaml = new Yaml(new Loader(new OpenSHAPAProjectConstructor()));
        try {
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            Object o = yaml.load(in);

            // Make sure the de-serialised object is a project file
            if (!(o instanceof Project)) {
                logger.error("Not an OpenSHAPA project file");
                return false;
            }

            Project project = (Project)o;
            OpenSHAPA.setProject(project);

            // Load database here?

            return true;
        } catch (FileNotFoundException ex) {
            logger.error("Cannot open project file: " + inFile, ex);
        }
        return false;
    }

    public boolean open(final File inFile) {
        Yaml yaml = new Yaml(new Loader(new OpenSHAPAProjectConstructor()));
        try {
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            Object o = yaml.load(in);

            // Make sure the de-serialised object is a project file
            if (!(o instanceof Project)) {
                logger.error("Not an OpenSHAPA project file");
                return false;
            }

            Project project = (Project)o;
            OpenSHAPA.setProject(project);

            // Load database here?

            return true;
        } catch (FileNotFoundException ex) {
            logger.error("Cannot open project file: " + inFile.getAbsolutePath(), ex);
        }
        return false;
    }

}
