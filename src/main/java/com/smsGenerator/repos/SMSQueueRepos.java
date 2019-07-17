package com.smsGenerator.repos;

import com.smsGenerator.domain.SMSQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMSQueueRepos extends JpaRepository<SMSQueue, Integer> {
}