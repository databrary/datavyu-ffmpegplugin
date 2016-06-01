package org.datavyu.util;

/**
 * Created by jesse on 5/30/16.
 */
public class VersionRange {
    public int lowVersion;
    public int highVersion;
    public VersionRange(int l, int h) {
        lowVersion = l;
        highVersion = h;
    }

    public boolean checkInRange(int v) {
        if(v >= lowVersion && v <= highVersion) {
            return true;
        } else {
            return false;
        }
    }
}
