package com.spring.batch.batch;

import com.spring.batch.entity.User;
import com.spring.batch.entity.UserLoadSkipReason;
import com.spring.batch.repository.UserLoadSkipReasonRepository;
import com.spring.batch.repository.UserRepository;
import com.spring.batch.repository.UsersToLoadCommandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Component
@StepScope
public class UserItemWriter implements ItemWriter<User> {

    private final UserRepository userRepository;

    private final Long usersLoadRequestId;
    private final UserLoadSkipReasonRepository userLoadSkipReasonRepository;
    private final UsersToLoadCommandRepository usersToLoadCommandRepository;

    public UserItemWriter(UserRepository userRepository,
                          @Value(("#{jobParameters['usersLoadRequestId']}")) Long usersLoadRequestId,
                          UserLoadSkipReasonRepository userLoadSkipReasonRepository,
                          UsersToLoadCommandRepository usersToLoadCommandRepository) {
        this.userRepository = userRepository;
        this.usersLoadRequestId = usersLoadRequestId;
        this.userLoadSkipReasonRepository = userLoadSkipReasonRepository;
        this.usersToLoadCommandRepository = usersToLoadCommandRepository;
    }

    @Override
    public void write(List<? extends User> users) {
        log.info("###Write users: {}", users.stream().map(User::getName).collect(Collectors.toList()));
        users.forEach(this::doWrite);
    }

    private void doWrite(User user) {
        boolean shouldTheItemBeSkipped = (new Random().nextInt(100) % 5 == 0);
        if(shouldTheItemBeSkipped) {

            UserLoadSkipReason userLoadSkipReason = new UserLoadSkipReason();
            userLoadSkipReason.setReason("some test reason");
            userLoadSkipReason.setUsername(user.getName());
            userLoadSkipReason.setUsersLoadRequest(usersToLoadCommandRepository.getOne(usersLoadRequestId));

            userLoadSkipReasonRepository.save(userLoadSkipReason);
        } else {
            userRepository.save(user);
        }
    }
}
