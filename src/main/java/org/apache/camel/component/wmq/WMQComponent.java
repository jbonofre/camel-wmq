package org.apache.camel.component.wmq;

import com.ibm.mq.MQQueueManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class WMQComponent extends UriEndpointComponent {

    private final static Logger LOGGER = LoggerFactory.getLogger(WMQComponent.class);

    private MQQueueManager queueManager;

    public WMQComponent() {
        super(WMQEndpoint.class);
    }

    public WMQComponent(CamelContext camelContext) {
        super(camelContext, WMQEndpoint.class);
    }

    public MQQueueManager getQueueManager() {
        if (queueManager == null) {
            LOGGER.debug("Connecting to MQQueueManager ...");
            Properties connectionProperties = new Properties();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("mq.properties");
            try {
                LOGGER.debug("Loading mq.properties from the classloader ...");
                connectionProperties.load(inputStream);
            } catch (Exception e) {
                LOGGER.debug("mq.properties not found in the classloader, trying from etc folder");
                try {
                    FileInputStream fileInputStream = new FileInputStream(new File(new File(System.getProperty("karaf.home")), "etc"));
                    connectionProperties.load(fileInputStream);
                } catch (Exception e1) {
                    LOGGER.debug("mq.properties not found from etc folder, falling to default");
                    connectionProperties.put("hostname", "localhost");
                    connectionProperties.put("port", 7777);
                    connectionProperties.put("channel", "QM_TEST.SVRCONN");
                    connectionProperties.put("name", "QM_TEST");
                }
            }
            if (connectionProperties.get("hostname") == null) {
                throw new IllegalArgumentException("hostname property is missing");
            }
            if (connectionProperties.get("port") == null) {
                throw new IllegalArgumentException("port property is missing");
            }
            if (connectionProperties.get("channel") == null) {
                throw new IllegalArgumentException("channel property is missing");
            }
            if (connectionProperties.get("name") == null) {
                throw new IllegalArgumentException("name property is missing");
            }
            try {
                this.queueManager = new MQQueueManager(((String) connectionProperties.get("name")), connectionProperties);
            } catch (Exception e) {
                throw new IllegalStateException("Can't create MQQueueManager", e);
            }
        }
        return queueManager;
    }

    public void setQueueManager(MQQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new WMQEndpoint(uri, this, remaining);
    }

}
