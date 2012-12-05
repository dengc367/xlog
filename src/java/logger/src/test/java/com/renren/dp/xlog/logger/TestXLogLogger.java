package com.renren.dp.xlog.logger;

import org.apache.log4j.xml.DOMConfigurator;

import com.renren.dp.xlog.pubsub.pull.PullXLogLoggerServer;

public class TestXLogLogger {
  public static void main(String[] args) {
    DOMConfigurator.configure(TestXLogLogger.class.getClassLoader().getResource("log4j.xml"));
    Class<ConsoleXLogLogger> clazz = ConsoleXLogLogger.class;
    // String[] categories = new String[] { "test", "3g", "access" };
    String[] categories = new String[] { "3g", "api", "access" };
    // String[] categories = new String[] { "ugc", "basic", "access_action" };
    // String[] categories = new String[] { "wap", "lbs", "cell-json-location"
    // };
    XLogLoggerServer xs = new PullXLogLoggerServer(categories, clazz);
    xs.start();

  }
}
