package com.renren.dp.xlog.metrics;

import java.io.IOException;

import com.renren.dp.xlog.config.Configuration;
import com.renren.monitor.AlarmManager;
import com.renren.monitor.MetricsManager;
import com.renren.monitor.Monitor;
import com.renren.monitor.alarm.impl.EmailAlarm;
import com.renren.monitor.alarm.impl.MobilePhoneAlarm;
import com.renren.monitor.exception.HandlerConstructException;
import com.renren.monitor.exception.MailException;
import com.renren.monitor.metrics.impl.DiskMetrics;
import com.renren.monitor.metrics.impl.JVMMetrics;

public class MetricsInitialization {

  private Monitor monitor = null;
  private MetricsManager metricsManager = null;
  private AlarmManager alarmManager = null;
  private String[] mobilephoneNumber;
  private String[] userNames;

  public MetricsInitialization() {
    // 初始化监控模块
    monitor = Monitor.getMonitor();
    metricsManager = new MetricsManager();
    metricsManager.addMetricsContext(new JVMMetrics());
    metricsManager.addMetricsContext(new DiskMetrics(
        new String[] { Configuration.getString("oplog.store.path") }));

    alarmManager = new AlarmManager();
    mobilephoneNumber = Configuration
        .getString("mobilepone.warning.number", "").split(",");
    String[] arr = Configuration.getString("system.administrator", "").split(
        ",");
    if (arr != null) {
      int len = arr.length;
      userNames = new String[len];
      for (int i = 0; i < len; i++) {
        userNames[i] = arr[i].split(":")[0];
      }
    }
  }

  public MetricsManager getMetricsManager() {
    return this.metricsManager;
  }

  public void initialize() throws IOException {
    if (!"".equals(mobilephoneNumber)) {
      try {
        alarmManager.addAlarm(new MobilePhoneAlarm(mobilephoneNumber));
      } catch (HandlerConstructException e1) {
        throw new IOException("Fail to initialize mobile phone alarm!", e1);
      }
    }
    if (userNames != null) {
      try {
        alarmManager.addAlarm(new EmailAlarm(userNames));
      } catch (HandlerConstructException e1) {
        throw new IOException("Fail to initialize email alarm!", e1);
      } catch (MailException e1) {
        throw new IOException("Fail to initialize email alarm!", e1);
      }
    }
    try {
      monitor.initialize(metricsManager, alarmManager);
    } catch (Exception e) {
      throw new IOException("Fail to initialize monitor!", e);
    }
  }
}
