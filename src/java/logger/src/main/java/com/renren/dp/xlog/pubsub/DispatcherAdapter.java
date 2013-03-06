package com.renren.dp.xlog.pubsub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jfree.util.Log;

import xlog.slice.DispatcherPrx;
import xlog.slice.DispatcherPrxHelper;
import xlog.slice.Subscription;
import xlog.slice.XLogException;

/**
 * A DispatcherI Adapter
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 3:57:56 PM Mar 5, 2013
 */
public class DispatcherAdapter {

  private static Ice.Communicator ic;
  private static ConcurrentHashMap<String, DispatcherPrx> psMap = new ConcurrentHashMap<String, DispatcherPrx>(
      new HashMap<String, DispatcherPrx>());

  public DispatcherAdapter() {
    Ice.Properties prop = Ice.Util.createProperties();
    prop.setProperty("Ice.MessageSizeMax", "20971520"); // 20M
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public DispatcherPrx getDispatcherPrx(String host, boolean createNew) {
    DispatcherPrx ps = psMap.get(host);
    if (createNew || ps == null) {
      ps = DispatcherPrxHelper.uncheckedCast(ic.stringToProxy(host).ice_locatorCacheTimeout(60).ice_compress(true));
      psMap.put(host, ps);
    }
    return ps;
  }

  public DispatcherPrx getDispatcherPrx(String host) {
    return getDispatcherPrx(host, false);
  }

  private/* volatile */static DispatcherAdapter _sa;

  public static DispatcherAdapter getInstance() {
    if (_sa == null) {
      synchronized (DispatcherAdapter.class) {
        if (_sa == null) {
          _sa = new DispatcherAdapter();
        }
      }
    }
    return _sa;
  }

  public int subscribe(String serverip, String[] categories, Map<String, String> options) throws XLogException {
    return getDispatcherPrx(serverip).subscribe(new Subscription(categories, options));

  }

  public void unsubscribe(String serverip, String[] categories, Map<String, String> options) throws XLogException {
    DispatcherPrx ps = psMap.remove(serverip);
    if (ps != null) {
      ps.unsubscribe(new Subscription(categories, options));
    }
  }

  public String[] getData(String iceEndpoint, int categoryId) throws XLogException {
    try {
      return getDispatcherPrx(iceEndpoint).getData(categoryId);
    } catch (Ice.ConnectionRefusedException e) {
      Log.error("Ice.ConnectionRefusedException, retry again. " + e);
      return getDispatcherPrx(iceEndpoint, true).getData(categoryId);
    }
  }
}
