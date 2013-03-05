package com.renren.dp.xlog.pubsub.push;

import com.renren.dp.xlog.logger.XLogLogger;
import com.renren.dp.xlog.logger.XLogLoggerServer;


/**
 * Push Server
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:58:46 PM Mar 5, 2013
 */
public class PushXLogLoggerServer implements XLogLoggerServer {

  public int port;

  public PushXLogLoggerServer(int port) {
    this.port = port;

  }

  @Override
  public void bind(String[] categories, Class<? extends XLogLogger> clazz) {
    sub = new Subscriber(port, categories);
    ps = new PushService(port, clazz);
  }

  private PushService ps;
  private Subscriber sub;

  @Override
  public void start() {
    ps.serve();
    sub.subscribe();
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
    
  }

}
