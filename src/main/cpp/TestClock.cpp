// Test for the clock
//
// Compile with
// cl -EHsc -I%CATCH_SINGLE_INCLUDE% TestClock.cpp /Fe"..\..\..\TestClock" /I"C:\Users\Florian\FFmpeg\FFmpeg-n3.4" "C:\Users\Florian\FFmpeg\FFmpeg-n3.4\libavutil\avutil.lib"
//
// Run in the 'datavy-ffmpegplugin' directory with: 
// TestClock.exe
//
// Note, I don't claim good test coverage on the clock
// Improvements: Tests for more last update pts

#define CATCH_CONFIG_MAIN  // Catch provides a main

#include <limits>
#include <chrono>
#include <thread>
#include "catch.hpp"
#include "Clock.hpp"

TEST_CASE("Create and delete clock (pass)", "[create-delete]") {
    int serial = 0;
    Clock clock(&serial);
}

TEST_CASE("Set and get (pass)", "[set-get]") {
	// Create a clock
	int serial = 0;
	Clock clock(&serial);

	// Set different serial should return NAN
	clock.set_clock(1.0, 2);
	REQUIRE(isnan(clock.get_clock()));

	// Set clock with same serial and get time
	double time = av_gettime_relative() / MICRO;
	clock.set_clock(time, serial);
	REQUIRE(fabs(clock.get_clock() - time) < std::numeric_limits<float>::epsilon());

	// Wait for 100 msec
	std::this_thread::sleep_for(std::chrono::milliseconds(100));
	double speed = 1.0;
	double newTime = av_gettime_relative() / MICRO;

	// Note, that in this setup ptsDrift ~= 0
	REQUIRE(fabs(clock.get_clock() - (newTime - (newTime - time) * (1.0 - speed))) < std::numeric_limits<float>::epsilon());
}

TEST_CASE("pause", "[pause]") {
	int serial = 0;
	Clock clock(&serial);

	// Set paused
	double pts = 1.0;
	clock.set_clock(pts, serial);
	clock.set_paused(1);

	// When paused, returns last pts
	REQUIRE(clock.get_clock() == pts);
}

TEST_CASE("Speed", "[speed]") {
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
		REQUIRE(fabs(clock.get_clock() - (newTime - (newTime - time) * (1.0 - speed))) < std::numeric_limits<float>::epsilon());
	}
}
