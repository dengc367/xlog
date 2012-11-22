package com.renren.dp.xlog.logger.zookeeper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ZooKeeperHelper{
	
	protected static ZooKeeperHelper _helper ;
	
	protected Watcher watcher;
	
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public static String ZOOKEEPER_ROOT_PATH = "/0";  // 位于/open开放平台系统的/cloud模块之下
	protected  ZooKeeper zk ;
	
	public ZooKeeperHelper(Watcher watcher) {
		this.watcher = watcher;
		init();
	}
	
	public void init() {
		if ( zk != null ){
			try {
				zk.close();
			} catch (InterruptedException e) {
				logger.warn("can not interrupted ", e);
			}
		}
		zk = ZooKeeperFactory.newZooKeeper(watcher);
		
	}
	
	public abstract void setRegister(ZooKeeperRegister register);
	

}
