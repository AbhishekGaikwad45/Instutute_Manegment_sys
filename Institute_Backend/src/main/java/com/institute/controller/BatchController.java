package com.institute.controller;

import com.institute.model.*;
import com.institute.repository.AttendanceRepository;
import com.institute.repository.FileRepository;
import com.institute.repository.StudentBatchRepository;
import com.institute.repository.StudentRepository;
import com.institute.service.BatchService;
import com.institute.service.FacultyService;
import com.institute.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/batch")
@CrossOrigin("*")
public class BatchController {

    @Autowired
    private BatchService service;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentBatchRepository studentBatchRepository;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private FileRepository fileRepo;




    // CREATE BATCH

    @PostMapping("/add")
    public ResponseEntity<?> addBatch(@RequestBody Batch batch) {
        Batch saved = service.saveBatch(batch);
        return ResponseEntity.ok(saved);
    }



    // GET ALL BATCHES

    @GetMapping("/all")
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(service.getAllBatches());
    }



    // GET BATCH BY batchCode

    @GetMapping("/code/{batchCode}")
    public ResponseEntity<?> getBatchByCode(@PathVariable String batchCode) {

        Batch batch = service.getBatchByCode(batchCode);

        if (batch == null)
            return ResponseEntity.status(404).body(Map.of("error", "Batch not found"));

        Faculty faculty = null;

        if (batch.getFacultyCode() != null) {
            faculty = facultyService.getFacultyByCode(batch.getFacultyCode());
        }

        // NULL SAFE RESPONSE
        Map<String, Object> response = new HashMap<>();
        response.put("batch", batch);
        response.put("faculty", faculty); // faculty NULL असेल तरी चालेल

        return ResponseEntity.ok(response);
    }




    // UPDATE BATCH BY batchCode

    @PutMapping("/update/{batchCode}")
    public ResponseEntity<?> updateBatch(
            @PathVariable String batchCode,
            @RequestBody Batch newBatch) {

        Batch updated = service.updateBatchByCode(batchCode, newBatch);

        if (updated == null)
            return ResponseEntity.status(404).body(Map.of("error", "Batch not found"));

        return ResponseEntity.ok(updated);
    }



    // DELETE BATCH BY batchCode

    @DeleteMapping("/delete/{batchCode}")
    public ResponseEntity<?> deleteBatch(@PathVariable String batchCode) {

        boolean deleted = service.deleteBatchByCode(batchCode);

        if (!deleted)
            return ResponseEntity.status(404).body(Map.of("error", "Batch not found"));

        return ResponseEntity.ok(Map.of("message", "Batch Deleted Successfully!"));
    }



    // ASSIGN FACULTY USING batchCode

    @PostMapping("/assign-faculty")
    public ResponseEntity<?> assignFaculty(@RequestBody Map<String, String> req) {

        String batchCode = req.get("batchCode");
        String facultyCode = req.get("facultyCode");

        // 1️⃣ Batch exists?
        Batch batch = batchService.getBatchByCode(batchCode);
        if (batch == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Batch not found"));
        }

        // 2️⃣ Faculty exists?
        Faculty faculty = facultyService.getFacultyByCode(facultyCode);
        if (faculty == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Faculty not found"));
        }

        // 3️⃣ SAME faculty already assigned
        if (batch.getFacultyCode() != null &&
                batch.getFacultyCode().equals(facultyCode)) {

            return ResponseEntity.ok(
                    Map.of("message", "Faculty already assigned to this batch")
            );
        }

        // 4️⃣ DIFFERENT faculty already assigned
        if (batch.getFacultyCode() != null &&
                !batch.getFacultyCode().isEmpty()) {

            return ResponseEntity.ok(
                    Map.of("message", "This batch already has a faculty assigned")
            );
        }

        // 5️⃣ Assign faculty
        batchService.assignFacultyByCode(batchCode, facultyCode);

        return ResponseEntity.ok(
                Map.of("message", "Faculty assigned successfully")
        );
    }






    // ASSIGN STUDENT USING batchCode

    @PostMapping("/assign-student/{batchCode}")
    public ResponseEntity<?> assignStudentToBatch(
            @PathVariable String batchCode,
            @RequestBody Map<String, String> body) {

        String studentId = body.get("studentId");

        if (studentId == null)
            return ResponseEntity.badRequest().body(Map.of("error", "studentId required"));

        //  CHECK IF ALREADY ASSIGNED
        boolean alreadyAssigned = studentBatchRepository
                .existsByStudentIdAndBatchCode(studentId, batchCode);

        if (alreadyAssigned) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Student already assigned to this batch!"));
        }

        //  CHECK IF STUDENT EXISTS
        Student student = studentRepo.findByStudentIdIgnoreCase(studentId);
        if (student == null)
            return ResponseEntity.status(404).body(Map.of("error", "Student Not Found"));

        //  CHECK IF BATCH EXISTS
        Batch batch = service.getBatchByCode(batchCode);
        if (batch == null)
            return ResponseEntity.status(404).body(Map.of("error", "Batch Not Found"));

        //  ASSIGN STUDENT
        Student updated = service.assignStudentToBatchByCode(studentId, batchCode);

        return ResponseEntity.ok(updated);
    }

    // GET STUDENTS OF A BATCH BY batchCode

    @GetMapping("/{batchCode}/students")
    public ResponseEntity<?> getStudentsOfBatch(@PathVariable String batchCode) {

        Batch batch = service.getBatchByCode(batchCode);

        if (batch == null)
            return ResponseEntity.status(404).body(Map.of("error", "Batch not found"));

        List<StudentBatch> mappings = studentBatchRepository.findByBatchCode(batch.getBatchCode());
        List<Student> students = new ArrayList<>();

        for (StudentBatch sb : mappings) {
            Student s = studentRepo.findByStudentIdIgnoreCase(sb.getStudentId());

            if (s != null) students.add(s);
        }

        return ResponseEntity.ok(students);
    }



    // GET BATCHES BY FACULTY

    @GetMapping("/by-faculty/{facultyId}")
    public ResponseEntity<List<Batch>> getBatchesByFaculty(@PathVariable int facultyId) {
        return ResponseEntity.ok(service.getBatchesByFaculty(facultyId));
    }

    @GetMapping("/full-data")
    public ResponseEntity<?> getFullBatchData() {

        List<Batch> batches = service.getAllBatches();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Batch b : batches) {

            Map<String, Object> batchData = new HashMap<>();

            // 1) Batch Info
            batchData.put("batch", b);

            // 2) Faculty Info
            Faculty faculty = null;
            if (b.getFacultyCode() != null) {
                faculty = facultyService.getFacultyByCode(b.getFacultyCode());
            }
            batchData.put("faculty", faculty);

            // 3) Students of the batch
            List<StudentBatch> mappings =
                    studentBatchRepository.findByBatchCode(b.getBatchCode());

            List<Student> students = new ArrayList<>();
            for (StudentBatch sb : mappings) {
                Student s = studentRepo.findByStudentIdIgnoreCase(sb.getStudentId());
                if (s != null) students.add(s);
            }
            batchData.put("students", students);

            // 4) Attendance for this batch
            List<Attendance> attendance =
                    attendanceRepo.findByBatchCode(b.getBatchCode());
            batchData.put("attendance", attendance);

            response.add(batchData);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBatch(@RequestParam String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Batch> all = service.getAllBatches();

        // Filter batchCode किंवा batchName ने
        List<Map<String, Object>> result = all.stream()
                .filter(b ->
                        b.getBatchCode().toLowerCase().contains(query.toLowerCase()) ||
                                (b.getBatchName() != null &&
                                        b.getBatchName().toLowerCase().contains(query.toLowerCase()))
                )
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("batchCode", b.getBatchCode());
                    map.put("batchName", b.getBatchName()); // <-- यामुळे योग्य नाव जाईल
                    return map;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/batch/{batchCode}")
    public ResponseEntity<?> getFilesByBatch(@PathVariable String batchCode) {
        return ResponseEntity.ok(fileRepo.findByBatchCode(batchCode));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getBatchesOfStudent(@PathVariable String studentId) {

        List<StudentBatch> mappings = studentBatchRepository.findByStudentId(studentId);

        List<Batch> batches = new ArrayList<>();

        for (StudentBatch sb : mappings) {
            Batch b = service.getBatchByCode(sb.getBatchCode());
            if (b != null) batches.add(b);
        }

        return ResponseEntity.ok(batches);
    }





}
