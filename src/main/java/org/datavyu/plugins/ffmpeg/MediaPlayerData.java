package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;

/**
 * This provides the interface to the media player data
 * when we decide to play back data through java.
 *
 * Otherwise, we play back data natively.
 */
public interface MediaPlayerData extends MediaPlayer {

    /**
     * Checks if the media has audio data
     *
     * @return True if data exists; otherwise false
     */
    boolean hasAudioData();

    /**
     * Checks if the media has image data
     *
     * @return True if data exists; otherwise false
     */
    boolean hasImageData();

    /**
     * Returns the output audio format that the audio data is formatted in
     *
     * @return The audio format
     */
    AudioFormat getAudioFormat();

    /**
     * Returns the color space that the image data is formatted in
     *
     * @return The color space
     */
    ColorSpace getColorSpace();

    /**
     * Update the byte buffer with the most recent audio data to play
     *
     * @param data The data that is updated on the native side
     * @return The audio data
     */
    void updateAudioData(byte[] data);

    /**
     * Update the byte buffer with the most recent image data to play
     *
     * @param data The data that is updated on the native side
     * @return The image data
     */
    void updateImageData(byte[] data);

}
