package org.datavyu.plugins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavyu.plugins.ffmpeg.FfmpegSdlMediaPlayer.SdlPlayerKeyEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class NativeMediaPlayer implements MediaPlayer {

  private static Logger logger = LogManager.getLogger(NativeMediaPlayer.class);

  public static final int eventPlayerUnknown = 100;
  public static final int eventPlayerReady = 101;
  public static final int eventPlayerPlaying = 102;
  public static final int eventPlayerPaused = 103;
  public static final int eventPlayerStopped = 104;
  public static final int eventPlayerStalled = 105;
  public static final int eventPlayerFinished = 106;
  public static final int eventPlayerError = 107;

  private final List<WeakReference<MediaErrorListener>> errorListeners = new ArrayList<>();
  private final List<WeakReference<PlayerStateListener>> playerStateListeners = new ArrayList<>();
  private final List<SdlKeyEventListener> keyListeners = new ArrayList<>();

  protected long nativeMediaRef = 0;
  private PlayerStateEvent.PlayerState playerState = PlayerStateEvent.PlayerState.UNKNOWN;
  private EventQueueThread eventLoop = new EventQueueThread();
  private final Lock disposeLock = new ReentrantLock();
  private boolean isDisposed = false;
  private double startTime = 0.0;
  private double stopTime = Double.POSITIVE_INFINITY;
  private boolean isStopTimeSet = false;
  protected boolean isStartTimeUpdated = false;

  protected float playBackRate = 1f;

  protected String mediaPath;

  private static String resolveURI(URI mediaPath) {
    // If file get the "modified" path
    if (mediaPath.getScheme().equals("file")) {
      // If file and windows strip off any leading / for files
      return System.getProperty("os.name").toLowerCase().contains("win")
          ? mediaPath.getPath().replaceFirst("/*", "")
          : mediaPath.getPath();
    }
    // TODO(fraudies): Check if we need make any custom transforms for schemas other than file
    return mediaPath.toString();
  }

  protected NativeMediaPlayer(URI mediaPath) {
    this.mediaPath = resolveURI(mediaPath);
  }

  public static class MediaErrorEvent extends PlayerEvent {

    private final Object source;
    private final MediaError error;

    MediaErrorEvent(Object source, MediaError error) {
      this.source = source;
      this.error = error;
    }

    public Object getSource() {
      return source;
    }

    public String getMessage() {
      return error.description();
    }

    public int getErrorCode() {
      return error.code();
    }
  }

  protected void initNative() {
    eventLoop.start();
  }

  protected long getNativeMediaRef() {
    return nativeMediaRef;
  }

  private class EventQueueThread extends Thread {
    private final BlockingQueue<PlayerEvent> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean stopped = false;

    EventQueueThread() {
      setName("Media Player EventQueueThread");
      setDaemon(true);
    }

    @Override
    public void run() {
      while (!stopped) {
        try {
          // trying to take an event from the queue.
          // this method will block until an event becomes available.
          PlayerEvent evt = eventQueue.take();

          if (!stopped) {
            if (evt instanceof PlayerStateEvent) {
              HandleStateEvents((PlayerStateEvent) evt);
            } else if (evt instanceof MediaErrorEvent) {
              HandleErrorEvents((MediaErrorEvent) evt);
            } else if (evt instanceof SdlPlayerKeyEvent) {
              HandleSdlKeyEvents((SdlPlayerKeyEvent) evt);
            }
          }
        } catch (Exception e) {
          System.err.println(e);
          // eventQueue.take() can throw InterruptedException,
          // also in rare case it can throw wrong
          // IllegalMonitorStateException
          // so we catch Exception
          // nothing to do, restart the loop unless it was properly stopped.
        }
      }

      eventQueue.clear();
    }

    private void HandleStateEvents(PlayerStateEvent evt) {
      playerState = evt.getState();

      for (ListIterator<WeakReference<PlayerStateListener>> it =
              playerStateListeners.listIterator();
          it.hasNext(); ) {
        PlayerStateListener listener = it.next().get();
        if (listener != null) {
          switch (playerState) {
            case READY:
              listener.onReady(evt);
              break;

            case PLAYING:
              listener.onPlaying(evt);
              break;

            case PAUSED:
              listener.onPause(evt);
              break;

            case STOPPED:
              listener.onStop(evt);
              break;

            case STALLED:
              listener.onStall(evt);
              break;

            case FINISHED:
              listener.onFinish(evt);
              break;

            case HALTED:
              listener.onHalt(evt);
              break;

            default:
              break;
          }
        } else {
          it.remove();
        }
      }
    }

    private void HandleErrorEvents(MediaErrorEvent evt) {
      for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator();
          it.hasNext(); ) {
        MediaErrorListener l = it.next().get();
        if (l != null) {
          l.onError(evt.getSource(), evt.getErrorCode(), evt.getMessage());
        } else {
          it.remove();
        }
      }
    }

    private void HandleSdlKeyEvents(SdlPlayerKeyEvent evt) {
      logger.debug("Received an event from the native side");
      logger.trace("key listener size " + keyListeners.size());
      for (ListIterator<SdlKeyEventListener> it = keyListeners.listIterator(); it.hasNext(); ) {
        SdlKeyEventListener l = it.next();
        if (l != null) {
          l.onKeyEvent(evt.getSource(), evt.getNativeMediaRef(), evt.getKeyCode());
        } else {
          it.remove();
        }
      }
    }

    public void postEvent(PlayerEvent event) {
      eventQueue.offer(event);
    }

    /** Signals the thread to terminate. */
    public void terminateLoop() {
      stopped = true;
      // put an event to unblock eventQueue.take()
      try {
        eventQueue.put(new PlayerEvent());
      } catch (InterruptedException ex) {
      }
    }
  }

  // **************************************************************************
  // ***** MediaPlayer implementation
  // **************************************************************************
  // ***** Listener (un)registration.
  @Override
  public void addMediaErrorListener(MediaErrorListener listener) {
    if (listener != null) {
      this.errorListeners.add(new WeakReference<>(listener));
    }
  }

  @Override
  public void removeMediaErrorListener(MediaErrorListener listener) {
    if (listener != null) {
      for (ListIterator<WeakReference<MediaErrorListener>> it = errorListeners.listIterator();
          it.hasNext(); ) {
        MediaErrorListener l = it.next().get();
        if (l == null || l == listener) {
          it.remove();
        }
      }
    }
  }

  @Override
  public void addMediaPlayerStateListener(PlayerStateListener listener) {
    if (listener != null) {
      playerStateListeners.add(new WeakReference(listener));
    }
  }

  @Override
  public void removeMediaPlayerStateListener(PlayerStateListener listener) {
    if (listener != null) {
      for (ListIterator<WeakReference<PlayerStateListener>> it =
              playerStateListeners.listIterator();
          it.hasNext(); ) {
        PlayerStateListener l = it.next().get();
        if (l == null || l == listener) {
          it.remove();
        }
      }
    }
  }

  @Override
  public void addSdlKeyEventListener(SdlKeyEventListener listener) {
    if (listener != null) {
      this.keyListeners.add(listener);
    }
  }

  @Override
  public void removeSdlKeyEventListener(SdlKeyEventListener listener) {
    if (listener != null) {
      for (ListIterator<SdlKeyEventListener> it = keyListeners.listIterator(); it.hasNext(); ) {
        SdlKeyEventListener l = it.next();
        if (l == null || l == listener) {
          it.remove();
        }
      }
    }
  }

  protected abstract long playerGetAudioSyncDelay() throws MediaException;

  protected abstract void playerSetAudioSyncDelay(long delay) throws MediaException;

  protected abstract void playerPlay() throws MediaException;

  protected abstract void playerStop() throws MediaException;

  protected abstract void playerStepForward() throws MediaException;

  protected abstract void playerStepBackward() throws MediaException;

  protected abstract void playerPause() throws MediaException;

  protected abstract void playerFinish() throws MediaException;

  protected abstract float playerGetRate() throws MediaException;

  protected abstract void playerSetRate(float rate) throws MediaException;

  protected abstract double playerGetPresentationTime() throws MediaException;

  protected abstract double playerGetFps() throws MediaException;

  protected abstract int playerGetImageHeight() throws MediaException;

  protected abstract int playerGetImageWidth() throws MediaException;

  protected abstract boolean playerGetMute() throws MediaException;

  protected abstract void playerSetMute(boolean state) throws MediaException;

  protected abstract float playerGetVolume() throws MediaException;

  protected abstract void playerSetVolume(float volume) throws MediaException;

  protected abstract float playerGetBalance() throws MediaException;

  protected abstract void playerSetBalance(float balance) throws MediaException;

  protected abstract double playerGetDuration() throws MediaException;

  protected abstract double playerGetStartTime() throws MediaException;

  protected abstract void playerSetStartTime(double startTime) throws MediaException;

  protected abstract boolean playerIsSeekPlaybackEnabled() throws MediaException;

  // protected abstract void playerGetStopTime() throws MediaException;

  // protected abstract void playerSetStopTime(double stopTime) throws MediaException;

  protected abstract void playerSeek(double streamTime) throws MediaException;
  
  protected abstract void playerDispose();

  protected abstract int playerGetWindowWidth() throws MediaException;

  protected abstract int playerGetWindowHeight() throws MediaException;

  protected abstract void playerSetWindowSize(int width, int height) throws MediaException;

  protected abstract void playerShowSDLWindow() throws MediaException;

  protected abstract void playerHideSDLWindow() throws MediaException;

  protected abstract boolean playerRateIsSupported(float rate) throws MediaException;

  @Override
  public void setAudioSyncDelay(long delay) {
    try {
      playerSetAudioSyncDelay(delay);
    } catch (MediaException me) {
      sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
    }
  }

  @Override
  public long getAudioSyncDelay() {
    try {
      return playerGetAudioSyncDelay();
    } catch (MediaException me) {
      sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
    }
    return 0;
  }

  @Override
  public void play() {
    if (disposeLock.tryLock()) {
      try {
        if (isStartTimeUpdated) {
          playerSeek(startTime);
        }
        playerPlay();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void stop() {
    if (disposeLock.tryLock()) {
      try {
        playerStop();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void pause() {
    if (disposeLock.tryLock()) {
      try {
        playerPause();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void togglePause() {
    if (disposeLock.tryLock()) {
      try {
        if (getState() == PlayerStateEvent.PlayerState.PAUSED) {
          play();
        } else {
          pause();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void stepForward() {
    if (disposeLock.tryLock()) {
      try {
        playerStepForward();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void stepBackward() {
    if (disposeLock.tryLock()) {
      try {
        playerStepBackward();
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public float getRate() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return isSeekPlaybackEnabled() ? playBackRate : playerGetRate();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return 0;
  }

  // ***** Public properties
  @Override
  public void setRate(float rate) {
    if (disposeLock.tryLock()) {
      try {
        playerSetRate(rate);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
      // Native players will throw an exception when rates are not supported
      playBackRate = rate;
    }
  }

  @Override
  public double getPresentationTime() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetPresentationTime();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return -1.0;
  }

  @Override
  public double getFps() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetFps();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return -1.0;
  }

  @Override
  public int getImageHeight() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetImageHeight();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return -1;
  }

  @Override
  public int getImageWidth() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetImageWidth();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return -1;
  }

  @Override
  public float getVolume() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetVolume();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return 0;
  }

  @Override
  public void setVolume(float vol) {
    if (disposeLock.tryLock()) {
      try {
        playerSetVolume(Math.max(Math.min(vol, 1F), 0F));
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public boolean getMute() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetMute();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return false;
  }

  /**
   * Enables/disable mute. If mute is enabled then disabled, the previous volume goes into effect.
   */
  @Override
  public void setMute(boolean enable) {
    if (disposeLock.tryLock()) {
      try {
        playerSetMute(enable);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public float getBalance() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetBalance();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return 0;
  }

  @Override
  public void setBalance(float bal) {
    if (disposeLock.tryLock()) {
      try {
        playerSetBalance(Math.max(Math.min(bal, 1F), -1F));
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public double getDuration() {
    if (disposeLock.tryLock()) {
      try {
        if (!isDisposed) {
          return playerGetDuration();
        }
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
    return Double.POSITIVE_INFINITY;
  }

  /** Gets the time within the duration of the media to start playing. */
  @Override
  public double getStartTime() {
    return playerGetStartTime();
  }

  /** Sets the start time within the media to play. */
  @Override
  public void setStartTime(double startTime) {
    if (disposeLock.tryLock()) {
      try {
        this.startTime = startTime;
        if (playerState != PlayerStateEvent.PlayerState.PLAYING
            && playerState != PlayerStateEvent.PlayerState.FINISHED
            && playerState != PlayerStateEvent.PlayerState.STOPPED) {
          playerSetStartTime(startTime);
        } else if (playerState == PlayerStateEvent.PlayerState.STOPPED) {
          isStartTimeUpdated = true;
        }
      } finally {
        disposeLock.unlock();
      }
    }
  }

  /** Gets the time within the duration of the media to stop playing. */
  @Override
  public double getStopTime() {
    return stopTime;
  }

  /** Sets the stop time within the media to stop playback. */
  @Override
  public void setStopTime(double stopTime) {
    if (disposeLock.tryLock()) {
      try {
        this.stopTime = stopTime;
        isStopTimeSet = true;
      } finally {
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void seek(double streamTime) {

    if (streamTime < 0.0) {
      streamTime = 0.0;
    } else {
      double duration = getDuration();
      if (duration >= 0.0 && streamTime > duration) {
        streamTime = duration;
      }
    }

    if (disposeLock.tryLock()) {
      try {
        playerSeek(streamTime);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally {
        disposeLock.unlock();
      }
    }
  }

  protected void sendPlayerEvent(PlayerEvent evt) {
    if (eventLoop != null) {
      eventLoop.postEvent(evt);
    }
  }

  /**
   * Retrieves the current {@link PlayerStateEvent.PlayerState state} of the player.
   *
   * @return the current player state.
   */
  @Override
  public PlayerStateEvent.PlayerState getState() {
    if (disposeLock.tryLock()) {
      try {
        return playerState;
      } finally {
        disposeLock.unlock();
      }
    }
    return null;
  }

  @Override
  public final void dispose() {
    disposeLock.lock();
    try {
      if (!isDisposed) {

        if (eventLoop != null) {
          eventLoop.terminateLoop();
          eventLoop = null;
        }

        // Terminate native layer
        playerDispose();

        if (playerStateListeners != null) {
          playerStateListeners.clear();
        }

        if (errorListeners != null) {
          errorListeners.clear();
        }

        if (keyListeners != null) {
          keyListeners.clear();
        }

        nativeMediaRef = 0;
        isDisposed = true;
      }
    } finally {
      disposeLock.unlock();
    }
  }

  @Override
  public boolean isSeekPlaybackEnabled() {
    if (disposeLock.tryLock()) {
      try {
        return playerIsSeekPlaybackEnabled();
      } finally{
        disposeLock.unlock();
      }
    }
    return false;
  }

  @Override
  public boolean isRateSupported(float rate) {
    if (disposeLock.tryLock()) {
      try {
        return playerRateIsSupported(rate);
      } finally{
        disposeLock.unlock();
      }
    }
    return false;
  }

  @Override
  public void showSDLWindow() {
    if (disposeLock.tryLock()) {
      try {
        playerShowSDLWindow();
        setMute(false);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
  }

  @Override
  public void hideSDLWindow() {
    if (disposeLock.tryLock()) {
      try {
        playerHideSDLWindow();
        setMute(true);
      } catch (MediaException me) {
        sendPlayerEvent(new MediaErrorEvent(this, me.getMediaError()));
      } finally{
        disposeLock.unlock();
      }
    }
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

  // **************************************************************************
  // ***** Non-JNI methods called by the native layer. These methods are called
  // ***** from the native layer via the invocation API. Their purpose is to
  // ***** dispatch certain events to the Java layer. Each of these methods
  // ***** posts an event on the <code>EventQueueThread</code> which in turn
  // ***** forwards the event to any registered listeners.
  // **************************************************************************
  protected void sendPlayerMediaErrorEvent(int errorCode) {
    sendPlayerEvent(new MediaErrorEvent(this, MediaError.getFromCode(errorCode)));
  }

  protected void sendPlayerStateEvent(int eventID, double time) {
    switch (eventID) {
      case eventPlayerReady:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.READY, time));
        break;
      case eventPlayerPlaying:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.PLAYING, time));
        break;
      case eventPlayerPaused:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.PAUSED, time));
        break;
      case eventPlayerStopped:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.STOPPED, time));
        break;
      case eventPlayerStalled:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.STALLED, time));
        break;
      case eventPlayerFinished:
        sendPlayerEvent(new PlayerStateEvent(PlayerStateEvent.PlayerState.FINISHED, time));
        break;
      default:
        break;
    }
  }

  protected void sendSdlPlayerkeyEvent(int keyEvent) {
    sendPlayerEvent(new SdlPlayerKeyEvent(this, this.getNativeMediaRef(), keyEvent));
  }
}
