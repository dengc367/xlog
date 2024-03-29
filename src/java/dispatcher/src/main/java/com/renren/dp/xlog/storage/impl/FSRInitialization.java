package com.renren.dp.xlog.storage.impl;

import java.io.File;
import java.io.FileFilter;

import xlog.slice.LogData;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.io.SuffixFileFilter;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.storage.StorageRepository;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.dp.xlog.storage.StorageRepositoryInitialization;
import com.renren.dp.xlog.util.Constants;

/**
 * @deprecated 通过文件存储，然后转发的方案已经废弃
 * @author xianquanzhang
 *
 */
public class FSRInitialization extends StorageRepositoryInitialization {

  private StorageRepository storageRepository = null;
  private File cacheLogDir = null;
  private int cacheLogDirLen;

  public void initialise() {
    this.storageRepository = StorageRepositoryFactory.getInstance();
    String storePath = Configuration.getString("oplog.store.path");
    cacheLogDir = new File(storePath + "/" + CacheManager.CACHE_TYPE);
    cacheLogDirLen = cacheLogDir.getAbsolutePath().length();

    FileFilter ff = new SuffixFileFilter(
        new String[] { Constants.LOG_WRITE_FINISHED_SUFFIX });
    initStorageRepository(cacheLogDir, ff);
  }

  private void initStorageRepository(File dir, FileFilter ff) {
    File[] logFiles = dir.listFiles(ff);
    if (logFiles == null || logFiles.length == 0) {
      return;
    }

    LogData logData = null;
    for (File logFile : logFiles) {
      if (logFile.isFile()) {
        logData = new LogData();
        logData.categories = new String[] { logFile.getParent().substring(
            cacheLogDirLen) };

        storageRepository.addToRepository(new LogMeta(logFile.getName(),
            logData,logData.categories[0]));
      } else {
        initStorageRepository(logFile, ff);
      }
    }
  }
}
