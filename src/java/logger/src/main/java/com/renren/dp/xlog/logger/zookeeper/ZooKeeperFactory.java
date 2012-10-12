package com.renren.dp.xlog.logger.zookeeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一封装获取ZooKeeper实例的逻辑
 * 
 * @author Zhancheng Deng (zhan8610189@yahoo.com.cn) 
 * @since 2011-2-17 上午11:36:38
 */
public class ZooKeeperFactory {
	
	static String HOSTS = null;
	
	static String ROOT_PATH = null;
	
	static {
		loadConnectString();
	}
	
	static final String PROPERTIES_FILE_NAME = "/server.properties";
	
	private static final int DEFUALT_ZOOKEEPER_SESSION_TIMEOUT = 30000;

	private static Logger logger = LoggerFactory.getLogger(ZooKeeperFactory.class);
	
	public static ZooKeeper newZooKeeper(Watcher watcher) {
		return newZooKeeper(getConnectString(), watcher);
	}
	
	public static ZooKeeper newZooKeeper(String connectString, Watcher watcher) {
		try {
			ZooKeeper zookeeper = new ZooKeeper(connectString,
					DEFUALT_ZOOKEEPER_SESSION_TIMEOUT, watcher);
			return zookeeper;
		} catch (IOException e) {
			throw new RuntimeException("Error occurs while creating ZooKeeper instance.", e);
		}
	}
	
	static void loadConnectString() {
		InputStream is = ZooKeeperFactory.class.getResourceAsStream(PROPERTIES_FILE_NAME);
		try {
			
			Properties prop = new Properties();
			prop.load(is);
			
			String hosts = (String)prop.get("hosts");
			if (hosts == null) {
				throw new RuntimeException("Need conf for zookeeper hosts.");
			}
			String rootPath = (String)prop.get("rootPath");
			if (rootPath == null) {
				throw new RuntimeException("Need conf for zookeeper rootPath.");
			}
			if (HOSTS != null) {
				logger.warn("Somewhere overrides the host config to: " + HOSTS);
			} else {
				HOSTS = hosts;
			}
			ROOT_PATH = rootPath;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static String getConnectString() {
		return HOSTS + ROOT_PATH;
	}
	
	static String getRootPath(){
		return ROOT_PATH;
	}
	
	public static void main(String[] args) {
		loadConnectString();
		System.out.println(getConnectString());
		System.out.println(getRootPath());
	}
	
}