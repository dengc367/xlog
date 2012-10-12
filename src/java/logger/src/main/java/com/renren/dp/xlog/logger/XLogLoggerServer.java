package com.renren.dp.xlog.logger;

import com.renren.dp.xlog.logger.client.Subscriber;

public class XLogLoggerServer {

  public String host;
  public int port;

  public XLogLoggerServer(String host, int port) {
    this.host = host;
    this.port = port;

  }

  public void bind(String[] categories, Class<? extends XLogLogger> clazz) {
    sub = new Subscriber(host, port, categories);
    ps = new PublishService(host, port, clazz);
  }

  private PublishService ps;
  private Subscriber sub;

  public void start() {
    ps.serve();
    sub.subscribe();
  }

}
