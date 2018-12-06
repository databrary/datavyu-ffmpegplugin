package org.datavyu.plugins.nativeosx;


import org.datavyu.plugins.MediaException;

import java.awt.*;
import java.net.URI;

public class AVFoundationMediaPlayer extends NativeOSXMediaPlayer {

  public AVFoundationMediaPlayer(URI mediaPath, Container container) {
    super(mediaPath, container);
  }

  @Override
  protected void avFoundatioPlayerSeek(double streamTime, SeekFlags flags) throws MediaException {
    switch (flags){
      case NORMAL_SEEK:
        nativePlayerCanvas.setTime((long) streamTime * 1000, id);
        break;
      case PRECISE_SEEK:
        nativePlayerCanvas.setTimePrecise((long) streamTime * 1000, id);
        break;
      case MODERATE_SEEK:
        nativePlayerCanvas.setTimeModerate((long) streamTime * 1000, id);
        break;
    }
  }
}
