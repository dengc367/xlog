package com.renren.dp.xlog.appender;

/**
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 4:05:59 PM Dec 11, 2012
 */
public interface XLogAppenderParamsI {
  public void setCacheFileDir(String cacheFileDir);

  public void setAsync(String async);
  
  public void setCompress(String compress);

  public void setCacheQueueSize(int cacheQueueSize);

  public void setMaxSendSize(int maxSendSize);

  public void setProtocolType(String protocolType);

}
