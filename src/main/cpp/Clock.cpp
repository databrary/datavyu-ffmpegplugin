#include "Clock.h"

Clock::Clock(const int *queue_serial) :
	lastUpdated(0),
	paused(0),
	pts(0),
	ptsDrift(0),
	serial(-1),
	queueSerial(queue_serial) {
	set_clock(NAN, -1);
}

Clock::Clock() : Clock(nullptr) {
	queueSerial = &serial;
}

double Clock::get_clock() const {
	if (*queueSerial != serial)
		return NAN;
	if (paused) {
		return pts;
	}
	else {
		//double time = av_gettime_relative() / MICRO;
		//return ptsDrift + time - (time - lastUpdated) * (1.0 - speed);
		return ptsDrift;
	}
}

double Clock::get_pts() const {
	return pts;
}

double Clock::get_lastUpdated() const {
	return lastUpdated;
}

double Clock::get_serial() const {
	return serial;
}

bool Clock::isPaused() const {
	return paused;
}

void Clock::setPaused(bool p) {
	paused = p;
}

void Clock::set_clock_at(double newPts, int newSerial, double time) {
	pts = newPts;
	lastUpdated = time;
	//ptsDrift = pts - time;
	ptsDrift = pts;
	serial = newSerial;
}

void Clock::set_clock(double newPts, int newSerial) {
	double time = av_gettime_relative() / MICRO;
	set_clock_at(newPts, newSerial, time);
}

void Clock::sync_clock_to_slave(Clock *c, Clock *slave) {
	double clock = c->get_clock();
	double slave_clock = slave->get_clock();
	if (!isnan(slave_clock) && (isnan(clock) || fabs(clock - slave_clock) > AV_NOSYNC_THRESHOLD))
		c->set_clock(slave_clock, slave->serial);
}