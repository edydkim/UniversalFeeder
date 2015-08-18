package org.ufm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@EnableBatchProcessing
public class JobRunner {
    private static final Logger log = LoggerFactory.getLogger(JobRunner.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importNewJob")
    Job importNewJob;

    @Scheduled(fixedRate = 1500)
    public void findAndRunJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(importAxialTxnJob, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    }
}
