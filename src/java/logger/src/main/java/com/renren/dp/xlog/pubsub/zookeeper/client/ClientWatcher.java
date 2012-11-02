package com.renren.dp.xlog.pubsub.zookeeper.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperRegister;



public class ClientWatcher implements Watcher {

	private static Logger logger = LoggerFactory.getLogger(ClientWatcher.class);
	
	private ZooKeeperRegister register;
	
	public void setRegister(ZooKeeperRegister register){
		this.register = register;
	}
	
	@Override
	public void process(WatchedEvent event) {
		
	    if (logger.isDebugEnabled()) {
            logger.debug("WatchedEvent received: " + event);
        }
	    
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch ( event.getState() ) {
            case SyncConnected:
                try {
                	register.callback();   //刷新数据, 防止重连时可能造成的信息丢失
                } catch (Exception e) {
                    if (e instanceof ConnectionLossException
                            || (e.getCause() != null && e.getCause() instanceof ConnectionLossException)) {
                        //如果是ConnectionLossException引起的，重连之
                    	ClientZooKeeperHelper.getInstance().init();
                    } else {
                        logger.error("Error while refreshing data from ZooKeeper on event: " + event, e);
                    }
                }
                
                break;
            case Expired:
            	logger.warn("Zookeeper session expired: " + event);
            	ClientZooKeeperHelper.getInstance().init();
                break;
            }
        } else if (
        		event.getType() == Event.EventType.NodeChildrenChanged 
        		|| event.getType() == Event.EventType.NodeDataChanged 
        		|| event.getType() == Event.EventType.NodeDeleted 
        		) {
        	register.callback(); 
        } else {
        	logger.warn("Unhandled event:" + event);
        }
	}

}
