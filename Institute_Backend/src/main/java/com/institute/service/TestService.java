package com.institute.service;

import com.institute.model.Batch;
import com.institute.model.TestEntity;
import com.institute.repository.BatchRepository;
import com.institute.repository.TestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepo repo;

    @Autowired
    private BatchRepository batchRepo;

    public TestEntity add(TestEntity t) {


        String code = t.getBatch().getBatchCode();


        Batch batch = batchRepo.findByBatchCode(code);

        if (batch == null) {
            throw new RuntimeException("Invalid Batch Code: " + code);
        }


        t.setBatch(batch);


        return repo.save(t);
    }
    public List<TestEntity> getByBatch(String batchCode) {
        return repo.findByBatch_BatchCode(batchCode);
    }
    public TestEntity getLatestTest(String batchCode) {
        return repo.findFirstByBatch_BatchCodeOrderByTestDateDesc(batchCode);
    }


}
