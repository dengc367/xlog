package com.renren.dp.xlog.pubsub.zookeeper.server;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerWatcher implements Watcher {

	private static Logger logger = LoggerFactory.getLogger(ServerWatcher.class);
	
	@Override
	public void process(WatchedEvent event) {
		logger.debug("WatchedEvent received : " + event);
	}

}
