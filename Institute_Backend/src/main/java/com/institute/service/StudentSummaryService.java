package com.institute.service;

import com.institute.model.*;
import com.institute.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentSummaryService {

    @Autowired private StudentRepository studentRepo;
    @Autowired private StudentBatchRepository studentBatchRepo;
    @Autowired private BatchRepository batchRepo;
    @Autowired private FacultyRepository facultyRepo;
    @Autowired private AttendanceRepository attendanceRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private TestRepo testRepo;
    @Autowired private StudentMarkRepo marksRepo;
    @Autowired private StudentProjectRepository projectRepo;




    public Map<String, Object> buildSummary(String studentId) {


        Student student = studentRepo.findByStudentIdIgnoreCase(studentId);
        if (student == null) throw new RuntimeException("Student Not Found!");

        Map<String, Object> data = new HashMap<>();

        // BASIC INFO
        data.put("student", student);
        data.put("name", student.getName());
        data.put("mobile", student.getMobile());
        data.put("email", student.getEmail());
        data.put("courseName", student.getCourseEnrolledFor());
        data.put("admissionDate", student.getAdmissionDate());



        // --------------------------
        // ATTENDANCE DETAILS
        // --------------------------
        List<Attendance> attendanceList = attendanceRepo.findByStudentId(studentId);

        List<Map<String, Object>> attendanceDetails = attendanceList.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", a.getDate());
            map.put("batchCode", a.getBatchCode());
            map.put("topic", a.getTopic());
            map.put("facultyCode", a.getFacultyCode());
            map.put("status", a.getStatus());

            Faculty f = facultyRepo.findById(a.getFacultyId()).orElse(null);
            map.put("facultyName", f != null ? f.getName() : "N/A");

            return map;
        }).collect(Collectors.toList());

        data.put("attendanceDetails", attendanceDetails);

        // --------------------------
// ALL BATCHES JOINED (FIX)
// --------------------------
        List<StudentBatch> joined = studentBatchRepo.findByStudentId(studentId);

        List<Batch> allBatches = joined.stream()
                .filter(sb -> sb != null && sb.getBatchCode() != null)   // ⭐ prevent null crash
                .map(sb -> batchRepo.findByBatchCode(sb.getBatchCode()))
                .filter(b -> b != null)
                .collect(Collectors.toList());

        data.put("allBatches", allBatches);



        // --------------------------
        // BATCH DETAILS
        // --------------------------
        StudentBatch sb = studentBatchRepo.findTopByStudentIdOrderByIdDesc(studentId);

        Batch batch = null;

        if (sb != null && sb.getBatchCode() != null) {
            batch = batchRepo.findByBatchCode(sb.getBatchCode());
        }

        data.put("batch", batch);
        data.put("batchCode", batch != null ? batch.getBatchCode() : "-");
        data.put("batchName", batch != null ? batch.getBatchName() : "-");
        data.put("batchTiming", batch != null ? batch.getBatchTiming() : "-");

        Faculty faculty = null;

        if (batch != null && batch.getFacultyCode() != null) {
            faculty = facultyRepo.findByFacultyCode(batch.getFacultyCode());
        }

        data.put("faculty", faculty);   // Always Faculty object or null



        // --------------------------
        // ATTENDANCE SUMMARY
        // --------------------------
        int totalLectures = attendanceList.size();
        long present = attendanceList.stream().filter(a -> a.getStatus().equals("PRESENT")).count();

        data.put("totalLectures", totalLectures);
        data.put("presentLectures", present);
        data.put("absentLectures", totalLectures - present);
        data.put("attendancePercentage", totalLectures == 0 ? 0 : (present * 100.0 / totalLectures));

        String lastPresent = attendanceList.stream()
                .filter(a -> a.getStatus().equals("PRESENT"))
                .map(Attendance::getDate)
                .max(String::compareTo)
                .orElse("N/A");

        data.put("lastPresentDate", lastPresent);

        // --------------------------
        // FEES SUMMARY
        // --------------------------
        double totalFee = Double.parseDouble(student.getTotalFees());
        double down = Double.parseDouble(student.getDownPayment());

        double paidInstallments = paymentRepo.findByStudentId(studentId)
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double totalPaid = down + paidInstallments;

        data.put("totalFees", totalFee);
        data.put("downPayment", down);
        data.put("paidAmount", totalPaid);
        data.put("pendingAmount", totalFee - totalPaid);

        // --------------------------
        // TEST MARKS
        // --------------------------
        List<StudentMark> marks = marksRepo.findByStudentId(studentId);
        data.put("testMarks", marks);

        // --------------------------
        // PROJECT RECORDS — FIXED ✔
        // --------------------------
        List<StudentProject> projects = projectRepo.findByStudentId(studentId);

        List<Map<String, Object>> projectRecords = projects.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();

            map.put("projectTopic", p.getProjectTopic());
            map.put("technology", p.getTechnology());
            map.put("status", p.getStatus());
            map.put("assignedDate", p.getAssignedDate());
            map.put("completedDate", p.getCompletedDate());

            return map;
        }).collect(Collectors.toList());

        data.put("projectRecords", projectRecords);

        return data;
    }
}

