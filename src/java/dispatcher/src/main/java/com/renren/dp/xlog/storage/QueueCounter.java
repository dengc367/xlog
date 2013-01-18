package com.renren.dp.xlog.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QueueCounter {

  private String queueName;
  //当前队列大小
  private long currentQueueSize;
  private long queueCapacity;
  private long queueRPS=0;
  //记录当前队列中每个category相关信息
  private Map<String,CategoriesCounter> categoriesCountMap;
  //记录队列中的各个category请求数
  private Map<String,Long> queueCountMap;
  //记录数据采集前的队列中各个category的请求数
  private Map<String,Long> lastQueueCountMap;
  
  protected QueueCounter(String queueName,long queueCapacity){
    this.queueName=queueName;
    this.queueCapacity=queueCapacity;
    this.queueCountMap=new ConcurrentHashMap<String,Long>();
    this.lastQueueCountMap=new ConcurrentHashMap<String,Long>();
    this.categoriesCountMap=new ConcurrentHashMap<String,CategoriesCounter>();
  }
  
  protected void incCategorySuccessCount(String category){
    if(categoriesCountMap.containsKey(category)){
      categoriesCountMap.get(category).incSuccessCount();
    }else{
      CategoriesCounter cc=new CategoriesCounter(category);
      cc.incSuccessCount();
      categoriesCountMap.put(category, cc);
    }
  }
  //采集rps数据
  public void collecteRPSData(){
    this.queueRPS=0;
    lastQueueCountMap.clear();
    lastQueueCountMap.putAll(queueCountMap);
    //采集时间1s
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    if(!queueCountMap.isEmpty()){
      Set<Map.Entry<String,Long>> ccSet=queueCountMap.entrySet();
      long rps;
      for(Map.Entry<String,Long> me:ccSet){
        rps=lastQueueCountMap.get(me.getKey())==null?0:lastQueueCountMap.get(me.getKey());
        rps=me.getValue()-rps;
        this.queueRPS+=rps;
        categoriesCountMap.get(me.getKey()).setCategoryRPS(rps);
      }
    }
  }
  
  protected void incQueueCount(String category){
    if(queueCountMap.containsKey(category)){
      long count = queueCountMap.get(category);
      count++;
      queueCountMap.put(category, count);
    }else{
      queueCountMap.put(category, 1L);
    }
  }
  
  protected void incCategoryFailureCount(String category){
    if(categoriesCountMap.containsKey(category)){
       categoriesCountMap.get(category).incFailCount();
    }else{
      CategoriesCounter cc=new CategoriesCounter(category);
      cc.incFailCount();
      categoriesCountMap.put(category, cc);
    }
  }

  public long getQueueCapacity() {
    return queueCapacity;
  }

  protected void setCurrentQueueSize(long currentQueueSize) {
    this.currentQueueSize = currentQueueSize;
  }

  public long getCurrentQueueSize() {
    return currentQueueSize;
  }

  public long getSuccessCount() {
    Collection<CategoriesCounter> ccs=categoriesCountMap.values();
    long successCount=0;
    for(CategoriesCounter cc:ccs){
      successCount+=cc.getSuccessCount();
    }
    return successCount;
  }

  public long getFailureCount() {
    Collection<CategoriesCounter> ccs=categoriesCountMap.values();
    long failureCount=0;
    for(CategoriesCounter cc:ccs){
      failureCount+=cc.getFailCount();
    }
    return failureCount;
  }

  public String getQueueName() {
    return queueName;
  }

  public long getQueueRPS() {
    return queueRPS;
  }

  public Collection<CategoriesCounter> getCategoriesCounters(){
    return this.categoriesCountMap.values();
  }
  
  public class CategoriesCounter implements Cloneable{
    private long successCount=0;
    private long failCount=0;
    private String category;
    private long categoryRPS=0;
    
    protected CategoriesCounter(String category){
      this.category=category;
    }

    public long getSuccessCount() {
      return successCount;
    }

    protected void incSuccessCount() {
      this.successCount++;
    }

    public long getFailCount() {
      return failCount;
    }

    protected void incFailCount() {
      this.failCount++;
    }

    public String getCategory() {
      return category;
    }

    public long getCategoryRPS() {
      return categoryRPS;
    }

    public void setCategoryRPS(long categoryRPS) {
      this.categoryRPS = categoryRPS;
    }
  }
}
