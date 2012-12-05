package com.renren.dp.xlog.log4j;

import static org.junit.Assert.*;

import java.util.Arrays;

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
    DOMConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.xml"));

    Logger l = Logger.getLogger("test.3g.access");

    // XLogAppender app = new XLogAppender();
    //
    // l.addAppender(app);

    // l.fatal("first fatal");
    // l = Logger.getLogger("test.3g.access.test 2");
    // l.error("second error");
    // l.debug("third debug");
    long i = 0;
    // while (true) {
    // i++;
    for (; i < 10; i++) {
      l = Logger.getLogger("test.3g.access");
      l.info("youku", new Exception("exp"));
      l.info("third23 infothird23 infeothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 info"
          + i);
      l = Logger.getLogger("test.3g.action");
      l.info("action action action action action action action action action action action action actioninfo" + i);
      // l.info("third23 " + i);
      // l.error("second error" + i);
      // try {
      // Thread.sleep(100);
      // } catch (InterruptedException e) {
      // e.printStackTrace();
      // }
      // System.out
      // .println("third23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 info"
      // + i);
      // System.out.println("second error" + i);
      // String[] a = "test.3g access sadf dsaf sdgaa.a".split("(\\.| )");
      // System.out.println(Arrays.toString(a) + ", " + a.length);
    }

    // try {
    // Thread.sleep(1000 * 100);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
  }
}
