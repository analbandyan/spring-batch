package com.spring.batch.controller;

import com.spring.batch.dto.UsersLoadProcessingStatus;
import com.spring.batch.dto.UsersToLoadRequestDto;
import com.spring.batch.service.UsersLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.batch.dto.UsersLoadRequestStatusDto;

@RestController
@RequestMapping("/users/load/requests")
@Slf4j
public class UsersLoadController {

    private final UsersLoadService usersLoadService;

    public UsersLoadController(UsersLoadService usersLoadService) {
        this.usersLoadService = usersLoadService;
    }

    @PostMapping()
    public  ResponseEntity<UsersLoadRequestStatusDto> submitUsersLoadTask(@RequestBody UsersToLoadRequestDto usersToLoadRequestDto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        UsersLoadRequestStatusDto usersLoadRequestStatusDto = usersLoadService.loadUsers(usersToLoadRequestDto);
        return ResponseEntity.ok(usersLoadRequestStatusDto);
    }

    @GetMapping("/{requestId}/status")
    public  ResponseEntity<Object> getTaskStatus(@PathVariable Long requestId) {
        UsersLoadProcessingStatus usersLoadProcessingStatus = usersLoadService.getUsersLoadStatus(requestId);
        if(usersLoadProcessingStatus == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usersLoadProcessingStatus);
    }

}
