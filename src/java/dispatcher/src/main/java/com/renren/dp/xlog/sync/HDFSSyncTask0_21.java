package com.renren.dp.xlog.sync;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class HDFSSyncTask0_21 extends HDFSSyncTask{

  public HDFSSyncTask0_21(File slaveLogFile, int slaveLogRootDirLen) {
    super(slaveLogFile, slaveLogRootDirLen);
  }

  @Override
  protected FileSystem getRemoteFS(Configuration conf) throws IOException {
    /** compile 1.0.3 */
    //return FileSystem.newInstance(conf);
    return null ;
  }

}
