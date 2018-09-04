#include "gtest/gtest.h"

#include "gtest/gtest.h";
#include "Clock.h"

#include <limits>
#include <chrono>
#include <thread>


TEST (ClockTest, CreateDeleteClockTest) {
    int serial = 0;
    Clock clock(&serial);
	ASSERT_EQ(clock.get_serial(), -1.0);
}

TEST (ClockTest, SetGetClockTest) {
	// Create a clock
	int serial = 0;
	Clock clock(&serial);

	// Set different serial should return NAN
	clock.set_clock(1.0, 2);
	ASSERT_TRUE(isnan(clock.get_clock()));

	// Set clock with same serial and get time
	double time = av_gettime_relative() / MICRO;
	clock.set_clock(time, serial);
	// ASSERT LESS THAN
	ASSERT_LT(fabs(clock.get_clock() - time), std::numeric_limits<float>::epsilon());

	// Wait for 100 msec
	std::this_thread::sleep_for(std::chrono::milliseconds(100));
	double speed = 1.0;
	double newTime = av_gettime_relative() / MICRO;

	// Note, that in this setup ptsDrift ~= 0
	// ASSERT LESS THAN
	ASSERT_LT(fabs(clock.get_clock() - (newTime - (newTime - time) * (1.0 - speed))), std::numeric_limits<float>::epsilon());
}

TEST (ClockTest, PauseClockTest) {
	int serial = 0;
	Clock clock(&serial);

	// Set paused
	double pts = 1.0;
	clock.set_clock(pts, serial);
	clock.setPaused(1);

	// When paused, returns last pts
	ASSERT_EQ(clock.get_clock(), pts);
}

TEST (ClockTest, SpeedClockTest) {
	// Create a clock
	int serial = 0;
	Clock clock(&serial);

	// Check the time update for several speeds (Note, in all cases ptsDrift ~= 0)
	for (double speed : { -2.0, -1.0, 1.0, 2.0, 4.0 }) {
		double time = av_gettime_relative() / 1000000.0;
		clock.set_clock(time, serial);
		clock.set_clock_speed(speed);

		// Wait for 100 msec
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
		double newTime = av_gettime_relative() / MICRO;
		// ASSERT LESS THAN
		ASSERT_LT(fabs(clock.get_clock() - (newTime - (newTime - time) * (1.0 - speed))), std::numeric_limits<float>::epsilon());
	}
}