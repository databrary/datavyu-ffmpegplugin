package org.datavyu.util;

import org.datavyu.plugins.ffmpeg.MediaPlayer;

public interface Subject {
    //methods to register and unregister observers
    void register(MediaPlayer obj);
    void unregister(MediaPlayer obj);

    //method to notify observers of change
    void notifyObservers();

    //method to get updates from subject
    Object getUpdate(MediaPlayer obj);
}
