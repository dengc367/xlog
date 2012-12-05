package com.renren.dp.xlog.log4j;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogbackXLogAppender {

  @Test
  public void test() {

    Logger l = LoggerFactory.getLogger("test.3g.access");

    long i = 0;
    // while (true) {
    // i++;
    for (; i < 10; i++) {
      l = LoggerFactory.getLogger("test.3g.access");
      l.info("exception, ", new Exception("throwable"));
      l.info("third23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 infothird23 info"
          + i);
      l = LoggerFactory.getLogger("test.3g.action");
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
