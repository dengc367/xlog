package com.renren.dp.xlog.pubsub.pull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.pubsub.PubSubConstants;

import xlog.slice.ErrorCode;
import xlog.slice.Subscription;
import xlog.slice.XLogException;

public class PullService implements Closeable {

  private static Map<Integer, LogFileStreamReader> readerMap = Maps.newHashMap();
  private static Map<String, Integer> idMap = Maps.newHashMap();
  private static Integer nextId = 1;
  FileSystem fs;

  public PullService() throws IOException {
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set("fs.default.name", Configuration.getString("storage.uri"));
    fs = DistributedFileSystem.get(conf);
  }

  public int subscribe(Subscription sub) throws XLogException {
    int ret = checkSubscription(sub);
    if (ret != PubSubConstants.SUCCESS) {
      throw new XLogException(ErrorCode.IllegalParameters, "illegal parameters");
    }
    String catStr = serializeCategories(sub.categories);
    String fetchsize = StringUtils.defaultIfEmpty(sub.options.get(PubSubConstants.XLOG_FETCH_LENGTH), "1048576");
    try {
      if (!idMap.containsKey(catStr)) { // check
        synchronized (idMap) {
          if (!idMap.containsKey(catStr)) { // double check
            LogFileStreamReader reader = new LogFileStreamReader(sub.categories, Integer.parseInt(fetchsize), fs);
            idMap.put(catStr, nextId);
            readerMap.put(nextId, reader);
            return nextId++;
          }
        }
      }
      int id = idMap.get(catStr);
      String fromScratch = StringUtils
          .defaultIfEmpty(sub.options.get(PubSubConstants.XLOG_FETCH_FROM_SCRATCH), "false");
      if ("true".equals(fromScratch)) {
        Closeables.closeQuietly(readerMap.get(id));
        readerMap.put(id, new LogFileStreamReader(sub.categories, Integer.parseInt(fetchsize), fs));
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
      Closeables.closeQuietly(readerMap.remove(catStr));
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

  public byte[] getBytes(int categoryId) throws IOException, XLogException {
    LogFileStreamReader reader = readerMap.get(categoryId);
    if (reader != null) {
      return reader.getByteStream();
    } else {
      throw new XLogException(ErrorCode.NoSubscription, "no subscription of the client");
    }
  }

  @Override
  public void close() throws IOException {
    if (fs == null) {
      fs.close();
    }
    readerMap.clear();
    idMap.clear();
    readerMap = null;
    idMap = null;
  }

}
