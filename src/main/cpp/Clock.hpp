#include <mutex>

extern "C" {
	#include <libavutil/time.h> // timer
}

#ifndef CLOCK_H_
#define CLOCK_H_

class Clock {
private:
    double pts;
    double ptsDrift;
    double lastUpdate;
    double speed;
    bool paused;
    std::mutex mu;
    void setClock(const Clock& clock) {
        std::unique_lock<std::mutex> locker(mu);
        this->pts = clock.pts;
        this->ptsDrift = clock.ptsDrift;
        this->lastUpdate = clock.lastUpdate;
        this->speed = clock.speed;
        this->paused = clock.paused;
        locker.unlock();
    }
public:
    Clock() :
        pts(NAN),
        ptsDrift(NAN),
        lastUpdate(av_gettime_relative() / 1000000.0),
        speed(1.0),
        paused(false) {}
    void setPaused(bool paused) {
        std::unique_lock<std::mutex> locker(mu);
        this->paused = paused;
        locker.unlock();
    }
    void setSpeed(double speed) {
        setTime(getTime());
        this->speed = speed;
    }
    void setTime(double pts) {
        std::unique_lock<std::mutex> locker(mu);
        double time = av_gettime_relative() / 1000000.0;
        this->pts = pts;
        this->lastUpdate = time;
        this->ptsDrift = pts - time;
        locker.unlock();
    }
    double getTime() {
        double returnTime;
        std::unique_lock<std::mutex> locker(mu);
        if (paused) {
            returnTime = pts;
        } else {
            double time = av_gettime_relative() / 1000000.0;
            returnTime = ptsDrift + time - (time - lastUpdate) * (1.0 - speed);
        }
        locker.unlock();
        return returnTime;
    }
    double getLastUpdated() const {
        return lastUpdate;
    }
    double getSpeed() const {
        return speed;
    }
    static void syncMasterToSlave(Clock* master, Clock* slave, double noSyncThreshold) {
        double masterTime = master->getTime();
        double slaveTime = slave->getTime();
        if (!isnan(slaveTime) && (isnan(masterTime) || fabs(masterTime - slaveTime) > noSyncThreshold))
            master->setClock(*slave);
    }
};

#endif CLOCK_H_