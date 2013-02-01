package com.renren.dp.xlog.storage.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.renren.dp.xlog.config.CategoriesHashKey;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.metrics.CategoriesCounter;
import com.renren.dp.xlog.metrics.CategoriesInfo;
import com.renren.dp.xlog.metrics.MetricsInitialization;
import com.renren.dp.xlog.metrics.QueueCounter;
import com.renren.dp.xlog.storage.EventListener;
import com.renren.dp.xlog.storage.StorageRepository;
import com.renren.dp.xlog.util.FileUtil;

public class DefaultStorageRepository implements StorageRepository {

  private int queueCapacity;
  private int queueCount;
  private String storageAdapterClassName = null;
  private String slaveRootDir = null;
  private EventListener[] eventListeners;
  private CategoriesHashKey categoriesHash;
  private CategoriesCounter categoriesCounter;

  public DefaultStorageRepository() {
    queueCount = Configuration.getInt("storage.repository.queue.count", 20);
    queueCapacity = Configuration.getInt("storage.repository.queue.capacity", 1000);
    slaveRootDir = Configuration.getString("oplog.store.path") + "/" + Configuration.getString("storage.type");
    storageAdapterClassName = Configuration.getString("storage.adapter.impl");
  }

  public void initialize(CategoriesCounter categoriesCounter) throws IOException {
    this.categoriesCounter = categoriesCounter;
    categoriesHash = new CategoriesHashKey();
    eventListeners = new EventListener[queueCount];
    MetricsInitialization metrics = new MetricsInitialization();
    // 初始化事件监听器
    for (int i = 0; i < queueCount; i++) {
      EventListener listener = new EventListener("StorageQueue" + i, queueCapacity, slaveRootDir,
          metrics.getMetricsManager());
      listener.initialize(storageAdapterClassName);
      listener.setDaemon(true);
      listener.start();
      eventListeners[i] = listener;
    }
    // 初始化metrics
    metrics.initialize();
  }

  public void addToRepository(LogMeta logMeta) {
    synchronized (eventListeners) {
      int queueID = categoriesHash.hash(logMeta.getCategory(), queueCount);
      eventListeners[queueID].add(logMeta);
    }
  }

  public void checkRepository() {
    String logFileNum = FileNameHandlerFactory.getInstance().getCacheLogFileNum();
    for (EventListener el : eventListeners) {
      el.checkExpiredLogFile(logFileNum);
    }
  }

  @Override
  public List<QueueCounter> getQueueInfo() {
    List<QueueCounter> list = new ArrayList<QueueCounter>();
    for (EventListener el : eventListeners) {
      list.add(el.getCount());
    }

    return list;
  }

  @Override
  public Collection<CategoriesInfo> getCategoryInfos() {
    return categoriesCounter.getCategoryInfos();
  }

  @Override
  public void close() {
    for (EventListener el : eventListeners) {
      el.close();
    }
    eventListeners = null;
  }

  @Override
  public long getCacheFilesSize() {
    File slaveFile = new File(slaveRootDir);
    if (!slaveFile.exists()) {
      return 0;
    }
    return FileUtil.computeDirectorySize(slaveFile);
  }
}
