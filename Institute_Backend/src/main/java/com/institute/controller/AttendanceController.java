package com.institute.controller;

import com.institute.model.Attendance;
import com.institute.repository.AttendanceRepository;
import com.institute.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceController {

    @Autowired
    private AttendanceService service;
    @Autowired
    private AttendanceRepository attendanceRepo;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Map<String, Object> req) {

        try {
            String batchCode = req.get("batchCode").toString();
            Integer facultyId = Integer.parseInt(req.get("facultyId").toString());
            String facultyCode = req.get("facultyCode").toString();
            String date = req.get("date").toString();
            String topic = req.get("topic").toString();

            List<Map<String, String>> records =
                    (List<Map<String, String>>) req.get("records");

            String result = service.saveAttendance(batchCode, facultyId, facultyCode, date, records, topic);

            if (result.equals("ALREADY_SAVED")) {
                return ResponseEntity.ok(Map.of("message", "ALREADY_SAVED"));
            }

            return ResponseEntity.ok(Map.of("message", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/batch/{batchCode}/all")
    public ResponseEntity<?> all(@PathVariable String batchCode) {
        return ResponseEntity.ok(service.getAllForBatch(batchCode));
    }

    @GetMapping("/batch/{batchCode}/date/{date}")
    public ResponseEntity<?> byDate(@PathVariable String batchCode, @PathVariable String date) {
        return ResponseEntity.ok(service.getByBatchAndDate(batchCode, date));
    }

    @GetMapping("/batch/{batchCode}/dates")
    public ResponseEntity<?> dates(@PathVariable String batchCode) {
        return ResponseEntity.ok(service.getDatesForBatch(batchCode));
    }

    // NEW — Get attendance of a particular student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentAttendance(@PathVariable String studentId) {
        List<Attendance> list = attendanceRepo.findByStudentId(studentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/percentage/{studentId}")
    public ResponseEntity<?> getPercentage(@PathVariable String studentId) {
        int percent = service.getAttendancePercentage(studentId);
        return ResponseEntity.ok(percent);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAttendanceStatus(
            @RequestParam String studentId,
            @RequestParam String date) {

        Attendance record = attendanceRepo.findByStudentIdAndDate(studentId, date);

        if (record != null && record.getStatus().equalsIgnoreCase("present")) {
            return ResponseEntity.ok(Map.of(
                    "studentId", studentId,
                    "date", date,
                    "status", "present"
            ));
        }

        // No attendance found means ABSENT
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "date", date,
                "status", "absent"
        ));
    }



}
