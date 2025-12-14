package com.institute.controller;

import com.institute.model.StudentMark;
import com.institute.service.MarksService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marks")
@CrossOrigin("*")
public class MarksController {

    private final MarksService service;

    public MarksController(MarksService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public StudentMark add(@RequestBody StudentMark sm) {
        return service.addMark(sm);
    }

    @GetMapping("/student/{studentId}")
    public List<StudentMark> getMarks(@PathVariable String studentId) {
        return service.getMarks(studentId);
    }
    // ⭐ GET Test Records -> Clean JSON For UI
    @GetMapping("/student/{studentId}/records")
    public List<Map<String, Object>> getTestRecords(@PathVariable String studentId) {

        List<StudentMark> marks = service.getMarks(studentId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (StudentMark m : marks) {

            Map<String, Object> map = new HashMap<>();

            map.put("testTitle", m.getTest().getTitle());
            map.put("testDate", m.getTest().getTestDate());
            map.put("marks", m.getMarks());
            map.put("grade", m.getGrade());

            response.add(map);
        }

        return response;
    }

}
