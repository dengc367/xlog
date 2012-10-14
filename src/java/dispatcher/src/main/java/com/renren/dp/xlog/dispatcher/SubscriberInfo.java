package com.renren.dp.xlog.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SubscriberInfo {

  /**
   * <host, time>
   */
  private Map<String, Long> hostMap = Maps.newHashMap();
  private Map<String, Long> blackHostMap = Maps.newHashMap();
  private List<String> hosts = Lists.newArrayList();
  private boolean shouldPublishAllNodes;
  private Random rand = new Random();

  public List<String> getSubScribeHosts() {
    if (shouldPublishAllNodes)
      return hosts;
    return Lists.newArrayList(hosts.get(rand.nextInt(hosts.size())));
  }

  public void setShouldPublishAllNodes(boolean shouldPublishAllNodes) {
    this.shouldPublishAllNodes = shouldPublishAllNodes;
  }

  public SubscriberInfo() {
    this(false);
  }

  public SubscriberInfo(boolean shouldPublishAllNodes) {
    this(new ArrayList<String>(), shouldPublishAllNodes);
  }

  public SubscriberInfo(List<String> hosts, boolean shouldPublishAllNodes) {
    this.shouldPublishAllNodes = shouldPublishAllNodes;
    Long time = System.currentTimeMillis();
    for (String host : hosts) {
      this.hostMap.put(host, time);
      if (!hosts.contains(host))
        this.hosts.add(host);
    }
  }

  public void addHost(String host) {
    this.hostMap.put(host, System.currentTimeMillis());
    if (!hosts.contains(host)) {
      hosts.add(host);
    }
    this.blackHostMap.remove(host);
  }

  public void removeHost(String host) {
    this.hostMap.remove(host);
    this.hosts.remove(host);
    this.blackHostMap.remove(host);
  }

  /**
   * TODO the better black host policy needed
   * 
   * @param host
   */
  public void setBlackHost(String host) {
    Long time = this.hostMap.remove(host);
    this.blackHostMap.put(host, time);
    this.hosts.remove(host);
  }
}
