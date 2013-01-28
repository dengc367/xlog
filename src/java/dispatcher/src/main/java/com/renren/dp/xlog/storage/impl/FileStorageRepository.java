package com.renren.dp.xlog.storage.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.metrics.CategoriesCounter;
import com.renren.dp.xlog.metrics.CategoriesInfo;
import com.renren.dp.xlog.metrics.QueueCounter;
import com.renren.dp.xlog.storage.FileListener;
import com.renren.dp.xlog.storage.StorageRepository;

public class FileStorageRepository implements StorageRepository {

  private List<String> categoriesList = new ArrayList<String>();
  private ReentrantLock lock = new ReentrantLock();

  private CategoriesCounter categoriesCounter=null;
  
  @Override
  public void addToRepository(LogMeta logMeta) {
    lock.lock();

    String categories = logMeta.getCategory();
    if (!categoriesList.contains(categories)) {
      categoriesList.add(categories);
      Thread t = new FileListener(categories);
      t.setDaemon(true);
      t.start();
    }
    logMeta.free();
    logMeta = null;
    lock.unlock();
  }

  @Override
  public void checkRepository() {
    // TODO Auto-generated method stub

  }

  @Override
  public void close() {
    // TODO Auto-generated method stub

  }

  @Override
  public List<QueueCounter> getQueueInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getCacheFilesSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void initialize(CategoriesCounter categoriesCounter) throws IOException {
    // TODO Auto-generated method stub
    this.categoriesCounter=categoriesCounter;
  }

  @Override
  public Collection<CategoriesInfo> getCategoryInfos() {
    return categoriesCounter.getCategoryInfos();
  }

}
