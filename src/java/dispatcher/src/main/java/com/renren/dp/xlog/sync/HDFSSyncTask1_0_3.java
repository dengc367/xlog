package com.renren.dp.xlog.sync;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class HDFSSyncTask1_0_3 extends HDFSSyncTask{

  public HDFSSyncTask1_0_3(File slaveLogFile, int slaveLogRootDirLen) {
    super(slaveLogFile, slaveLogRootDirLen);
  }

  @Override
  protected FileSystem getRemoteFS(Configuration conf) throws IOException {
    return FileSystem.get(conf);
  }

}
