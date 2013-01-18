package com.renren.dp.xlog.sync;

import java.io.File;

import com.renren.dp.xlog.config.Configuration;

public class HDFSSyncTaskFactory {

  public static HDFSSyncTask getInstance(File slaveLogFile,int slaveLogRootDirLen) throws Exception{
    String storageVersion=Configuration.getString("storage.version");
    if("1.0.3".equals(storageVersion)){
      return new HDFSSyncTask1_0_3(slaveLogFile,slaveLogRootDirLen);
    }else if("0.21".equals(storageVersion)){
      return new HDFSSyncTask0_21(slaveLogFile,slaveLogRootDirLen);
    }else{
      throw new UnsupportedOperationException(
          "it dosen't support this hdfs version : "+storageVersion);
    }
  }
}
