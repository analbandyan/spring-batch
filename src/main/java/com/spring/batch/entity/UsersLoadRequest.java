package com.spring.batch.entity;

import com.spring.batch.converter.ListToJsonConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class UsersLoadRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "usernames", length = 8192)
    private List<String> usernames;


}
