package com.renren.dp.xlog.logger;

public class ConsoleXLogLogger implements XLogLogger {

  @Override
  public void log(String[] logs) {

    for (int i = 0; i < logs.length; i++) {
      System.out.println(logs[i]);
    }

  }

}
