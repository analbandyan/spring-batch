package com.spring.batch.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UsersToLoadRequestDto {

    private Set<String> usernames;

}
