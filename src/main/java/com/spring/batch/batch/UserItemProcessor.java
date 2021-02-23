package com.spring.batch.batch;

import com.spring.batch.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    private int i = 14;

    @Override
    public User process(User user) {
        log.info("***Process user {}", user.getName());
        //throw exception on 14th item
//        if(--i == 0) {
//            throw new RuntimeException("hhh");
//        }
        return user;
    }
}
