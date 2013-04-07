package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the xlog reader
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:57:40 PM Mar 5, 2013
 */
public class XLogReader implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(XLogReader.class);

  private DispatcherClusterReader client;

  /**
   * the xlog reader constructor
   * 
   * @param categories
   * @param zkConnString
   * @param options
   * @throws IOException
   */
  public XLogReader(String[] categories, String zkConnString, Map<String, String> options) throws IOException {
    client = new DispatcherClusterReader(categories, zkConnString, options);
    LOG.info("initialize the xlogInputStream successfully.");
  }

  /**
   * read one line
   * 
   * @return
   * @throws IOException
   */
  public String readLine() throws IOException {
    return client.readLine();
  }

  @Override
  public void close() throws IOException {
    client.close();
  }

}
