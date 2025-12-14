package com.institute.repository;

import com.institute.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestRepo extends JpaRepository<TestEntity, Long> {

    // Fetch tests of a batch
    List<TestEntity> findByBatch_BatchCode(String batchCode);
    TestEntity findFirstByBatch_BatchCodeOrderByTestDateDesc(String batchCode);
    List<TestEntity> findByBatch_BatchCodeOrderByTestDateDesc(String batchCode);




}
