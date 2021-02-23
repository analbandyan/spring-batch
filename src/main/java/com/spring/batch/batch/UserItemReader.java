package com.spring.batch.batch;

import com.spring.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import com.spring.batch.service.UsersToLoadCommandService;

@Slf4j
@Component
@StepScope
public class UserItemReader extends AbstractItemCountingItemStreamItemReader<User> {

    public static final String ITEM_STREAM_READER_NAME = "UsersToLoadItemReader";

    private final UsersToLoadCommandService usersToLoadCommandService;
    private final Long usersLoadRequestId;
    private List<String> usernames;

    public UserItemReader(@Value(("#{jobParameters['usersLoadRequestId']}")) Long usersLoadRequestId, UsersToLoadCommandService usersToLoadCommandService) {
        this.usersLoadRequestId = usersLoadRequestId;
        this.usersToLoadCommandService = usersToLoadCommandService;
        setName(ITEM_STREAM_READER_NAME);
    }

    @Override
    protected User doRead() {
        String username = usernames.get(getCurrentItemCount() - 1);
        User user = new User();
        user.setName(username);
        log.info(">>>Read user: {}", user.getName());
        doWait(500);
        return user;
    }

    private static void doWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doOpen() {
        usernames = usersToLoadCommandService.fetchUsersToLoad(usersLoadRequestId);
        setMaxItemCount(usernames.size());
    }

    @Override
    protected void doClose() {
    }

}
