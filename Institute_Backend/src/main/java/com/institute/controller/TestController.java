package com.institute.controller;

import com.institute.model.TestEntity;
import com.institute.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin("*")
public class TestController {

    @Autowired
    private TestService service;

    @PostMapping("/add")
    public TestEntity add(@RequestBody TestEntity t) {
        return service.add(t);
    }

    @GetMapping("/by-batch/{batchCode}")
    public List<TestEntity> getByBatch(@PathVariable String batchCode) {
        return service.getByBatch(batchCode);
    }
    @GetMapping("/latest/{batchCode}")
    public TestEntity getLatest(@PathVariable String batchCode) {
        return service.getLatestTest(batchCode);
    }


}
