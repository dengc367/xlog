package com.renren.dp.xlog.appender;

public interface XLogAppenderParamsI {
  public void setCacheFileDir(String cacheFileDir);

  public void setAsync(String async);

  public void setCacheQueueSize(int cacheQueueSize);

  public void setMaxSendSize(int maxSendSize);

  public void setProtocolType(String protocolType);

}
