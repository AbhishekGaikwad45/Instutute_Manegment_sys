package com.institute.repository;

import com.institute.model.StudentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentBatchRepository extends JpaRepository<StudentBatch, Integer> {
    List<StudentBatch> findByStudentId(String studentId);
    List<StudentBatch> findByBatchCode(String batchCode);

    boolean existsByStudentIdAndBatchCode(String studentId, String batchCode);

    // convenience: latest mapping for a student (if you still want top)
    StudentBatch findTopByStudentIdOrderByIdDesc(String studentId);
}
