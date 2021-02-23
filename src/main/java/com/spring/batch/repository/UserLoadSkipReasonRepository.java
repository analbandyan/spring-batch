package com.spring.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.batch.entity.UserLoadSkipReason;

public interface UserLoadSkipReasonRepository extends JpaRepository<UserLoadSkipReason, Long> {
}
