package com.renren.dp.xlog.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.storage.StorageRepository;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.LogDataFormat;

import xlog.slice.LogData;

public class SyncTask extends Thread {

  private File slaveLogFile = null;
  private int batchCommitSize;
  private int slaveLogRootDirLen;
  private CacheManager cm=null;

  private final static Logger LOG = LoggerFactory.getLogger(SyncTask.class);

  public SyncTask(CacheManager cm, File slaveLogFile,
      int slaveLogRootDirLen, int batchCommitSize){
    this.cm=cm;
    this.slaveLogFile = slaveLogFile;
    this.batchCommitSize = batchCommitSize;
    this.slaveLogRootDirLen = slaveLogRootDirLen;
  }

  @Override
  public void run() {
    String categories = slaveLogFile.getParent().substring(
        slaveLogRootDirLen + 1);
    boolean res = store(slaveLogFile, categories);
    if (res) {
      LOG.info("Success to sync data,slave file:"
          + slaveLogFile.getAbsolutePath());
      rename(slaveLogFile);
    } else {
      LOG.info("Fail to sync data,slave file:"
          + slaveLogFile.getAbsolutePath());
    }
  }

  private boolean store(File sourceFile, String categories) {
    StorageRepository sr = StorageRepositoryFactory.getInstance();
    FileReader fr = null;
    try {
      fr = new FileReader(sourceFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }

    BufferedReader br = new BufferedReader(fr);
    String line = null;
    int i = 0;
    String[] logs = new String[batchCommitSize];
    LogData logData = null;
    String logFileNum = null;
    boolean res = true;
    try {
      while ((line = br.readLine()) != null) {
        logs[i++] = line;

        if (i == batchCommitSize) {
          i = 0;
          logData = LogDataFormat.transformToLogData(categories, logs);
          logFileNum = FileNameHandlerFactory.getInstance()
              .getCacheLogFileNum();
          LogMeta logMeta = new LogMeta(logFileNum, logData,categories, 2);
          if (cm.writeCache(logMeta)) {
            sr.addToRepository(logMeta);
          }
          logs = new String[batchCommitSize];
        }
      }
      if (i > 0) {
        logData = LogDataFormat.transformToLogData(categories, logs);
        logFileNum = FileNameHandlerFactory.getInstance().getCacheLogFileNum();
        LogMeta logMeta = new LogMeta(logFileNum, logData,categories, 2);
        if (cm.writeCache(logMeta)) {
          sr.addToRepository(logMeta);
        }
      }
    } catch (IOException e) {
      res = false;
      LOG.error(
          "fail to sync file! the file_name : "
              + slaveLogFile.getAbsolutePath() + ",the exception is : {} ",
          e.getMessage());
    } finally {
      try {
        br.close();
      } catch (IOException e) {
      }
      try {
        fr.close();
      } catch (IOException e) {
      }
    }
    return res;
  }

  private boolean rename(File file) {
    return file.renameTo(new File(file.getAbsolutePath()
        + Constants.LOG_SYNC_FINISHED_SUFFIX));
  }
}
