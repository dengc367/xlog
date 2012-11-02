package com.renren.dp.xlog.pubsub.zookeeper.client;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientZooKeeperAdapter {
  private static Logger logger = LoggerFactory.getLogger(ClientZooKeeperAdapter.class);

  private static Queue<String> addresses = new LinkedList<String>();

  static ClientZooKeeperHelper _helper;

  static {
    _helper = ClientZooKeeperHelper.getInstance();
    _helper.setRegister(new ClientZooKeeperRegistry());
  }

  public static void loadAddresses() {
    Set<String> ips = _helper.getServerSet();
    synchronized (addresses) {
      addresses.clear();
      if (CollectionUtils.isNotEmpty(ips)) {
        Iterator<String> iter = ips.iterator();
        while (iter.hasNext()) {
          String host = iter.next();
          addresses.offer(host);
        }
      }
    }
    logger.info("the addresses：　" + Arrays.toString(addresses.toArray()));
  }

  public static String[] getAddresses() {
    if (CollectionUtils.isEmpty(addresses)) {
      loadAddresses();
    }
    return addresses.toArray(new String[0]);
  }

  public static String getAddress() {
    String ip = null;
    if (CollectionUtils.isEmpty(addresses)) {
      loadAddresses();
    }
    synchronized (addresses) {
      ip = addresses.poll();
      addresses.offer(ip);
    }
    return ip;
  }

  public static void main(String[] args) {
    System.out.println(ClientZooKeeperAdapter.getAddress());
    System.out.println(ClientZooKeeperAdapter.getAddress());
    System.out.println(ClientZooKeeperAdapter.getAddress());
  }
}
