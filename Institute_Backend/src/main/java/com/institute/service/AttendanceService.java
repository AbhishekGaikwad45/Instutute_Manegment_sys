package com.institute.service;

import com.institute.model.Attendance;
import com.institute.model.Batch;
import com.institute.model.Student;
import com.institute.repository.AttendanceRepository;
import com.institute.repository.BatchRepository;
import com.institute.repository.StudentBatchRepository;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private StudentBatchRepository studentBatchRepo;

    public String saveAttendance(String batchCode, Integer facultyId, String facultyCode,
                                 String date, List<Map<String, String>> records, String topic) {

        if (date == null || date.isBlank()) {
            date = LocalDate.now().toString();
        }

        //  Check — attendance already saved for same batch and same date
        boolean alreadySaved = attendanceRepo.existsByBatchCodeAndDate(batchCode, date);
        if (alreadySaved) {
            return "ALREADY_SAVED";
        }

        Batch batch = batchRepo.findByBatchCode(batchCode);
        if (batch == null) return "Invalid Batch Code";

        Set<String> batchStudentIds = studentBatchRepo.findByBatchCode(batchCode)
                .stream()
                .map(sb -> sb.getStudentId())
                .collect(Collectors.toSet());

        int count = 0;

        for (Map<String, String> r : records) {

            String studentId = r.get("studentId");
            String status = r.get("status");

            if (studentId == null || status == null) continue;
            if (!batchStudentIds.contains(studentId)) continue;

            Student st = studentRepo.findByStudentIdIgnoreCase(studentId);
            if (st == null) continue;

            Attendance att = new Attendance();
            att.setBatchCode(batchCode);
            att.setDate(date);
            att.setTopic(topic);
            att.setStudentId(studentId);
            att.setStudentName(st.getName());
            att.setStatus(status);
            att.setFacultyCode(facultyCode);
            att.setFacultyId(facultyId);

            attendanceRepo.save(att);
            count++;
        }

        return "Saved " + count + " records";
    }




    public List<Attendance> getAllForBatch(String batchCode) {
        return attendanceRepo.findByBatchCodeOrderByDateDesc(batchCode);
    }

    public List<Attendance> getByBatchAndDate(String batchCode, String date) {
        return attendanceRepo.findByBatchCodeAndDate(batchCode, date);
    }

    public List<String> getDatesForBatch(String batchCode) {
        return attendanceRepo.findByBatchCode(batchCode)
                .stream()
                .map(Attendance::getDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public int getAttendancePercentage(String studentId) {

        Long total = attendanceRepo.totalClasses(studentId);
        Long present = attendanceRepo.presentClasses(studentId);

        if (total == 0) return 0;

        return (int) ((present * 100) / total);
    }

    // INACTIVE STUDENTS → last 7 days no PRESENT
    public List<Student> getInactiveStudents() {

        List<Student> all = studentRepo.findAll();
        List<Student> result = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(7);

        for (Student s : all) {

            List<Attendance> att = attendanceRepo.findByStudentId(s.getStudentId());

            boolean presentFound = att.stream().anyMatch(a -> {
                try {
                    LocalDate d = LocalDate.parse(a.getDate());
                    return !d.isBefore(fromDate) &&
                            !d.isAfter(today) &&
                            a.getStatus().equalsIgnoreCase("present");
                } catch (Exception e) {
                    return false;
                }
            });

            if (!presentFound) {
                result.add(s);
            }
        }

        return result;
    }




    //  REGULAR STUDENTS → last 5 days present
    public List<Student> getRegularStudents() {

        List<Student> all = studentRepo.findAll();
        List<Student> result = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(5);

        for (Student s : all) {

            List<Attendance> att = attendanceRepo.findByStudentId(s.getStudentId());

            long presentCount = att.stream().filter(a -> {
                try {
                    LocalDate d = LocalDate.parse(a.getDate());
                    return !d.isBefore(fromDate) &&
                            !d.isAfter(today) &&
                            a.getStatus().equalsIgnoreCase("present");
                } catch (Exception e) {
                    return false;
                }
            }).count();

            if (presentCount >= 1) {
                result.add(s);
            }
        }

        return result;
    }




}
