package com.institute.controller;

import com.institute.model.StudentProject;
import com.institute.service.StudentProjectService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@CrossOrigin("*")
public class ProjectController {

    private final StudentProjectService service;

    public ProjectController(StudentProjectService service) {
        this.service = service;
    }

    /** CREATE PROJECT */
    @PostMapping("/add")
    public StudentProject addProject(@RequestBody StudentProject p) {
        return service.add(p);
    }

    /** UPDATE PROJECT */
    @PutMapping("/update/{id}")
    public Object updateProject(@PathVariable Long id, @RequestBody StudentProject p) {
        StudentProject updated = service.update(id, p);
        if (updated == null)
            return java.util.Map.of("error", "Project not found");
        return updated;
    }

    /** DELETE PROJECT */
    @DeleteMapping("/delete/{id}")
    public Object deleteProject(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (!deleted)
            return java.util.Map.of("error", "Project not found");
        return java.util.Map.of("message", "Project deleted successfully");
    }

    /** GET ALL PROJECTS */
    @GetMapping("/all")
    public List<StudentProject> getAllProjects() {
        return service.getAll();
    }

    /** GET PROJECTS OF ONE STUDENT */
    @GetMapping("/student/{studentId}")
    public List<StudentProject> getByStudent(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }

    @GetMapping("/student/{studentId}/records")
    public List<Map<String, Object>> getProjectRecords(@PathVariable String studentId) {

        List<StudentProject> list = service.getByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (StudentProject p : list) {

            Map<String, Object> map = new HashMap<>();

            map.put("projectTopic", p.getProjectTopic());
            map.put("technology", p.getTechnology());
            map.put("status", p.getStatus());
            map.put("assignedDate", p.getAssignedDate());
            map.put("completedDate", p.getCompletedDate());

            result.add(map);
        }

        return result;
    }

}
