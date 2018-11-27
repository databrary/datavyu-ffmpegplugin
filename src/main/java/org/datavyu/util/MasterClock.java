package org.datavyu.util;

import org.datavyu.plugins.ffmpeg.MediaPlayer;

/**
 * The Master Clock interface is a set of call backs that
 * a player could request from an External Clock attached
 * to it, any External Clock is Considered as a Master
 * Clock and the player Clock will be a slave
 */
public interface MasterClock {

    /**
     * Method to get time updates from the External Clock
     * @param mediaPlayer
     * @return double the current time of the External Clock
     */
    double getTimeUpdate(MediaPlayer mediaPlayer);

    /**
     * Method to get min time updates from the External Clock
     * @param mediaPlayer
     * @return double the min boundary of the External Clock
     */
    double getMinTimeUpdate(MediaPlayer mediaPlayer);

    /**
     * Method to get max time updates from the External Clock
     * @param mediaPlayer the player
     * @return double the max boundary of the External Clock
     */
    double getMaxTimeUpdate(MediaPlayer mediaPlayer);
}
