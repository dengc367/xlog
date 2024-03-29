package com.renren.dp.xlog.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.io.LogWriter;
import com.renren.dp.xlog.io.impl.DefaultLogWriter;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.metrics.QueueCounter;
import com.renren.dp.xlog.metrics.QueueMetrics;
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.ReflectionUtil;
import com.renren.monitor.MetricsManager;

public class EventListener extends Thread {
  private ConcurrentHashMap<String, LogWriter> logWriters = null;
  private static final long COUNT_THRESHOLD = 10000;
  private List<LogMeta> logBQ = null;
  private int queueCapacity;
  private StorageAdapter sa = null;
  private String slaveRootDir = null;
  private String currentLogFileNum = null;

  private QueueCounter counter = null;
  private boolean isClosed = false;
  private long lastRequestFailureTimestamp = 0;
  private long deltaRequestFailureTime = 0;
  private final long REQUEST_FAILURE_MAX_TIMEOUT;
  /** 队列满时发送数据的计数器 */
  private long bqFullCount = 0;

  private final static Logger LOG = LoggerFactory.getLogger(EventListener.class);

  public EventListener(String queueName, int queueCapacity, String slaveRootDir, MetricsManager metricsManager) {
    counter = new QueueCounter(queueName, queueCapacity);
    counter.setDaemon(true);
    counter.start();

    metricsManager.addMetricsContext(new QueueMetrics(queueName, this));
    this.queueCapacity = queueCapacity;
    this.slaveRootDir = slaveRootDir;
    this.REQUEST_FAILURE_MAX_TIMEOUT = Configuration.getLong("request.failure.max.timeout", 300) * 1000;
  }

  public void initialize(String storageAdapterClassName) throws IOException {
    this.logWriters = new ConcurrentHashMap<String, LogWriter>();
    logBQ = new LinkedList<LogMeta>();
    try {
      this.sa = (StorageAdapter) ReflectionUtil.newInstance(storageAdapterClassName);
    } catch (ReflectionException e1) {
      LOG.error("Fail to reflection StorageAdapter instance", e1);
      throw new IOException(e1);
    }
    sa.initialize();
  }

  public void add(LogMeta logMeta) {
    synchronized (logBQ) {
      if (isClosed) {
        LOG.info("EventListener is closed ,can't receive log data!");
        return;
      }
      String category = logMeta.getCategory();
      if (category != null) {
        this.counter.incQueueCount(category);
      }
      if (logBQ.size() >= queueCapacity) {
        bqFullCount++;
        if (bqFullCount % COUNT_THRESHOLD == 0) {
          LOG.warn("Queue :" + this.counter.getQueueName() + " is full.bqFullCount : " + bqFullCount);
        }
        logMeta.free();
        logMeta = null;
      } else {
        logBQ.add(logMeta);
      }
      logBQ.notify();
    }
  }

  public QueueCounter getCount() {
    this.counter.setCurrentQueueSize(logBQ.size());
    return this.counter;
  }

  /**
   * 检测是否日志文件需要关闭流,例如错误日志恢复正常，某些category没有流量等
   * @param logFileNum
   */
  public void checkExpiredLogFile(String logFileNum) {
    synchronized (logWriters) {
      if (!logWriters.isEmpty()) {
         /**
          * 记录已经没有打错误日志的category
          */
        List<String> expiredCategories=new ArrayList<String>();
        Set<Map.Entry<String, LogWriter>> set = logWriters.entrySet();
        for (Map.Entry<String, LogWriter> me : set) {
          LogWriter logWriter = me.getValue();
          if(!logWriter.getLogFileName().equals(logFileNum)){
            logWriter.close();
            logWriter.rename(Constants.LOG_WRITE_ERROR_SUFFIX);
            expiredCategories.add(me.getKey());
            }
          }
         for(String category:expiredCategories){
           logWriters.remove(category);
          }
       }
      }
     this.sa.flush(logFileNum);
  }

