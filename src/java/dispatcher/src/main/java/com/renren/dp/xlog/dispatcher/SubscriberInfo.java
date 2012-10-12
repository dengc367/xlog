package com.renren.dp.xlog.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class SubscriberInfo {

  private List<String> hosts;
  private boolean shouldPublishAllNodes;
  private Random rand;

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
    rand = new Random(hosts.size());
  }

  public void addSubScribeHost(String host) {
    this.hosts.add(host);
    rand = new Random(hosts.size());
  }

  public static void main(String[] args) {
    Random r = new Random(10);
    for (int i = 0; i < 10; i++)
      System.out.println(r.nextInt(10));
  }
}
