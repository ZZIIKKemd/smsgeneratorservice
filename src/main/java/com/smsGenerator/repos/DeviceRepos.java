package com.smsGenerator.repos;

import com.smsGenerator.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepos extends JpaRepository<Device, Integer> {

    Device findByNumberPort(Integer numberPort);
}
