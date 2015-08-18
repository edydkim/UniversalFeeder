package org.ufm.reader;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSession;
import org.ufm.common.MQConnectionSingleton;
import org.ufm.listener.ReceiverMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Component
public class MQItemReader<E> implements ItemReader, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(MQItemReader.class);

    private final int NUMBER_OF_LATCH = 1;
    private final int AWAIT_TIME_OUT = 10;

    private Queue<E> items;
    private String hostname;
    private int port;
    private String channel;
    private String queueManager;
    private String queueName;
    private int batchMaxPoolSize;
    private MQQueueReceiver receiver;
    private CountDownLatch latch = new CountDownLatch(NUMBER_OF_LATCH);

    public MQItemReader() {
    }

    public MQItemReader(Queue<E> items) {
        this.items = items;
    }

    public void doOpen() {
        try {
            /* NOTE: Blocking Approach
            MQQueueConnection mqQueueConnection;
            mqQueueConnection = MQConnectionSingleton.getInstance().getMqConnection();
            MQQueueSession session = (MQQueueSession) mqQueueConnection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
            MQQueue queue = (MQQueue) session.createQueue(queueName);

            receiver = (MQQueueReceiver) session.createReceiver(queue);
            */

            // Non-Blocking Approach
            MQConnectionSingleton mqConnectionSingleton = MQConnectionSingleton.getInstance();
            mqConnectionSingleton.setHostname(hostname);
            mqConnectionSingleton.setPort(port);
            mqConnectionSingleton.setChannel(channel);
            mqConnectionSingleton.setQueueManager(queueManager);

            MQQueueSession session =mqConnectionSingleton.getMqSession();
            MQQueue queue = (MQQueue) session.createQueue(queueName);
            receiver = (MQQueueReceiver) session.createReceiver(queue);

            // FIXME: messageListener - take all messages from MQ in asynchronous
            ReceiverMessageListener receiverMessageListener = new ReceiverMessageListener(latch, batchMaxPoolSize);
            receiver.setMessageListener(receiverMessageListener);
            
            // Deprecated Here using <- mqQueueConnection.start();

            latch.await(AWAIT_TIME_OUT, TimeUnit.SECONDS);

            /* TODO: reading a bunch of mq in reader step
            for (int i = 0; i < batchMaxPoolSize; i++) {
                TextMessage msg = (TextMessage) receiver.receive();

                log.debug("received a message: " + msg);

                Stream stream = new Stream();
                stream.setTextMessage(msg);

                UpStream<Stream> upStream = new UpStream<>();
                upStream.setStream(stream);

                items.add((E) upStream);
            }
            */
        } catch (JMSException e) {
            log.error("MQConnection initializing is failed..", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                receiver.close();
            } catch (JMSException e) {
                log.error("MQQueueReceiver Closing is failed..", e);
            }
        }

        log.info("MQConnection init() is done..");
    }

    @Override
    public E read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (items.isEmpty()) return null;

        return items.poll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public void addItem(E item) {
        items.add(item);
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getBatchMaxPoolSize() {
        return batchMaxPoolSize;
    }

    public void setBatchMaxPoolSize(int batchMaxPoolSize) {
        this.batchMaxPoolSize = batchMaxPoolSize;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }
}
