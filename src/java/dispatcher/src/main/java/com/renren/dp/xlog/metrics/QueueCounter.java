package com.renren.dp.xlog.metrics;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QueueCounter extends Thread {

  private String queueName;
  // 当前队列大小
  private long currentQueueSize;
  private long queueCapacity;
  private long queueRPS = 0;
  // 记录当前队列中每个category相关信息
  private Map<String, CategoriesInfo> categoriesCountMap;
  // 记录队列中的各个category请求数
  private Map<String, Long> queueCountMap;
  // 记录数据采集前的队列中各个category的请求数
  private Map<String, Long> lastQueueCountMap;
  private volatile boolean isRunning=true;

  public QueueCounter(String queueName, long queueCapacity) {
    this.queueName = queueName;
    this.queueCapacity = queueCapacity;
    this.queueCountMap = new ConcurrentHashMap<String, Long>();
    this.lastQueueCountMap = new ConcurrentHashMap<String, Long>();
    this.categoriesCountMap = new ConcurrentHashMap<String, CategoriesInfo>();
  }

  public void incCategorySuccessCount(String category) {
    if (categoriesCountMap.containsKey(category)) {
      categoriesCountMap.get(category).incSuccessCount();
    } else {
      CategoriesInfo ci = new CategoriesInfo(category);
      ci.incSuccessCount();
      categoriesCountMap.put(category, ci);
    }
  }

  // 采集rps数据
  private void collecteRPSData() {
    synchronized (queueCountMap) {
      if (!queueCountMap.isEmpty()) {
        Set<Map.Entry<String, Long>> ccSet = queueCountMap.entrySet();
        long rps,totalRPS=0;
        for (Map.Entry<String, Long> me : ccSet) {
          if(lastQueueCountMap.containsKey(me.getKey())){
            rps=me.getValue()-lastQueueCountMap.get(me.getKey());
          }else{
            rps=me.getValue();
           }
          totalRPS += rps;
          if(categoriesCountMap.containsKey(me.getKey())){
            categoriesCountMap.get(me.getKey()).setCategoryRPS(rps);
          }else{
            CategoriesInfo ci = new CategoriesInfo(me.getKey());
            ci.setCategoryRPS(rps);
            categoriesCountMap.put(me.getKey(), ci);
          }
          
        }
       this.queueRPS=totalRPS;
       lastQueueCountMap.clear();
       lastQueueCountMap.putAll(queueCountMap);
      }
    }
  }

  public void run() {
    while (isRunning) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
       }
      collecteRPSData();
    }
  }

  public void close(){
    isRunning=false;
  }
  public void incQueueCount(String category) {
    synchronized (queueCountMap) {
      if (queueCountMap.containsKey(category)) {
        long count = queueCountMap.get(category);
        count++;
        queueCountMap.put(category, count);
      } else {
        queueCountMap.put(category, 1L);
      }
    }
  }

  public void incCategoryFailureCount(String category) {
    if (categoriesCountMap.containsKey(category)) {
      categoriesCountMap.get(category).incFailCount();
    } else {
      CategoriesInfo ci = new CategoriesInfo(category);
      ci.incFailCount();
      categoriesCountMap.put(category, ci);
    }
  }

  public long getQueueCapacity() {
    return queueCapacity;
  }

  public void setCurrentQueueSize(long currentQueueSize) {
    this.currentQueueSize = currentQueueSize;
  }

  public long getCurrentQueueSize() {
    return currentQueueSize;
  }

  public long getSuccessCount() {
    Collection<CategoriesInfo> cis = categoriesCountMap.values();
    long successCount = 0;
    for (CategoriesInfo ci : cis) {
      successCount += ci.getSuccessCount();
    }
    return successCount;
  }

  public long getFailureCount() {
    Collection<CategoriesInfo> ccs = categoriesCountMap.values();
    long failureCount = 0;
    for (CategoriesInfo ci : ccs) {
      failureCount += ci.getFailCount();
    }
    return failureCount;
  }

  public String getQueueName() {
    return queueName;
  }

  public long getQueueRPS() {
    return queueRPS;
  }

  public Collection<CategoriesInfo> getCategoriesInfos() {
    return this.categoriesCountMap.values();
  }
}
