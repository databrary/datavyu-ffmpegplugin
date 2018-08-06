package org.datavyu.plugins.ffmpeg;

import javax.sound.sampled.AudioFormat;
import java.awt.color.ColorSpace;
import java.nio.ByteBuffer;

/**
 * This provides the interface to the media player data
 * when we decide to play back data through java.
 *
 * Otherwise, we play back data natively.
 */
public interface MediaPlayerData {

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
     * @return The audio data
     */
    ByteBuffer getAudioData();

    /**
     * Update the byte buffer with the most recent image data to play
     *
     * @return The image data
     */
    ByteBuffer getImageData();

    /**
     * Get the width of the image in pixels
     *
     * @return Image width
     */
    int getImageWidth();

    /**
     * Get the height of the image in pixels
     *
     * @return Image height
     */
    int getImageHeight();
}
