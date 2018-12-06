package org.datavyu.plugins;

/**
 *
 */
public interface PlayerStateListener {
    /**
     * The ready state indicates the media is loaded.
     * For best results, developers should wait on OnReady() before playing a media.
     *
     * @param evt
     */
    void onReady(PlayerStateEvent evt);

    /**
     * The play state indicates the media is beginning to play.
     *
     * @param evt
     */
    void onPlaying(PlayerStateEvent evt);

    /**
     * The pause state indicates playback has paused.
     *
     * @param evt
     */
    void onPause(PlayerStateEvent evt);

    /**
     * The stop state indicates playback has paused and presentation time has been reset back to 0.
     * If the player is asked to play() again, playback begins from the beginning.
     *
     * @param evt
     */
    void onStop(PlayerStateEvent evt);

    void onStall(PlayerStateEvent evt);


    /**
     * The finish state indicates playback has completed playback to the end.
     *
     * @param evt
     */
    void onFinish(PlayerStateEvent evt);

    /**
     * The error notification provides information on any error during playback.
     *
     * @param evt
     */
    void onHalt(PlayerStateEvent evt);
}
