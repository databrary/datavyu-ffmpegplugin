#include "Clock.h"

extern "C" {
#include "libavutil/log.h"
}

Clock::Clock(const int *queue_serial)
    : time(0.0), serial(-1), queueSerial(queue_serial) {
  set_time(NAN, -1);
}

Clock::Clock() : time(0.0), serial(-1), queueSerial(&serial) {
  set_time(NAN, -1);
}

double Clock::get_time() const {
  if (is_seek()) {
    return NAN;
  }
  return time;
}

void Clock::set_time(double newTime, int newSerial) {
  if (!isnan(newTime)) {
    time = newTime;
  }
  serial = newSerial;
}

void Clock::sync_slave_to_master(Clock *slave, Clock *master,
                                 double noSyncThreshold) {
  double master_time = master->get_time();
  double slave_time = slave->get_time();
  if (!isnan(slave_time) &&
      (isnan(master_time) ||
       fabs(master_time - slave_time) > noSyncThreshold)) {
    av_log(NULL, AV_LOG_TRACE, "Sync %7.2f to %7.2f\n", slave_time,
           master_time);
    slave->set_time(slave_time, master->serial);
  }
}
