package com.renren.dp.xlog.dispatcher;

import java.util.HashMap;
import java.util.Map;

import xlog.slice.AgentPrx;
import xlog.slice.AgentPrxHelper;
import xlog.slice.LogData;
import xlog.slice.PublisherServicePrx;
import xlog.slice.PublisherServicePrxHelper;

public class PublishAdapter {

  private static final String UDP_MESSAGE_SIZE = "10485760";
  private/* volatile */static PublishAdapter _pa;
  private static Map<String, PublisherServicePrx> psMap = new HashMap<String, PublisherServicePrx>();
  private static Ice.Communicator ic;

  private PublishAdapter() {
    Ice.Properties prop = Ice.Util.createProperties();
    // 10M udp unit is bytes
    // prop.setProperty("Ice.UDP.SndSize", UDP_MESSAGE_SIZE);
    // prop.setProperty("Ice.UDP.RcvSize", UDP_MESSAGE_SIZE);
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public int publish(String host, LogData data) {
    int ret = 0;
    try {
      ret = getPublisherServicePrx(host).publish(data);
    } catch (Exception e) {
      e.printStackTrace();
      ret = 1;
    }
    return ret;
  }

  private PublisherServicePrx getPublisherServicePrx(String host) {
    PublisherServicePrx ps = psMap.get(host);
    if (ps == null) {
      ps = PublisherServicePrxHelper.uncheckedCast(ic.stringToProxy(host).ice_locatorCacheTimeout(60)
          .ice_compress(true));
      psMap.put(host, ps);
    }
    return ps;
  }

  public static PublishAdapter getInstance() {
    if (_pa == null) {
      synchronized (PublishAdapter.class) {
        if (_pa == null) {
          _pa = new PublishAdapter();
        }
      }
    }
    return _pa;
  }
}