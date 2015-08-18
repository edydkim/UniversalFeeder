package org.ufm;

import org.ufm.common.MQConnectionSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jms.JMSException;

@SpringBootApplication
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            SpringApplication springApplication = new SpringApplication(new Object[] {JobRunner.class, APIRunner.class});
            springApplication.setShowBanner(true);
            springApplication.run(args);
        } catch (Exception e) {
            try {
                MQConnectionSingleton.getInstance().closeMQQueueConnection();

                log.error("Application is failed..: ", e);
            } catch (JMSException jmse) {
                log.error("MQQueueConnection Closing is failed..: ", jmse);
            }
        }
    }
}
