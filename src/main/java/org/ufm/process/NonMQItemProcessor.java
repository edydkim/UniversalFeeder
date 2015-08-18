package com.ufm.process;

import com.nomura.tcs.lpm.datum.dto.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;


public class NonMQItemProcessor implements ItemProcessor<Stream, Stream> {
        private static final Logger log = LoggerFactory.getLogger(NonMQItemProcessor.class);

        @Override
        public Stream process(final Stream stream) throws Exception {
                // TODO: implement below..
                Stream transformedStream = stream;
                log.info("Converting (" + stream + ") into (" + transformedStream + ")");

                return transformedPerson;
        }

}
