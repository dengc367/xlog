#ifndef __LOGGER_H__
#define __LOGGER_H__

#include <log4cplus/logger.h>
#include <log4cplus/configurator.h>
#include <log4cplus/fileappender.h>
#include <log4cplus/consoleappender.h>
#include <log4cplus/loggingmacros.h>
#include <iomanip>

//inline void default_init_logger(const std::string& name, const std::string& loglevel = "WARN") {
//  log4cplus::SharedAppenderPtr appender(new log4cplus::ConsoleAppender(true, true));
//  appender->setName(name);
//  std::auto_ptr<log4cplus::Layout> layout(new log4cplus::PatternLayout(LOG4CPLUS_TEXT("[%D] [%p] %m%n")));
//  appender->setLayout(layout);
//  log4cplus::Logger logger = log4cplus::Logger::getInstance(LOG4CPLUS_TEXT(name));
//  logger.addAppender(appender);
//  logger.setLogLevel(log4cplus::LogLevelManager().fromString(loglevel));
//}

#define INIT_LOG4CPLUS_PROPERTIES(path) \
  log4cplus::PropertyConfigurator::doConfigure(path);           


#define LOG_LOGGER(logger_name, LOG_LEVEL, msg) \
do { \
   LOG4CPLUS_##LOG_LEVEL(log4cplus::Logger::getInstance(logger_name), msg); \
} while(0);


#define XLOG_TRACE(msg) LOG_LOGGER("xlog", TRACE, msg); 
#define XLOG_DEBUG(msg) LOG_LOGGER("xlog", DEBUG, msg); 
#define XLOG_INFO(msg)  LOG_LOGGER("xlog", INFO, msg); 
#define XLOG_WARN(msg)  LOG_LOGGER("xlog", WARN, msg); 
#define XLOG_ERROR(msg) LOG_LOGGER("xlog", ERROR, msg); 

//int
//main()
//{
//    INIT_LOG4CPLUS_PROPERTIES("log4cplus.properties");
//    XLOG_INFO(LOG4CPLUS_TEXT("Hello, xlog World!"));
//    long a = 3;
//    XLOG_INFO("Hello, xlog World!" << (int)a );
//    return 0;
//}

#endif





