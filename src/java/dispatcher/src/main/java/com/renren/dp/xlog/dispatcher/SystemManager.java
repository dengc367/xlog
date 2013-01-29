package com.renren.dp.xlog.dispatcher;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.renren.dp.xlog.VersionAnnotation;
import com.renren.dp.xlog.cache.CacheManagerFactory;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.dispatcher.DispatcherApp;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.metrics.CategoriesInfo;
import com.renren.dp.xlog.metrics.QueueCounter;
import com.renren.dp.xlog.storage.StorageRepositoryFactory;
import com.renren.monitor.metrics.impl.JVMMetrics;

public class SystemManager {

  public VersionAnnotation getVersion(){
    Package p = VersionAnnotation.class.getPackage();
    VersionAnnotation va = p.getAnnotation(VersionAnnotation.class);
    return va;
  }

  public String getStartDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date d = DispatcherApp.getInstance().getStartDate();
    if (d == null) {
      return null;
    }
    return sdf.format(d);
  }

  public String getMemoryInfo(){
    String heapMemory=formatSize(JVMMetrics.getHeapMemoryUsage());
    String totalMemory=formatSize(JVMMetrics.getTotalMemory());
    return heapMemory+"/"+totalMemory;
  }
  public int getDispatcherStatus() {
    return DispatcherApp.getInstance().getStatus();
  }

  public List<QueueCounter> getQueueInfo() {
    return StorageRepositoryFactory.getInstance().getQueueInfo();
  }

  public Collection<CategoriesInfo> getCategoryInfos(){
    return StorageRepositoryFactory.getInstance().getCategoryInfos();
  }
  
  public boolean start() {
    try {
      DispatcherApp.getInstance().start();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean stop() {
    try {
      DispatcherApp.getInstance().stopDispatcher();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public List<ParameterMode> getParameters() {
    List<ParameterMode> list=new ArrayList<ParameterMode>();
    Set<?> params=Configuration.getParameterNames();
    String key;
    for(Object param:params){
      key=(String)param;
      list.add(new ParameterMode(key,Configuration.getString(key),Configuration.getDescription(key)));
    }
    Collections.sort(list);
    return list;
  }

  public String getCacheFilesSize() {
    try {
      return formatSize(CacheManagerFactory.getInstance().getCacheFilesSize());
    } catch (ReflectionException e) {
      return "0";
    }
  }

  public String getSendFailureFileSize() {
    return formatSize(StorageRepositoryFactory.getInstance()
        .getCacheFilesSize());
  }

  private String formatSize(long size) {
    DecimalFormat df = new DecimalFormat("#.0");
    double data=size/1024d;
    if (data < 1024) {
      return df.format(data) + "KB";
    } else if (data < 1048576) {
      data = data/1024d;
      return df.format(data) + "MB";
    } else {
      data = data /1048576d;
      return df.format(data) + "GB";
    }
  }
  public class ParameterMode implements Comparable<Object>{
    private String key;
    private String value;
    private String description;
    
    public ParameterMode(String key, String value, String description) {
      super();
      this.key = key;
      this.value = value;
      this.description = description;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }

    @Override
    public int compareTo(Object o) {
      if(!(o instanceof ParameterMode)){
        return -1;
      }
      ParameterMode pm =(ParameterMode)o;
      return this.key.compareTo(pm.getKey());
    }
  }
}
