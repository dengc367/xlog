package com.renren.dp.xlog.fs;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * configuration parameters
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 2:51:01 PM Apr 8, 2013
 */
public class XLogConfiguration {
  private Map<String, String> options = Maps.newHashMap();

  /**
   * copy from the com.renren.dp.xlog.pubsub.PubSubConstants at the
   * xlog-dispatcher jar
   */
  public static String XLOG_FETCH_LENGTH = "xlog.fetch.length";
  public static String XLOG_FETCH_FROM_SCRATCH = "xlog.fetch.from.scratch";

  /**
   * set the fetch size per request
   * 
   * @param size
   */
  public void setFetchSize(int size) {
    options.put(XLOG_FETCH_LENGTH, String.valueOf(size));
  }

  /**
   * reset the inputStream
   * 
   * @param b
   */
  public void resetInputStream(boolean b) {
    options.put(XLOG_FETCH_FROM_SCRATCH, String.valueOf(b));
  }

  public Map<String, String> map() {
    return options;
  }

  public String getFetchSize() {
    return options.get(XLOG_FETCH_LENGTH);
  }

}
