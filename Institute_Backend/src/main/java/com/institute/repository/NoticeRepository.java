package com.institute.repository;

import com.institute.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {


    List<Notice> findAllByOrderByDateDescIdDesc();


    List<Notice> findTop5ByOrderByDateDescIdDesc();
}
