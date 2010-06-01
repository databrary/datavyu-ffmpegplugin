package org.openshapa.controllers.project;

import java.util.Map;
import java.util.TreeMap;

import org.openshapa.models.project.Project;
import org.openshapa.models.project.TrackSettings;
import org.openshapa.models.project.ViewerSetting;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;


/**
 * Used to create a custom YAML representation of an openshapa project.
 */
public class OpenSHAPAProjectRepresenter extends Representer {

    /**
     * Default Constructor.
     */
    public OpenSHAPAProjectRepresenter() {
        representers.put(Project.class, new RepresentProject());
        representers.put(ViewerSetting.class, new RepresentViewerSetting());
        representers.put(TrackSettings.class, new RepresentTrackSettings());
    }

    /**
     * Used to represent the {@link Project} class.
     */
    private class RepresentProject implements Represent {
        public Node representData(final Object obj) {
            Project project = (Project) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("version", Project.VERSION);
            map.put("name", project.getProjectName());
            map.put("origpath", project.getOriginalProjectDirectory());
            map.put("dbFile", project.getDatabaseFileName());
            map.put("viewerSettings", project.getViewerSettings());
            map.put("trackSettings", project.getTrackSettings());

            return representMapping("!project", map, Boolean.FALSE);
        }
    }

    /**
     * Used to represent the {@link ViewerSetting} class.
     */
    private class RepresentViewerSetting implements Represent {
        public Node representData(final Object obj) {
            ViewerSetting viewerSetting = (ViewerSetting) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("feed", viewerSetting.getFilePath());
            map.put("plugin", viewerSetting.getPluginName());
            map.put("offset", Long.toString(viewerSetting.getOffset()));

            return representMapping("!vs", map, Boolean.FALSE);
        }
    }

    /**
     * Used to represent the {@link TrackSettings} class.
     */
    private class RepresentTrackSettings implements Represent {
        public Node representData(final Object obj) {
            TrackSettings interfaceSettings = (TrackSettings) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("feed", interfaceSettings.getFilePath());
            map.put("locked", interfaceSettings.isLocked());
            map.put("bookmark",
                Long.toString(interfaceSettings.getBookmarkPosition()));

            return representMapping("!ts", map, Boolean.FALSE);
        }
    }

}
