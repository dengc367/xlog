package com.renren.dp.xlog.pubsub.zookeeper.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperHelper;
import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperRegister;



public class ServerZooKeeperHelper extends ZooKeeperHelper {
	
	private ServerZooKeeperHelper() {
		super(new ServerWatcher());
	}
	
	public static ServerZooKeeperHelper getInstance(){
		if ( _helper == null ) {
			_helper = new ServerZooKeeperHelper();
		}
		return (ServerZooKeeperHelper) _helper;
	}
	
	
	public void createEphemeralServer(String connectionString){
		if ( StringUtils.isBlank(connectionString) ){
			throw new NullPointerException("the connection string is null");
		}
		String path = ZOOKEEPER_ROOT_PATH + "/" + connectionString;//"123.125.45.86:80";
		try {
			
			if ( zk.exists( path, false ) == null ){
				zk.create( path, "enabled".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
				logger.info( path + " is not exist, create it");
			} else {
				throw new KeeperException.NodeExistsException(path);
			}
		} catch (KeeperException e) {
			String msg = "[ ZooKeeper Exception ] : the zookeeper node path: " + e.getPath() + ", the exception message: " + e.getMessage();
			logger.error( msg );
			throw new IllegalArgumentException(msg, e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setRegister(ZooKeeperRegister register) {
		
	}
	
	

}
