package com.smsGenerator.repos;

import com.smsGenerator.domain.RequestInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestInfoRepos extends JpaRepository<RequestInfo, Integer> {
}
