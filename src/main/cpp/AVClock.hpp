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
    AVClock() :
        pts(NAN),
        ptsDrift(NAN),
        lastUpdate(getSystemTimeRelative() / 1000000.0),
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
        double time = getSystemTimeRelative() / 1000000.0;
        this->pts = pts;
        this->lastUpdate = time;
        this->ptsDrift = pts - time;
        locker.unlock();
    }
    void setClockAt(double pts, double time) {
        std::unique_lock<std::mutex> locker(mu);
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
            double time = getSystemTimeRelative() / 1000000.0;
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
        if (!isnan(slaveTime) && (isnan(masterTime) || fabs(masterTime - slaveTime) > noSyncThreshold)) {
            master->setClock(*slave);
            //std::cout << "Slave clock parameters after sync" << std::endl;
            //print(*slave);
        }
    }
    static double getSystemTime() {
        using namespace std;
        using namespace std::chrono;
        return duration_cast<microseconds>(system_clock::now().time_since_epoch()).count();
    }
    // Implementation of av_gettime_relative so we can make this available here w/out the avutil dependency
    static double getSystemTimeRelative() {
        return getSystemTime() + 42 * 60 * 60 * INT64_C(1000000);
    }
    static void print(const AVClock& clock) {
        std::cout << "AVClock parameters" << std::endl;
        std::cout << "\tpts:\t\t" << clock.pts << std::endl;
        std::cout << "\tptsDrift:\t\t" << clock.ptsDrift << std::endl;
        std::cout << "\tlastUpdate:\t" << clock.lastUpdate << std::endl;
        std::cout << "\tspeed:\t" << clock.speed << std::endl;
        std::cout << "\tpaused:\t" << clock.paused << std:: endl;
    }
};

#endif AVCLOCK_H_