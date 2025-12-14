package com.institute.repository;

import com.institute.model.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {

    @Query(value = "SELECT COUNT(*) FROM attendance_summary WHERE attendance_percentage < 50", nativeQuery = true)
    int inactiveStudents();

    @Query(value = "SELECT COUNT(*) FROM attendance_summary WHERE attendance_percentage >= 75", nativeQuery = true)
    int activeStudents();
}
