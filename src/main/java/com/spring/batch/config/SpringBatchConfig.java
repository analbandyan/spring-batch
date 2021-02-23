package com.spring.batch.config;

import com.spring.batch.batch.UserItemReader;
import com.spring.batch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

    public static final String JOB_NAME = "Users-Load";

    private final JobRepository jobRepository;

    public SpringBatchConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public JobLauncher asyncJobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(threadPoolTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(3);
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(10);
        return executor;
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, UserItemReader itemReader,
                     ItemProcessor<User, User> itemProcessor, ItemWriter<User> itemWriter) {
        return stepBuilderFactory.get("file-load-step")
                .<User, User>chunk(1/*consider setting to more*/)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .stream(itemReader)
                .build();
    }

}
