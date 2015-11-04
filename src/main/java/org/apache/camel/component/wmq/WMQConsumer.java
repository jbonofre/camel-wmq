package org.apache.camel.component.wmq;

import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQHeader;
import com.ibm.mq.headers.MQHeaderIterator;
import com.ibm.mq.headers.MQHeaderList;
import com.ibm.mq.headers.MQRFH2;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.SuspendableService;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class WMQConsumer extends ScheduledPollConsumer implements SuspendableService {

    private final static Logger LOGGER = LoggerFactory.getLogger(WMQConsumer.class);

    public WMQConsumer(WMQEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = getEndpoint().createExchange();

        Message in = exchange.getIn();

        WMQComponent component = (WMQComponent) getEndpoint().getComponent();
        MQQueueManager queueManager = component.getQueueManager();

        MQQueue queue = null;
        try {
            LOGGER.debug("Consuming from queue {}", getEndpoint().getDestinationName());
            queue = queueManager.accessQueue(getEndpoint().getDestinationName(), MQConstants.MQOO_INPUT_AS_Q_DEF, null, null, null);
            MQMessage message = new MQMessage();
            MQGetMessageOptions options = new MQGetMessageOptions();
            options.options = MQConstants.MQGMO_WAIT + MQConstants.MQGMO_PROPERTIES_COMPATIBILITY + MQConstants.MQGMO_ALL_SEGMENTS_AVAILABLE + MQConstants.MQGMO_COMPLETE_MSG + MQConstants.MQGMO_ALL_MSGS_AVAILABLE;
            options.waitInterval = MQConstants.MQWI_UNLIMITED;
            LOGGER.info("Waiting for message ...");
            queue.get(message, options);

            LOGGER.info("Message consumed");

            MQHeaderList headerList = new MQHeaderList(message);
            // TODO MQRFH, MQCIH, MQDLH, MQIIH, MQRMH, MQSAPH, MQWIH, MQXQH, MQDH, MQEPH headers support
            int index = headerList.indexOf("MQRFH2");
            if (index >= 0) {
                LOGGER.info("MQRFH2 header detected (index " + index + ")");
                MQRFH2 rfh = (MQRFH2) headerList.get(index);
                LOGGER.info("\tformat: " + rfh.getFormat());
                in.setHeader("mq.rfh2.format", rfh.getFormat());
                LOGGER.info("\tstruct id: " + rfh.getStrucId());
                in.setHeader("mq.rfh2.struct.id", rfh.getStrucId());
                LOGGER.info("\tencoding: " + rfh.getEncoding());
                in.setHeader("mq.rfh2.encoding", rfh.getEncoding());
                LOGGER.info("\tcoded charset id: " + rfh.getCodedCharSetId());
                in.setHeader("mq.rfh2.coded.charset.id", rfh.getCodedCharSetId());
                LOGGER.info("\tflags: " + rfh.getFlags());
                in.setHeader("mq.rfh2.flags", rfh.getFlags());
                LOGGER.info("\tversion: " + rfh.getVersion());
                in.setHeader("mq.rfh2.version", rfh.getVersion());
                MQRFH2.Element[] folders = rfh.getFolders();
                for (MQRFH2.Element folder : folders) {
                    LOGGER.info("folder " + folder.getName() + ": " + folder.toXML());
                    in.setHeader("mq.rfh2.folder." + folder.getName(), folder.toXML());
                }
            }

            LOGGER.info("Reading body");
            byte[] buffer = new byte[message.getDataLength()];
            message.readFully(buffer);
            String body = new String(buffer, "UTF-8");

            in.setBody(body, String.class);
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            if (queue != null)
                queue.close();
        }

        if (exchange.getException() != null) {
            getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
        }

        return 1;
    }

    @Override
    public WMQEndpoint getEndpoint() {
        return (WMQEndpoint) super.getEndpoint();
    }

}
