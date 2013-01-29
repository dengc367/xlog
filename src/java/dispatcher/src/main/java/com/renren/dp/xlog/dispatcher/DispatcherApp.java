package com.renren.dp.xlog.dispatcher;

import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.config.Configuration;

public class DispatcherApp {

  private Ice.Communicator ic = null;
  private DispatcherI dispatcher = null;
  private int status = 0;
  private Date startDate;
  private Ice.ObjectAdapter adapter=null;

  private static DispatcherApp da = null;
  
  private final static Logger LOG = LoggerFactory
      .getLogger(DispatcherApp.class);

  private DispatcherApp() {
  }

  public static DispatcherApp getInstance() {
    if (da == null) {
      da = new DispatcherApp();
    }
    return da;
  }

  public int getStatus() {
    return status;
  }

  public Date getStartDate() {
    return startDate;
  }
  public void start() {
    if (status == 1) {
      return;
    }
    Runtime.getRuntime().addShutdownHook(new Thread(){
      public void run(){
        LOG.info("Dispacher shutdown ice server....");
        stopDispatcher();
        LOG.info("Dispacher shutdown ice server end!");
      }
    });
    startDate = new Date();
    status = 1;
    initLog4j();
    Ice.Properties prop = Ice.Util.createProperties();
    prop.setProperty("Ice.MessageSizeMax",
        Configuration.getString("ice.message.size.max"));
    Ice.InitializationData initData = new Ice.InitializationData();
    initData.properties = prop;
    ic = Ice.Util.initialize(initData);

    adapter = ic.createObjectAdapterWithEndpoints(
        "XlogDispatcher", "default");
    dispatcher = new DispatcherI();
    dispatcher.initialize(adapter);
    LOG.info("Dispacher start successful!");
    adapter.activate();
  }

  public void stopDispatcher() {
    status = 0;
    adapter.destroy();
    ic.destroy();
    dispatcher.close();
  }

  private void initLog4j() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    PropertyConfigurator.configure(classLoader
        .getResource("conf/log4j.properties"));
  }
}
