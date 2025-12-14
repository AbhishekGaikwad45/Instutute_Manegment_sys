package com.institute.repository;

import com.institute.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findByStudentIdIgnoreCase(String studentId);
    Student findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
    @Query("SELECT s FROM Student s WHERE LOWER(s.studentId) LIKE %:text% OR LOWER(s.name) LIKE %:text%")
    List<Student> searchByIdOrName(@Param("text") String text);


    //  CORRECT QUERY (students table + STRING DATE handling)
    @Query(value = "SELECT COUNT(*) FROM students " +
            "WHERE STR_TO_DATE(admission_date, '%Y-%m-%d') " +
            "BETWEEN DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01') " +
            "AND LAST_DAY(CURRENT_DATE())",
            nativeQuery = true)
    int countThisMonthAdmissions();


    @Query("SELECT s FROM Student s WHERE s.studentId IN (SELECT sb.studentId FROM StudentBatch sb WHERE sb.batchCode = :batchCode)")
    List<Student> findStudentsByBatchCode(@Param("batchCode") String batchCode);

    @Query("SELECT s FROM Student s WHERE s.active = true")
    List<Student> findAllActiveStudents();
    @Query(value = "SELECT student_id FROM students ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastStudentId();


}
