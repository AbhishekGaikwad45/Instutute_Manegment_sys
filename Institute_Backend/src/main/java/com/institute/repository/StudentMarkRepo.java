package com.institute.repository;

import com.institute.model.StudentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentMarkRepo extends JpaRepository<StudentMark, Long> {

    // Fetch marks of student
    List<StudentMark> findByStudentIdOrderByCreatedAtDesc(String studentId);

    // Fetch marks by batch
    List<StudentMark> findByTest_Batch_BatchCode(String batchCode);

    List<StudentMark> findByStudentId(String studentId);
}
