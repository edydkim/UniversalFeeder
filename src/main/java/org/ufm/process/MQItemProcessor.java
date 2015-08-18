package org.ufm;

import org.ufm.datum.dao.UpStream;
import org.ufm.datum.dto.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public class MQItemProcessor implements ItemProcessor<UpStream, UpStream> {
    private static final Logger log = LoggerFactory.getLogger(MQItemProcessor.class);

    @Override
    public UpStream process(final UpStream upStream) throws Exception {
        // TODO: Using JSON DOM Parsing
        log.info("GetMessages: " + ((Stream) upStream.getStream()).getTextMessage());

        log.info("Converting (" + upStream + ") into (" + upStream + ")");

        return upStream;
    }
}
