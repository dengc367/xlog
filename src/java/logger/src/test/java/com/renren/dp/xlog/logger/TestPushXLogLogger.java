package com.renren.dp.xlog.logger;

import com.renren.dp.xlog.pubsub.push.PushXLogLoggerServer;

public class TestPushXLogLogger {
  public static void main(String[] args) {
    int port = 10000;
    Class<ConsoleXLogLogger> clazz = ConsoleXLogLogger.class;
    String[] categories = new String[] { "test", "3g", "access" };
    XLogLoggerServer xs = new PushXLogLoggerServer(port);
    xs.bind(categories, clazz);
    xs.start();

  }
}
