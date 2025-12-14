package com.institute.service;

import com.institute.model.Batch;
import com.institute.model.Faculty;
import com.institute.model.Student;
import com.institute.model.StudentBatch;
import com.institute.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private StudentBatchRepository studentBatchRepo;

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private FacultyRepository facultyRepo;
    @Autowired
    private PaymentRepository paymentRepo;

    public String generateStudentId(String admissionDate) {

        LocalDate date;
        try {
            date = (admissionDate == null || admissionDate.trim().isEmpty())
                    ? LocalDate.now()
                    : LocalDate.parse(admissionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            date = LocalDate.now();
        }

        String monthCode = date.getMonth().name().substring(0, 3); // DEC

        // Get last student_id (e.g., DEC-ST-008)
        String lastId = studentRepo.getLastStudentId();

        int nextNumber = 1;

        if (lastId != null && lastId.length() >= 10) {
            try {
                // extract last number: 008
                int lastNumber = Integer.parseInt(lastId.substring(lastId.length() - 3));
                nextNumber = lastNumber + 1;
            } catch (Exception ignored) {}
        }

        String formatted = String.format("%03d", nextNumber);

        return monthCode + "-ST-" + formatted;
    }


    public Student saveStudent(Student student) {
        if (student.getEmail() != null && studentRepo.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (student.getMobile() != null && studentRepo.existsByMobile(student.getMobile())) {
            throw new RuntimeException("Mobile number already exists!");
        }

        if (student.getAdmissionDate() == null || student.getAdmissionDate().trim().isEmpty()) {
            student.setAdmissionDate(LocalDate.now().toString());
        }

        String newId = generateStudentId(student.getAdmissionDate());
        student.setStudentId(newId);

        return studentRepo.save(student);
    }

    public Student saveStudent(Student student, String batchCode) {
        if (student.getAdmissionDate() == null || student.getAdmissionDate().trim().isEmpty()) {
            student.setAdmissionDate(LocalDate.now().toString());
        }
        String newId = generateStudentId(student.getAdmissionDate());
        student.setStudentId(newId);

        Student saved = studentRepo.save(student);

        Batch batch = batchRepo.findByBatchCode(batchCode);
        if (batch != null) {
            StudentBatch sb = new StudentBatch();
            sb.setStudentId(saved.getStudentId());
            sb.setBatchCode(batchCode);
            studentBatchRepo.save(sb);
        }
        return saved;
    }

    public Student assignStudentToBatch(String studentUniqueId, String batchCode) {
        Student student = studentRepo.findByStudentIdIgnoreCase(studentUniqueId);
        if (student == null) return null;

        Batch batch = batchRepo.findByBatchCode(batchCode);
        if (batch == null) return null;

        if (studentBatchRepo.existsByStudentIdAndBatchCode(student.getStudentId(), batchCode)) {
            return student;
        }

        StudentBatch sb = new StudentBatch();
        sb.setStudentId(student.getStudentId());
        sb.setBatchCode(batchCode);
        studentBatchRepo.save(sb);

        return student;
    }

    // returns list of { batch, faculty }
    public List<Map<String, Object>> getBatchesForStudent(String studentUniqueId) {
        Student student = studentRepo.findByStudentIdIgnoreCase(studentUniqueId);
        if (student == null) return List.of();

        List<StudentBatch> mappings = studentBatchRepo.findByStudentId(student.getStudentId());
        List<Map<String, Object>> list = new ArrayList<>();

        for (StudentBatch sb : mappings) {
            Batch batch = batchRepo.findByBatchCode(sb.getBatchCode());
            if (batch == null) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("batch", batch);

            if (batch.getFacultyCode() != null) {
                Faculty faculty = facultyRepo.findByFacultyCode(batch.getFacultyCode());
                map.put("faculty", faculty);
            } else {
                map.put("faculty", null);
            }
            list.add(map);
        }
        return list;
    }

    public Student getStudentByUniqueId(String uniqueId) {
        return studentRepo.findByStudentIdIgnoreCase(uniqueId);
    }
    public List<Student> getStudentsByBatch(String batchCode) {
        return studentRepo.findStudentsByBatchCode(batchCode);
    }

    public List<Map<String, Object>> getAllStudentsWithFees() {
        List<Student> all = studentRepo.findAll();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Student s : all) {

            double totalFees = Double.parseDouble(s.getTotalFees());
            double downPayment = Double.parseDouble(s.getDownPayment());

            // Total installments added from payment table
            Double paidInstallments = paymentRepo.totalPaidByStudent(s.getStudentId());
            if (paidInstallments == null) paidInstallments = 0.0;

            // FINAL PAID = downPayment + other installments
            double totalPaid = downPayment + paidInstallments;

            double pending = totalFees - totalPaid;
            if (pending < 0) pending = 0;

            Map<String, Object> map = new HashMap<>();
            map.put("student", s);
            map.put("totalPaid", totalPaid);
            map.put("pending", pending);

            list.add(map);
        }

        return list;
    }




}
