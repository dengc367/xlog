package com.renren.dp.xlog.pubsub.pull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlog.slice.ErrorCode;
import xlog.slice.XLogException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.renren.dp.xlog.logger.XLogLogger;
import com.renren.dp.xlog.pubsub.DispatcherAdapter;
import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperRegister;
import com.renren.dp.xlog.pubsub.zookeeper.client.ClientZooKeeperAdapter;

/**
 * Pull method to get the Pub/Sub data
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:57:11 PM Mar 5, 2013
 */
public class Puller implements ZooKeeperRegister {

  private static final Logger LOG = LoggerFactory.getLogger(Puller.class);
  public static String endPointPattern = "P:default -h <HOST> -p <PORT>";
  private DispatcherAdapter diapatcherAdapter;
  private String[] categories;
  private XLogLogger logger;
  private ConcurrentHashMap<String, PullerThread> threadMap;

  private ClientZooKeeperAdapter zookeeperAdapter;

  public Puller(String[] categories, XLogLogger logger) {
    diapatcherAdapter = DispatcherAdapter.getInstance();
    this.categories = categories;
    this.logger = logger;
    threadMap = new ConcurrentHashMap<String, PullerThread>(new HashMap<String, PullerThread>());
    zookeeperAdapter = ClientZooKeeperAdapter.getInstance();
    zookeeperAdapter.addRegister(this);
  }

  public synchronized void subscribe() {
    LOG.info("subscribe for the categories: " + Arrays.toString(categories));
    String[] ips = zookeeperAdapter.getAddresses();
    LOG.info("the endpoints：　" + Arrays.toString(ips));
    if (ips == null || ips.length == 0) {
      LOG.error("can not get the xlog zookeeper cluster.");
      return;
    }
    for (int i = 0; i < ips.length; i++) {
      String ip = ips[i];
      if (!threadMap.containsKey(ip)) {
        PullerThread t = new PullerThread(ips[i]);
        t.start();
        threadMap.put(ips[i], t);
      }
    }
    closeThreads(Sets.difference(threadMap.keySet(), Sets.newHashSet(ips)));
  }

  private synchronized void closeThreads(final Set<String> aa) {
    Set<String> bb = Sets.newHashSet(aa);
    if (bb.isEmpty())
      return;
    LOG.info("Disconnecting some endpoints.");
    for (String b : bb) {
      PullerThread t = threadMap.remove(b);
      t.close();
    }
  }

  public void unsubscribe() {
    LOG.info("Unsubscribe all endpoints.");
    closeThreads(threadMap.keySet());
    zookeeperAdapter = null;
    diapatcherAdapter = null;
  }

  private class PullerThread extends Thread {
    private String iceEndpoint;
    private boolean running = true;

    public PullerThread(String iceEndpoint) {
      super(iceEndpoint.replace(" ", "_") + Arrays.toString(categories));
      this.iceEndpoint = iceEndpoint;
    }

    public void close() {
      running = false;
      LOG.info("-- close the thread: " + getName() + ", running: " + running);
      try {
        diapatcherAdapter.unsubscribe(iceEndpoint, categories, null);
      } catch (Exception e) {
      }
    }

    private int sub() {
      LOG.info("subscribing the categories: " + categories + " to the dispatcher endpoint: " + iceEndpoint);

      Map<String, String> options = Maps.newHashMap();
      options.put("xlog.fetch.length", "5");
      options.put("xlog.fetch.from.scratch", "true");
      int categoryId;
      try {
        categoryId = diapatcherAdapter.subscribe(iceEndpoint, categories, options);
        LOG.info("subscribe to the endpoint: " + iceEndpoint + " succeed");
      } catch (XLogException e) {
        LOG.error("subscribe the endpoint " + iceEndpoint + " failed.", e);
        categoryId = -1;
      }
      return categoryId;
    }

    @Override
    public void run() {
      int categoryId = sub();
      int i = 1;
      while (running && categoryId > 0) {
        String[] logs = null;
        try {
          logs = diapatcherAdapter.getData(iceEndpoint, categoryId);
        } catch (XLogException e) {
          if (e.code == ErrorCode.NoSubscription) {
            categoryId = sub();
            continue;
          } else if (e.code == ErrorCode.IOException) {
            logs = null;
          }
        } catch (Exception e) {
          LOG.error("Fail to getData from the thread " + getName() + ", running=" + running + "  : ", e);
        }
        if (logs != null && logs.length != 0) {
          synchronized (logger) {
            logger.print(logs);
          }
          i = 1;
        } else {
          ThreadUtils.sleep(100 * i++);
        }
      }
    }
  }

  @Override
  public void callback() {
    subscribe();
  }
}

class ThreadUtils {
  static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e1) {
    }
  }
}