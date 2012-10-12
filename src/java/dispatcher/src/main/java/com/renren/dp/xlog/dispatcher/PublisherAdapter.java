package com.renren.dp.xlog.dispatcher;

import java.util.HashMap;
import java.util.Map;

import xlog.slice.AgentPrx;
import xlog.slice.AgentPrxHelper;
import xlog.slice.LogData;
import xlog.slice.PublisherServicePrx;
import xlog.slice.PublisherServicePrxHelper;

public class PublisherAdapter {

  private static final String UDP_MESSAGE_SIZE = "10485760";
  private/* volatile */static PublisherAdapter _pa;
  private static Map<String, PublisherServicePrx> psMap = new HashMap<String, PublisherServicePrx>();
  private static Ice.Communicator ic;

  private PublisherAdapter() {
    Ice.Properties prop = Ice.Util.createProperties();
    // 10M udp unit is bytes
    prop.setProperty("Ice.UDP.SndSize", UDP_MESSAGE_SIZE);
    prop.setProperty("Ice.UDP.RcvSize", UDP_MESSAGE_SIZE);
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public int publish(String host, LogData data) {
    return getPublisherServicePrx(host).publish(data);
  }

  private PublisherServicePrx getPublisherServicePrx(String host) {
    PublisherServicePrx ps = psMap.get(host);
    if (ps == null) {
      ps = PublisherServicePrxHelper.uncheckedCast(ic.stringToProxy(host).ice_locatorCacheTimeout(60)
          .ice_compress(true).ice_datagram());
    }
    return ps;
  }

  public static PublisherAdapter getInstance() {
    if (_pa == null) {
      synchronized (PublisherAdapter.class) {
        if (_pa == null) {
          _pa = new PublisherAdapter();
        }
      }
    }
    return _pa;
  }
}
