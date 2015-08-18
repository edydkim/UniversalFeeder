package org.ufm.common;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.jms.JMSException;
import javax.jms.Session;

public class MQConnectionSingleton {
    private static final Logger log = LoggerFactory.getLogger(MQConnectionSingleton.class);

    // MQQueueConnection
    private static MQQueueConnection mqConnection = null;
    private String hostname;
    private int port;
    private String QueueManager;
    private String channel;

    private MQConnectionSingleton() {
    }

    private static class MQConnectionSingletonHolder {
        private static final MQConnectionSingleton INSTANCE = new MQConnectionSingleton();
    }

    public static MQConnectionSingleton getInstance() {
        return MQConnectionSingletonHolder.INSTANCE;
    }

    private void initialize() throws JMSException {
        Assert.noNullElements(new Object[]{hostname, port, QueueManager, channel}, "MQ Server Info is required..");

        MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

        cf.setHostName(hostname);
        cf.setPort(port);
        cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        cf.setQueueManager(QueueManager);
        cf.setChannel(channel);
        // FIXME: cf.setClientID("???");

        // FIXME: mqConnection = MQConnectionFactoryCreator.getQueueConnection();

        mqConnection = (MQQueueConnection) cf.createQueueConnection();
        mqConnection.start();
    }

    public MQQueueConnection getMqConnection() throws JMSException {
        if (mqConnection == null)   initialize();

        return mqConnection;
    }

    public MQQueueSession getMqSession() throws JMSException {
        return (MQQueueSession) getMqConnection().createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
    }

    public void closeMQQueueConnection() throws JMSException {
        if (mqConnection != null)   mqConnection.close();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getQueueManager() {
        return QueueManager;
    }

    public void setQueueManager(String queueManager) {
        QueueManager = queueManager;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
