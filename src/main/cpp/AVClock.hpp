#include <mutex>
#include <ctime>

#ifndef AVCLOCK_H_
#define AVCLOCK_H_

class AVClock {
private:
    double pts;
    double ptsDrift;
    double lastUpdate;
    double speed;
    bool paused;
    std::mutex mu;
    void setClock(const AVClock& clock) {
        std::unique_lock<std::mutex> locker(mu);
        this->pts = clock.pts;
        this->ptsDrift = clock.ptsDrift;
        this->lastUpdate = clock.lastUpdate;
        this->speed = clock.speed;
        this->paused = clock.paused;
        locker.unlock();
    }
public:
    double getSystemTime() {
        using namespace std;
        using namespace std::chrono;
        return duration_cast<microseconds>(system_clock::now().time_since_epoch()).count();
    }
    AVClock() :
        pts(NAN),
        ptsDrift(NAN),
        lastUpdate(getSystemTime() / 1000000.0),
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
        double time = getSystemTime() / 1000000.0;
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
            double time = getSystemTime() / 1000000.0;
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
    static void syncMasterToSlave(AVClock* master, AVClock* slave, double noSyncThreshold) {
        double masterTime = master->getTime();
        double slaveTime = slave->getTime();
        if (!isnan(slaveTime) && (isnan(masterTime) || fabs(masterTime - slaveTime) > noSyncThreshold))
            master->setClock(*slave);
    }
};

#endif AVCLOCK_H_