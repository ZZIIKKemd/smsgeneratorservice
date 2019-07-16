package com.smsGenerator.repos;

import com.smsGenerator.domain.Device;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface DeviceRepos extends JpaRepository<Device, Integer> {

}
