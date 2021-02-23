package com.spring.batch.service;

import com.spring.batch.entity.UsersLoadRequest;
import com.spring.batch.repository.UsersToLoadCommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsersToLoadCommandService {

    private final UsersToLoadCommandRepository usersToLoadCommandRepository;

    public UsersToLoadCommandService(UsersToLoadCommandRepository usersToLoadCommandRepository) {
        this.usersToLoadCommandRepository = usersToLoadCommandRepository;
    }

    public List<String> fetchUsersToLoad(Long id) {
        UsersLoadRequest one = usersToLoadCommandRepository.getOne(id);
        return one.getUsernames();
    }
}
