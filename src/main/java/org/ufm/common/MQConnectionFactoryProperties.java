package org.ufm.common;

import com.ibm.mq.jms.JMSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;


@Component
@ConfigurationProperties(locations = "classpath:application.properties", ignoreUnknownFields = true, prefix = "mq")
public class MQConnectionFactoryProperties {
    private static final Logger log = LoggerFactory.getLogger(MQConnectionFactoryProperties.class);

    @NotNull
    private String hostname;
    
    @NotNull
    private int port = 1000;

    private boolean inMemory = true;

    private boolean pooled = true;

    private int transactionType = JMSC.MQJMS_TP_CLIENT_MQ_TCPIP;

    private String queueManager = "DEFAULT";

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public boolean isPooled() {
        return pooled;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public String getQueueManager() {
        return queueManager;
    }
}
