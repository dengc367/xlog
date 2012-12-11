package com.renren.dp.xlog.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

/**
 * XLog Log4j Appender
 * 
 * @author Zhancheng Deng <b>{@link zhancheng.deng@renren-inc.com}</b>
 * @since 6:56:48 PM Sep 12, 2012
 */
public class LogBackAppender extends AppenderBase<ILoggingEvent> implements XLogAppenderParamsI {

  private Layout<ILoggingEvent> layout;

  public Layout<ILoggingEvent> getLayout() {
    return layout;
  }

  public void setLayout(Layout<ILoggingEvent> layout) {
    this.layout = layout;
  }

  public LogBackAppender() {
  }

  XLogClientWrapper wrapper = new XLogClientWrapper();

  @Override
  public void start() {
    if (this.layout == null) {
      addError("No layout set for the appender named [" + name + "].");
      return;
    }
    wrapper.init();
    super.start();
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
  protected void append(ILoggingEvent event) {
    String log = layout.doLayout(event);
    wrapper.append(event.getLoggerName(), log);
  }
}
