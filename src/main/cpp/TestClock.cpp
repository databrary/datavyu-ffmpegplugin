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
	clock.set_time(1.0, 2);
	ASSERT_TRUE(isnan(clock.get_time()));

	// Set clock with same serial and get time
	double time = av_gettime_relative() / MICRO;
	clock.set_time(time, serial);
	// ASSERT LESS THAN
	ASSERT_LT(fabs(clock.get_time() - time), std::numeric_limits<float>::epsilon());

	// Wait for 100 msec
	std::this_thread::sleep_for(std::chrono::milliseconds(100));
	double speed = 1.0;
	double newTime = av_gettime_relative() / MICRO;

	// Note, that in this setup ptsDrift ~= 0
	// ASSERT LESS THAN
	ASSERT_LT(fabs(clock.get_time() - (newTime - (newTime - time) * (1.0 - speed))), std::numeric_limits<float>::epsilon());
}