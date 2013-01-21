package com.renren.dp.xlog.storage;

import java.io.IOException;
import java.util.List;

import com.renren.dp.xlog.logger.LogMeta;

public interface StorageRepository {

  public void initialize() throws IOException;
  
  public void addToRepository(LogMeta logMeta);

  public void checkRepository();

  public List<QueueCounter> getQueueInfo();

  public long getCacheFilesSize();

  public void close();
}
