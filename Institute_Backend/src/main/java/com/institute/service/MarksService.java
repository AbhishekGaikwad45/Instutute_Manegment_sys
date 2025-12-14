package com.institute.service;

import com.institute.model.Student;
import com.institute.model.StudentMark;
import com.institute.model.TestEntity;
import com.institute.repository.StudentMarkRepo;
import com.institute.repository.StudentRepository;
import com.institute.repository.TestRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarksService {

    private final StudentMarkRepo marksRepo;
    private final TestRepo testRepo;
    private final StudentRepository studentRepo;

    public MarksService(StudentMarkRepo marksRepo, TestRepo testRepo, StudentRepository studentRepo) {
        this.marksRepo = marksRepo;
        this.testRepo = testRepo;
        this.studentRepo = studentRepo;
    }

    public StudentMark addMark(StudentMark sm) {

        // Validate Student using STRING studentId
        Student st = studentRepo.findByStudentIdIgnoreCase(sm.getStudentId());
        if (st == null) throw new RuntimeException("Invalid Student ID!");

        // Validate Test
        TestEntity test = testRepo.findById(sm.getTest().getId())
                .orElseThrow(() -> new RuntimeException("Test not found!"));

        sm.setTest(test);
        return marksRepo.save(sm);
    }

    public List<StudentMark> getMarks(String studentId) {
        return marksRepo.findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
