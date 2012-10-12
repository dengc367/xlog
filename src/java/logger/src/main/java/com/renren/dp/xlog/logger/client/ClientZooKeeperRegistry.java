package com.renren.dp.xlog.logger.client;

import com.renren.dp.xlog.logger.zookeeper.ZooKeeperRegister;



public class ClientZooKeeperRegistry implements ZooKeeperRegister {

	@Override
	public void callback() {
		ClientZooKeeperAdapter.loadAddresses();
	}

}
