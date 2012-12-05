package com.renren.dp.xlog.logger;

public interface XLogLoggerServer {

  /**
   * start the xlog pub sub program
   */
  public void start();

  /**
   * stop the xlog pub sub program
   */
  public void stop();

  public void bind(String[] categories, Class<? extends XLogLogger> clazz);

}
