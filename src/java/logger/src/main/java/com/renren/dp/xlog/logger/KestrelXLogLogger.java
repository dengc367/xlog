package com.renren.dp.xlog.logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kestrel.KestrelComponent;
import org.apache.camel.component.kestrel.KestrelConfiguration;
import org.apache.camel.impl.DefaultCamelContext;

import xlog.slice.LogData;

public class KestrelXLogLogger implements XLogLogger {

  // START SNIPPET: e1
  CamelContext context = new DefaultCamelContext();

  @Override
  public void log(String[] logs) {
    // Set up the ActiveMQ JMS Components
    // START SNIPPET: e2
    KestrelConfiguration conf = new KestrelConfiguration();
    conf.setAddresses(new String[] { "10.11.31.60:22133" });
    conf.setWaitTimeMs(100);
    conf.setConcurrentConsumers(1);
    Component c = new KestrelComponent(conf);
    // ConnectionFactory connectionFactory = new
    // ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");

    // Note we can explicit name the component
    context.addComponent("test-kestrel", c);
    // END SNIPPET: e2
    // Add some configuration by hand ...
    // START SNIPPET: e3
    // try {
    // context.addRoutes(new RouteBuilder() {
    // public void configure() {
    // from("kestrel://10.11.31.60:22133/test?concurrentConsumers=10&waitTimeMs=500").to("file://test");
    // }
    // });
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    // END SNIPPET: e3
    // Camel template - a handy class for kicking off exchanges
    // START SNIPPET: e4
    ProducerTemplate template = context.createProducerTemplate();
    // END SNIPPET: e4
    // Now everything is set up - lets start the context
    try {
      context.start();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Now send some test text to a component - for this case a JMS Queue
    // The text get converted to JMS messages - and sent to the Queue
    // test.queue
    // The file component is listening for messages from the Queue
    // test.queue, consumes
    // them and stores them to disk. The content of each file will be the
    // test we sent here.
    // The listener on the file component gets notified when new files are
    // found ... that's it!
    // START SNIPPET: e5
    for (int i = 0; i < 10; i++) {
      template.sendBody("kestrel://10.11.31.60:22133/test?concurrentConsumers=10&waitTimeMs=500", "Test Message: " + i);
    }
    // END SNIPPET: e5

    // wait a bit and then stop
    try {
      Thread.sleep(1000);
      context.stop();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    new KestrelXLogLogger().log(new String[] { "aa" });
  }
}
