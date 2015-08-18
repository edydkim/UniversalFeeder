package org.ufm.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(MQConnectionFactoryProperties.class)
public class MQConnectionFactoryCreator {
    private static final Logger log = LoggerFactory.getLogger(MQConnectionFactoryCreator.class);

    @Autowired
    @Qualifier("MQConnectionFactoryProperties")
    private MQConnectionFactoryProperties mqConnectionFactoryProperties;

    public MQConnectionFactoryCreator() {
    }
}
