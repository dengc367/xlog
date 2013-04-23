package com.renren.dp.xlog.storage.impl;

import java.io.File;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.io.SuffixFileFilter;
import com.renren.dp.xlog.storage.StorageRepositoryInitialization;

public class DSRInitialization extends StorageRepositoryInitialization {

  @Override
  public void initialise() {
    String storePath = Configuration.getString("oplog.store.path");
    this.cacheLogRootDir=storePath + "/" + CacheManager.CACHE_TYPE;
    this.slaveLogRootDir=storePath + "/"+ Configuration.getString("storage.type");
    SuffixFileFilter suffixFileFilter = new SuffixFileFilter();

    check(new File(this.slaveLogRootDir),suffixFileFilter);
  }
}
