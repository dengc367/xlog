package com.renren.dp.xlog.appender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

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
public class Log4jAppender extends AppenderSkeleton implements XLogAppenderParamsI {

  // parameters with the logger
  private XLogClientWrapper wrapper = new XLogClientWrapper();

  public Log4jAppender() {
  }

  public Log4jAppender(Layout layout) {
    this.layout = layout;
  }

  @Override
  public void setCacheFileDir(String cacheFileDir) {
    wrapper.setCacheFileDir(cacheFileDir);
  }

  @Override
  public void setAsync(String async) {
    wrapper.setAsync(async);
  }
  
  @Override
  public void setCompress(String compress) {
    wrapper.setCompress(compress);
  }

  @Override
  public void setCacheQueueSize(int cacheQueueSize) {
    wrapper.setCacheQueueSize(cacheQueueSize);
  }

  @Override
  public void setMaxSendSize(int maxSendSize) {
    wrapper.setMaxSendSize(maxSendSize);
  }

  @Override
  public void setProtocolType(String protocolType) {
    wrapper.setProtocolType(protocolType);
  }

  @Override
  public void activateOptions() {
    wrapper.init();
  }

  @Override
  public void close() {
    wrapper.close();
  }

  @Override
  public boolean requiresLayout() {
    return true;
  }

  @Override
  protected void append(LoggingEvent event) {
    StringBuilder logBuilder = new StringBuilder(layout.format(event));
    if (event.getThrowableInformation() != null) {
      for (String o : event.getThrowableStrRep()) {
        logBuilder.append('\n').append(o);
      }
    }
    wrapper.append(event.getLoggerName(), logBuilder.toString());
  }

}
