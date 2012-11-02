package com.renren.dp.xlog.pubsub;

import java.util.HashMap;
import java.util.Map;

import xlog.slice.DispatcherPrx;
import xlog.slice.DispatcherPrxHelper;
import xlog.slice.Subscription;

public class SubscribeAdapter {

  private static Ice.Communicator ic;
  private static Map<String, DispatcherPrx> psMap = new HashMap<String, DispatcherPrx>();

  public SubscribeAdapter() {
    Ice.Properties prop = Ice.Util.createProperties();
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public DispatcherPrx getDispatcherPrx(String host) {
    DispatcherPrx ps = psMap.get(host);
    if (ps == null) {
      ps = DispatcherPrxHelper.uncheckedCast(ic.stringToProxy(host).ice_locatorCacheTimeout(60).ice_compress(true));
      psMap.put(host, ps);
    }
    return ps;
  }

  private/* volatile */static SubscribeAdapter _sa;

  public static SubscribeAdapter getInstance() {
    if (_sa == null) {
      synchronized (SubscribeAdapter.class) {
        if (_sa == null) {
          _sa = new SubscribeAdapter();
        }
      }
    }
    return _sa;
  }

  public void subscribe(String serverip, String endpoint, String[] categories) {
    getDispatcherPrx(serverip).subscribe(new Subscription(endpoint, categories, null));
  }

  public void unsubscribe(String serverip, String endpoint, String[] categories) {
    getDispatcherPrx(serverip).unsubscribe(new Subscription(endpoint, categories, null));
  }
}
