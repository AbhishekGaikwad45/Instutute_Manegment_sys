package com.institute.controller;

import com.institute.model.FileDocument;
import com.institute.repository.FileRepository;
import com.institute.service.CloudinaryUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
@CrossOrigin("*")
public class FileUploadController {

    @Autowired
    private CloudinaryUploadService cloudService;

    @Autowired
    private FileRepository fileRepo;




    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("batchCode") String batchCode) {

        try {
            Map uploadResult = cloudService.upload(file);

            FileDocument doc = new FileDocument();
            doc.setFileName(file.getOriginalFilename());
            doc.setUrl((String) uploadResult.get("secure_url"));
            doc.setPublicId((String) uploadResult.get("public_id"));
            doc.setResourceType((String) uploadResult.get("resource_type"));
            doc.setBatchCode(batchCode);

            fileRepo.save(doc);

            return ResponseEntity.ok(doc);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }




    //  GET ALL FILES
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(fileRepo.findAll());
    }


    //  GET FILES BY BATCH
    @GetMapping("/batch/{batchCode}")
    public ResponseEntity<?> getByBatch(@PathVariable String batchCode) {
        return ResponseEntity.ok(fileRepo.findByBatchCode(batchCode));
    }


    //  SEARCH BY BATCH CODE
    @GetMapping("/search/{text}")
    public ResponseEntity<?> search(@PathVariable String text) {
        return ResponseEntity.ok(fileRepo.findByBatchCodeContainingIgnoreCase(text));
    }


    //  DELETE FILE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            FileDocument file = fileRepo.findById(id).orElse(null);

            if (file == null)
                return ResponseEntity.badRequest().body("File not found");

            cloudService.delete(file.getPublicId(), file.getResourceType());

            fileRepo.delete(file);

            return ResponseEntity.ok("Deleted Successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Delete failed: " + e.getMessage());
        }
    }
}
