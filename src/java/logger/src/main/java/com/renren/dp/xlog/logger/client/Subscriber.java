package com.renren.dp.xlog.logger.client;

import java.text.MessageFormat;

public class Subscriber {

  public static String hostPattern = "P:{0} -h {1} -p {2}";
  private SubscriberAdapter sa;
  private String[] categories;
  private String host;

  public Subscriber(String ip, int port, String[] categories) {
    sa = SubscriberAdapter.getInstance();
    host = MessageFormat.format(hostPattern, "tcp", ip, port);
    this.categories = categories;
  }

  public void subscribe() {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    for (int i = 0; i < ips.length; i++) {
      sa.subscribe(ips[i], host, categories);
    }
  }
}
