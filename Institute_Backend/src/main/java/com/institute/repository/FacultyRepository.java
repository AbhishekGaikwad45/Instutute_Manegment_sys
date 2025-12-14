package com.institute.repository;

import com.institute.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Faculty findByFacultyCode(String facultyCode);

    boolean existsByEmail(String email);


    boolean existsByMobile(String mobile);
    Faculty findByEmailIgnoreCase(String email);



    Faculty findByEmail(String email);

    @Query(value = "SELECT faculty_code FROM faculty ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastFacultyCode();


}
