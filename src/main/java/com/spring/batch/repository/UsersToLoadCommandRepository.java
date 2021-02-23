package com.spring.batch.repository;

import com.spring.batch.entity.UsersLoadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersToLoadCommandRepository extends JpaRepository<UsersLoadRequest, Long> {
}
