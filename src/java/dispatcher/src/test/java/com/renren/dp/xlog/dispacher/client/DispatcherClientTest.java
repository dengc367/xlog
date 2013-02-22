package com.renren.dp.xlog.dispacher.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mahout.common.RandomUtils;
import org.uncommons.maths.random.MersenneTwisterRNG;

import junit.framework.TestCase;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.dispatcher.adapter.DispatcherAdapter;

import xlog.slice.DispatcherPrx;
import xlog.slice.LogData;

import dp.zk.ZkConn;

public class DispatcherClientTest extends TestCase {
  private final String[] CATEGORIES = new String[] { "xlog", "example",
      "level3", "file" };

  private DispatcherPrx prx = null;
  private List<String[]> LOG_DATA_CATEGORIES = null;

  public void setUp() {
    ZkConn conn = new ZkConn(Configuration.getString("zookeeper.connstr"),
        Configuration.getInt("zk.session.timeout", 5) * 1000, null);

    Ice.Communicator ic = Ice.Util.initialize();
    DispatcherAdapter<DispatcherPrx> cfg = null;
    try {
      cfg = DispatcherAdapter.create(conn, ic);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      prx = cfg.getDispatcher(CATEGORIES);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    LOG_DATA_CATEGORIES = new ArrayList<String[]>();
    for (int i = 0; i < 20; i++) {
      String[] category = new String[] { "3g", "game", "test" + i };
      LOG_DATA_CATEGORIES.add(category);
    }
  }

  Random RANDOM = RandomUtils.getRandom();
  public void testAddLogData() {
    RANDOM.setSeed(20);
    try {
      int count=0;
      while (true) {
        LogData logData = new LogData();
        logData.categories = LOG_DATA_CATEGORIES.get(RANDOM.nextInt());
        logData.checkSum = "123";
        logData.logs = new String[] {
            "here is 3 cache file name format.0 express 5 min generate one file,1 express 10 min generate one file,2 express one hour generate one file",
            "There is 3 mode.0 express memory storage and file storage,1 express file storage only,2 express some categories storage at file,and other storage at memory",
            "Storage adapter implements class.It can use this parameter to supports many storages." };

        prx.addLogData(logData);
        count++;
//        Thread.sleep(10);
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
