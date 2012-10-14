package com.renren.dp.xlog.dispatcher;

import Ice.Current;

public class ClientInfo {

  private static final String REMOTE_ADDRESS_KEY = "remote address =";

  public static String getRemoteClientIp(Current __current) {
    String connStr = __current.con._toString();
    connStr = connStr.substring(connStr.indexOf(REMOTE_ADDRESS_KEY) + REMOTE_ADDRESS_KEY.length());
    return connStr.split(":")[0];
  }
}
