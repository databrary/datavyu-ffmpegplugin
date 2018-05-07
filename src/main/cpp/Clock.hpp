#include "Clock.hpp"

extern "C" {
	#include <libavutil/time.h> // timer
}

#ifndef CLOCK_H_
#define CLOCK_H_

/* no AV correction is done if too big error */
#define AV_NOSYNC_THRESHOLD 2.0

class Clock {
private:
    double pts;
    double ptsDrift;
    double lastUpdate;
    double speed;
    bool paused;

public:
    Clock() :
        pts(NAN),
        ptsDrift(NAN),
        lastUpdate(av_gettime_relative() / 1000000.0),
        speed(1.0),
        paused(false) {}
    void setPaused(bool paused) {
        this->paused = paused;
    }
    void setTime(double pts) {
        double time = av_gettime_relative() / 1000000.0;
        this->pts = pts;
        this->lastUpdate = time;
        this->ptsDrift = pts - time;
    }
    double getTime() {
        if (paused) {
            return pts;
        } else {
            double time = av_gettime_relative() / 1000000.0;
            return ptsDrift + time - (time - lastUpdated) * (1.0 - speed);
        }
    }
    double getLastUpdated() const {
        return lastUpdate;
    }
    static void syncMasterToSlave(Clock* master, const Clock& slave) {
        double masterTime = master->getTime();
        double slaveTime = slave.getTime();
        if (!isnan(slaveTime) && (isnan(masterTime) || fabs(masterTime - slaveTime) > AV_NOSYNC_THRESHOLD))
            master->setClock(slave);
    }
};

#endif CLOCK_H_