package org.datavyu.plugins;

import java.util.ListIterator;

/**
 * This provides the interface to control the media player Window when we
 * decide to play back streams through native display.
 */
public interface MediaPlayerWindow extends MediaPlayer {

  /**
   * Adds a listener for SDL player key events.
   *
   * @param listener listener to be added
   * @throws IllegalArgumentException if <code>listener</code> is <code>null</code>.
   */
  void addSdlKeyEventListener(SdlKeyEventListener listener);

  /**
   * Removes a listener for SDL player key events.
   *
   * @param listener listener to be removed
   * @throws IllegalArgumentException if <code>listener</code> is <code>null</code>.
   */
  void removeSdlKeyEventListener(SdlKeyEventListener listener);

  /**
   * Expose SDL player window, this method is available only for the SDL player, will throw an
   * exception if called from a Java player.
   */
  void showWindow();

  /**
   * Hide SDL player window, this method is available only for the SDL player, will throw an
   * exception if called from a Java player.
   */
  void hideWindow();

  /**
   * Return Window Height
   *
   * @return window height
   */
  int getWindowHeight();

  /**
   * Return Window Width
   *
   * @return window width
   */
  int getWindowWidth();

  /**
   * Set Window new width and height
   *
   * @param width
   * @param height
   */
  void setWindowSize(int width, int height);
}
