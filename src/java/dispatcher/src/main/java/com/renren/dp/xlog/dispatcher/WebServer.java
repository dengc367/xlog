package com.renren.dp.xlog.dispatcher;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.Server;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

  private Server server = null;

  private final static Logger LOG = LoggerFactory.getLogger(WebServer.class);

  public WebServer(String host, int port) {
    String applicationHome = System.getProperty("user.dir");
    server = new Server(); // 新建一个jetty服务器
    Connector listener = createChannelConnector(); // 新建Connector组件
    listener.setHost(host);
    listener.setPort(port);

    server.addConnector(listener);
    QueuedThreadPool qtp = new QueuedThreadPool();
    server.setThreadPool(qtp);

    ContextHandlerCollection contexts = new ContextHandlerCollection();
    server.setHandler(contexts);

    WebAppContext webAppContext = new WebAppContext();// 新建Handler组件
    webAppContext.setDisplayName("WebAppsContext");
    webAppContext.setContextPath("/"); // Web应用的上下文根路径
    webAppContext.setWar(applicationHome + "/webapp");// web应用所在的路径或者是war包路径
    server.addHandler(webAppContext);// 插入服务器主件中

  }

  public void start() throws Exception {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        LOG.info("Dispacher shutdown webserver ....");
        stopWebServer();
        LOG.info("Dispacher shutdown webserver end!");
      }
    });
    server.start();
    server.join();
  }

  private void stopWebServer() {
    try {
      server.stop();
    } catch (Exception e) {
      LOG.warn("Fail to close webserver!",e);
    }
  }

  private Connector createChannelConnector() {
    SelectChannelConnector scc = new SelectChannelConnector(); // 新建Connector组件
    scc.setLowResourceMaxIdleTime(10000);
    scc.setAcceptQueueSize(128);
    scc.setResolveNames(false);
    scc.setUseDirectBuffers(false);

    return scc;

  }
}
