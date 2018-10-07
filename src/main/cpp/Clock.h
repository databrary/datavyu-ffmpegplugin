#include <mutex>

extern "C" {
	#include <libavutil/time.h> // timer
}

#ifndef CLOCK_H_
#define CLOCK_H_

#define AV_NOSYNC_THRESHOLD 10 // 10 sec
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
        double lastSet;
        bool paused;
		double rate;				// playback speed of the clock
        double time;				// clock time
		double pts;					// last pts
        int serial;					// clock is based on a packet with this serial
        const int *queueSerial;	// pointer to the current packet queue serial, used for obsolete clock detection
		inline bool is_seek() const { return *queueSerial != serial; }
    public:
        enum {
            AV_SYNC_AUDIO_MASTER, // default
            AV_SYNC_VIDEO_MASTER,
            AV_SYNC_EXTERNAL_CLOCK, // synchronize to an external clock
        };

		Clock(const int *queue_serial);

		Clock();
    
		double get_time() const; // for stream time, depends on rate

		inline double get_pts() const { return pts; } // for sync between streams, independent of rate

		inline double get_serial() const { return serial; }

		inline double get_rate() const { return rate; }

		inline double get_lastSet() const { return lastSet; }

		inline double get_last_set_time() const { return time; }

		inline bool isPaused() const { return paused; }

		inline void setPaused(bool newPaused) { paused = newPaused; }

		void set_time(double newPts, int newSerial, double newRate);

		static void sync_clock_to_slave(Clock *c, Clock *slave);
};

#endif CLOCK_H_