package com.renren.dp.xlog.pubsub.pull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlog.slice.ErrorCode;
import xlog.slice.XLogException;

import com.google.common.collect.Maps;
import com.renren.dp.xlog.logger.XLogLogger;
import com.renren.dp.xlog.pubsub.DispatcherAdapter;
import com.renren.dp.xlog.pubsub.zookeeper.client.ClientZooKeeperAdapter;

public class Puller {

  private static final Logger LOG = LoggerFactory.getLogger(Puller.class);
  private static final int MAX_BLOCKING_QUEUE_SIZE = 100000;
  public static String endPointPattern = "P:default -h <HOST> -p <PORT>";
  private DispatcherAdapter sa;
  private String[] categories;
  private ExecutorService executor;
  private int numPerIp = 1;

  public Puller(String[] categories, XLogLogger logger) {
    sa = DispatcherAdapter.getInstance();
    this.categories = categories;
    Thread logThread = new LogThread(logger);
    logThread.setDaemon(true);
    logThread.start();

  }

  public void subscribe() {
    LOG.info("subscribe for the categories: " + Arrays.toString(categories));
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    if (ips == null || ips.length == 0) {
      LOG.error("can not get the xlog zookeepre cluster.");
      return;
    }
    executor = Executors.newFixedThreadPool(ips.length * numPerIp);
    for (int i = 0; i < ips.length; i++) {
      for (int j = 0; j < numPerIp; j++) {
        executor.execute(new PullerThread(ips[i]));
      }
    }
  }

  public void unsubscribe() {
    String[] ips = ClientZooKeeperAdapter.getAddresses();
    for (int i = 0; i < ips.length; i++) {
      LOG.debug("the subscription: " + ips[i]);
    }
    // TODO
  }

  class PullerThread extends Thread {
    String iceEndpoint;

    public PullerThread(String iceEndpoint) {
      super(iceEndpoint.replace(" ", "_") + Arrays.toString(categories));
      this.iceEndpoint = iceEndpoint;
    }

    private int subscribe() {
      LOG.info("subscribing the categories: " + categories + " to the dispatcher endpoint: " + iceEndpoint);

      Map<String, String> options = Maps.newHashMap();
      options.put("xlog.fetch.length", String.valueOf(500));
      options.put("xlog.fetch.from.scratch", "true");
      int categoryId;
      try {
        categoryId = sa.subscribe(iceEndpoint, categories, options);
        LOG.info("subscribe to the endpoint: " + iceEndpoint + " succeed");
      } catch (XLogException e) {
        LOG.error("subscribe the endpoint " + iceEndpoint + " failed.", e);
        categoryId = -1;
      }

      return categoryId;

    }

    @Override
    public void run() {
      int categoryId = subscribe();
      int i = 1;
      while (categoryId > 0) {
        String[] logs = null;
        try {
          logs = sa.getData(iceEndpoint, categoryId);
        } catch (XLogException e) {

          if (e.code == ErrorCode.NoSubscription) {
            categoryId = subscribe();
            continue;
          } else if (e.code == ErrorCode.IOException) {
            logs = null;
          }
        }
        if (logs != null && logs.length != 0) {
          for (String log : logs) {
            try {
              bq.offer(log);
            } catch (IllegalStateException e) {
              ThreadUtils.sleep(1000);
              // TODO
            } catch (Exception e) {
              // TODO: handle exception
            }
          }
          i = 1;
        } else {
           ThreadUtils.sleep(100  * i++);
//          ThreadUtils.sleep(100);
        }
      }
    }
  }

  class LogThread extends Thread {
    XLogLogger logger;

    public LogThread(XLogLogger logger) {
      super(logger.getClass().getSimpleName() + "Thread");
      this.logger = logger;
    }

    @Override
    public void run() {
      String log;
      while (true) {
        try {
          log = bq.take();
        } catch (InterruptedException e) {
          LOG.warn("interrupted unknownly", e);
          log = null;
        }
        if (log != null) {
          logger.print(log);
        }

      }
    }
  }

  // private ArrayList<String> bq = new
  // ArrayList<String>(MAX_BLOCKING_QUEUE_SIZE);
  private BlockingQueue<String> bq = new ArrayBlockingQueue<String>(MAX_BLOCKING_QUEUE_SIZE);
}

class ThreadUtils {
  static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }
}