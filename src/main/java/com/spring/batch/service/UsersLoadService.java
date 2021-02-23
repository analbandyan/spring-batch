package com.spring.batch.service;

import com.spring.batch.batch.UserItemReader;
import com.spring.batch.config.SpringBatchConfig;
import com.spring.batch.dto.UsersToLoadRequestDto;
import com.spring.batch.entity.UsersLoadRequest;
import com.spring.batch.repository.UsersToLoadCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import com.spring.batch.dto.UsersLoadRequestStatusDto;
import com.spring.batch.dto.UsersLoadProcessingStatus;

import static com.spring.batch.batch.UserItemReader.ITEM_STREAM_READER_NAME;
import static com.spring.batch.config.SpringBatchConfig.JOB_NAME;

@Slf4j
@Service
public class UsersLoadService {

    private final JobLauncher jobLauncher;

    private final Job job;

    private final JobRepository jobRepository;

    private final UsersToLoadCommandRepository usersToLoadCommandRepository;

    public UsersLoadService(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher, Job job, JobRepository jobRepository, UsersToLoadCommandRepository usersToLoadCommandRepository) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.jobRepository = jobRepository;
        this.usersToLoadCommandRepository = usersToLoadCommandRepository;
    }

    public UsersLoadRequestStatusDto loadUsers(UsersToLoadRequestDto usersToLoadRequestDto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Long usersLoadRequestId = saveUsersLoadRequest(usersToLoadRequestDto);
        JobParameters jobParameters = constructJobParameters(usersLoadRequestId);

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        log.info("Final job execution: {}", jobExecution);
        log.info("Final job execution status: {}", jobExecution.getStatus());

        return new UsersLoadRequestStatusDto(usersLoadRequestId);

    }

    private Long saveUsersLoadRequest(UsersToLoadRequestDto usersToLoadRequestDto) {
        UsersLoadRequest usersLoadRequest = new UsersLoadRequest();
        usersLoadRequest.setUsernames(new ArrayList<>(usersToLoadRequestDto.getUsernames()));
        return usersToLoadCommandRepository.save(usersLoadRequest).getId();
    }

    public UsersLoadProcessingStatus getUsersLoadStatus(Long commandId) {
        JobParameters jobParameters = constructJobParameters(commandId);

        JobExecution lastJobExecution = jobRepository.getLastJobExecution(JOB_NAME, jobParameters);
        if(lastJobExecution == null) {
            return null;
        }

        BatchStatus batchJobExecutionStatus = lastJobExecution.getStatus();

        Integer readCount = null;
        Integer readCountTotal = null;
        Iterator<StepExecution> stepExecutionsIterator = lastJobExecution.getStepExecutions().iterator();
        if (stepExecutionsIterator.hasNext()) {
            StepExecution stepExecution = stepExecutionsIterator.next();
            ExecutionContext stepExecutionContext = getStepExecutionContext(lastJobExecution.getJobInstance(), stepExecution.getStepName());
            readCount = stepExecutionContext.containsKey(ITEM_STREAM_READER_NAME + ".read.count") ? stepExecutionContext.getInt("UsersToLoadItemReader.read.count") : null;
            readCountTotal = stepExecutionContext.containsKey(ITEM_STREAM_READER_NAME + ".read.count.max") ? stepExecutionContext.getInt("UsersToLoadItemReader.read.count.max") : null;
        }

        return UsersLoadProcessingStatus.builder()
                .status(batchJobExecutionStatus)
                .countProcessing(readCount)
                .countAll(readCountTotal)
                .startTime(toInstant(lastJobExecution.getStartTime()))
                .endTime(toInstant(lastJobExecution.getEndTime()))
                .build();
    }

    private ExecutionContext getStepExecutionContext(JobInstance jobInstance, String stepName) {
        StepExecution stepExecutionWithContext = jobRepository.getLastStepExecution(jobInstance, stepName);
        return Objects.requireNonNull(stepExecutionWithContext).getExecutionContext();
    }

    private static Instant toInstant(Date date) {
        return Optional.ofNullable(date).map(Date::toInstant).orElse(null);
    }

    private static JobParameters constructJobParameters(Long usersLoadRequestId) {
        Map<String, JobParameter> params = Map.of(
                "usersLoadRequestId", new JobParameter(usersLoadRequestId)
        );
        return new JobParameters(params);
    }
}
