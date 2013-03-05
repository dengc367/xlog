package com.renren.dp.xlog.pubsub.push;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlog.slice.XLogException;

import com.google.common.collect.Maps;
import com.renren.dp.xlog.pubsub.DispatcherAdapter;
import com.renren.dp.xlog.pubsub.zookeeper.client.ClientZooKeeperAdapter;
import static com.renren.dp.xlog.pubsub.PubSubConstants.*;

/**
 * A subscriber
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:58:57 PM Mar 5, 2013
 */
public class Subscriber {

  private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
  public static String endPointPattern = "P:default -h <HOST> -p <PORT>";
  private DispatcherAdapter sa;
  private String[] categories;
  private String endpoint;
  private Timer t;

  public Subscriber(int port, String[] categories) {
    sa = DispatcherAdapter.getInstance();
    endpoint = endPointPattern.replace("<PORT>", String.valueOf(port));
    this.categories = categories;
  }

  public void subscribe() {
    t = new Timer();
    t.schedule(new SubscribeTimer(), 0, 10000);
  }

  public void unsubscribe() throws XLogException {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    Map<String, String> a = Maps.newHashMap();
    a.put(PUSH_SERVICE_ENDPOINT, endpoint);
    for (int i = 0; i < ips.length; i++) {
      sa.unsubscribe(ips[i], categories, a);
      logger.debug("the subscription: " + ips[i]);
    }
    t.cancel();
    t = null;
  }

  class SubscribeTimer extends TimerTask {

    @Override
    public void run() {
      String[] ips = ClientZooKeeperAdapter.getAddresses();
      Map<String, String> a = Maps.newHashMap();
      a.put(PUSH_SERVICE_ENDPOINT, endpoint);
      for (int i = 0; i < ips.length; i++) {
        try {
          sa.subscribe(ips[i], categories, a);
        } catch (XLogException e) {
          e.printStackTrace();
        }
        logger.debug("the subscription: " + ips[i]);
      }
    }
  }
}
