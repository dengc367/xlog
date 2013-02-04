package com.renren.dp.xlog.sync;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.FileSystemUtil;

public class HDFSSyncTask extends Thread {

  private String categories = null;
  private File slaveLogFile = null;
  private Configuration conf = null;

  private static Logger LOG = Logger.getLogger(HDFSSyncTask.class);

  public HDFSSyncTask(File slaveLogFile, int slaveLogRootDirLen) {
    this.categories = slaveLogFile.getParent().substring(slaveLogRootDirLen + 1);
    this.slaveLogFile = slaveLogFile;
    conf = new Configuration();
    conf.set("dfs.replication", com.renren.dp.xlog.config.Configuration.getString("storage.replication"));
    conf.set("dfs.socket.timeout", com.renren.dp.xlog.config.Configuration.getString("dfs.socket.timeout"));
    conf.set("fs.default.name", com.renren.dp.xlog.config.Configuration.getString("storage.uri"));
  }

  public void run() {
    FileSystem localFs = null;
    try {
      localFs = FileSystem.getLocal(conf);
    } catch (IOException e) {
      LOG.error("Fail to get FileSystem for slave log path : " + slaveLogFile.getAbsolutePath(), e);
      return;
    }
    FileSystem remoteFs = null;
    try {
      remoteFs = FileSystemUtil.createFileSystem();
    } catch (IOException e) {
      LOG.error(
          "Fail to remote FileSystem  instance,then it dose not sync slave log : " + slaveLogFile.getAbsolutePath(), e);
      return;
    }
    try {
      if (FileUtil.copy(localFs, new Path(slaveLogFile.toString()), remoteFs,
          new Path(categories + "/" + slaveLogFile.getName() + ".recover"), false, conf)) {
        rename(slaveLogFile);
      }
    } catch (IOException e) {
      LOG.error("Fail to sync slave log to hdfs.file name : " + slaveLogFile.getAbsolutePath(), e);
    }
    if (localFs != null) {
      try {
        localFs.close();
      } catch (IOException e) {
      }
    }
    if (remoteFs != null) {
      try {
        remoteFs.close();
      } catch (IOException e) {
      }
    }
  }

  private boolean rename(File file) {
    return file.renameTo(new File(file.getAbsolutePath() + Constants.LOG_SYNC_FINISHED_SUFFIX));
  }
}
