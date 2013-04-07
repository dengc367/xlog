package com.renren.dp.xlog.fs;

import java.io.IOException;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 统一封装获取ZooKeeper实例的逻辑
 * 
 * @author Zhancheng Deng (zhan8610189@yahoo.com.cn) 
 * @since 2011-2-17 上午11:36:38
 */
public class ZooKeeperFactory {
	
	private static final int DEFUALT_ZOOKEEPER_SESSION_TIMEOUT = 30000;

	public static ZooKeeper newZooKeeper(String connectString, Watcher watcher) {
		try {
			ZooKeeper zookeeper = new ZooKeeper(connectString,
					DEFUALT_ZOOKEEPER_SESSION_TIMEOUT, watcher);
			return zookeeper;
		} catch (IOException e) {
			throw new RuntimeException("Error occurs while creating ZooKeeper instance.", e);
		}
	}
	
}