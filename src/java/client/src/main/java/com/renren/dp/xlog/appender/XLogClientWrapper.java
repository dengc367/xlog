package com.renren.dp.xlog.appender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.helpers.LogLog;

import xlog.slice.LogData;

import com.renren.dp.xlog.client.XlogClient;
import com.renren.dp.xlog.client.XlogClient.ProtocolType;
import com.renren.dp.xlog.client.XlogClientFactory;
import com.renren.dp.xlog.client.exception.InitializationException;

/**
 * XLog Log4j Appender
 * 
 * @author Zhancheng Deng <b>{@link zhancheng.deng@renren-inc.com}</b>
 * @since 6:56:48 PM Sep 12, 2012
 */
public class XLogClientWrapper implements XLogAppenderParamsI {

  private boolean initialized = false;
  private XlogClient client;
  // parameters with the logger
  private String cacheFileDir = "cache.data.dir";
  private int maxSendSize = 0;
  private int DEFAULT_MAX_SEND_SIZE = 50000;
  private ProtocolType protocolType = ProtocolType.UDP;
  private boolean async = true;
  private Map<String, String[]> categoriesMapCache;
  private int cacheQueueSize;

  public XLogClientWrapper() {
  }

  @Override
  public void setCacheFileDir(String cacheFileDir) {
    if (cacheFileDir != null && !cacheFileDir.isEmpty())
      this.cacheFileDir = cacheFileDir;
  }

  @Override
  public void setAsync(String async) {
    if (null != async) {
      this.async = Boolean.parseBoolean(async);
    }
  }

  @Override
  public void setCacheQueueSize(int cacheQueueSize) {
    this.cacheQueueSize = cacheQueueSize;
  }

  @Override
  public void setMaxSendSize(int maxSendSize) {
    if (maxSendSize < DEFAULT_MAX_SEND_SIZE) {
      this.maxSendSize = maxSendSize;
    } else {
      this.maxSendSize = DEFAULT_MAX_SEND_SIZE;
    }
  }

  @Override
  public void setProtocolType(String protocolType) {
    if (protocolType != null && protocolType.equalsIgnoreCase("tcp")) {
      this.protocolType = ProtocolType.TCP;
    }
  }

  public void init() {

    client = XlogClientFactory.getInstance(async);
    try {
      client.initialize(cacheFileDir, cacheQueueSize, protocolType);
      logMap = new HashMap<String, List<String>>();
      lengthMap = new HashMap<String, Integer>();
      categoriesMapCache = new HashMap<String, String[]>(10);
      initialized = true;
      System.out.println("Xlog Appender (" + this + ") initialized, the appender parameters: cacheFileDir="
          + cacheFileDir + ", cacheQueueSize=" + cacheQueueSize + ", maxSendSize=" + maxSendSize + ", protocolType="
          + protocolType + ", async=" + async);
    } catch (InitializationException e) {
      initialized = false;
      System.err.println("XLog Log4j Appender initialized failed.");
      System.err
          .println("the tutorial & Reference link: http://wiki.d.xiaonei.com/pages/viewpage.action?pageId=14846863");
      e.printStackTrace();
    }
  }

  public void close() {
    if (null != logMap) {
      Set<String> categories = logMap.keySet();
      for (String c : categories) {
        LogData ld = new LogData();
        List<String> logList = logMap.get(c);
        if (logList.size() == 0) {
          continue;
        }
        ld.categories = getCategories(c);
        ld.logs = logList.toArray(new String[0]);
        client.doSend(new LogData[] { ld });
        logMap.remove(c);
        lengthMap.remove(c);
      }
      logMap.clear();
      logMap = null;
    }
    if (null != lengthMap) {
      lengthMap.clear();
      lengthMap = null;
    }
    if (null != categoriesMapCache) {
      categoriesMapCache.clear();
      categoriesMapCache = null;
    }
    client = null;
  }

  private String[] getCategories(String loggerName) {
    if (!categoriesMapCache.containsKey(loggerName)) {
      categoriesMapCache.put(loggerName, loggerName.split("(\\.| )"));
    }
    return categoriesMapCache.get(loggerName);
  }

  private Map<String, List<String>> logMap;
  private Map<String, Integer> lengthMap;

  public void append(String categories, String formatedLog) {
    if (!initialized) {
      return;
    }
    List<String> logList = logMap.get(categories);
    int logLength = 0;
    if (null != logList) {
      logLength = lengthMap.get(categories);
    } else {
      logList = new ArrayList<String>();
    }
    logList.add(formatedLog);
    logLength += formatedLog.length();
    if (logLength < maxSendSize) {
      logMap.put(categories, logList);
      lengthMap.put(categories, logLength);
    } else {
      LogData ld = new LogData();
      ld.categories = getCategories(categories);
      ld.logs = logList.toArray(new String[0]);
      client.doSend(new LogData[] { ld });
      logList.clear();
      logMap.put(categories, logList);
      lengthMap.put(categories, 0);
    }

  }

}
