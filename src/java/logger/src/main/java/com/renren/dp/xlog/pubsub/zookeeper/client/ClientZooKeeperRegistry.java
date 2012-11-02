package com.renren.dp.xlog.pubsub.zookeeper.client;

import com.renren.dp.xlog.pubsub.zookeeper.ZooKeeperRegister;



public class ClientZooKeeperRegistry implements ZooKeeperRegister {

	@Override
	public void callback() {
		ClientZooKeeperAdapter.loadAddresses();
	}

}
