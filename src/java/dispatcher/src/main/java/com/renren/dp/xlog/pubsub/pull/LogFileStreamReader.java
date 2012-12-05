package com.renren.dp.xlog.pubsub.pull;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.google.common.collect.Lists;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.pubsub.PubSubUtils;

public class LogFileStreamReader {
  private static int FIRST_BEGIN_POINT = 1024 * 10;

  FileSystem fs;
  String[] categories;
  boolean pathExists;
  String currentFileName;
  long offset;
  int fetchLength; // fetch size from the client
  FSDataInputStream in;

  public LogFileStreamReader(String[] categories, int fetchSize) throws IOException {
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set("fs.default.name", Configuration.getString("storage.uri"));
    fs = DistributedFileSystem.get(conf);
    this.categories = categories;
    fetchLength = fetchSize;
    initDataInputStream(FIRST_BEGIN_POINT);
  }

  @SuppressWarnings("deprecation")
  private boolean initDataInputStream(int begin_point) throws IOException {
    if (!pathExists) {
      currentFileName = getFileName();
      Path currentPath = getPathIfExist(categories, currentFileName);
      if (currentPath != null) {
        long fileSize = fs.getFileStatus(currentPath).getLen();
        if (fileSize > begin_point) {
          offset = fileSize - begin_point;
        }
        in = fs.open(currentPath);
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

  private boolean reinitDataInputStream(int begin_point) throws IOException {
    if (!pathExists) {
      String tempPathName = getFileName();
      Path tempPath = getPathIfExist(categories, tempPathName);
      if (tempPath != null) {
        in = fs.open(tempPath);
        if (!currentFileName.equals(tempPathName)) {
          currentFileName = tempPathName;
          in.seek(0);
        } else {
          in.seek(offset);
        }
        pathExists = true;
      }
    }
    return pathExists;
  }

  public String getFileName() {
    return FileNameHandlerFactory.getInstance().getCacheLogFileNum() + ".d1";
  }

  public String generatePathString(String[] categories, String fileName) {
    return "/user/xlog/" + PubSubUtils.serializeCategories(categories, "/") + "/" + fileName;
  }

  public Path getPathIfExist(String[] categories, String fileName) throws IOException {
    String pathStr = generatePathString(categories, fileName);
    Path path = new Path(pathStr);
    if (fs.exists(path) && fs.isFile(path)) {
      return path;
    }
    return null;
  }

  public synchronized String[] getLineStream() throws IOException {
    if (!reinitDataInputStream(0)) {
      return new String[0];
    }
    String line;
    int cnt = 0;
    List<String> loggers = Lists.newArrayList();
    while ((line = in.readLine()) != null) {
      loggers.add(line);
      if (++cnt > fetchLength) {
        break;
      }
    }
    if (line == null) {
      pathExists = false;
    }
    offset = in.getPos();
    return loggers.toArray(new String[0]);
  }

  public static void main(String[] args) {
    try {
      LogFileStreamReader r = new LogFileStreamReader(new String[] { "3g", "api", "access" }, 50);
      String[] arr = r.getLineStream();
      for (String s : arr) {
        System.out.println(s);
      }

      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      arr = r.getLineStream();
      for (String s : arr) {
        System.out.println(s);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
