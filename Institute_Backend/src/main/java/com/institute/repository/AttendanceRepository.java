package com.institute.repository;

import com.institute.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Attendance findByStudentIdAndDate(String studentId, String date);
    List<Attendance> findByBatchCodeOrderByDateDesc(String batchCode);
    List<Attendance> findByBatchCodeAndDate(String batchCode, String date);
    List<Attendance> findByBatchCode(String batchCode);
    List<Attendance> findByStudentId(String studentId);
    boolean existsByBatchCodeAndDate(String batchCode, String date);


    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId")
    Long totalClasses(@Param("studentId") String studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = 'present'")
    Long presentClasses(@Param("studentId") String studentId);


    @Query(
            value = "SELECT * FROM attendance " +
                    "WHERE student_id = :studentId " +
                    "AND STR_TO_DATE(date, '%Y-%m-%d') >= :fromDate " +
                    "ORDER BY STR_TO_DATE(date, '%Y-%m-%d') DESC",
            nativeQuery = true
    )
    List<Attendance> findLastNDays(
            @Param("studentId") String studentId,
            @Param("fromDate") LocalDate fromDate
    );
}
