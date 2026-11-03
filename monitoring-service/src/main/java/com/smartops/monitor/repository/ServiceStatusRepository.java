package com.smartops.monitor.repository;

import com.smartops.monitor.model.ServiceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceStatusRepository
        extends MongoRepository<ServiceStatus, String> {

    List<ServiceStatus> findByUserId(String userId);

    void deleteByUserId(String userId);

}