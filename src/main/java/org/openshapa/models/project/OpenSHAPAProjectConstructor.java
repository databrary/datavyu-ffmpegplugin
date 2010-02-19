package org.openshapa.models.project;

import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Used to construct an openshapa project from its YAML representation.
 */
public class OpenSHAPAProjectConstructor extends Constructor {

    /**
     * Default Constructor.
     */
    public OpenSHAPAProjectConstructor() {
        yamlConstructors.put("!vs", new ConstructViewerSetting());
        yamlConstructors.put("!project", new ConstructProject());
    }

    /**
     * Used to construct the Project class.
     */
    private class ConstructProject extends AbstractConstruct {

        public Object construct(final Node node) {
            MappingNode mnode = (MappingNode) node;
            Map values = constructMapping(mnode);
            Project project = new Project();
            project.setProjectName((String) values.get("name"));
            project.setProjectDescription((String) values.get("description"));
            project.setDatabaseDir((String) values.get("dbDir"));
            project.setDatabaseFile((String) values.get("dbFile"));
            project.setViewerSettings((Map) values.get("viewerSettings"));
            project.setChanged(false);
            project.setNewProject(false);
            return project;
        }

    }

    /**
     * Used to construct the ViewerSetting class.
     */
    private class ConstructViewerSetting extends AbstractConstruct {

        public Object construct(final Node node) {
            MappingNode mnode = (MappingNode) node;
            Map values = constructMapping(mnode);
            ViewerSetting vs = new ViewerSetting();
            vs.setFilePath((String) values.get("feed"));
            vs.setPluginName((String) values.get("plugin"));
            // WARNING: SnakeYAML refuses to parse this as a Long.
            vs.setOffset(Long.parseLong((String) values.get("offset")));
            String bookmark = (String) values.get("bookmark");
            if (bookmark != null) {
                vs.setBookmark(Long.parseLong(bookmark));
            }
            return vs;
        }
    }

}
