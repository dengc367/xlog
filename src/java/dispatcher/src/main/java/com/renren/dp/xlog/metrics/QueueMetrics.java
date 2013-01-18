package com.renren.dp.xlog.metrics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.storage.EventListener;
import com.renren.dp.xlog.storage.QueueCounter;
import com.renren.monitor.alarm.AlarmMessage;
import com.renren.monitor.metrics.MetricsContext;
import com.renren.monitor.metrics.MetricsRecord;
import com.renren.monitor.metrics.MetricsRecord.State;
import com.renren.monitor.util.ParamsManager;

public class QueueMetrics implements MetricsContext{

  private final double QUEUE_USED_RATION_WARNING_THRESHOLD;
  private final long REQUEST_FAILURE_MAX_TIMEOUT;
  
  private MetricsRecord metrics=null;
  private EventListener listener=null;
  private DecimalFormat df;
  
  public QueueMetrics(String metricName,EventListener listener){
    this.listener=listener;
    this.metrics=new MetricsRecord(metricName,ParamsManager.getInt("warning.count.threshold", 3));
    this.QUEUE_USED_RATION_WARNING_THRESHOLD=Double.parseDouble(Configuration.getString("queue.used.ration.warning.threshold", "0.80"));
    this.REQUEST_FAILURE_MAX_TIMEOUT=Configuration.getLong("request.failure.max.timeout",300)*1000;
    this.df = new DecimalFormat("#.0");
  }
  @Override
  public void doUpdates() {
    QueueCounter qc=listener.getCount();
    double queueUsedRation=qc.getCurrentQueueSize()/(double)qc.getQueueCapacity();
    long deltaRequestFailureTime=listener.getDeltaRequestFailureTime();
    if(queueUsedRation>QUEUE_USED_RATION_WARNING_THRESHOLD || deltaRequestFailureTime>=REQUEST_FAILURE_MAX_TIMEOUT){
      metrics.setMetricState(State.ERROR);
    }else{
      metrics.setMetricState(State.OK);
    }
    metrics.addMetric("queueUsedRatio", df.format(queueUsedRation*100)+"%");
    metrics.addMetric("requestFailureTime",deltaRequestFailureTime/1000+"s");
  }

  @Override
  public List<AlarmMessage> getAlarmMessage() {
    List<AlarmMessage> list=new ArrayList<AlarmMessage>();
    list.add(new AlarmMessage(metrics.getMetricName(), metrics.toString()));
    return list;
  }

  @Override
  public boolean isMetricsValueException() {
    return !metrics.isMetricNormal();
  }

}
