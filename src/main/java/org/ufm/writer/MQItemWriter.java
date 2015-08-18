package org.ufm.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;


public class MQItemWriter<E> implements ItemWriter, InitializingBean {

    @Override
    public void write(List list) throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
