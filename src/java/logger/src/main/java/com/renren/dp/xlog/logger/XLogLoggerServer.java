package com.renren.dp.xlog.logger;

import java.util.Timer;
import java.util.TimerTask;

import com.renren.dp.xlog.pubsub.Subscriber;

public class XLogLoggerServer {

  public int port;
  private Timer t;
  private int retriedSubscriptionPeriod = 1000 * 60;

  public XLogLoggerServer(int port) {
    this.port = port;
    t = new Timer();

  }

  public void bind(String[] categories, Class<? extends XLogLogger> clazz) {
    ps = new PublishService(port, clazz);
    sub = new Subscriber(port, categories);
  }

  /**
   * 当时隔多长时间没有接收到log，再发送订阅请求。
   * 
   * @param retriedSubscriptionPeriod
   */
  public void setRetriedSubscriptionPeriod(int retriedSubscriptionPeriod) {
    this.retriedSubscriptionPeriod = retriedSubscriptionPeriod;
  }

  private PublishService ps;
  private Subscriber sub;

  /**
   * start the xlog pub sub program
   */
  public void start() {
    ps.serve();
    t.schedule(new SubscribeTimer(), 0, retriedSubscriptionPeriod);
  }

  class SubscribeTimer extends TimerTask {

    @Override
    public void run() {
      if (!ps.isReceivedLog()) {
        sub.subscribe();
      }
    }

  }

  /**
   * stop the xlog pub sub program
   */
  public void stop() {
    ps.destroy();
    sub.unsubscribe();
    t.cancel();
  }

}
