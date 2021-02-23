package com.spring.batch.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.batch.core.BatchStatus;

import java.time.Instant;

@Data
@Builder
public class UsersLoadProcessingStatus {

    private final BatchStatus status;

    private final Integer countProcessing;
    private final Integer countAll;

    private final Instant startTime;
    private final Instant endTime;

    public Integer getProcessingPercentage() {
        if(countProcessing != null && countAll != null && countAll != 0) {
            return 100 * countProcessing / countAll;
        }
        return null;
    }

}
