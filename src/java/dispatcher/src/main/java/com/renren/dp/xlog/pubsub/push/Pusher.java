package com.renren.dp.xlog.pubsub.push;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.pubsub.PubSubUtils;

import xlog.slice.LogData;
import xlog.slice.PushSubscription;
import xlog.slice.Subscription;

public class Pusher {
  public static int SUCCESS = 0;
  public static int SUBSCRIPTION_PARAM_ILLEGAL = 1000;
  Map<String, PushServiceNodesInfo> subMap;

  private BlockingQueue<LogMeta> bq = new ArrayBlockingQueue<LogMeta>(MAX_BLOCKING_QUEUE_SIZE, false);
  private static int MAX_BLOCKING_QUEUE_SIZE = 100000;
  private Logger LOG = LoggerFactory.getLogger(this.getClass());
  
  private PushAdapter pa;
  Thread pubThread;

  public Pusher() {
    pa = PushAdapter.getInstance();
    subMap = Maps.newHashMap();
    pubThread = new PublishThread("PublishThread");
    pubThread.setDaemon(true);
    pubThread.start();
  }

  class PublishThread extends Thread {
    public PublishThread(String name) {
      super(name);
    }

    private List<String> blackinghosts = Lists.newArrayList();

    /**
     * @param data
     */
    public void sendLog(LogData data) {
      PushServiceNodesInfo sub = subMap.get(PubSubUtils.serializeCategories(data.categories));
      if (null != sub) {
        List<String> hosts = sub.getSubScribeHosts();
        for (String h : hosts) {
          // TODO the best method to solve the publish problems
          int ret = pa.publish(h, data);
          if (ret != 0) {
            ret += pa.publish(h, data); // try twice
          }
          if (ret >= 2) {
            blackinghosts.add(h);
            continue;
          }
          if (!sub.isPulishAllNodes())
            break;
        }
        if (!blackinghosts.isEmpty()) {
          sub.setBlackHosts(blackinghosts);
          blackinghosts.clear();
        }
      }
    }

    @Override
    public void run() {
      while (true) {
        try {
          LogMeta logMeta = bq.take();
          sendLog(logMeta.getLogData());
          logMeta.free();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }

  

  public void publish(LogMeta logMeta) {

    if (bq.size() >= MAX_BLOCKING_QUEUE_SIZE) {
      LOG.warn("the log size is full(>=" + MAX_BLOCKING_QUEUE_SIZE + "), drop the new log");
      logMeta.free();
      return;
    }
    bq.add(logMeta);
  }

  public int subscribe(PushSubscription sub) {
    int ret = checkSubscription(sub);
    if (ret == SUCCESS) {
      String catStr = PubSubUtils.serializeCategories(sub.categories);
      boolean shouldPublishAllNodes = Configuration.getBoolean(catStr + ".shouldPublishAllNodes", false);
      PushServiceNodesInfo si = subMap.get(catStr);
      if (si == null) {
        subMap.put(catStr, new PushServiceNodesInfo(Lists.newArrayList(sub.host), shouldPublishAllNodes));
      } else {
        si.addHost(sub.host);
        si.setShouldPublishAllNodes(shouldPublishAllNodes);
        subMap.put(catStr, si);
      }
    }
    return ret;
  }

  public int unsubscribe(PushSubscription sub) {
    int ret = checkSubscription(sub);
    if (ret == SUCCESS) {
      String catStr = PubSubUtils.serializeCategories(sub.categories);
      subMap.remove(catStr);
    }
    return ret;
  }

  private int checkSubscription(Subscription sub) {
    String[] cat = sub.categories;
    if (!ArrayUtils.isEmpty(cat)) {
      for (int i = 0; i < cat.length; i++) {
        if (cat[i].isEmpty()) {
          return SUBSCRIPTION_PARAM_ILLEGAL;
        }
      }
      return SUCCESS;
    }
    return SUBSCRIPTION_PARAM_ILLEGAL;
  }

  public boolean isSubscribed(String[] categories) {
    return subMap.containsKey(PubSubUtils.serializeCategories(categories));
  }

}
