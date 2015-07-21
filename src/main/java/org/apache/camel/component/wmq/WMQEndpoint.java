package org.apache.camel.component.wmq;

import com.ibm.mq.MQQueueManager;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@ManagedResource(description = "Managed WMQ Endpoint")
@UriEndpoint(scheme = "wmq", consumerClass = WMQConsumer.class)
public class WMQEndpoint extends DefaultEndpoint {

    @UriParam
    private String destinationName;

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public WMQEndpoint() {
    }

    public WMQEndpoint(String uri, Component component, String destinationName) {
        super(uri, component);
        this.destinationName = destinationName;
    }

    public Producer createProducer() throws Exception {
        return new WMQProducer(this);
    }

    public WMQConsumer createConsumer(Processor processor) throws Exception {
        return new WMQConsumer(this, processor);
    }

    @ManagedAttribute
    public boolean isSingleton() {
        return true;
    }

}
