package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;

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
public interface XLogReader extends Closeable {

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
  public String readLine() throws IOException;
}
