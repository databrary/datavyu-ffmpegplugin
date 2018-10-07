#include "Clock.h"

Clock::Clock(const int *queue_serial) :
	lastSet(0),
	paused(0),
	rate(1.0),
	time(0.0),
	serial(-1),
	queueSerial(queue_serial) {
	set_time(NAN, -1, 1.0);
}

Clock::Clock() : 
	lastSet(0),
	paused(0),
	rate(1.0),
	time(0.0),
	serial(-1),
	queueSerial(&serial) {
	set_time(NAN, -1, 1.0);
}

double Clock::get_time() const {
	if (is_seek()) {
		return NAN;
	}
	if (paused) {
		return time;
	}
	return time + (av_gettime_relative() / MICRO - lastSet) / rate;
}

void Clock::set_time(double newPts, int newSerial, double newRate) {
	lastSet = av_gettime_relative() / MICRO;
	// TODO(fraudies): Find a better threshold
	// We had a seek and need to set the new time
	if (is_seek() || fabs(newPts - pts) > AV_NOSYNC_THRESHOLD/10) {
		time = newPts;
	}
	else if (!isnan(newPts)) {
		time += (newPts - pts) / rate;
	}
	// otherwise don't change the time
	pts = newPts;
	serial = newSerial;
	rate = newRate;
}

void Clock::sync_clock_to_slave(Clock *clock, Clock *slave) {
	double clock_time = clock->get_time();
	double slave_time = slave->get_time();
	if (!isnan(slave_time) && (isnan(clock_time) 
		|| fabs(clock_time - slave_time) > AV_NOSYNC_THRESHOLD)) {
		clock->set_time(slave_time, slave->serial, slave->rate);
	}
}