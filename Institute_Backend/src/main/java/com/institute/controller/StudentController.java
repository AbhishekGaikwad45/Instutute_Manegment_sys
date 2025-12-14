package com.institute.controller;

import com.institute.model.Batch;
import com.institute.model.Faculty;
import com.institute.model.Student;
import com.institute.model.StudentBatch;
import com.institute.repository.StudentBatchRepository;
import com.institute.repository.StudentRepository;
import com.institute.service.AttendanceService;
import com.institute.service.BatchService;
import com.institute.service.FacultyService;
import com.institute.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private StudentBatchRepository studentBatchRepo;

    @Autowired
    private BatchService batchService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private AttendanceService  Service;

    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student saved = studentService.saveStudent(student);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/add/{batchCode}")
    public ResponseEntity<?> addStudentToBatch(@PathVariable String batchCode, @RequestBody Student student) {
        try {
            Student saved = studentService.saveStudent(student, batchCode);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/assign/{batchCode}")
    public ResponseEntity<?> assignStudentToBatch(@PathVariable String batchCode, @RequestBody Map<String, String> body) {
        String studentId = body.get("studentId");
        if (studentId == null) return ResponseEntity.badRequest().body(Map.of("error", "studentId required"));

        Student st = studentService.assignStudentToBatch(studentId, batchCode);
        if (st == null) return ResponseEntity.status(400).body(Map.of("error", "Failed to assign"));
        return ResponseEntity.ok(Map.of("message", "Assigned"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String studentId = req.get("studentId");
        String birthDate = req.get("birthDate");
        Student student = studentRepo.findByStudentIdIgnoreCase(studentId);

        if (student == null)
            return ResponseEntity.status(404).body(Map.of("error", "Invalid Student ID"));

        if (!Objects.equals(student.getBirthDate(), birthDate))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Birth Date"));

        return ResponseEntity.ok(student);
    }

    @GetMapping("/{studentId}/batches")
    public ResponseEntity<?> getStudentBatches(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getBatchesForStudent(studentId));
    }

    @GetMapping("/by-id/{uniqueId}")
    public ResponseEntity<?> getStudentByUniqueId(@PathVariable String uniqueId) {
        Student student = studentService.getStudentByUniqueId(uniqueId);
        if (student == null) return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/delete/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentId) {
        Student student = studentRepo.findByStudentIdIgnoreCase(studentId);
        if (student == null) return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        studentRepo.delete(student);
        return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        List<Student> list = studentRepo.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Student st : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("studentId", st.getStudentId());
            obj.put("name", st.getName());
            obj.put("mobile", st.getMobile());
            obj.put("courseEnrolledFor", st.getCourseEnrolledFor());
            obj.put("birthDate", st.getBirthDate());

            StudentBatch sb = studentBatchRepo.findTopByStudentIdOrderByIdDesc(st.getStudentId());
            if (sb != null) {
                obj.put("batchCode", sb.getBatchCode());
                Batch batch = batchService.getBatchByCode(sb.getBatchCode());
                if (batch != null && batch.getFacultyCode() != null) {
                    Faculty fac = facultyService.getFacultyByCode(batch.getFacultyCode());
                    if (fac != null) {
                        obj.put("assignedFacultyCode", fac.getFacultyCode());
                        obj.put("assignedFacultyName", fac.getName());
                    } else {
                        obj.put("assignedFacultyCode", null);
                        obj.put("assignedFacultyName", null);
                    }
                } else {
                    obj.put("assignedFacultyCode", null);
                    obj.put("assignedFacultyName", null);
                }
            } else {
                obj.put("batchCode", null);
                obj.put("assignedFacultyCode", null);
                obj.put("assignedFacultyName", null);
            }
            response.add(obj);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId, @RequestBody Student updatedData) {
        Student existing = studentRepo.findByStudentIdIgnoreCase(studentId);
        if (existing == null) return ResponseEntity.status(404).body(Map.of("error", "Student not found"));

        // update fields
        existing.setName(updatedData.getName());
        existing.setFatherName(updatedData.getFatherName());
        existing.setAddressLine1(updatedData.getAddressLine1());
        existing.setAddressLine2(updatedData.getAddressLine2());
        existing.setNativePlace(updatedData.getNativePlace());
        existing.setState(updatedData.getState());
        existing.setMobile(updatedData.getMobile());
        existing.setParentContact(updatedData.getParentContact());
        existing.setEmail(updatedData.getEmail());
        existing.setBirthDate(updatedData.getBirthDate());
        existing.setQualification(updatedData.getQualification());
        existing.setPassOutYear(updatedData.getPassOutYear());
        existing.setAnyOtherCertification(updatedData.getAnyOtherCertification());
        existing.setCourseEnrolledFor(updatedData.getCourseEnrolledFor());
        existing.setTotalFees(updatedData.getTotalFees());
        existing.setDownPayment(updatedData.getDownPayment());
        existing.setAdmissionDate(updatedData.getAdmissionDate());

        studentRepo.save(existing);
        return ResponseEntity.ok(Map.of("message", "Student updated successfully"));
    }

    @GetMapping("/{batchCode}/students")
    public ResponseEntity<?> getStudentsByBatch(@PathVariable String batchCode) {
        List<StudentBatch> mappings = studentBatchRepo.findByBatchCode(batchCode);

        List<Student> students = mappings.stream()
                .map(m -> studentRepo.findByStudentIdIgnoreCase(m.getStudentId()))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(students);
    }

    @GetMapping("/by-batch/{batchCode}")
    public List<Student> getByBatch(@PathVariable String batchCode) {
        return studentService.getStudentsByBatch(batchCode);
    }
    @GetMapping("/check/{email}")
    public ResponseEntity<?> checkStudentEmail(@PathVariable String email) {
        Student st = studentRepo.findByEmail(email);
        return ResponseEntity.ok(Map.of("exists", st != null));
    }

    @GetMapping("/all-with-fees")
    public List<Map<String, Object>> allStudentsFees() {
        return studentService.getAllStudentsWithFees();
    }

    @GetMapping("/search/{text}")
    public ResponseEntity<?> searchStudents(@PathVariable String text) {
        List<Student> students = studentRepo.searchByIdOrName(text.toLowerCase());
        return ResponseEntity.ok(students);
    }


    @GetMapping("/inactive")
    public List<Map<String, Object>> getInactiveStudents() {
        List<Student> list = Service.getInactiveStudents();

        return list.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", s.getName());
            map.put("mobile", s.getMobile());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/regular")
    public List<Map<String, Object>> getRegularStudents() {
        List<Student> list = Service.getRegularStudents();

        return list.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", s.getName());
            map.put("mobile", s.getMobile());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/check-mobile")
    public ResponseEntity<?> checkMobile(@RequestParam String mobile) {
        boolean exists = studentRepo.existsByMobile(mobile);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = studentRepo.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }


}
