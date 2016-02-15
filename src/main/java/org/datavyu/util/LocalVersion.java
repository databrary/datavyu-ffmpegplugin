package org.datavyu.util;

import org.datavyu.Build;
import org.datavyu.Datavyu;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/* Class for handling the local version number. */
public class LocalVersion {
    public String version = "";
    public String build = "";

    public LocalVersion() {
        ResourceMap bMap = Application.getInstance(Datavyu.class).getContext().getResourceMap(Build.class);
        ResourceMap rMap = Application.getInstance(Datavyu.class).getContext().getResourceMap(Datavyu.class);
        version = rMap.getString("Application.version");
        build = bMap.getString("Application.build");
    }

    public String getVersion(){
        return version;
    }

    public String getBuild(){
        return build;
    }
}