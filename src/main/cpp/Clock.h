#include <mutex>

extern "C" {
#include <libavutil/time.h> // timer
}

#ifndef CLOCK_H_
#define CLOCK_H_

#define MICRO 1000000.0

// Clock to keep decoding in sync
//
// This is a port of the Clock  ffplay.c from ffmpeg into c++
//
// Big warning: This clock is NOT thread safe. The original ffplay.c code used
// locks around the clock for the set/get in some places. We may decide to move
// these locks into the set/get method if deemed necessary (it would be better
// code design at the cost of performance).
//
class Clock {
private:
  double time;            // clock time
  int serial;             // clock is based on a packet with this serial
  const int *queueSerial; // pointer to the current packet queue serial, used
                          // for obsolete clock detection
  double noSyncThreshold;

  inline bool is_seek() const { return *queueSerial != serial; }

public:
  Clock(const int *queue_serial);

  Clock();

  double get_time() const; // for stream time, depends on rate

  inline double get_serial() const { return serial; }

  void set_time(double newTime, int newSerial);

  static void sync_slave_to_master(Clock *c, Clock *slave,
                                   double noSyncThreshold);
};

#endif CLOCK_H_
