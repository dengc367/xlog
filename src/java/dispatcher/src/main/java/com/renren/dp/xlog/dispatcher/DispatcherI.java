package com.renren.dp.xlog.dispatcher;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import xlog.slice.DispatcherPrx;
import xlog.slice.ErrorCode;
import xlog.slice.LogData;
import xlog.slice.LoggerPrx;
import xlog.slice.Subscription;
import xlog.slice.XLogException;
import xlog.slice._DispatcherDisp;
import Ice.Current;
import Ice.Endpoint;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.config.DispatcherCluster;
import com.renren.dp.xlog.logger.LoggerI;
import com.renren.dp.xlog.pubsub.PubSubService;
import com.renren.dp.xlog.pubsub.PubSubUtils;

import dp.election.impl.DefaultWatcher;
import dp.zk.ZkConn;

public class DispatcherI extends _DispatcherDisp {
  private static final long serialVersionUID = 5776184542612999955L;

  private ObjectPrx myprx;
  private DispatcherCluster<DispatcherPrx> cfg;
  private LoggerI logger;
  private PubSubService pubsub = null;
  private ZkConn conn = null;
  boolean ispubSubStart = false;
  private volatile boolean isRunning = true;

  private static Logger LOG = Logger.getLogger(DispatcherI.class);

  protected boolean initialize(ObjectAdapter adapter) {
    myprx = adapter.add(this, adapter.getCommunicator().stringToIdentity("D"));
    logger = new LoggerI();
    logger.initialize(adapter);
    ispubSubStart = Configuration.getBoolean("xlog.pubsub.start", false);
    if (ispubSubStart) {
      LOG.info("the pubsub server is starting.");
      pubsub = new PubSubService();
      logger.setPubSub(pubsub);
    }
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
    this.isRunning = false;
    conn.destroy();
    conn = null;
    logger.close();
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
    if (isRunning) {
      try {
        conn.reconnect();
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      return initCluster(0);
    }
    return false;
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
    logger.addLogData(data);
    // TODO
  }

  @Override
  public int subscribe(Subscription sub, Current __current) throws XLogException {
    if (!ispubSubStart) {
      LOG.warn("the pubsub server is not started, but the client " + PubSubUtils.getRemoteClientIp(__current)
          + " want to subscribe. ");
      throw new XLogException(ErrorCode.PubSubNotStartedException, "the pubsub server is not started");
    }
    return pubsub.subscribe(sub, __current);
  }

  @Override
  public int unsubscribe(Subscription sub, Current __current) {
    return pubsub.unsubscribe(sub, __current);
  }

  @Override
  public String[] getData(int categoryId, Current __current) throws XLogException {
    try {
      if (!ispubSubStart) {
        LOG.warn("the pubsub server is not started, but the client " + PubSubUtils.getRemoteClientIp(__current)
            + " want to getData. ");
        throw new XLogException(ErrorCode.PubSubNotStartedException, "the pubsub server is not started");
      }
      return pubsub.getData(categoryId, __current);
    } catch (IOException e) {
      LOG.error(e);
      return null;
    }
  }

}
