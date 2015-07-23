Apache Camel IBM MQ component
-----------------------
This Apache Camel components allows you to deal directly with IBM MQ without using the JMS wrapping.
It natively uses the MQ API to consume and produce messages on the destinations.

The component provides both consumer and producer endpoints.

MQ Connection Configuration
---------------------------

To establish the connection to the MQ broker, the component looks for a mq.properties file containing:

hostname=
port=
channel=
name=

The component tries to load the mq.properties from the classloader. If the mq.properties is not found in
the classloader, it tries to load from KARAF_HOME/etc folder.

URI
---

The endpoint URI is:

wmq:destinationName

By default, the component deals with MQ queue.

You can specify the destination type (queue or topic) using the destinationType option:

wmq:topicName?destinationType=topic

Talend Studio
-------------
The component is also provided as an alldep artifact embedding/shading all dependency.

To use the component in the Talend studio, you can use a cMessagingEndpoint, and use the alldep jar in the
cMessagingEndpoint advanced settings.
