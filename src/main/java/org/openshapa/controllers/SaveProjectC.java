package org.openshapa.controllers;

import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openshapa.OpenSHAPA;
import org.openshapa.project.Project;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Controller for saving the OpenSHAPA project to disk.
 */
public class SaveProjectC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveProjectC.class);

    public void save(final String outFile) {
        Project project = OpenSHAPA.getProject();

//        Dumper dumper = new Dumper(new SHAPARepresenter(), new DumperOptions());
//        Yaml yaml = new Yaml(dumper);
        Yaml yaml = new Yaml();

        String fileName = outFile;
        if (! fileName.endsWith(".openshapa")) {
            fileName = fileName.concat(".openshapa");
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            yaml.dump(project, out);
            project.saveProject();
        } catch (IOException ex) {
            logger.error("Unable to save project file", ex);
        }
    }

    private class SHAPARepresenter extends Representer {
        @Override
        protected Set<Property> getProperties(Class<? extends Object> type)
                throws IntrospectionException {
            Set<Property> set = super.getProperties(type);
            if (type.equals(Project.class)) {
                for (Property prop : set) {
                    if (prop.getName().equals("mediaViewerSettings")) {
                        set.remove(prop);
                    }
                    if (prop.getName().equals("mediaFiles")) {
                        set.remove(prop);
                    }
                    if (prop.getName().equals("changed")) {
                        set.remove(prop);
                    }
                }
            }
            return set;
        }
    }

}
