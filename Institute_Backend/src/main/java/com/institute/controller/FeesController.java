package com.institute.controller;

import com.institute.model.Student;
import com.institute.repository.PaymentRepository;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "http://localhost:3000")
public class FeesController {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private PaymentRepository paymentRepo;


    //  PENDING FEES COUNT API
    @GetMapping("/pending/count")
    public ResponseEntity<?> getPendingCount() {

        List<Student> students = studentRepo.findAll();
        int pendingCount = 0;

        for (Student s : students) {
            try {
                int total = Integer.parseInt(s.getTotalFees());
                int paid = paymentRepo.getTotalPaid(s.getStudentId());

                if (paid < total) pendingCount++;

            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(pendingCount);
    }


    //  PENDING FEES STUDENTS LIST API
    @GetMapping("/pending/students")
    public ResponseEntity<?> getPendingStudents() {

        List<Student> students = studentRepo.findAll();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Student s : students) {
            try {
                int total = Integer.parseInt(s.getTotalFees());
                int paid = paymentRepo.getTotalPaid(s.getStudentId());

                if (paid < total) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", s.getStudentId());
                    map.put("name", s.getName());
                    map.put("mobile", s.getMobile());
                    map.put("pendingAmount", total - paid);
                    map.put("totalFees", total);
                    map.put("paidFees", paid);

                    list.add(map);
                }

            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(list);
    }
}
