package com.renren.dp.xlog.logger;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.renren.dp.xlog.pubsub.pull.PullXLogLoggerServer;

public class TestXLogLogger {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: $0 api.rest.invoke test.3g.access");
      System.exit(1);
    }
    System.out.println("The args: " + Joiner.on(' ').join(args));
    String[] categories;
    for (int i = 0; i < args.length; i++) {
      categories = Lists.newArrayList(Splitter.on(CharMatcher.anyOf("./")).split(args[i])).toArray(new String[0]);
      FileXLogLogger logger = new FileXLogLogger(categories);
      XLogLoggerServer xs = new PullXLogLoggerServer(categories, logger);
      xs.start();
    }

  }
}
