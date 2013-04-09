package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the line reader from the xlog input stream
 * <p>
 * 
 * you can see the javadoc of the class {@link XLogFileSystem }
 * <p>
 * the value returned of {@link #readLine()} has: <code>null</code>, the empty
 * string <code>""</code>, or the real string.
 * <p>
 * when all the dispatcher nodes are down, it will return null, or the exception
 * is throwed, it will return the empty string "".
 * 
 * 
 * @see XLogFileSystem
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
  public XLogReader(String[] categories, String zkConnString, XLogConfiguration conf) throws IOException {
    client = new DispatcherClusterReader(categories, zkConnString, conf);
    LOG.info("initialize the xlogInputStream successfully.");
  }

  /**
   * the value returned has: <code>null</code>, the empty string <code>""</code>
   * , or the real string. *
   * <p>
   * when all the dispatcher nodes are down, it will return null, or the
   * exception is throwed, it will return the empty string "".
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
