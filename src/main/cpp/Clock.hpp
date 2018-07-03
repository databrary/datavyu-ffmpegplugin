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

        void set_clock_at(double newPts, int newSerial, double time) {
            pts = newPts;
            lastUpdated = time;
            ptsDrift = pts - time;
            serial = newSerial;
        }

    public:
        enum {
            AV_SYNC_AUDIO_MASTER, // default
            AV_SYNC_VIDEO_MASTER,
            AV_SYNC_EXTERNAL_CLOCK, // synchronize to an external clock
        };

        Clock(const int *queue_serial) : 
			lastUpdated(0), 
			paused(0), 
			pts(0), 
			ptsDrift(0), 
			speed(1.0), 
			serial(-1), 
			queueSerial(queue_serial) {
            set_clock(NAN, -1);
        }
    
        double get_clock() {
            if (*queueSerial != serial)
                return NAN;
            if (paused) {
                return pts;
            } else {
                double time = av_gettime_relative() / MICRO;
				// TODO(fraudies): Check if this holds for negative speeds as well
                return ptsDrift + time - (time - lastUpdated) * (1.0 - speed);
            }
        }

		double get_pts() const { 
			return pts; 
		}

		double get_lastUpdated() const {
			return lastUpdated;
		}

		double get_serial() const {
			return serial;
		}

		bool isPaused() const {
			return paused;
		}

		void setPaused(bool p) {
			paused = p;
		}

		void set_paused(int newPaused) {
			paused = newPaused;
		}

		inline double get_clock_speed() const {
			return speed;
		}

        void set_clock(double newPts, int newSerial) {
            double time = av_gettime_relative() / MICRO;
            set_clock_at(newPts, newSerial, time);
        }

        void set_clock_speed(double newSpeed) {
            set_clock(get_clock(), serial);
            speed = newSpeed;
        }

        static void sync_clock_to_slave(Clock *c, Clock *slave) {
            double clock = c->get_clock();
            double slave_clock = slave->get_clock();
            if (!isnan(slave_clock) && (isnan(clock) || fabs(clock - slave_clock) > AV_NOSYNC_THRESHOLD))
                c->set_clock(slave_clock, slave->serial);
        }
};

#endif CLOCK_H_