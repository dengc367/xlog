package com.renren.dp.xlog.logger;

import com.renren.dp.xlog.logger.client.Subscriber;

public class XLogLoggerServer {

  public int port;

  public XLogLoggerServer(int port) {
    this.port = port;

  }

  public void bind(String[] categories, Class<? extends XLogLogger> clazz) {
    sub = new Subscriber(port, categories);
    ps = new PublishService(port, clazz);
  }

  private PublishService ps;
  private Subscriber sub;

  public void start() {
    ps.serve();
    sub.subscribe();
  }

}
