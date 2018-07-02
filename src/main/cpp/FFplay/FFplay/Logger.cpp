#include "Logger.h"
// compile cl Logger.cpp
// Run Logger.exe

int main(int argc, char** argv) {
	Logger* pLogger = new StreamLogger(&std::cerr);
	//Logger* pLogger = new FileLogger("logger.txt");
	pLogger->setLevel(Logger::ALL);
	pLogger->info("This is the first logging message");
	pLogger->warn("You better get smaller than %d.", 5);
	pLogger->debug("Hehe, a floating point %2.2f", 3.14);
	pLogger->error("This is an error with code %d.", 7);
	pLogger->warn("This is a warning with decimal %f.", 1.5);
	pLogger->debug("A debug message with another string %s.", "another one");
	pLogger->info("Just so you know!");
	delete pLogger;
	pLogger = nullptr;
	return 0;
}