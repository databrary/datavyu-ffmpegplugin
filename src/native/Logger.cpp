#include <iostream>
#include <string>
#include <mutex>
#include <fstream>
#include "Logger.h"
// compile cl Logger.cpp
// Run Logger.exe
int main(int argc, char** argv) {
	//StreamLogger logger(&std::cout);
	FileLogger logger("logger.txt");
	logger.setLevel(Logger::ALL);
	logger.log(Logger::INFO, "This is the first logging message");
	logger.log(Logger::WARN, "You better get smaller than %d.", 5);
	logger.log(Logger::DEBUG, "Hehe, a floating point %2.2f", 3.14);
	logger.error("This is an error with code %d.", 7);
	logger.warn("This is a warning with decimal %f.", 1.5);
	logger.debug("A debug message with another string %s.", "another one");
	logger.info("Just so you know!");
	return 0;
}