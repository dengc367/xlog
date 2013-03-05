package com.renren.dp.xlog.pubsub.pull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.logger.XLogLogger;
import com.renren.dp.xlog.logger.XLogLoggerServer;
import com.renren.dp.xlog.pubsub.pull.Puller;

/**
 * Pull Server
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:57:40 PM Mar 5, 2013
 */
public class PullXLogLoggerServer implements XLogLoggerServer {

  private static final Logger LOG = LoggerFactory.getLogger(PullXLogLoggerServer.class);
  private boolean inited = false;

  public PullXLogLoggerServer(String[] categories, Class<? extends XLogLogger> loggerClazz) {
    try {
      puller = new Puller(categories, loggerClazz.newInstance());
      inited = true;
    } catch (InstantiationException e) {
      LOG.error("the XLogLogger implementation is not instantiatized, please check it.", e);
    } catch (IllegalAccessException e) {
      LOG.error("the XLogLogger implementation is illegal accessed, please check it.", e);
    }
  }

  public PullXLogLoggerServer(String[] categories, XLogLogger logger) {
    puller = new Puller(categories, logger);
    inited = true;
  }

  private Puller puller;

  /**
   * start the xlog pub sub program
   */
  @Override
  public void start() {
    if (!inited) {
      LOG.warn("the xlog log consumer initialization failed, please check the xlog log implemenation.");
      return;
    }
    puller.subscribe();
  }

  /**
   * stop the xlog pub sub program
   */
  @Override
  public void stop() {
    puller.unsubscribe();
  }

  @Override
  public void bind(String[] categories, Class<? extends XLogLogger> clazz) {
    // need not implemented
  }

}
