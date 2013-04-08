package com.renren.dp.xlog.fs;

import java.io.IOException;
import java.util.Map;
import com.google.common.collect.Maps;

/**
 * xlog file system
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:15:56 PM Apr 7, 2013
 */
public class XLogFileSystem {

  /**
   * copy from the com.renren.dp.xlog.pubsub.PubSubConstants at the
   * xlog-dispatcher jar
   */
  public static String XLOG_FETCH_LENGTH = "xlog.fetch.length";
  public static String XLOG_FETCH_FROM_SCRATCH = "xlog.fetch.from.scratch";

  public static String ZK_ROOT_PATH = "/xlog/dispatchers";
  public static String DEFAULT_ZK_CLUSTER_IPS = "10.4.19.78:10060,10.4.19.79:10060,10.4.19.80:10060";

  private String zkConnectString;
  Map<String, String> options = Maps.newHashMap();

  /**
   * default constructure
   */
  public XLogFileSystem() {
    this(DEFAULT_ZK_CLUSTER_IPS);
  }

  /**
   * customized zkconnectstring constructure
   * 
   * @param zkConnectString
   */
  public XLogFileSystem(String zkConnectString) {
    this.zkConnectString = zkConnectString;
  }

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

  /**
   * get the categories line reader
   * 
   * @param categories
   * @return
   * @throws IOException
   */
  public XLogReader open(String[] categories) throws IOException {
    return new XLogReader(categories, zkConnectString + ZK_ROOT_PATH, options);
  }

}
