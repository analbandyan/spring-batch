package com.spring.batch.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class UserLoadSkipReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UsersLoadRequest usersLoadRequest;

    private String username;

    @Column(length = 512)
    private String reason;

}
