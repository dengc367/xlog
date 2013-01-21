package com.renren.dp.xlog.sync;

import java.util.Timer;

import com.renren.dp.xlog.cache.WriteLocalOnlyCategoriesCache;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.storage.StorageRepositoryInitialization;
import com.renren.dp.xlog.storage.impl.DSRInitialization;
import com.renren.dp.xlog.storage.impl.FSRInitialization;

public class LogSyncInitialization {

  public void initialise(WriteLocalOnlyCategoriesCache wlcc) {
    StorageRepositoryInitialization init = null;
    // the cache time unit is min at config file
    int localStorageCacheTime = getLocalStorageCacheTime();
    int errorDataCacheTime = Configuration
        .getInt("error.data.cache.time", 60) * 60 * 1000;
    int storageRepositoryMode = Configuration.getInt("storage.repository.mode",
        0);
    // unit is second
    long syncInterval = Configuration.getLong("oplog.sync.interval", 3600)*1000;
    if (storageRepositoryMode == 0) {
      init = new DSRInitialization();
      init.initialise();

      Timer t = new Timer();
      t.schedule(new SyncTimer(wlcc), syncInterval, syncInterval);
    } else if (storageRepositoryMode == 1) {
      init = new FSRInitialization();
      init.initialise();
    }

    long clearInterval = Configuration.getLong("oplog.clear.interval", 300)*1000;
    Timer t1 = new Timer();
    t1.schedule(new ClearTimer(localStorageCacheTime, errorDataCacheTime), 0,
        clearInterval);
  }
  
  private int getLocalStorageCacheTime(){
    int localStorageCacheTime = Configuration.getInt("local.store.cache.time",0);
    int type=Configuration.getInt("cache.file.name.format",2);
    switch(type){
      case 0:if(localStorageCacheTime<=5) localStorageCacheTime=5;break;
      case 1:if(localStorageCacheTime<=10) localStorageCacheTime=10;break;
      case 2:if(localStorageCacheTime<=60) localStorageCacheTime=60;
    }
    return localStorageCacheTime * 60 * 1000;
  }
}
