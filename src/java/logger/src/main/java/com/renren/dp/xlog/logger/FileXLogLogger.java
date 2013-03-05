package com.renren.dp.xlog.logger;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.base.Joiner;

/**
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 4:00:32 PM Mar 5, 2013
 */
public class FileXLogLogger implements XLogLogger {

  Logger logger;

  public FileXLogLogger(String[] categories) {
    try {
      logger = Logger.getLogger(Joiner.on('-').join(categories));
      logger.addAppender(getAppender(categories));
      logger.setAdditivity(false);
      logger.setLevel(Level.DEBUG);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void print(String[] logs) {

    for (int i = 0; i < logs.length; i++) {
      print(logs[i]);
    }

  }

  @Override
  public void print(String log) {
    logger.debug(log);
  }

  private Appender getAppender(String[] categories) throws IOException {
    DailyRollingFileAppender app;
    try {
      app = new DailyRollingFileAppender(new Layout() {
        StringBuffer sbuf = new StringBuffer(128);

        @Override
        public void activateOptions() {
        }

        @Override
        public boolean ignoresThrowable() {
          return false;
        }

        @Override
        public String format(LoggingEvent event) {
          sbuf.setLength(0);
          sbuf.append(event.getRenderedMessage());
          sbuf.append(LINE_SEP);
          return sbuf.toString();
        }
      }, "logs/" + Joiner.on('/').join(categories) + ".log", "'.'yyyy-MM-dd-HH");
      return app;
    } catch (IOException e) {
      throw e;
    }

  }

}
