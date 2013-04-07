package com.renren.dp.xlog.pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.renren.dp.xlog.config.Configuration;

import Ice.Current;

public class PubSubUtils {

  private static final String REMOTE_ADDRESS_KEY = "remote address =";

  public static String getRemoteClientIp(Current __current) {
    String connStr = __current.con._toString();
    connStr = connStr.substring(connStr.indexOf(REMOTE_ADDRESS_KEY) + REMOTE_ADDRESS_KEY.length());
    return connStr.split(":")[0];
  }

  public static void getLocalFile(String filePath) throws IOException {
    FileSystem fs;
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set("fs.default.name", Configuration.getString("storage.uri"));
    // fs = DistributedFileSystem.get(conf);
    fs = DistributedFileSystem.getLocal(conf);
    Path path = new Path(filePath);
    if (fs.exists(path) && fs.isFile(path)) {
      FSDataInputStream fsDataStream = fs.open(path, 4096 * 4096);
      InputStreamReader fr = new InputStreamReader(fsDataStream);
      BufferedReader br = new BufferedReader(fr);
      if (br.markSupported()) {
        // br.mark(10);
      }
      String line;
      int i = 1;
      while ((line = br.readLine()) != null) {
        System.out.println(i++ + line);
      }
    }
  }

  public static String serializeCategories(String[] categories) {
    return serializeCategories(categories, "/");
  }

  public static String serializeCategories(String[] categories, String separator) {
    StringBuilder catStr = new StringBuilder();
    for (int i = 0; i < categories.length; i++) {
      catStr.append(categories[i]);
      if (i < categories.length - 1) {
        catStr.append(separator);
      }
    }
    return catStr.toString();
  }
}
