package org.openshapa.models.project;

import java.util.Map;
import java.util.TreeMap;

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
    }

    /**
     * Used to represent the Project class.
     */
    private class RepresentProject implements Represent {

        public Node representData(final Object obj) {
            Project project = (Project) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("version", Project.VERSION);
            map.put("name", project.getProjectName());
            map.put("description", project.getProjectDescription());
            map.put("dbDir", project.getDatabaseDir());
            map.put("dbFile", project.getDatabaseFile());
            map.put("viewerSettings", project.getViewerSettings());
            return representMapping("!project", map, Boolean.FALSE);
        }
    }

    /**
     * Used to represent the ViewerSetting class.
     */
    private class RepresentViewerSetting implements Represent {

        public Node representData(final Object obj) {
            ViewerSetting viewerSetting = (ViewerSetting) obj;
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put("feed", viewerSetting.getFilePath());
            map.put("plugin", viewerSetting.getPluginName());
            map.put("offset", Long.toString(viewerSetting.getOffset()));
            map.put("bookmark", Long.toString(viewerSetting.getBookmark()));
            return representMapping("!vs", map, Boolean.FALSE);
        }
    }

}
