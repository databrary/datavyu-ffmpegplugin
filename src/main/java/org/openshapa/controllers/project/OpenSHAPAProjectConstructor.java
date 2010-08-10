package org.openshapa.controllers.project;

import java.util.List;
import java.util.Map;

import org.openshapa.models.project.Project;
import org.openshapa.models.project.TrackSettings;
import org.openshapa.models.project.ViewerSetting;

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
        yamlConstructors.put("!ts", new ConstructTrackSettings());
    }

    /**
     * Used to construct the {@link Project} class.
     */
    private class ConstructProject extends AbstractConstruct {

        public Object construct(final Node node) {
            MappingNode mnode = (MappingNode) node;
            Map values = constructMapping(mnode);
            Project project = new Project();
            project.setProjectName((String) values.get("name"));
            project.setDatabaseFileName((String) values.get("dbFile"));

            final int projectVersion = (Integer) values.get("version");

            if (projectVersion <= 2) {
                Map vs = (Map) values.get("viewerSettings");
                vs.values();
                project.setViewerSettings(vs.values());
            }

            if (projectVersion >= 3) {
                project.setViewerSettings((List) values.get("viewerSettings"));
            }

            if ((3 <= projectVersion) && (projectVersion <= 4)) {
                project.setTrackSettings((List) values.get("trackSettings"));
            }

            if (projectVersion >= 4) {
                project.setOriginalProjectDirectory((String) values.get(
                        "origpath"));
            }

            return project;
        }
    }

    /**
     * Used to construct the {@link ViewerSetting} class.
     */
    private class ConstructViewerSetting extends AbstractConstruct {

        public Object construct(final Node node) {
            MappingNode mnode = (MappingNode) node;
            Map values = constructMapping(mnode);
            ViewerSetting vs = new ViewerSetting();
            vs.setFilePath((String) values.get("feed"));
            vs.setPluginName((String) values.get("plugin"));

            // WARNING: SnakeYAML refuses to parse this as a Long.
            // TODO: remove this
            String offset = (String) values.get("offset");

            if (offset != null) {
                vs.setOffset(Long.parseLong(offset));
            }

            // BugzID:1806
            Object versionObj = values.get("version");

            if (versionObj == null) {
                vs.setSettingsId(null);
            } else {
                int version = (Integer) versionObj;

                vs.setSettingsId((String) values.get("settingsId"));

                if (version >= 3) {
                    vs.setTrackSettings((TrackSettings) values.get(
                            "trackSettings"));
                }
            }

            return vs;
        }
    }

    /**
     * Used to construct the {@link TrackSettings} class.
     */
    private class ConstructTrackSettings extends AbstractConstruct {

        public Object construct(final Node node) {
            MappingNode mnode = (MappingNode) node;
            Map values = constructMapping(mnode);

            int version = 0;
            Object versionObj = values.get("version");

            if (versionObj != null) {
                version = (Integer) versionObj;
            }

            TrackSettings ts = new TrackSettings();

            if (version == 1) {
                ts.setFilePath((String) values.get("feed"));
            }

            ts.setLocked((Boolean) values.get("locked"));
            ts.setBookmarkPosition(Long.parseLong(
                    (String) values.get("bookmark")));

            return ts;
        }
    }

}
