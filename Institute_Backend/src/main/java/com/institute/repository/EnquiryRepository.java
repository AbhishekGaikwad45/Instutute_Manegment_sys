package com.institute.repository;

import com.institute.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    @Query(value = "SELECT COUNT(*) FROM enquiries WHERE created_at = CURRENT_DATE()", nativeQuery = true)
    int countTodayEnquiries();

    @Query("SELECT e FROM Enquiry e WHERE e.createdAt = CURRENT_DATE")
    List<Enquiry> findTodayEnquiries();

}

