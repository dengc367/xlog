package com.renren.dp.xlog.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.metrics.CategoriesCounter;
import com.renren.dp.xlog.metrics.CategoriesInfo;
import com.renren.dp.xlog.metrics.QueueCounter;

public interface StorageRepository {

  public void initialize(CategoriesCounter categoriesCounter) throws IOException;
  
  public void addToRepository(LogMeta logMeta);

  public void checkRepository();

  public List<QueueCounter> getQueueInfo();
  
  public Collection<CategoriesInfo> getCategoryInfos();

  public long getCacheFilesSize();

  public void close();
}