  public void run() {
    LogMeta logMeta = null;
    while (!isClosed) {
      try {
        synchronized (logBQ) {
          /**
           * 在高并发的情况下，需要double check队列是否为空，否则从队列中取数据会抛出异常
           */
          if (logBQ.isEmpty()) {
            try {
              logBQ.wait();
            } catch (InterruptedException e) {
              continue;
            }
            if (logBQ.isEmpty()) {
              continue;
            }
          }
          logMeta = logBQ.remove(0);
        }
        currentLogFileNum = logMeta.getLogFileNum();
        boolean res = sa.store(logMeta);
        String category = logMeta.getCategory();
        if (res) {
          /**
           * 判断上次存储数据是否失败，如果失败，则需要将记录的错误信息清除
           */
          if (lastRequestFailureTimestamp > 0) {
            lastRequestFailureTimestamp = 0;
            deltaRequestFailureTime = 0;
          }
          this.counter.incCategorySuccessCount(category);
          if (!currentLogFileNum.equals(logMeta.getLogFileNum())) {
            if (logWriters.contains(category)) {
              LogWriter logWriter = logWriters.remove(category);
              logWriter.close();
              logWriter.rename(Constants.LOG_WRITE_ERROR_SUFFIX);
            }
          }
        } else {
          processError(category, logMeta);
          long currentTime = System.currentTimeMillis();
          if (lastRequestFailureTimestamp == 0) {
            lastRequestFailureTimestamp = currentTime;
          } else {
            deltaRequestFailureTime = currentTime - lastRequestFailureTimestamp;
            /**
             * 判断写失败是否达到阈值，如果达到则睡眠一段时间再执行。
             */
            if (deltaRequestFailureTime > REQUEST_FAILURE_MAX_TIMEOUT) {
              lastRequestFailureTimestamp = currentTime;
              LOG.info("Fail to send request,try sleep " + REQUEST_FAILURE_MAX_TIMEOUT + "ms");
              try {
                Thread.sleep(REQUEST_FAILURE_MAX_TIMEOUT);
              } catch (InterruptedException e) {
              }
              /**
               * 睡眠完后需要将记录的错误信息清除，否则下次如果出现一次失败的话，将直接进入睡眠，应该写一段时间一直没有成功才进入睡眠
               */
              lastRequestFailureTimestamp = 0;
              deltaRequestFailureTime = 0;
            }
          }
        }
        logMeta.free();
        logMeta = null;
      } catch (Exception e) {
        LOG.warn("Fail to store logdata. CurrentThread: " + Thread.currentThread().getName() + ", QueueName: "
            + counter.getQueueName() + ": \n", e);
        continue;
      }
    }
  }

  private void processError(String category, LogMeta logMeta) {
    this.counter.incCategoryFailureCount(category);
    LogWriter logWriter;
    if (logWriters.contains(category)) {
      logWriter = logWriters.get(category);
      if (currentLogFileNum.equals(logMeta.getLogFileNum())) {
        logWriter.write(logMeta.getLogData().logs, true);
      } else {
        logWriter.close();
        logWriter.rename(Constants.LOG_WRITE_ERROR_SUFFIX);

        logWriter = new DefaultLogWriter();
        logWriter.createFile(new File(slaveRootDir + "/" + category + "/" + logMeta.getLogFileNum()));
        logWriters.put(category, logWriter);
        logWriter.write(logMeta.getLogData().logs, true);
      }
    } else {
      logWriter = new DefaultLogWriter();
      logWriter.createFile(new File(slaveRootDir + "/" + category + "/" + logMeta.getLogFileNum()));
      logWriters.put(category, logWriter);
      logWriter.write(logMeta.getLogData().logs, true);
    }
  }

  public void close() {
    this.isClosed = true;
    counter.close();
    // counter.interrupt();
    counter = null;
    while (logBQ.size() > 0) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
    logBQ = null;
    sa.destory();
    Collection<LogWriter> c = logWriters.values();
    for (LogWriter logWriter : c) {
      logWriter.close();
    }
    logWriters.clear();
    logWriters = null;
    // this.interrupt();
  }

  public long getDeltaRequestFailureTime() {
    return this.deltaRequestFailureTime;
  }
}
