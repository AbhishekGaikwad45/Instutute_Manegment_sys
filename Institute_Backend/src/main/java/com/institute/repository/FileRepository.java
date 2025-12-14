package com.institute.repository;

import com.institute.model.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileDocument, Long> {

    List<FileDocument> findByBatchCode(String batchCode);

    List<FileDocument> findByBatchCodeContainingIgnoreCase(String text);
}
