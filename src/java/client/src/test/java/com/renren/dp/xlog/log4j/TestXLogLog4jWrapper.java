package com.renren.dp.xlog.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

public class TestXLogLog4jWrapper {

  Logger l = Logger.getLogger("test.3g.action");
  private Boolean async = true;
  private String cacheFileDir = "xlog.cache.file.dir";
  private int cacheQueueSize = 100000;
  private int maxSendSize = 30000;
  private String protocolType = "udp";
  private Level level = Level.INFO;
  
  @Test
  public void test() {
    XLogAppender appender = new XLogAppender(new PatternLayout("%m"));
    appender.setAsync(String.valueOf(async));
    appender.setCacheFileDir(cacheFileDir);
    appender.setCacheQueueSize(cacheQueueSize);
    appender.setMaxSendSize(maxSendSize);
    appender.setName("xlogAppender");
    appender.setProtocolType(protocolType);
    appender.activateOptions();
    l.setAdditivity(false);
    l.setLevel(level);
    l.addAppender(appender);
    for (int i = 0; i < 1000; i++)
      l.info("the new messagemessagemessagemessagemessagemessagemessagemessagemessagemessage 1.");
  }
}
