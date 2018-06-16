#include "org_datavyu_plugins_ffmpegplayer_MediaPlayer1.h"
#include <mutex>
#include <ctime>

extern "C" {
	#include <libavutil/time.h> // timer
}

#ifndef AV_CLOCK_H_
#define AV_CLOCK_H_

#define AV_NOSYNC_THRESHOLD 10.0

// TODO(fraudies): Convert this into a C++ class

class Clock{
    private:


    void set_clock_at(double pts, int serial, double time) {
        this->pts = pts;
        this->last_updated = time;
        this->pts_drift = this->pts - time;
        this->serial = serial;
    }

    public:
        double last_updated;
        int paused;    
        double pts;           /* clock base */
        double pts_drift;     /* clock base minus time at which we updated the clock */
        double speed;
        int serial;           /* clock is based on a packet with this serial */
        int *queue_serial;    /* pointer to the current packet queue serial, used for obsolete clock detection */

        Clock(int *queue_serial) {
            this->speed = 1.0;
            this->paused = 0;
            this->queue_serial = queue_serial;
            set_clock(NAN, -1);
        }
    
        enum {
            AV_SYNC_AUDIO_MASTER, /* default choice */
            AV_SYNC_VIDEO_MASTER,
            AV_SYNC_EXTERNAL_CLOCK, /* synchronize to an external clock */
        };    
    
        double get_clock() {
            if (*this->queue_serial != this->serial)
                return NAN;
            if (this->paused) {
                return this->pts;
            } else {
                double time = av_gettime_relative() / 1000000.0;
                return this->pts_drift + time - (time - this->last_updated) * (1.0 - this->speed);
            }
        }

        void set_clock(double pts, int serial) {
            double time = av_gettime_relative() / 1000000.0;
            set_clock_at(pts, serial, time);
        }

        void set_clock_speed(double speed) {
            set_clock(get_clock(), this->serial);
            this->speed = speed;
        }

        static void sync_clock_to_slave(Clock *c, Clock *slave) {
            double clock = c->get_clock();
            double slave_clock = slave->get_clock();
            if (!isnan(slave_clock) && (isnan(clock) || fabs(clock - slave_clock) > AV_NOSYNC_THRESHOLD))
                c->set_clock(slave_clock, slave->serial);
        }
};


#endif AV_CLOCK_H_