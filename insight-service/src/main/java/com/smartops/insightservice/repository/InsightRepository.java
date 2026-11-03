
package com.smartops.insightservice.repository;

import com.smartops.insightservice.model.Insight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsightRepository extends MongoRepository<Insight, String> {

    List<Insight> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Insight> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    void deleteByUserId(String userId);

    void deleteByUserIdAndServiceName(String userId, String serviceName);

    long countByUserId(String userId);
}
