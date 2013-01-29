package com.renren.dp.xlog.metrics;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CategoriesCounter extends Thread {

  private Map<String, CategoriesInfo> categoryInfosMap;
  private Map<String,Long> categoriesMap;
  private Map<String,Long> lastCategoriesMap;
  private volatile boolean isRunning=true;

  public CategoriesCounter() {
    categoryInfosMap = new ConcurrentHashMap<String, CategoriesInfo>();
    categoriesMap = new ConcurrentHashMap<String, Long>();
    lastCategoriesMap = new ConcurrentHashMap<String, Long>();
  }

  public void incCategoryCount(String category) {
    synchronized (categoriesMap) {
      if (categoriesMap.containsKey(category)) {
        categoriesMap.put(category, categoriesMap.get(category)+1);
      } else {
        categoriesMap.put(category, 1L);
      }
    }
  }

  // 采集rps数据
  private void collecteRPSData() {
    synchronized(categoriesMap){
      if(categoriesMap.isEmpty()){
        return ;
      }
      long rps;
      CategoriesInfo ci;
      Set<Map.Entry<String,Long>> set=categoriesMap.entrySet();
      for(Map.Entry<String,Long> me:set){
        if(lastCategoriesMap.containsKey(me.getKey())){
          rps=me.getValue()-lastCategoriesMap.get(me.getKey());
        }else{
          rps=me.getValue();
        }
        if(categoryInfosMap.containsKey(me.getKey())){
          ci=categoryInfosMap.get(me.getKey());
          ci.setCategoryRPS(rps);
          ci.setSuccessCount(me.getValue());
        }else{
          ci=new CategoriesInfo(me.getKey());
          ci.setCategoryRPS(rps);
          ci.setSuccessCount(me.getValue());
          categoryInfosMap.put(me.getKey(),ci);
        }
      }
     lastCategoriesMap.clear();
     lastCategoriesMap.putAll(categoriesMap);
    }
  }

  public void run() {
    while (isRunning) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      collecteRPSData();
    }
  }
  
  public void close(){
    this.isRunning=false;
  }
  public Collection<CategoriesInfo> getCategoryInfos(){
    return categoryInfosMap.values();
  }
}
