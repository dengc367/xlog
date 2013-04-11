package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.io.Closeables;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;

public class LogFileStreamReader implements Closeable {
  private static final Log LOG = LogFactory.getLog(LogFileStreamReader.class);

  private FileSystem fs;
  private String[] categories;
  private boolean pathExists;
  private String currentFileNum;
  private long offset;
  private int fetchLength; // fetch size from the client
  private FSDataInputStream in;
  private byte[] byteCache;
  private AbstractFileNameHandler fileHandler = FileNameHandlerFactory.getInstance();

  public LogFileStreamReader(String[] categories, int fetchLength, FileSystem fs) throws IOException {
    this.fs = fs;
    this.categories = categories;
    this.fetchLength = fetchLength;
    byteCache = new byte[fetchLength];
    checkInputStream();
  }

  @SuppressWarnings("deprecation")
  private boolean checkInputStream() throws IOException {
    if (!pathExists) {
      String tempFileNum = fileHandler.getCacheLogFileNum();
      Path tmpPath = getAbsolutePathIfExist(categories, tempFileNum);
      if (tmpPath != null) {
        currentFileNum = tempFileNum;
        long fileSize = fs.getFileStatus(tmpPath).getLen();
        in = fs.open(tmpPath);
        if (fileSize > fetchLength) {
          offset = fileSize - fetchLength;
          in.seek(offset);
          in.readLine(); // remove the first line because the first line is not
          // integer.
        }
        pathExists = true;
      }
    }
    return pathExists;
  }

  private boolean recheckInputStream() throws IOException {
    if (!pathExists) {
      if (currentFileNum == null) {
        return checkInputStream();
      }
      String tempPathName = fileHandler.getCacheLogFileNum();
      if (tempPathName.equals(currentFileNum)) {
        Closeables.closeQuietly(in);
      } else {
        try {
          tempPathName = fileHandler.NextLogFileNum(currentFileNum);
        } catch (ParseException e) {
          LOG.error("LogFileNum parse error: " + e);
        }
      }
      Path tempPath = getAbsolutePathIfExist(categories, tempPathName);
      if (tempPath != null) {
        in = fs.open(tempPath);
        if (!currentFileNum.equals(tempPathName)) {
          currentFileNum = tempPathName;
          offset = 0;
        }
        in.seek(offset);
        pathExists = true;
      }
    }
    return pathExists;
  }

  private Path getAbsolutePathIfExist(String[] categories, String fileNum) throws IOException {
    String pathStr = fs.getWorkingDirectory() + "/" + PubSubUtils.serializeCategories(categories, "/") + "/" + fileNum
        + "." + Configuration.getString("xlog.uuid");
    Path path = new Path(pathStr);
    if (fs.exists(path) && fs.isFile(path)) {
      return path;
    }
    return null;
  }

  public byte[] getByteStream() throws IOException {
    if (!recheckInputStream()) {
      return null;
    }

    int size = in.read(byteCache, 0, fetchLength);
    if (size < fetchLength) {
      pathExists = false;
    }
    if (size <= 0) {
      return null;
    }
    offset += size;
    byte[] bb = new byte[size];
    System.arraycopy(byteCache, 0, bb, 0, size);
    return bb;
  }

  @Override
  public void close() throws IOException {
    if (in != null) {
      in.close();
    }
    fileHandler = null;
    categories = null;
  }
}
