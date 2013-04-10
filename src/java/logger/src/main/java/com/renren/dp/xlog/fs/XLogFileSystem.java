package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * reader.close();
 * xfs.close();
 * </pre>
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:15:56 PM Apr 7, 2013
 */
public class XLogFileSystem implements Closeable {

  public static String ZK_ROOT_PATH = "/xlog/dispatchers";
  public static String DEFAULT_ZK_CLUSTER_IPS = "10.4.19.78:10060,10.4.19.79:10060,10.4.19.80:10060";

  private String zkConnectString;
  private XLogConfiguration conf;
  private List<XLogReader> readerList;

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
    readerList = new CopyOnWriteArrayList<XLogReader>();
  }

  /**
   * get the categories line reader
   * 
   * @param categories
   * @return
   * @throws IOException
   */
  public XLogReader open(String[] categories) throws IOException {
    return new XLogReaderImpl(categories, zkConnectString + ZK_ROOT_PATH, conf);
  }

  @Override
  public void close() throws IOException {
    for (XLogReader reader : readerList) {
      reader.close();
    }
    readerList.clear();
    readerList = null;
  }

  class XLogReaderImpl implements XLogReader {

    private final Logger LOG = LoggerFactory.getLogger(XLogReaderImpl.class);

    private DispatcherClusterReader client;

    /**
     * the xlog reader constructor
     * 
     * @param categories
     * @param zkConnString
     * @param options
     * @throws IOException
     */
    public XLogReaderImpl(String[] categories, String zkConnString, XLogConfiguration conf) throws IOException {
      client = new DispatcherClusterReader(categories, zkConnString, conf);
      readerList.add(this);
      LOG.info("Initialize the xlogInputStream (categories=" + Arrays.toString(categories) + ", zkConnString="
          + zkConnString + ") successfully.");
    }

    @Override
    public String readLine() throws IOException {
      return client.readLine();
    }

    @Override
    public void close() throws IOException {
      client.close();
      readerList.remove(this);
    }

  }
}
