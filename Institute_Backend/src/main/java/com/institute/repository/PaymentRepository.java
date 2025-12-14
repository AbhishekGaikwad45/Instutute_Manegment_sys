package com.institute.repository;

import com.institute.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.studentId = :sid AND p.status = 'PAID'")
    Double totalPaidByStudent(@Param("sid") String sid);

    @Query("SELECT COUNT(DISTINCT p.studentId) FROM Payment p WHERE p.status = 'PENDING'")
    int pendingStudentCount();

    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE status = 'PENDING'")
    Double pendingAmount();

    List<Payment> findByStudentId(String studentId);
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.studentId = :studentId")
    int getTotalPaid(@Param("studentId") String studentId);








}
