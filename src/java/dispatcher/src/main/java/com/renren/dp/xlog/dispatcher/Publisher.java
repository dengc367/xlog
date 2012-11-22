package com.renren.dp.xlog.dispatcher;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.renren.dp.xlog.config.Configuration;

import xlog.slice.LogData;
import xlog.slice.Subscription;

public class Publisher {
  public static int SUCCESS = 0;
  public static int SUBSCRIPTION_PARAM_ILLEGAL = 1000;
  Map<String, SubscriberInfo> subMap;

  PublishAdapter pa;

  public Publisher() {
    pa = PublishAdapter.getInstance();
    subMap = Maps.newHashMap();
  }

  public void publish(LogData data) {
    SubscriberInfo sub = subMap.get(serializeCategories(data.categories));
    if (null != sub) {
      List<String> hosts = sub.getSubScribeHosts();
      for (String h : hosts) {
        // TODO the best method to solve the publish problems
        int ret = pa.publish(h, data);
        if (ret != 0) {
          ret += pa.publish(h, data); // try twice
        }
        if (ret >= 2) {
          sub.setBlackHost(h);
        }
      }
    }
  }

  public int subscribe(Subscription sub) {
    int ret = checkSubscription(sub);
    if (ret == SUCCESS) {
      String catStr = serializeCategories(sub.categories);
      boolean shouldPublishAllNodes = Configuration.getBoolean(catStr + ".shouldPublishAllNodes", false);
      SubscriberInfo si = subMap.get(catStr);
      if (si == null) {
        subMap.put(catStr, new SubscriberInfo(Lists.newArrayList(sub.host), shouldPublishAllNodes));
      } else {
        si.addHost(sub.host);
        si.setShouldPublishAllNodes(shouldPublishAllNodes);
        subMap.put(catStr, si);
      }
    }
    return ret;
  }

  public int unsubscribe(Subscription sub) {
    int ret = checkSubscription(sub);
    if (ret == SUCCESS) {
      String catStr = serializeCategories(sub.categories);
      subMap.remove(catStr);
    }
    return ret;
  }

  private String serializeCategories(String[] categories) {
    StringBuilder catStr = new StringBuilder();
    for (int i = 0; i < categories.length; i++) {
      catStr.append(categories[i]);
      if (i < categories.length - 1) {
        catStr.append(".");
      }
    }
    return catStr.toString();
  }

  private int checkSubscription(Subscription sub) {

    String[] cat = sub.categories;
    if (!ArrayUtils.isEmpty(cat) && StringUtils.isNotBlank(sub.host)) {
      for (int i = 0; i < cat.length; i++) {
        if (cat[i].isEmpty()) {
          return SUBSCRIPTION_PARAM_ILLEGAL;
        }
      }
      return SUCCESS;
    }
    return SUBSCRIPTION_PARAM_ILLEGAL;
  }
}
