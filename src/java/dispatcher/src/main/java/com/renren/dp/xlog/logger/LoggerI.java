package com.renren.dp.xlog.logger;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.cache.CacheManagerFactory;
import com.renren.dp.xlog.cache.WriteLocalOnlyCategoriesCache;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.metrics.CategoriesCounter;
import com.renren.dp.xlog.pubsub.PubSubService;
import com.renren.dp.xlog.storage.StorageRepository;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.dp.xlog.sync.LogSyncInitialization;
import com.renren.dp.xlog.util.LogDataFormat;

import xlog.slice.LogData;
import xlog.slice._LoggerDisp;
import Ice.Current;
import Ice.ObjectAdapter;

public class LoggerI extends _LoggerDisp {
  private static final long serialVersionUID = -3117295957500314988L;

  private CacheManager cacheManager = null;
  private AbstractFileNameHandler fileNameHandler = null;
  private WriteLocalOnlyCategoriesCache wlcc = null;
  private StorageRepository storageRepository=null;
  private CategoriesCounter categoriesCounter=null;

  private PubSubService pubsub = null;

  private static Logger LOG = Logger.getLogger(LoggerI.class);
  
  public boolean initialize(ObjectAdapter adapter) {
    adapter.add(this, adapter.getCommunicator().stringToIdentity("L"));

    try {
      cacheManager = CacheManagerFactory.getInstance();
    } catch (ReflectionException e) {
      LOG.error("Fail to get CacheManager instance!",e);
      return false;
    }
    categoriesCounter=new CategoriesCounter();
    categoriesCounter.setDaemon(true);
    
    storageRepository=StorageRepositoryFactory.getInstance();
    try {
      storageRepository.initialize(categoriesCounter);
    } catch (IOException e) {
      LOG.error("Fail to initialize Storage Repository",e);
      return false;
    }
    
    cacheManager.initialize();
    fileNameHandler = FileNameHandlerFactory.getInstance();

    wlcc = new WriteLocalOnlyCategoriesCache();
    try {
      wlcc.initialize();
    } catch (IOException e) {
      LOG.error("Fail to initialize WriteLocalOnlyCategoriesCache!",e);
      return false;
    }
    LogSyncInitialization logSync = new LogSyncInitialization();
    logSync.initialise(wlcc);
    
    categoriesCounter.start();
    
    return true;
  }

  @Override
  public void add(LogData[] data, Current __current) {
    if (data == null) {
      return;
    }
    for (LogData o : data) {
      addLogData(o, __current);
    }
  }

  @Override
  public void addLogData(LogData data, Current __current) {
    if (data == null || data.categories == null || data.categories.length==0) {
      return;
    }
    String category = LogDataFormat
        .transformCategories(data.categories);
    /**
     * category计数器
     */
    categoriesCounter.incCategoryCount(category);
    
    String logFileNum = fileNameHandler.getCacheLogFileNum();
    LogMeta logMeta = null;
    /**
     * 判断是否有订阅
     */
    if (pubsub != null && pubsub.isSubscribed(data.categories)) {
      logMeta = new LogMeta(logFileNum, data,category, 3);
      pubsub.publish(logMeta);
    } else {
      logMeta = new LogMeta(logFileNum, data,category, 2);
    }
    /**
     * 写本地缓存
     */
    boolean res = cacheManager.writeCache(logMeta);
    if(!res){
      LOG.error("Fail to write data to local cache.Category:"+category);
      return ;
    }
    if (wlcc.isWriteLocalOnly(category)) {
      logMeta.free();
      logMeta = null;
    }
    storageRepository.addToRepository(logMeta);
  }

  public void setPubSub(PubSubService pubsub) {
    this.pubsub = pubsub;
  }

}
