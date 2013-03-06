package com.renren.dp.xlog.pubsub.zookeeper.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Sets;
import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperRegister;

public class ClientZooKeeperAdapter implements ZooKeeperRegister {
  private static ClientZooKeeperHelper _helper;
  private static ClientZooKeeperAdapter _instance;

  private Set<ZooKeeperRegister> set = Sets.newHashSet();
  private Queue<String> addresses = new LinkedList<String>();

  private ClientZooKeeperAdapter() {
    _helper = ClientZooKeeperHelper.getInstance();
    _helper.setRegister(this);
  }

  public String[] getAddresses() {
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
    return addresses.toArray(new String[0]);
  }

  public void addRegister(ZooKeeperRegister register) {
    synchronized (set) {
      set.add(register);
    }
  }

  @Override
  public void callback() {
    synchronized (set) {
      for (ZooKeeperRegister reg : set) {
        reg.callback();
      }
    }
  }

  public synchronized static ClientZooKeeperAdapter getInstance() {
    if (_instance == null) {
      _instance = new ClientZooKeeperAdapter();
    }
    return _instance;
  }
}
