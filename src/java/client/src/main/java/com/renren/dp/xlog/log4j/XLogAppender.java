package com.renren.dp.xlog.log4j;

import org.apache.log4j.Layout;

import com.renren.dp.xlog.appender.Log4jAppender;

;

/**
 * XLog Log4j Appender, please use com.renren.dp.xlog.appender.Log4jAppender
 * instead.
 * 
 * @author Zhancheng Deng <b>{@link zhancheng.deng@renren-inc.com}</b>
 * @since 6:56:48 PM Sep 12, 2012
 */
@Deprecated
public class XLogAppender extends Log4jAppender {

  public XLogAppender() {
  }

  public XLogAppender(Layout layout) {
    super(layout);
  }

}
