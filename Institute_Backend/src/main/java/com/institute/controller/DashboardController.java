package com.institute.controller;

import com.institute.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    @Autowired private StudentRepository studentRepo;
    @Autowired private FacultyRepository facultyRepo;
    @Autowired private BatchRepository batchRepo;

    @Autowired private EnquiryRepository enquiryRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private AttendanceSummaryRepository attendanceRepo;

    @Autowired private StudentRepository studentRepository;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> data = new HashMap<>();

        // OLD DATA
        data.put("totalStudents", studentRepo.count());
        data.put("totalFaculty", facultyRepo.count());
        data.put("activeBatches", batchRepo.count());

        // New admissions this month
        try {
            data.put("newAdmissions", studentRepository.countThisMonthAdmissions());
        } catch (Exception e) {
            data.put("newAdmissions", 0);
        }

        // NEW DATA
        data.put("todaysEnquiries", enquiryRepo.countTodayEnquiries());
        data.put("pendingFeesCount", paymentRepo.pendingStudentCount());
        data.put("pendingFeesAmount", paymentRepo.pendingAmount());
        data.put("inactiveStudents", attendanceRepo.inactiveStudents());
        data.put("activeAttendanceStudents", attendanceRepo.activeStudents());

        return data;
    }
}
    