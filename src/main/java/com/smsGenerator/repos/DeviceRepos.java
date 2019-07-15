package com.smsGenerator.repos;

import com.smsGenerator.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface DeviceRepos extends JpaRepository<Device, Integer> {

}
