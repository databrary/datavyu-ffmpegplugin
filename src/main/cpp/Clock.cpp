#include "Clock.h"

extern "C" {
#include "libavutil/log.h"
}

Clock::Clock(const int *queue_serial)
    : time(0.0), serial(-1), p_serial_(queue_serial) {
  SetTime(NAN, -1);
}

Clock::~Clock() {
  if (p_serial_) {
    p_serial_ == nullptr;
  }
}

Clock::Clock() : time(0.0), serial(-1), p_serial_(&serial) { SetTime(NAN, -1); }

double Clock::GetTime() const {
  if (SerialNoMatch()) {
    return NAN;
  }
  return time;
}

void Clock::SetTime(double newTime, int newSerial) {
  if (!isnan(newTime)) {
    time = newTime;
  }
  serial = newSerial;
}

void Clock::SyncMasterToSlave(Clock *master, Clock *slave,
                              double noSyncThreshold) {
  double master_time = master->GetTime();
  double slave_time = slave->GetTime();
  if (!isnan(slave_time) &&
      (isnan(master_time) ||
       fabs(master_time - slave_time) > noSyncThreshold)) {
    av_log(NULL, AV_LOG_TRACE, "Sync %7.2f to %7.2f\n", slave_time,
           master_time);
    master->SetTime(slave_time, slave->serial);
  }
}
