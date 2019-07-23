package org.datavyu.plugins;

/**
 * An interface used to receive notifications of key events, encountered
 * while using the SDL media player.
 */
public interface SdlKeyEventListener {

  /**
   * Reports the occurrence of a key event in SDL Media player window.
   * The SDL key Code is mapped to a Java Key Code ID in the Native.
   *
   * @param source the source of the event, likely the object calling this method.
   * @param nativeMediaRef the native reference of the SDL media Player.
   * @param javaKeyCode a <code>KeyEvent</code> value.
   */
  void onKeyEvent(Object source, long nativeMediaRef,int javaKeyCode);
}
