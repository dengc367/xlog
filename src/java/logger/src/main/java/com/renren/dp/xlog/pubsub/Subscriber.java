package com.renren.dp.xlog.pubsub;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.pubsub.zookeeper.client.ClientZooKeeperAdapter;

public class Subscriber {

  private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
  public static String endPointPattern = "P:default -h <HOST> -p <PORT>";
  private SubscribeAdapter sa;
  private String[] categories;
  private String endpoint;
  

  public Subscriber(int port, String[] categories) {
    sa = SubscribeAdapter.getInstance();
    endpoint = endPointPattern.replace("<PORT>", String.valueOf(port));
    this.categories = categories;
  }

  public void subscribe() {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    for (int i = 0; i < ips.length; i++) {
      sa.subscribe(ips[i], endpoint, categories);
      logger.debug("the subscription: " + ips[i]);
    }
  }

  public void unsubscribe() {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    for (int i = 0; i < ips.length; i++) {
      sa.unsubscribe(ips[i], endpoint, categories);
      logger.debug("the subscription: " + ips[i]);
    }
  }

}
