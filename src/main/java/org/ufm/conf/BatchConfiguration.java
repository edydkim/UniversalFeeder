package org.ufm.conf;

import org.ufm.datum.dao.UpStream;
import org.ufm.listener.JobCompletionNotificationListener;
import org.ufm.process.MQItemProcessor;
import org.ufm.reader.MQItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

@Configuration
@ComponentScan (
        basePackages = {"org.ufm"}, excludeFilters = @ComponentScan.Filter(value = Service.class, type = FilterType.ANNOTATION)
)
@Import({JobCompletionNotificationListener.class})
@PropertySource({"classpath:application.properties", "classpath:datasource.properties"})
public class BatchConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${mq.hostname}")
    private String MQ_HOSTNAME;

    @Value("${mq.port}")
    private int MQ_PORT;

    @Value("${mq.qm}")
    private String MQ_QUEUE_MANAGER;

    @Value("${mq.channel}")
    private String MQ_CHANNEL;

    @Value("${qn.sample}")
    private String QN_SAMPLE;

    @Value("${batch.max.pool.size:10}")
    private int batchMaxPoolSize;

    @Value("${batch.pool.capacity:2}")
    private int batchPoolCapacity;

    @Value("classpath:some-query-here.sql")
    private Resource schemeScript;

    @Value("${datasource.url}")
    private String DATASOURCE_URL;
    @Value("${datasource.user}")
    private String DATASOURCE_USER;
    @Value("${datasource.password}")
    private String DATASOURCE_PASSWORD;

    private Queue<UpStream> upStreams = new ConcurrentLinkedQueue<>();

    // tag::readerwriterprocessor[]
    public ItemReader<UpStream> reader() {
        log.info("reader is started..");

        MQItemReader<UpStream> mqItemReader = new MQItemReader<>(upStreams);

        mqItemReader.setHostname(MQ_HOSTNAME);
        mqItemReader.setPort(MQ_PORT);
        mqItemReader.setQueueManager(MQ_QUEUE_MANAGER);
        mqItemReader.setChannel(MQ_CHANNEL);
        mqItemReader.setQueueName(QN_SAMPLE);
        mqItemReader.setBatchMaxPoolSize(batchMaxPoolSize);
        mqItemReader.doOpen();

      // NOTE: Non-MQ Items Only. ex, read from a file or FTP
       /*
       FlatFileItemReader<UpStream> reader = new FlatFileItemReader<UpStream>();
        reader.setResource(new ClassPathResource("sample.csv"));
        reader.setLineMapper(new DefaultLineMapper<UpStream>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"firstName", "lastName"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<UpStream>() {{
                setTargetType(UpStream.class);
            }});
        }});
        */

        log.info("reader is done..");

        return mqItemReader;
    }

    public ItemProcessor<UpStream, UpStream> processor() throws Exception {
        // NOTE: Non-MQ Items Only. ex, read from a file or FTP
        /* return new NonMQItemProcessor(); */

        ItemProcessor<UpStream, UpStream> itemProcessor = new MQItemProcessor();

        log.info("Processor is converting tag..");

        // READY: <- itemProcessor.process(upStreams.element());

        return itemProcessor;
    }

    public ItemWriter<UpStream> writer(@Qualifier("hsqlDataSource") DataSource dataSource) {
        JdbcBatchItemWriter<UpStream> writer = new JdbcBatchItemWriter<UpStream>();

        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<UpStream>());
        writer.setSql("SELECT curdate() FROM (VALUES (0));");
        // <- for placeholder: writer.setSql("INSERT INTO table (col1, col2) VALUES (:col1, :col2)");
        writer.setDataSource(dataSource);

        log.info("writer is done..");
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::executor[]

    @Bean
    public TaskExecutor taskExecutor() {
        TaskExecutor saTaskExecutor = new SimpleAsyncTaskExecutor();

        // NOTE: use below when each step make running Async with Listener (Event Handler)
        // <- AsyncListenableTaskExecutor

        // NOTE: ThreadPoolTaskExecutor with Sync
        /* <-
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(batchMaxPoolSize * batchPoolCapacity);
        taskExecutor.setCorePoolSize(batchMaxPoolSize);
        taskExecutor.setQueueCapacity(batchMaxPoolSize * batchPoolCapacity);
        taskExecutor.afterPropertiesSet();
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setDaemon(true);
        */

        return saTaskExecutor;
    }

    @Bean
    public TaskExecutor simpleAsyncTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
    // end::executor[]

    @Bean
    protected Tasklet tasklet() {
        return (contribution, context) -> {
            reader();
            processor();
            writer(hsqlDataSource());
            return RepeatStatus.FINISHED;
        };
    }

    /* NOTE: <-
    @Bean
    public Job anotherJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
        return jobs.get("anotherJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }
    */
    // tag::jobstep[]

    @Bean
    public Job importNewJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
        return jobs.get("importNewJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                /* <- .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.debug("Where am I before");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.debug("Where am I after");
                    }
                })*/
                .validator(new JobParametersValidator() {
                    @Override
                    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
                    }
                })
                // NOTE: <- .start(s1)
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, Tasklet tasklet, TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet)
                /* NOTE: in case using @Bean
                .<UpStream, UpStream>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                */
                .taskExecutor(taskExecutor)
                .build();
    }
    // end::jobstep[]

    @Bean
    @Primary
    public DataSource hsqldbDataSource() throws SQLException {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.hsqldb.jdbcDriver());
        dataSource.setUrl("jdbc:hsqldb:file:/Documents/Test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /* NOTE: MySQL
    @Bean
    public DataSource mysqlDataSource() throws SQLException {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.jdbc.Driver());
        dataSource.setUrl("jdbc:mysql://localhost/spring_batch_example");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        DatabasePopulatorUtils.execute(databasePopulator(), dataSource);
        return dataSource;
    }
    */

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemeScript);
        return populator;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("hsqldbDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
