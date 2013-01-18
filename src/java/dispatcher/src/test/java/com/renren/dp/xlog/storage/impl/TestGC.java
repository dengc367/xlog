package com.renren.dp.xlog.storage.impl;

import com.renren.monitor.metrics.impl.JVMMetrics;

public class TestGC {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    while(true){
      System.out.println(JVMMetrics.getHeapMemoryUsage()+"/"+JVMMetrics.getTotalMemory());
      try {
        Thread.sleep(1000*2);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
