package com.renren.dp.xlog.logger;

/**
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:56:56 PM Mar 5, 2013
 */
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
