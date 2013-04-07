package com.renren.dp.xlog.logger;

import java.io.IOException;

import com.renren.dp.xlog.fs.XLogFileSystem;
import com.renren.dp.xlog.fs.XLogReader;

public class TestXLogFileSystem {
  public static void main(String[] args) {
    XLogFileSystem xfs = new XLogFileSystem("10.7.19.14:10060");
    
    try {
      XLogReader reader = xfs.open(new String[] { "feed", "dispatchtoV2" });
      String line;
      while((line = reader.readLine()) != null){
        System.out.println(line);
      }
      System.out.println("-------------------------------------");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
