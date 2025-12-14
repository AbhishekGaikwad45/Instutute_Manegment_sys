package com.institute.service;

import com.institute.model.Batch;
import com.institute.model.Faculty;
import com.institute.model.Student;
import com.institute.model.StudentBatch;
import com.institute.repository.BatchRepository;
import com.institute.repository.FacultyRepository;
import com.institute.repository.StudentBatchRepository;
import com.institute.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BatchService {

    @Autowired
    private BatchRepository repo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private StudentBatchRepository studentBatchRepo;

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private FacultyRepository facultyRepo;


    // ------------------------------------------------------
    // GENERATE BATCH CODE (example: NOV-345)
    // ------------------------------------------------------
    public String generateBatchCode(String startDate) {

        LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String monthCode = date.getMonth().name().substring(0, 3);  // NOV

        int randomNum = new Random().nextInt(900) + 100; // 100–999

        return monthCode + "-" + randomNum;
    }


    // ------------------------------------------------------
    // SAVE BATCH (Auto-generate batchCode)
    // ------------------------------------------------------
    public Batch saveBatch(Batch batch) {

        batch.setBatchCode(generateBatchCode(batch.getStartDate()));

        return repo.save(batch);
    }


    // ------------------------------------------------------
    // FETCH ALL BATCHES
    // ------------------------------------------------------
    public List<Batch> getAllBatches() {
        return repo.findAll();
    }


    // ------------------------------------------------------
    // FETCH BATCH BY batchCode
    // ------------------------------------------------------
    public Batch getBatchByCode(String batchCode) {
        return repo.findByBatchCode(batchCode);
    }


    // ------------------------------------------------------
    // UPDATE BATCH BY batchCode
    // ------------------------------------------------------
    public Batch updateBatchByCode(String batchCode, Batch newBatch) {

        Batch existing = repo.findByBatchCode(batchCode);

        if (existing == null) return null;

        existing.setBatchName(newBatch.getBatchName());
        existing.setCourseName(newBatch.getCourseName());
        existing.setBatchTiming(newBatch.getBatchTiming());
        existing.setStartDate(newBatch.getStartDate());


        //  MOST IMPORTANT – facultyCode update कर
        existing.setFacultyCode(newBatch.getFacultyCode());

        return repo.save(existing);
    }



    // ------------------------------------------------------
    // DELETE BATCH BY batchCode
    // ------------------------------------------------------
    public boolean deleteBatchByCode(String batchCode) {

        Batch batch = repo.findByBatchCode(batchCode);

        if (batch == null) return false;

        repo.delete(batch);
        return true;
    }


    // ------------------------------------------------------
    // ASSIGN FACULTY BY batchCode
    // ------------------------------------------------------
    public Batch assignFacultyByCode(String batchCode, String facultyCode) {

        Batch batch = repo.findByBatchCode(batchCode);
        if (batch == null) return null;

        Faculty fac = facultyRepo.findByFacultyCode(facultyCode);
        if (fac == null) return null;

        // already assigned → do nothing
        if (batch.getFacultyCode() != null && !batch.getFacultyCode().isEmpty()) {
            return batch;
        }

        batch.setFacultyCode(facultyCode);
        return repo.save(batch);
    }



    public Student assignStudentToBatchByCode(String studentId, String batchCode) {

        Batch batch = repo.findByBatchCode(batchCode);
        if (batch == null) return null;

        Student student = studentRepo.findByStudentIdIgnoreCase(studentId);
        if (student == null) return null;

        // 🔒 ALREADY ASSIGNED CHECK
        boolean exists = studentBatchRepo
                .existsByStudentIdAndBatchCode(studentId, batchCode);

        if (exists) {
            return student; // already assigned → silent return
        }

        StudentBatch sb = new StudentBatch();
        sb.setStudentId(student.getStudentId());
        sb.setBatchCode(batchCode);

        studentBatchRepo.save(sb);
        return student;
    }




    // ------------------------------------------------------
    // GET BATCHES BY FACULTY
    // ------------------------------------------------------
    public List<Batch> getBatchesByFaculty(int facultyId) {

        List<Batch> all = repo.findAll();
        List<Batch> result = new ArrayList<>();

        for (Batch b : all) {
            if (b.getFacultyCode() != null && b.getFacultyCode() == b.getFacultyCode()) {
                result.add(b);
            }
        }
        return result;
    }
    public List<Map<String, Object>> getBatchesForStudent(String studentUniqueId) {

        Student student = studentRepo.findByStudentIdIgnoreCase(studentUniqueId);
        if (student == null) return List.of();

        List<StudentBatch> mappings = studentBatchRepo.findByStudentId(studentUniqueId);
        List<Map<String, Object>> list = new ArrayList<>();

        for (StudentBatch sb : mappings) {

            Batch batch = batchRepo.findByBatchCode(sb.getBatchCode());
            if (batch == null) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("batch", batch);

            if (batch.getFacultyCode() != null) {
                Faculty faculty = facultyRepo.findByFacultyCode(batch.getFacultyCode());
                map.put("faculty", faculty);
            }

            list.add(map);
        }

        return list;
    }




}
