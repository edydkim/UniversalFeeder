package org.ufm.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.ufm.drools.XMLBasedVO;


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
            // Case 1, XMLBasedVO
            log.trace("Source read: " + textMessage.getText());
            VO<XMLBasedVO> vo = new XMLBasedVO(textMessage.getText());
            log.debug("XMLBasedVO: " + vo.getRef());
            
            // TODO: do something more...
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
