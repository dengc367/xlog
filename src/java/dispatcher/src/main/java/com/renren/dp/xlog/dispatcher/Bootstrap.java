package com.renren.dp.xlog.dispatcher;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.renren.dp.xlog.config.Configuration;

public class Bootstrap {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int port = Configuration.getInt("web.server.port", 10000);
    String ip = Configuration.getString("web.server.host", "");
    SystemManager sm=null;
    if ("".equals(ip)) {
      try {
        ip = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        System.err.println("Fail to get local host address.message is : "
            + e.getMessage());
        return;
      }
    }
    if (args == null || args.length == 0) {
      System.err.println("please type start parameter!");
    } else if (args[0].equals("webserver")) {
      try {
        new WebServer(ip, port).start();
      } catch (Exception e) {
        System.err.println("Fail to start webserver!exception is "+e.getMessage());
      }
    } else if (args[0].equals("dispatcher")) {
      sm=new SystemManager();
      sm.start();
    } else if (args[0].equals("all")) {
      sm=new SystemManager();
      sm.start();
      try {
        new WebServer(ip, port).start();
      } catch (Exception e) {
        System.err.println("Fail to start webserver!exception is "+e.getMessage());
      }
    }
  }
}
