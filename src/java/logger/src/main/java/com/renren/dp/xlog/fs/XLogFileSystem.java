package com.renren.dp.xlog.fs;

import java.io.IOException;

/**
 * xlog file system simple example:
 * 
 * <pre>
 * XLogFileSystem xfs = new XLogFileSystem();
 * XLogReader reader = xfs.open(new String[] { &quot;feed&quot;, &quot;dispatchtoV2&quot; });
 * String line;
 * while ((line = reader.readLine()) != null) {
 *   System.out.println(line);
 * }
 * </pre>
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:15:56 PM Apr 7, 2013
 */
public class XLogFileSystem {

  public static String ZK_ROOT_PATH = "/xlog/dispatchers";
  public static String DEFAULT_ZK_CLUSTER_IPS = "10.4.19.78:10060,10.4.19.79:10060,10.4.19.80:10060";

  private String zkConnectString;
  private XLogConfiguration conf;

  /**
   * default constructure
   */
  public XLogFileSystem() {
    this(DEFAULT_ZK_CLUSTER_IPS, new XLogConfiguration());
  }

  /**
   * customized zkconnectstring constructure
   * 
   * @param zkConnectString
   */
  public XLogFileSystem(String zkConnectString) {
    this(zkConnectString, new XLogConfiguration());
  }

  public XLogFileSystem(XLogConfiguration conf) {
    this(DEFAULT_ZK_CLUSTER_IPS, conf);
  }

  public XLogFileSystem(String zkConnectString, XLogConfiguration conf) {
    this.zkConnectString = zkConnectString;
    this.conf = conf;
  }

  /**
   * get the categories line reader
   * 
   * @param categories
   * @return
   * @throws IOException
   */
  public XLogReader open(String[] categories) throws IOException {
    return new XLogReader(categories, zkConnectString + ZK_ROOT_PATH, conf);
  }

}
