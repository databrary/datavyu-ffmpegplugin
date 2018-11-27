package org.datavyu.util;

import org.datavyu.plugins.ffmpeg.MediaPlayer;

public interface Subject {
    /**
     * Method to get time updates from the Clock timer
     * @param mediaPlayer
     * @return Object
     */
    Object getTimeUpdate(MediaPlayer mediaPlayer);

    /**
     * Method to get min time updates from the Clock timer
     * @param mediaPlayer
     * @return Object
     */
    Object getMinTimeUpdate(MediaPlayer mediaPlayer);

    /**
     * Method to get max time updates from the Clock timer
     * @param mediaPlayer
     * @return Object
     */
    Object getMaxTimeUpdate(MediaPlayer mediaPlayer);
}
