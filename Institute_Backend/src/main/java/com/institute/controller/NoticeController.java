package com.institute.controller;

import com.institute.model.Notice;
import com.institute.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin(origins = "http://localhost:3000")
public class NoticeController {

    @Autowired
    private NoticeService service;

    // Create notice (Faculty / Counselor )
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Notice n) {
        try {
            Notice saved = service.addNotice(n);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity
                    .status(400)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // All notices (Admin / Faculty / Student
    @GetMapping("/all")
    public List<Notice> all() {
        return service.getAll();
    }

    // Latest 5 notices
    @GetMapping("/latest")
    public List<Notice> latest() {
        return service.getLatest();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.deleteNotice(id);

        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("error", "Notice not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Notice notice) {
        try {
            Notice updated = service.updateNotice(id, notice);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

}
