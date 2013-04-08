package com.renren.dp.xlog.fs;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class XLogHDFSReader {

  public static void main(String[] args) {
    try {
      FileSystem fs = FileSystem.get(URI.create("hdfs://YZSJHL19-42.opi.com:8020"), new Configuration());

      Path path = new Path("/user/xlog/socialgraph/reconnect/homeReconnect/2013-03-12-13.t2");
      long fileSize = fs.getFileStatus(path).getLen();
      long offset = (fileSize > 1024) ? fileSize - 1024 : 0;
      ;
      FSDataInputStream in = fs.open(path);
      in.seek(offset);
      IOUtils.copyBytes(in, System.out, 1024, false);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
