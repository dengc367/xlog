package com.renren.dp.xlog.logger;

import xlog.slice.LogData;

public class LogMeta {

  private String logFileNum = null;
  private LogData logData = null;
  private String category=null;
  // 异步环境中共享对象计算器,写缓存和写资源库是异步的，当都写完了就需要释放内存，此变量就是描述共享数量的，当值为0就可以释放内存。
  private int countDownLatch = 0;

  public LogMeta(String logFileNum, LogData logData,String category) {
    super();
    this.logFileNum = logFileNum;
    this.logData = logData;
    this.category=category;
  }

  public LogMeta(String logFileNum, LogData logData,String category, int shareCount) {
    super();
    this.logFileNum = logFileNum;
    this.logData = logData;
    this.countDownLatch = shareCount;
    this.category=category;
  }

  public String getLogFileNum() {
    return logFileNum;
  }

  public LogData getLogData() {
    return logData;
  }

  public void free() {
    synchronized (this) {
      countDownLatch--;
      if (countDownLatch <= 0) {
        this.logFileNum = null;
        this.logData.categories = null;
        this.logData.checkSum = null;
        this.logData.logs = null;
        this.logData = null;
      }
    }
  }

  public String getCategory() {
    return category;
  }

  // private String categories=null;
  // private String log=null;
  // private String checkSum=null;
  // public String getCategories() {
  // return categories;
  // }
  // public void setCategories(String categories) {
  // this.categories = categories;
  // }
  // public String getLog() {
  // return log;
  // }
  // public void setLog(String log) {
  // this.log = log;
  // }
  // public String getCheckSum() {
  // return checkSum;
  // }
  // public void setCheckSum(String checkSum) {
  // this.checkSum = checkSum;
  // }
  //
  // public String toString(){
  // return "["+categories+"],["+log+"],["+checkSum+"]";
  // }
}
