package com.renren.dp.xlog.sync;

import java.io.File;

import junit.framework.TestCase;

public class HDFSSyncTask1_0_3Test extends TestCase{

  private HDFSSyncTask syncTask=null;
  
  public void setUp(){
    String slaveRootPath="/home/xianquanzhang/data/oplog1/hdfs";
    syncTask=new HDFSSyncTask(new File("/home/xianquanzhang/data/oplog1/hdfs/3g/access/2013-01-06-10.x1"),slaveRootPath.length());
  }
  
  public void testRun(){
    syncTask.run();
  }
}
