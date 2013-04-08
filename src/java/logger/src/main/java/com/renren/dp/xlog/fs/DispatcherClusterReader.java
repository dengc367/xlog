package com.renren.dp.xlog.fs;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Closeables;

/**
 * a dispatcher cluster reader
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 4:28:22 PM Apr 7, 2013
 */
public class DispatcherClusterReader implements Watcher, Closeable {
  private static final Logger LOG = LoggerFactory.getLogger(DispatcherClusterReader.class);
  private static final String ZOOKEEPER_ROOT_PATH = "/";
  private ZooKeeper zk;
  private String[] categories;
  private XLogConfiguration conf;
  private DispatcherReader[] readerArray = new DispatcherReader[0];
  private int next = 0;
  private String zkConnString;

  public DispatcherClusterReader(String[] categories, String zkConnString, XLogConfiguration conf) {
    this.categories = categories;
    this.zkConnString = zkConnString;
    this.conf = conf;
    connectZooKeeper();
    initDispatcherClient();
  }

  private void connectZooKeeper() {
    zk = ZooKeeperFactory.newZooKeeper(zkConnString, this);
  }

  public synchronized String readLine() throws IOException {
    if (readerArray.length == 0) {
      return null;
    }
    for (int i = readerArray.length - 1; i >= 0; i++) {
      String line = readerArray[next].readLine();
      next = (next + 1) % readerArray.length;
      if (line != null) {
        return line;
      }
    }
    return null;
  }

  @Override
  public void process(WatchedEvent event) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("WatchedEvent received: " + event);
    }

    if (event.getType() == Event.EventType.None) {
      // We are are being told that the state of the
      // connection has changed
      switch (event.getState()) {
      case SyncConnected:
        try {
          initDispatcherClient(); // 刷新数据, 防止重连时可能造成的信息丢失
        } catch (Exception e) {
          if (e instanceof ConnectionLossException
              || (e.getCause() != null && e.getCause() instanceof ConnectionLossException)) {
            // 如果是ConnectionLossException引起的，重连之
            connectZooKeeper();
          } else {
            LOG.error("Error while refreshing data from ZooKeeper on event: " + event, e);
          }
        }
        break;
      case Expired:
        LOG.warn("Zookeeper session expired: " + event);
        connectZooKeeper();
        break;
      }
    } else if (event.getType() == Event.EventType.NodeChildrenChanged
        || event.getType() == Event.EventType.NodeDataChanged || event.getType() == Event.EventType.NodeDeleted) {
      initDispatcherClient();
    } else {
      LOG.warn("Unhandled event:" + event);
    }
  }

  private void initDispatcherClient() {
    Set<String> oldSet = getOldSet();
    Set<String> newSet = getServerSet();
    updateReaders(Sets.difference(oldSet, newSet), Sets.difference(newSet, oldSet));

  }

  private Set<String> getOldSet() {
    Set<String> tmp = Sets.newHashSet();
    for (int i = 0; i < readerArray.length; i++) {
      tmp.add(readerArray[i].getEndpoint());
    }
    return tmp;
  }

  private synchronized void updateReaders(SetView<String> removeSet, SetView<String> addSet) {
    List<DispatcherReader> list = Lists.newArrayList();
    DispatcherReader[] tmpArray = readerArray;
    for (DispatcherReader reader : tmpArray) {
      if (removeSet.contains(reader.getEndpoint())) {
        Closeables.closeQuietly(reader);
        LOG.debug("Close " + reader);
      } else {
        list.add(reader);
        LOG.debug("Add old dispatcherReader, endpoint: " + reader + ", categories: " + Arrays.toString(categories));
      }
    }
    for (String reader : addSet) {
      try {
        list.add(new DispatcherReader(reader, categories, conf));
      } catch (IOException e) {
        LOG.error(
            "Update dispatcherReader failed: endpoint: " + reader + ", categories: " + Arrays.toString(categories), e);
      }
    }
    Collections.shuffle(list);
    readerArray = list.toArray(new DispatcherReader[list.size()]);
  }

  private Set<String> getServerSet() {

    Set<String> hosts = new HashSet<String>();
    try {
      if (zk.exists(ZOOKEEPER_ROOT_PATH, false) != null) {
        List<String> hc = zk.getChildren(ZOOKEEPER_ROOT_PATH, this, null);
        for (int j = 0; j < hc.size(); j++) {
          String path = ZOOKEEPER_ROOT_PATH + hc.get(j);
          List<String> hostChildren = zk.getChildren(path, this, null);
          for (int i = 0; i < hostChildren.size(); i++) {
            String host = hostChildren.get(i);
            byte[] bytes = zk.getData(path + "/" + host, this, null);
            String data = new String(bytes);
            if (StringUtils.isNotBlank(data)) {
              // data format: D:tcp -h 10.4.19.90 -p 53416
              hosts.add(data);
            }
          }
        }
      }
    } catch (KeeperException e) {
      LOG.warn("the ZooKeeper throw Exception ", e);
    } catch (InterruptedException e) {
      LOG.warn("the thread was interrupted! ", e);
    }
    return hosts;
  }

  public void close() throws IOException {
    synchronized (this) {
      for (DispatcherReader reader : readerArray) {
        reader.close();
      }
    }
  }
}
