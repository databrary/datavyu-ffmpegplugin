package org.datavyu.plugins.ffmpeg;


import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;

/**
 * This interface is similar to the one in javafx
 * But at the time (July 2018) we decided against making a dependency on javafx
 *
 * Note, the media error listener allows to handle all media errors; if not
 * handled through the listener these media errors get dropped
 *
 * Unlike, the original design we decided to introduce an init method that
 * initializes the player. Add any listeners BEFORE calling the init method.
 *
 */
public interface MediaPlayer {

    //**************************************************************************
    //***** Public methods
    //**************************************************************************
    /**
     * Adds a listener for warnings which occur within the lifespan of the player.
     *
     * @param listener The warning listener.
     * @throws IllegalArgumentException if <code>listener</code> is
     * <code>null</code>.
     */
    void addMediaErrorListener(MediaErrorListener listener);

    /**
     * Removes a listener for warnings.
     *
     * @param listener The warning listener.
     * @throws IllegalArgumentException if <code>listener</code> is
     * <code>null</code>.
     */
    void removeMediaErrorListener(MediaErrorListener listener);

    /**
     * Adds a listener for media state.
     *
     * @param listener
     * @throws IllegalArgumentException if <code>listener</code> is
     * <code>null</code>.
     */
    void addMediaPlayerStateListener(PlayerStateListener listener);

    /**
     * Removes a listener for media state.
     *
     * @param listener
     * @throws IllegalArgumentException if <code>listener</code> is
     * <code>null</code>.
     */
    void removeMediaPlayerStateListener(PlayerStateListener listener);

    /**
     * Set the amount of time to delay for the audio.
     *
     * @param delay time in milliseconds
     */
    void setAudioSyncDelay(long delay);

    /**
     * Retrieve the audio sync delay.
     */
    long getAudioSyncDelay();

    /**
     * Initializes all resources to play the media.
     */
    void init();

    /**
     * Begins playing of the media.  To ensure smooth playback, catch the
     * onReady event in the MediaPlayerListener before playing.
     */
    void play();

    /**
     * Stops playing of the media.
     */
    void stop();

    /**
     * Pauses the media playing.  Calling play() after pause() will continue
     * playing the media from where it left off.
     */
    void pause();

    /**
     * Step to the next frame and pause the media.
     */
    void stepForward();

    /**
     * Step back to one frame and pause the media.
     */
    void stepBackward();

    /**
     * Get the rate of playback.
     */
    float getRate();

    //**************************************************************************
    //***** Public properties
    //**************************************************************************
    /**
     * Sets the rate of playback. A positive value indicates forward play and
     * a negative value reverse play.
     *
     * @param rate
     */
    void setRate(float rate);

    /**
     * Gets the current presentation time. If the time is unknown or cannot be
     * obtained when this method is invoked, a negative value will be returned.
     *
     * @return the current presentation time
     */
    double getPresentationTime();

    /**
     * Gets Frame rate (expressed in frames per second) of the current video stream
     *
     * @return the current video frame rate
     */
    double getFps();

    /**
     * Gets the current volume.
     *
     * @return the current volume
     */
    float getVolume();

    /**
     * Sets the volume. Values will be clamped to the range
     * <code>[0,&nbsp;1.0]</code>.
     *
     * @param volume A value in the range <code>[0,&nbsp;1.0]</code>.
     */
    void setVolume(float volume);

    /**
     * Gets the muted state. While muted no audio will be heard.
     * @return true if audio is muted.
     */
    boolean getMute();

    /**
     * Enables/disable mute.  If mute is enabled then disabled, the previous
     * volume goes into effect.
     */
    void setMute(boolean enable);

    /**
     * Gets the current balance.
     *
     * @return the current balance
     */
    float getBalance();

    /**
     * Sets the balance. A negative value indicates left of center and a positive
     * value right of center. Values will be clamped to the range
     * <code>[-1.0,&nbsp;1.0]</code>.
     *
     * @param balance A value in the range <code>[-1.0,&nbsp;1.0]</code>.
     */
    void setBalance(float balance);

    /**
     * Gets the duration in seconds. If the duration is unknown or cannot be
     * obtained when this method is invoked, a negative value will be returned.
     */
    double getDuration();

    /**
     * Gets the time within the duration of the media to start playing.
     */
    double getStartTime();

    /**
     * Sets the start time within the media to play.
     */
    void setStartTime(double streamTime);

    /**
     * Gets the time within the duration of the media to stop playing.
     */
    double getStopTime();

    /**
     * Sets the stop time within the media to stop playback.
     */
    void setStopTime(double streamTime);

    /**
     * Seeks playback to the specified time. The state of the player
     * is unchanged. A negative value will be clamped to zero, and a positive
     * value to the duration, if known.
     *
     * @param streamTime The time in seconds to which to seek.
     */
    void seek(double streamTime);

    /**
     * Retrieves the current {@link PlayerStateEvent.PlayerState state} of the player.
     * @return the current player state.
     */
    PlayerStateEvent.PlayerState getState();

    /**
     * Release any resources held by this player. The player will be unusable
     * after this method is invoked.
     */
    void dispose();
}
