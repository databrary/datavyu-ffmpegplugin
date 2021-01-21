package org.datavyu.plugins;

import org.datavyu.util.LibraryLoader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public abstract class DatavyuMediaPlayer extends NativeMediaPlayer implements MediaPlayerWindow {

  protected float mutedVolume = 1.0f; // last volume before mute
  protected boolean muteEnabled = false;

  /** Library dependencies for ffmpeg */
  protected static final List<LibraryLoader.LibraryDependency> FFMPEG_DEPENDENCIES =
      new ArrayList<LibraryLoader.LibraryDependency>() {
        {
          add(new LibraryLoader.LibraryDependency("avutil", "56"));
          add(new LibraryLoader.LibraryDependency("swscale", "5"));
          add(new LibraryLoader.LibraryDependency("swresample", "3"));
          add(new LibraryLoader.LibraryDependency("avcodec", "58"));
          add(new LibraryLoader.LibraryDependency("avformat", "58"));
          add(new LibraryLoader.LibraryDependency("avfilter", "7"));
          add(new LibraryLoader.LibraryDependency("avdevice", "58"));
          add(new LibraryLoader.LibraryDependency("postproc", "55"));
        }
      };

  protected DatavyuMediaPlayer(URI mediaPath) {
    super(mediaPath);
  }

  protected void throwMediaErrorException(int code, Throwable cause) throws MediaException {
    MediaError me = MediaError.getFromCode(code);
    throw new MediaException(me.description(), cause, me);
  }

  @Override
  protected boolean playerGetMute() throws MediaException {
    return muteEnabled;
  }

  @Override
  protected synchronized void playerSetMute(boolean enable) throws MediaException {
    if (enable != muteEnabled) {
      if (enable) {
        // Cache the current volume.
        float currentVolume = getVolume();

        // Set the volume to zero.
        playerSetVolume(0);

        // Set the mute flag. It is necessary to do this after
        // calling setVolume() as otherwise the volume will not
        // be set to zero.
        muteEnabled = true;

        // Save the pre-mute volume.
        mutedVolume = currentVolume;
      } else {
        // Unset the mute flag. It is necessary to do this before
        // calling setVolume() as otherwise the volume will not
        // be set to the cached value.
        muteEnabled = false;

        // Set the volume to the cached value.
        playerSetVolume(mutedVolume);
      }
    }
  }
  @Override
  public void showWindow() {
    if (disposeLock.tryLock()) {
      try {
        playerShowWindow();
        setMute(false);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void hideWindow() {
    if (disposeLock.tryLock()) {
      try {
        playerHideWindow();
        setMute(true);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public boolean isVisible() {
    if (disposeLock.tryLock()) {
      try {
        return playerIsVisible();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }

    return false;
  }

  @Override
  public int getWindowHeight() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetWindowHeight();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
    return -1;
  }

  @Override
  public int getWindowWidth() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetWindowWidth();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
    return -1;
  }

  @Override
  public void setWindowSize(final int width, final int height) {
    if (disposeLock.tryLock()) {
      try {
        playerSetWindowSize(width, height);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  protected abstract int playerGetWindowWidth() throws MediaException;

  protected abstract int playerGetWindowHeight() throws MediaException;

  protected abstract void playerSetWindowSize(int width, int height) throws MediaException;

  protected abstract void playerShowWindow() throws MediaException;

  protected abstract void playerHideWindow() throws MediaException;

  protected abstract boolean playerIsVisible() throws MediaException;
}
