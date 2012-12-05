package com.renren.dp.xlog.pubsub.push;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PushServiceNodesInfo {

  /**
   * <host, time>
   */
  private Map<String, Long> hostMap = Maps.newHashMap();
  private Map<String, Long> blackHostMap = Maps.newHashMap();
  // TODO add the gray hosts
  private Map<String, Long> grayhostMap = Maps.newHashMap();  
  private List<String> hosts = Lists.newArrayList();
  private boolean shouldPublishAllNodes;

  public List<String> getSubScribeHosts() {
    return hosts;
  }

  public void setShouldPublishAllNodes(boolean shouldPublishAllNodes) {
    this.shouldPublishAllNodes = shouldPublishAllNodes;
  }

  public boolean isPulishAllNodes() {
    return this.shouldPublishAllNodes;
  }

  public PushServiceNodesInfo() {
    this(false);
  }

  public PushServiceNodesInfo(boolean shouldPublishAllNodes) {
    this(new ArrayList<String>(), shouldPublishAllNodes);
  }

  public PushServiceNodesInfo(List<String> hosts, boolean shouldPublishAllNodes) {
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

  public void setBlackHosts(List<String> blackhosts) {
    if (null == blackhosts || blackhosts.isEmpty()) {
      return;
    }
    for (String host : blackhosts) {
      setBlackHost(host);
    }
  }
}
