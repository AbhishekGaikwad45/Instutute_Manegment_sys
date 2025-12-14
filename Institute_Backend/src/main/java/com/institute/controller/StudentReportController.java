package com.institute.controller;

import com.institute.service.ReportService;
import com.institute.service.StudentSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentReportController {

    @Autowired private StudentSummaryService summaryService;
    @Autowired private ReportService reportService;

    // SUMMARY API
    @GetMapping("/{studentId}/summary")
    public ResponseEntity<?> summary(@PathVariable String studentId) {
        studentId = studentId.toUpperCase();
        return ResponseEntity.ok(summaryService.buildSummary(studentId));
    }

    // PDF REPORT API
    @GetMapping("/{studentId}/report")
    public ResponseEntity<byte[]> report(@PathVariable String studentId) throws Exception {
        studentId = studentId.toUpperCase();

        Map<String, Object> summary = summaryService.buildSummary(studentId);

        byte[] pdf = reportService.generatePdf(summary);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "inline; filename=StudentReport.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
