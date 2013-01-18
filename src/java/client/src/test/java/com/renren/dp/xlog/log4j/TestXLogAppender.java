package com.renren.dp.xlog.log4j;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

public class TestXLogAppender {

  @Test
  public void test() {
    // PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
    // DOMConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.xml"));
    Logger l = Logger.getLogger("test.3g.access");

    // XLogAppender app = new XLogAppender();
    //
    // l.addAppender(app);

    l.fatal("first fatal");
    // l = Logger.getLogger("test.3g.access.test 2");
    l.error("second error");
    l.debug("third debug");
    long i = 0;
    // while (true) {
    for (; i < 100; i++) {
      l.info("third23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 info"
          + i++);
      l.error("second error");
      // try {
      // Thread.sleep(100);
      // } catch (InterruptedException e) {
      // e.printStackTrace();
      // }
      // System.out
      // .println("third23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 info"
      // + i);

      System.out.println("second error");
    }
    // l.trace("fourth shouldn't be printed");
    //
    // for (LoggingEvent le : app.eventsList) {
    // System.out.println("***" + le.getMessage());
    // }

    try {
      Thread.sleep(1000 * 100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
