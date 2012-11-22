package com.renren.dp.xlog.logger.client;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Subscriber {

  private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
  public static String endPointPattern = "P:default -h <HOST> -p <PORT>";
  private SubscriberAdapter sa;
  private String[] categories;
  private String endpoint;
  private Timer t;

  public Subscriber(int port, String[] categories) {
    sa = SubscriberAdapter.getInstance();
    endpoint = endPointPattern.replace("<PORT>", String.valueOf(port));
    this.categories = categories;
  }

  public void subscribe() {
    t = new Timer();
    t.schedule(new SubscribeTimer(), 0, 10000);
  }

  public void unsubscribe() {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    for (int i = 0; i < ips.length; i++) {
      sa.unsubscribe(ips[i], endpoint, categories);
      logger.debug("the subscription: " + ips[i]);
    }
    t.cancel();
    t = null;
  }

  class SubscribeTimer extends TimerTask {

    @Override
    public void run() {
      String[] ips = ClientZooKeeperAdapter.getAddresses();
      for (int i = 0; i < ips.length; i++) {
        sa.subscribe(ips[i], endpoint, categories);
        logger.debug("the subscription: " + ips[i]);
      }
    }

  }
}
