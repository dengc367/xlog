package com.renren.dp.xlog.pubsub.pull;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.renren.dp.xlog.pubsub.PubSubConstants;

import xlog.slice.ErrorCode;
import xlog.slice.Subscription;
import xlog.slice.XLogException;

public class PullService {
  

  private static Map<Integer, LogFileStreamReader> readerMap = Maps.newHashMap();
  private static Map<String, Integer> idMap = Maps.newHashMap();
  private static Integer nextId = 1;

  public PullService() {
  }

  public String[] getData(int id) throws IOException, XLogException {
    LogFileStreamReader reader = readerMap.get(id);
    if (reader != null) {
      return reader.getLineStream();
    } else {
      throw new XLogException(ErrorCode.NoSubscription, "no subscription of the client");
    }
  }

  public int subscribe(Subscription sub) throws XLogException {
    int ret = checkSubscription(sub);
    if (ret != PubSubConstants.SUCCESS) {
      throw new XLogException(ErrorCode.IllegalParameters, "illegal parameters");
    }
    String catStr = serializeCategories(sub.categories);
    String fetchsize = StringUtils.defaultIfEmpty(sub.options.get(PubSubConstants.XLOG_FETCH_LENGTH), "50");
    try {
      if (!idMap.containsKey(catStr)) { // check
        synchronized (idMap) {
          if (!idMap.containsKey(catStr)) { // double check
            LogFileStreamReader reader = new LogFileStreamReader(sub.categories, Integer.parseInt(fetchsize));
            idMap.put(catStr, nextId);
            readerMap.put(nextId, reader);
            return nextId++;
          }
        }
      }
      int id = idMap.get(catStr);
      String fromScratch = StringUtils.defaultIfEmpty(sub.options.get(PubSubConstants.XLOG_FETCH_FROM_SCRATCH), "false");
      if ("true".equals(fromScratch)) {
        LogFileStreamReader reader = new LogFileStreamReader(sub.categories, Integer.parseInt(fetchsize));
        readerMap.put(id, reader);
      }
      return id;
    } catch (IOException e) {
      throw new XLogException(ErrorCode.IOException, "io exception.");
    }
  }

  public int unsubscribe(Subscription sub) {
    int ret = checkSubscription(sub);
    if (ret == PubSubConstants.SUCCESS) {
      String catStr = serializeCategories(sub.categories);
      readerMap.remove(catStr);
    }
    return ret;
  }

  public static String serializeCategories(String[] categories) {
    return serializeCategories(categories, "/");
  }

  private int checkSubscription(Subscription sub) {
    if (sub.options == null) {
      return PubSubConstants.SUBSCRIPTION_PARAM_ILLEGAL;
    }
    String[] cat = sub.categories;
    if (!ArrayUtils.isEmpty(cat)) {
      for (int i = 0; i < cat.length; i++) {
        if (cat[i].isEmpty()) {
          return PubSubConstants.SUBSCRIPTION_PARAM_ILLEGAL;
        }
      }
      return PubSubConstants.SUCCESS;
    }
    return PubSubConstants.SUBSCRIPTION_PARAM_ILLEGAL;
  }

  public boolean isSubscribed(String[] categories) {
    return readerMap.containsKey(serializeCategories(categories));
  }

  public static String serializeCategories(String[] categories, String string) {
    StringBuilder catStr = new StringBuilder();
    for (int i = 0; i < categories.length; i++) {
      catStr.append(categories[i]);
      if (i < categories.length - 1) {
        catStr.append(string);
      }
    }
    return catStr.toString();
  }

}
