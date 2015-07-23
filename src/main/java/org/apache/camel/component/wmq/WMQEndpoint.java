package org.apache.camel.component.wmq;

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

    @UriParam
    private String destinationType;

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public WMQEndpoint() {
    }

    public WMQEndpoint(String uri, Component component, String destinationName, String destinationType) {
        super(uri, component);
        this.destinationName = destinationName;
        this.destinationType = "queue";
        if (destinationType != null && destinationType.equalsIgnoreCase("topic")) {
            this.destinationType = "topic";
        }
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
