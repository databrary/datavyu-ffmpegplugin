package org.openshapa.controllers.project;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openshapa.models.project.Project;
import org.openshapa.models.project.TrackSettings;
import org.openshapa.models.project.ViewerSetting;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
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

            return representMapping(new Tag("!project"), map, Boolean.FALSE);
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

            // BugzID:2482
            map.put("digest", viewerSetting.getDigest());
            
            // BugzID:2108
            map.put("classifier", viewerSetting.getPluginClassifier());

            // BugzID:1806
            map.put("settingsId", viewerSetting.getSettingsId());
            map.put("version", ViewerSetting.VERSION);

            // BugzID:2107
            map.put("trackSettings", viewerSetting.getTrackSettings());

            return representMapping(new Tag("!vs"), map, Boolean.FALSE);
        }
    }

    /**
     * Used to represent the {@link TrackSettings} class.
     */
    private class RepresentTrackSettings implements Represent {
        public Node representData(final Object obj) {
            TrackSettings interfaceSettings = (TrackSettings) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("locked", interfaceSettings.isLocked());

            final List<Long> bookmarks =
                interfaceSettings.getBookmarkPositions();

            if (bookmarks.isEmpty()) {

                // DEPRECATED - only included for backwards compatibility
                map.put("bookmark", "-1");
                map.put("bookmarks", bookmarks);
            } else {

                // DEPRECATED - only included for backwards compatibility
                map.put("bookmark", Long.toString(bookmarks.get(0)));

                map.put("bookmarks", bookmarks);
            }

            map.put("version", TrackSettings.VERSION);

            return representMapping(new Tag("!ts"), map, Boolean.FALSE);
        }
    }

}
