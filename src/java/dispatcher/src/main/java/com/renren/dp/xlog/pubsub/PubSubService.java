package com.renren.dp.xlog.pubsub;

import java.io.IOException;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.pubsub.pull.PullService;
import com.renren.dp.xlog.pubsub.push.Pusher;

import static com.renren.dp.xlog.pubsub.PubSubConstants.*;

import Ice.Current;
import xlog.slice.PushSubscription;
import xlog.slice.Subscription;
import xlog.slice.XLogException;

public class PubSubService {

  private PullService pullService;
  private Pusher pusher;

  public PubSubService() {
    pullService = new PullService();
    pusher = new Pusher();
  }

  public int subscribe(Subscription sub, Current __current)
      throws XLogException {
    String endpoint = sub.options.get(PUSH_SERVICE_ENDPOINT);
    if (endpoint != null) {
      return pusher
          .subscribe(new PushSubscription(sub.categories, sub.options, endpoint
              .replace("<HOST>", PubSubUtils.getRemoteClientIp(__current))));
    } else {
      return pullService.subscribe(sub);
    }
  }

  public int unsubscribe(Subscription sub, Current __current) {
    String endpoint = sub.options.get(PUSH_SERVICE_ENDPOINT);
    if (endpoint != null) {
      return pusher.unsubscribe(new PushSubscription(sub.categories,
          sub.options, endpoint.replace("<HOST>",
              PubSubUtils.getRemoteClientIp(__current))));
    } else {
      return pullService.unsubscribe(sub);
    }
  }

  public String[] getData(int categoryId, Current __current)
      throws IOException, XLogException {
    return pullService.getData(categoryId);
  }

  public boolean isSubscribed(String[] categories) {
    return pusher.isSubscribed(categories);
  }

  public void publish(LogMeta logMeta) {
    pusher.publish(logMeta);
  }
}
