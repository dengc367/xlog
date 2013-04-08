package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jfree.util.Log;

import com.google.common.io.Closeables;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;

public class LogFileStreamReader implements Closeable {

  FileSystem fs;
  String[] categories;
  boolean pathExists;
  String currentFileNum;
  long offset;
  int fetchLength; // fetch size from the client
  FSDataInputStream in;
  AbstractFileNameHandler fileHandler = FileNameHandlerFactory.getInstance();

  public LogFileStreamReader(String[] categories, int fetchLength, FileSystem fs) throws IOException {
    this.fs = fs;
    this.categories = categories;
    this.fetchLength = fetchLength;
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
        if (fileSize > fetchLength) {
          offset = fileSize - fetchLength;
        }
        in = fs.open(tmpPath);
        in.seek(offset);
        if (offset > 0) {
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
          Log.error("LogFileNum parse error: " + e);
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
      return new byte[0];
    }
    byte[] aa = new byte[fetchLength];
    int size = in.read(aa, 0, fetchLength);
    offset += size;
    if (size < fetchLength) {
      pathExists = false;
    }
    return aa;
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
