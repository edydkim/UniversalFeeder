package org.ufm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            // TODO: Refactoring below
            /* for HSQL
            List<Stream> results = jdbcTemplate.query("SELECT col1, col2 FROM table", new RowMapper<Stream>() {
                @Override
                public Person mapRow(ResultSet rs, int row) throws SQLException {
                    Stream stream = new Stream();
                    stream.setTextMessage(rs.getString(1));
                    return stream;
                }
            });

            for (Stream Stream : results) {
                log.debug("Found <" + stream + "> in the database.");
            }
            */
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("beforeJob is started..");
    }
}
