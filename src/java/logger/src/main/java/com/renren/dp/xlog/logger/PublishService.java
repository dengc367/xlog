package com.renren.dp.xlog.logger;

import Ice.Current;
import xlog.slice.LogData;
import xlog.slice._PublisherServiceDisp;

public class PublishService extends _PublisherServiceDisp {

  private static final long serialVersionUID = 6920617857631824253L;

  XLogLogger logger;

  public PublishService(String host, int port, Class<? extends XLogLogger> clazz) {
    try {
      this.logger = clazz.newInstance();
      // TODO
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int publish(LogData data, Current __current) {
    logger.log(data.logs);
    return 0;
  }

  private static Ice.Communicator ic = null;
  static {
    Ice.Properties prop = Ice.Util.createProperties();
    prop.setProperty("Ice.MessageSizeMax", "10480");
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public void serve() {
    Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("XlogLogger", "default");
    adapter.add(this, adapter.getCommunicator().stringToIdentity("P"));
    // TODO add the host, and port
    adapter.activate();
  }
}
