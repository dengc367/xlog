package com.renren.dp.xlog.logger;

public class TestXLogLogger {
  public static void main(String[] args) {
    String host = "10.2.62.72";
    int port = 10000;
    Class clazz = ConsoleXLogLogger.class;
    String[] categories = new String[] { "test", "3g", "access" };
    XLogLoggerServer xs = new XLogLoggerServer(host, port);
    xs.bind(categories, clazz);
    xs.start();

  }
}
