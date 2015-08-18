package org.ufm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class ReceiverMessageListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(ReceiverMessageListener.class);

    private TextMessage textMessage;
    private AtomicInteger latchCounter = new AtomicInteger(0);
    private CountDownLatch latch;
    private int batchMaxPoolSize;

    public ReceiverMessageListener() {}

    public ReceiverMessageListener(CountDownLatch latch, int batchMaxPoolSize) {
        this.latch = latch;
        this.batchMaxPoolSize = batchMaxPoolSize;
    }

    @Override
    public void onMessage(Message message) {
        textMessage = (TextMessage) message;
        log.debug("received a message: " + textMessage);
        log.debug("Receiver Counter: " + latchCounter.get());

        try {
            // TODO: do something..
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (latchCounter.get() > batchMaxPoolSize) {
            latch.countDown();
        }

        latchCounter.getAndIncrement();
    }
}
