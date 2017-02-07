#ifndef LOGGER_H
#define LOGGER_H

#include <iostream>
#include <string>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <ctime>
#include <deque>
#include <cstdarg>

/**
 * This buffer wraps around a dqeue a thread-safe push_front and back, pop_back.
 * The model used is a producer/consumer model.
 * - Producers can write as long as the queue is smaller than a maximum size.
 * - Consumers can read as long as the queue has at least one item.
 */
template<class T>
class Buffer {
private:
	/** A deque with operations push_front, back, and pop_back. */
	std::deque<T> q;

	/** The maximum number of items in this buffer. */
    const unsigned int nMax = 50;

	/** Boolean that indicates a flush. E.g. used for clean destroy of buffer.*/
	bool doFlush = false;

	/** Mutex used for exclusive access of the deque. */
	std::mutex mu;

	/** Condition variable is used to avoid overflow and underflow. */
	std::condition_variable cv;	
public:

	/**
	 * Push an element to the front of the deque.
	 *	elem -- The element.
	 *  Returns: True if it was pushed, otherwise false.
	 */
    bool push_front(const T& elem) {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return q.size() < nMax || doFlush;});
		if (doFlush) return false;
        q.push_front(elem);
        locker.unlock();
        cv.notify_all();
        return true;
    }

	/**
	 * Pops element from the back of the deque.
	 *	Returns: The element or T() if no flushed.
	 */
    T pop_back() {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return q.size() > 0 || doFlush;});
		if (doFlush) return T();
        T back = q.back();
        q.pop_back(); 
        locker.unlock();
        cv.notify_all();
        return back;
    }

	/**
	 * Checks if the deque is not empty.
	 *	Returns: True if the deque has at least one element otherwise false.
	 */
	bool nonEmpty() {
		bool isNonEmpty = false;
		//std::unique_lock<std::mutex> locker(mu);
		isNonEmpty = q.size() > 0;
		//locker.unlock();
		return isNonEmpty;
	}

	/**
	 * Retrieves the size of the deque.
	 *	Return: The size of the deque.
	 */
	int size() {
		int nElem = 0;
		//std::unique_lock<std::mutex> locker(mu);
		nElem = q.size();
		//locker.unlock();
		return nElem;
	}

	/**
	 * Flush the buffer.
	 */
	void flush() {
		doFlush = true;
		cv.notify_all();
	}
};


/**
 * This is an abstract logger class.
 *
 * It defines the interface for the logger and various logging levels.
 *
 * The logging levels follow are in increasing order:
 *	
 *	ALL < ERROR < DEBUG < WARN < INFO < OFF
 *
 * Lower is more important!
 */
class Logger {
protected:
	/**
	 * This method logs with a variable argument list that is explicitly given.
	 * Notice, the user should not call this method and, thus, it is protected.
	 */
	virtual void log(unsigned int logLevel, const char* msg, 
					 const va_list& args) = 0;
public:
	/** Logging level for all messages. */
	const static unsigned int ALL = 0;

	/** Logging level for error messages. */
	const static unsigned int ERROR = 10;

	/** Logging level for debug messages. */
	const static unsigned int DEBUG = 20;

	/** Logging level for warning messages. */
	const static unsigned int WARN = 30;

	/** Logging level for information messages. */
	const static unsigned int INFO = 40;

	/** Logging level for no messages. Turn off logging. */
	const static unsigned int OFF = 100;

	/**
	 * Sets the logging level to the new level.
	 *	newLevel -- The new logging level.
	 */
	virtual void setLevel(unsigned int newLevel) = 0 ;

	/**
	 * Returns the current logging level.
	 *	Return: The current logging level.
	 */
	virtual unsigned int getLevel() const = 0;

	/**
	 * Log a message at the level: Logger::ERROR.
	 *	msg -- The message as string.
	 *  varargs -- A variable list of arguments. Use this like sprintf.
	 */
	void error(const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(Logger::ERROR, msg, args);
		va_end(args);
	}

	/**
	 * Log a message at the level: Logger::DEBUG.
	 *	msg -- The message as string.
	 *  varargs -- A variable list of arguments. Use this like sprintf.
	 */
	void debug(const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(Logger::DEBUG, msg, args);
		va_end(args);
	}

	/**
	 * Log a message at the level: Logger:WARN.
	 *	msg -- The message as string.
	 *  varargs -- A variable list of arguments. Use this like sprintf.
	 */
	void warn(const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(Logger::WARN, msg, args);
		va_end(args);
	}

	/**
	 * Log a message at the level: Logger::INFO.
	 *	msg -- The message as string.
	 *  varargs -- A variable list of arguments. Use this like sprintf.
	 */
	void info(const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(Logger::INFO, msg, args);
		va_end(args);
	}
};

class StreamLogger : public Logger {
protected:
	Buffer<std::string> buffer; // the thread safe buffer wout limit in size
	std::thread reader;
	std::ostream* os; // the output stream to be written to
	std::thread writer; // takes from buffer and pushes it into the stream
	bool writing; // controls writer.
	std::string timeFormat; // Formatting of log messages.
	unsigned int level; // current log level
	void reading() {
		//std::cout << "started reading from buffer.\n" << std::endl;
		while (writing) {
			(*os) << buffer.pop_back();
		}		
	}
public:
	StreamLogger(std::ostream* os, const std::string& timeFormat = "%Y-%m-%d %H:%M:%S", 
		unsigned int level = INFO) : os(os), timeFormat(timeFormat), 
		level(level), writing(true) {
			reader = std::thread(&StreamLogger::reading, this);
	}
	virtual ~StreamLogger() {
		writing = false;
		//std::cout << "destroy the logger.\n" << std::endl;
		//std::cout << "the size of the buffer is: " << buffer.size() << std::endl;
		flush();
		//std::cout << "After flushing, the size of the buffer is: " << buffer.size() << std::endl;
		buffer.flush();
		reader.join();
	}
	void log(unsigned int logLevel, const char* msg, const va_list& args) {
		if (logLevel >= level) {
			char msgBuffer[256];
			char timeBuffer[128];
			std::vsprintf(msgBuffer, msg, args);
			std::time_t t = std::time(NULL);
			std::strftime(timeBuffer, sizeof(timeBuffer), timeFormat.c_str(), std::localtime(&t));
			buffer.push_front(std::string(timeBuffer) + "  " + std::string(msgBuffer) + "\n");
		}		
	}
	void log(unsigned int logLevel, const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(logLevel, msg, args);
		va_end(args);
	}
	void flush() {
		//std::cout << "the size of the buffer is: " << buffer.size() << std::endl;
		while (buffer.nonEmpty()) {
			//std::cout << "Emptying.\n";
			(*os) << buffer.pop_back();
		}
		(*os) << std::flush;
	}
	void setLevel(unsigned int newLevel) { level = newLevel; }
	unsigned int getLevel() const { return level; }
};

class FileLogger : public StreamLogger {
private:
	std::ofstream ofs;
public:
	FileLogger(const std::string& fileName, 
		const std::string& timeFormat = "%Y-%m-%d %H:%M:%S", 
		unsigned int level = INFO) : StreamLogger(nullptr, timeFormat, level), 
		ofs(std::ofstream()) {
		ofs.open(fileName, std::ofstream::out);
		os = &ofs; // assign this file to the output stream.
	}
	virtual ~FileLogger() {
		//std::cout << "Closing the file output stream.\n";
		flush();
		ofs.close();
	}
};

#endif
