package com.renren.dp.xlog.pubsub;

import java.io.IOException;

import com.renren.dp.xlog.pubsub.pull.PullService;

import Ice.Current;
import xlog.slice.Subscription;
import xlog.slice.XLogException;

public class PubSubService {

  private PullService pullService;

  public PubSubService() throws IOException {
    pullService = new PullService();
  }

  public int subscribe(Subscription sub, Current __current) throws XLogException {
    return pullService.subscribe(sub);
  }

  public int unsubscribe(Subscription sub, Current __current) {
    return pullService.unsubscribe(sub);
  }

  public boolean isSubscribed(String[] categories) {
    return pullService.isSubscribed(categories);
  }

  public byte[] getBytes(int categoryId, Current __current) throws XLogException, IOException {
    return pullService.getBytes(categoryId);
  }
}
