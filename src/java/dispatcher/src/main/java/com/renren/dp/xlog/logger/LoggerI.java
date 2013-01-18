package com.renren.dp.xlog.logger;

import java.io.IOException;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.cache.CacheManagerFactory;
import com.renren.dp.xlog.cache.WriteLocalOnlyCategoriesCache;
import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.dp.xlog.sync.LogSyncInitialization;

import xlog.slice.LogData;
import xlog.slice._LoggerDisp;
import Ice.Current;
import Ice.ObjectAdapter;

public class LoggerI extends _LoggerDisp {
  private static final long serialVersionUID = -3117295957500314988L;

  private CacheManager cacheManager = null;
  private AbstractFileNameHandler fileNameHandler=null;
  private WriteLocalOnlyCategoriesCache wlcc=null;
  
  public boolean initialize(ObjectAdapter adapter) {
    adapter.add(this, adapter.getCommunicator().stringToIdentity("L"));

    cacheManager = CacheManagerFactory.getInstance();
    cacheManager.initialize();
    fileNameHandler=FileNameHandlerFactory.getInstance();
    
    wlcc=new WriteLocalOnlyCategoriesCache();
    try {
		wlcc.initialize();
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
    LogSyncInitialization logSync=new LogSyncInitialization();
    logSync.initialise(wlcc);
    
    return true;
  }

  @Override
  public void add(LogData[] data, Current __current) {
    if(data==null){
      return ;
     }
    for(LogData o:data){
    	addLogData(o,__current);
    }
  }

  @Override
  public void addLogData(LogData data, Current __current) {
    if(data==null){
      return ;
     }
    String logFileNum=fileNameHandler.getCacheLogFileNum();
    LogMeta logMeta=new LogMeta(logFileNum,data,2);
    boolean res = cacheManager.writeCache(logMeta);
    if (res && (!wlcc.isWriteLocalOnly(data))) {
      StorageRepositoryFactory.getInstance().addToRepository(logMeta);
    }
  }

}
