package com.renren.dp.xlog.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Current;
import Ice.Endpoint;
import xlog.slice.LogData;
import xlog.slice._PublisherServiceDisp;

public class PublishService extends _PublisherServiceDisp {

  private static final Logger LOG = LoggerFactory.getLogger(PublishService.class);
  private static final long serialVersionUID = 6920617857631824253L;
  private int port;
  XLogLogger logger;

  public PublishService(int port, Class<? extends XLogLogger> clazz) {
    this.port = port;
    if (null == clazz) {
      clazz = ConsoleXLogLogger.class;
      LOG.info("the XLogLogger implementation is not given, using the default internel implementation: "
          + clazz.getName());
    }
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
    prop.setProperty("Ice.MessageSizeMax", "204800000");
    prop.setProperty("Ice.Warn.Connections", "1");
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);
  }

  public void serve() {
    Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("XlogLogger", "default -p " + port);
    adapter.add(this, adapter.getCommunicator().stringToIdentity("P"));
    // TODO add the host, and port
    adapter.activate();
    String msg = "the XLog Publish/Subscribe Service program activated! the listening port is: " + port;
    System.out.println(msg);
    LOG.info(msg);
  }
}
