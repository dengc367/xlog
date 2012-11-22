package com.renren.dp.xlog.dispatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.KeeperException;

import xlog.proto.Xlog.ItemInfo;
import xlog.proto.Xlog.ItemInfo.Builder;
import xlog.slice.DispatcherPrx;
import xlog.slice.LogData;
import xlog.slice.LoggerPrx;
import xlog.slice.Subscription;
import xlog.slice._DispatcherDisp;
import Ice.Connection;
import Ice.Current;
import Ice.Endpoint;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.config.DispatcherCluster;
import com.renren.dp.xlog.logger.LoggerI;

import dp.election.impl.DefaultWatcher;
import dp.zk.ZkConn;

public class DispatcherI extends _DispatcherDisp {
  private static final long serialVersionUID = 5776184542612999955L;

  private ObjectPrx myprx;
  private DispatcherCluster<DispatcherPrx> cfg;
  private LoggerI logger;
  private Publisher publisher;
  private ZkConn conn = null;

  protected boolean initialize(ObjectAdapter adapter) {
    myprx = adapter.add(this, adapter.getCommunicator().stringToIdentity("D"));
    logger = new LoggerI();
    logger.initialize(adapter);
    publisher = new Publisher();
    long delayTime = Configuration.getLong("master.start.delay", 300) * 1000;
    int zkSessionTimeOut = Configuration.getInt("zk.session.timeout", 2) * 1000;
    conn = new ZkConn(Configuration.getString("zookeeper.connstr"), zkSessionTimeOut, new DefaultWatcher(this));
    try {
      cfg = DispatcherCluster.create(conn, adapter.getCommunicator());
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return initCluster(delayTime);
  }

  public void close() {
    conn.close();
  }

  private boolean initCluster(long delayTime) {
    cfg.addDispatcher(this);

    try {
      cfg.addWatcher(delayTime);
    } catch (KeeperException e) {
      e.printStackTrace();
      return false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean reinitialize() {
    try {
      conn.reconnect();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return initCluster(0);
  }

  @Override
  public boolean register(LoggerPrx subscriber, int frequence, Current __current) {
    return false;
  }

  @Override
  public void add(LogData[] datas, Current __current) {
    logger.add(datas);
  }

  public String toString() {
    StringBuffer endpointline = new StringBuffer("D");
    Endpoint[] es = myprx.ice_getEndpoints();
    for (Endpoint e : es) {
      endpointline.append(":").append(e._toString());
    }
    return endpointline.toString();
  }

  @Override
  public void createZNode(int slot, Current __current) {
    cfg.createZNode(slot);
  }

  @Override
  public void addLogData(LogData data, Current __current) {
    publisher.publish(data);
    logger.addLogData(data);
  }

  @Override
  public int subscribe(Subscription sub, Current __current) {
    String remoteIp = ClientInfo.getRemoteClientIp(__current);
    sub.host = sub.host.replace("<HOST>", remoteIp);
    return publisher.subscribe(sub);
  }

  @Override
  public int unsubscribe(Subscription sub, Current __current) {
    String remoteIp = ClientInfo.getRemoteClientIp(__current);
    sub.host = sub.host.replace("<HOST>", remoteIp);
    return publisher.unsubscribe(sub);
  }

}
