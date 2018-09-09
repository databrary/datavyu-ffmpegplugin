#include <mutex>

extern "C" {
	#include <libavutil/time.h> // timer
}

#ifndef CLOCK_H_
#define CLOCK_H_

#define AV_NOSYNC_THRESHOLD 10.0
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
        double lastUpdated;
        int paused;
        double pts;					// clock base
        double ptsDrift;			// clock base minus time at which we updated the clock
        double speed;
        int serial;					// clock is based on a packet with this serial
        const int *queueSerial;	// pointer to the current packet queue serial, used for obsolete clock detection

    public:
        enum {
            AV_SYNC_AUDIO_MASTER, // default
            AV_SYNC_VIDEO_MASTER,
            AV_SYNC_EXTERNAL_CLOCK, // synchronize to an external clock
        };

		Clock(const int *queue_serial);

		Clock();
    
		double get_clock() const; // keeps always track of the actual time (independent of the speed)

		double get_pts() const; // keeps always track of the actual time as much as possible

		double get_lastUpdated() const;

		double get_serial() const;

		bool isPaused() const;

		void setPaused(bool p);

		double get_clock_speed() const;

		void set_clock_at(double newPts, int newSerial, double time);

		void set_clock(double newPts, int newSerial);

		void set_clock_speed(double newSpeed);

		static void sync_clock_to_slave(Clock *c, Clock *slave);
};

#endif CLOCK_H_