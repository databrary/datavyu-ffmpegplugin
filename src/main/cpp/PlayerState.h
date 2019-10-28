#ifndef PLAYERSTATE_H_
#define PLAYERSTATE_H_

namespace PlayerState {
enum State {
  Unknown = 0,
  Ready = 1,
  Playing = 2,
  Paused = 3,
  Stopped = 4,
  Stalled = 5,
  Finished = 6,
  Error = 7
};
}

#endif
