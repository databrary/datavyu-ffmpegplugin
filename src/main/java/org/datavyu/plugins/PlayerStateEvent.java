package org.datavyu.plugins;

/**
 * This player state event is for the communication of the player
 * state between the native code which controls the state
 * transitions and the java side which requires state transitions.
 */
public class PlayerStateEvent extends PlayerEvent {

  public enum PlayerState {
    UNKNOWN,
    READY,
    PLAYING,
    PAUSED,
    STOPPED,
    STALLED,
    FINISHED,
    HALTED
  }

  private PlayerState playerState;
  private double playerTime;
  private String message;

  /**
   * Constructor.
   *
   * @param state The state of the player.
   * @param time The time in seconds when this event occurred.
   * @throws IllegalArgumentException if <code>state</code> is <code>null</code> or <code>
   *     time&lt;0.0</code>.
   */
  public PlayerStateEvent(PlayerState state, double time) {
    if (state == null) {
      throw new IllegalArgumentException("state == null!");
    } else if (time < 0.0) {
      throw new IllegalArgumentException("time < 0.0!");
    }

    this.playerState = state;
    this.playerTime = time;
  }

  /**
   * Constructor.
   *
   * @param state The state of the player.
   * @param time The time in seconds when this event occurred.
   * @param message Carries auxiliary message. HALTED state has additional information.
   * @throws IllegalArgumentException if <code>state</code> is <code>null</code> or <code>
   *     time&lt;0.0</code>.
   */
  public PlayerStateEvent(PlayerState state, double time, String message) {
    this(state, time);
    this.message = message;
  }

  /**
   * Retrieves the state of the media player.
   *
   * @return The player's state.
   */
  public PlayerState getState() {
    return playerState;
  }

  /**
   * Presentation time when the event occurred.
   *
   * @return The time in seconds of the state transition.
   */
  public double getTime() {
    return playerTime;
  }

  /**
   * Auxiliary message information when available.
   *
   * @return The message or null.
   */
  public String getMessage() {
    return message;
  }
}
