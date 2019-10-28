#include "Clock.h"
#include "gtest/gtest.h"

#ifdef __APPLE__
#include <cmath>
#endif
#include <chrono>
#include <limits>
#include <thread>

// TODO(fraudies): Add test for sync method

TEST(ClockTest, CreateAndDeleteClockTest) {
  int serial = 0;
  Clock clock(&serial);
  ASSERT_EQ(clock.GetSerial(), -1.0);
}

TEST(ClockTest, SetGetTimeTest) {
  // Create a clock
  int serial = 0;
  Clock clock(&serial);

  // Set different serial should return NAN
  clock.SetTime(1.0, 2);
  ASSERT_TRUE(isnan(clock.GetTime()));

  // Set clock with same serial and get time
  double time = av_gettime_relative() / MICRO;
  clock.SetTime(time, serial);
  // ASSERT LESS THAN
  ASSERT_LT(fabs(clock.GetTime() - time),
            std::numeric_limits<float>::epsilon());

  // Wait for 100 msec
  std::this_thread::sleep_for(std::chrono::milliseconds(100));
  double speed = 1.0;
  double newTime = av_gettime_relative() / MICRO;

  // ASSERT LESS THAN, the expired time does not matter, only the setTime
  // changes the clock's time
  ASSERT_LT(fabs(clock.GetTime() - time),
            std::numeric_limits<float>::epsilon());
}
