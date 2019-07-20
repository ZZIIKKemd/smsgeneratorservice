package com.smsGenerator.repos;

import com.smsGenerator.domain.SmsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsStatusRepos extends JpaRepository<SmsStatus, Integer> {
}
