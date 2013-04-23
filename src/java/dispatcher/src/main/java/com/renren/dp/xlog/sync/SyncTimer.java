package com.renren.dp.xlog.sync;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.cache.CacheManagerFactory;
import com.renren.dp.xlog.cache.WriteLocalOnlyCategoriesCache;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.io.SuffixFileFilter;
import com.renren.dp.xlog.storage.StorageRepository;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.dp.xlog.util.Constants;

public class SyncTimer extends TimerTask {

  private int slaveLogRootDirLen;
  private String slaveLogDir = null;
  private int batchCommitSize;
  private WriteLocalOnlyCategoriesCache wlcc = null;
  private ExecutorService threadPool = null;
  private int taskCount;
  private CacheManager cm = null;
  private String storageType = null;

  private static Logger LOG = LoggerFactory.getLogger(SyncTimer.class);

  public SyncTimer(WriteLocalOnlyCategoriesCache wlcc) {
    this.wlcc = wlcc;
    storageType = Configuration.getString("storage.type");
    String storePath = Configuration.getString("oplog.store.path");
    slaveLogDir = storePath + "/" + storageType;
    slaveLogRootDirLen = slaveLogDir.length();
    batchCommitSize = Configuration.getInt("batch.commit.size", 1000);
    threadPool = Executors.newFixedThreadPool(Configuration.getInt(
        "error.data.sync.thread.count", 10));
    try {
      cm = CacheManagerFactory.getInstance();
    } catch (ReflectionException e) {
      LOG.error("Fail to get Cache Manager", e);
    }
  }

  @Override
  public void run() {
    taskCount = 0;
    cm.checkCache();
    StorageRepository sr = StorageRepositoryFactory.getInstance();
    sr.checkRepository();

    FileFilter ff = new SuffixFileFilter(new String[] {Constants.LOG_WRITE_ERROR_SUFFIX });
    try {
      buildSyncTask(new File(slaveLogDir), ff);
    } catch (Exception e) {
      LOG.error("Fail to build sync task!",e);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("it start " + taskCount + " threads to sync data!");
    }
  }

  private void buildSyncTask(File dir, FileFilter ff) throws Exception {
    File[] logFiles = dir.listFiles(ff);
    if (logFiles == null || logFiles.length == 0) {
      return;
    }
    for (File logFile : logFiles) {
      if (logFile.isFile() && logFile.length() > 0) {
        if (storageType.equalsIgnoreCase("hdfs")) {
          threadPool.execute(new HDFSSyncTask(logFile,slaveLogRootDirLen));
        } else {
          threadPool.execute(new SyncTask(cm, logFile,
              slaveLogRootDirLen, batchCommitSize));
        }
        taskCount++;
      } else {
        if (isDemandSync(logFile.getAbsolutePath())) {
          buildSyncTask(logFile, ff);
        }
      }
    }
  }

  private boolean isDemandSync(String cachePath) {
    Set<String> set = wlcc.getCategories();
    if (set.isEmpty()) {
      return true;
    }
    for (String s : set) {
      if (cachePath.endsWith(s)) {
        return false;
      }
    }
    return true;
  }
}
