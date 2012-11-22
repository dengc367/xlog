package com.renren.dp.xlog.logger;

public class TestXLogLogger {
  public static void main(String[] args) {
    int port = 10000;
    Class<ConsoleXLogLogger> clazz = ConsoleXLogLogger.class;
    String[] categories = new String[] { "test", "3g", "access" };
    XLogLoggerServer xs = new XLogLoggerServer(port);
    xs.bind(categories, clazz);
    xs.start();

  }
}
