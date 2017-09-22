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
#include <fstream>
#include <atomic>

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

	/** Maximum number of items in the buffer. */
    const unsigned int nMax = 5000;

	/** Boolean that indicates a flush used for to cleanly destroy of buffer.*/
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
        cv.wait(locker, [this](){return (q.size() < nMax) || doFlush;});
		if (doFlush) return false;
        q.push_front(elem);
        locker.unlock();
        cv.notify_all();
        return true;
    }

	/**
	 * Pops element from the back of the deque.
	 *	Returns: The element or T() if flushed.
	 */
    T pop_back() {
        std::unique_lock<std::mutex> locker(mu);
        cv.wait(locker, [this](){return (q.size() > 0) || doFlush;});
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
	bool nonEmpty() const {
		return q.size() > 0;
	}

	/**
	 * Retrieves the size of the deque.
	 *	Return: The size of the deque.
	 */
	int size() const {
		return q.size();
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

	std::string logLevelToStr(unsigned int logLevel) {
		if (logLevel <= ERROR) {		return "ERRR";
		} else if (logLevel <= DEBUG) {	return "DEBG";
		} else if (logLevel <= WARN) {	return "WARN";
		} else if (logLevel <= INFO) {	return "INFO";
		} else {						return "MISC"; }
	}
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
	 * Default destructor.
	 */
	virtual ~Logger() {}

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
	/** 
	 * This stream logger allows multiple threads to log messages into a thread
	 * safe buffer. Another reader thread reads from the buffer and writes to 
	 * the stream.
	 */
protected:
	/** A thread safe buffer with a limit in size. */
	Buffer<std::string> buffer;

	/** Output stream for this logger. */
	std::ostream* os;

	/** This writer takes messages from the buffer and pushes to the stream. */
	std::thread writer;

	/** While true the writer thread writes. */
	std::atomic<bool> writing;

	/** Formatting of the logging messages. */
	std::string timeFormat;

	/** Current log level. */
	unsigned int level;

	/** Writing loop. */
	void writingLoop() {
		while (writing) {
			(*os) << buffer.pop_back();
		}
	}

public:
	/** Start the writer thread. */
	void startWriter() {
		if (writing) {
			writer = std::thread(&StreamLogger::writingLoop, this);
		}
	}

	/**
	 * This logger logs into a stream.
	 *	os -- Output stream.
	 *	timeFormat -- Formatting of the output log message.
	 *	level -- Log level for messages.
	 */
	StreamLogger(std::ostream* os, 
		const std::string& timeFormat = "%Y-%m-%d %H:%M:%S", 
		unsigned int level = INFO, bool writing = true) : os(os), 
		timeFormat(timeFormat), level(level), writing(writing) {
		startWriter();
	}

	/**
	 * Destructor for the stream logger stops the writing thread, flushes any
	 * outstanding messages into the stream, and frees the buffer.
	 */
	virtual ~StreamLogger() {
		flush(); // Uses the writer thread to empty the buffer.
		writing = false;
		buffer.flush();
		writer.join();
	}

	/**
	 * Log a message with a level.
	 *	logLevel -- The level this messages is logged. The message only is 
	 *				logged when logLevel >= level.
	 *	msg -- The message string.
	 *	args -- A variable number of arguments.
	 */
	void log(unsigned int logLevel, const char* msg, const va_list& args) {
		if (logLevel >= level) {
			char msgBuffer[2048];
			char timeBuffer[128];
			std::vsprintf(msgBuffer, msg, args);
			std::time_t t = std::time(NULL);
			std::strftime(timeBuffer, sizeof(timeBuffer), timeFormat.c_str(), 
						  std::localtime(&t));
			buffer.push_front(std::string(timeBuffer) + " " 
							+ logLevelToStr(logLevel) + " - "
							+ std::string(msgBuffer) + "\n");
		}		
	}

	/**
	 * Log a message with the level.
	 *	logLevel -- The level this messages is logged. The message only is 
	 *				logged when logLevel >= level.
	 *	msg -- The message string.
	 *	... -- A variable number of arguments.
	 */
	void log(unsigned int logLevel, const char* msg, ...) {
		va_list args;
		va_start(args, msg);
		log(logLevel, msg, args);
		va_end(args);
	}

	/**
	 * Flush the messages from the buffer into the stream.
	 */
	void flush() {
		// Give time for the writer to finish until empty.
		while (buffer.nonEmpty()) {
			std::this_thread::sleep_for(std::chrono::milliseconds(100));
		}
		// Flush the stream.
		(*os) << std::flush;
	}

	/**
	 * Set the logging level.
	 *	newLevel -- The new log level.
	 */
	void setLevel(unsigned int newLevel) { level = newLevel; }

	/**
	 * Get the logging level.
	 *	Returns: The logging level.
	 */
	unsigned int getLevel() const { return level; }
};


class FileLogger : public StreamLogger {
	/** 
	 * This stream file logger allows multiple threads to log messages into a 
	 * thread safe buffer. Another reader thread reads from the buffer and 
	 * writes to the stream.
	 */
private:
	/** The file output stream.*/
	std::ofstream ofs;

public:
	/**
	 * This logger logs into a file.
	 *	fileName -- The file name.
	 *	timeFormat -- Formatting of the output log message.
	 *	level -- Log level for messages.
	 */
	FileLogger(const std::string& fileName, 
		const std::string& timeFormat = "%Y-%m-%d %H:%M:%S", 
		unsigned int level = INFO) : StreamLogger(nullptr, timeFormat, level, false) {
		ofs.open(fileName, std::ofstream::out);
		os = &ofs; // Assigns this file to the output stream.
		writing = true;
		startWriter();
	}

	/**
	 * Destruct the file logger. Flush the logger, close the file stream. Then
	 * calls the destructor of the StreamLogger.
	 */
	virtual ~FileLogger() {
		flush();
		ofs.close();
	}
};

#endif
