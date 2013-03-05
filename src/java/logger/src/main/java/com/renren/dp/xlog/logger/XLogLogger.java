package com.renren.dp.xlog.logger;

/**
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:57:04 PM Mar 5, 2013
 */
public interface XLogLogger {
  public void print(String[] logs);

  public void print(String log);

}
