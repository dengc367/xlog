package com.renren.dp.xlog.logger;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.fs.XLogFileSystem;
import com.renren.dp.xlog.fs.XLogReader;

public class TestXLogFileSystem {
  private static final Logger LOG = LoggerFactory.getLogger("xlog.pubsub.log");

  public static void main(String[] args) {
    XLogFileSystem xfs = new XLogFileSystem("10.7.19.14:10060");
    try {
      XLogReader reader = xfs.open(new String[] { "feed", "dispatchtoV2" });
      String line;
      int i = 10;
      while ((line = reader.readLine()) != null) {
        LOG.debug(line);
        System.out.println(line);
        // if (i-- <= 0) {
        // break;
        // }
      }
      reader.close();
      reader = xfs.open(new String[] { "api", "data", "active_user_log" });
      i = 10;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
        if (i-- <= 0) {
          break;
        }
      }
      System.out.println("-------------------------------------");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
