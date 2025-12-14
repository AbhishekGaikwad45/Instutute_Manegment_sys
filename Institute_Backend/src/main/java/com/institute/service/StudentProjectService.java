package com.institute.service;

import com.institute.model.StudentProject;
import com.institute.repository.StudentProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentProjectService {

    private final StudentProjectRepository repo;

    public StudentProjectService(StudentProjectRepository repo) {
        this.repo = repo;
    }

    public StudentProject add(StudentProject p) {
        return repo.save(p);
    }

    public StudentProject update(Long id, StudentProject updated) {
        return repo.findById(id).map(existing -> {
            existing.setProjectName(updated.getProjectName());
            existing.setProjectTopic(updated.getProjectTopic());
            existing.setTechnology(updated.getTechnology());
            existing.setStatus(updated.getStatus());
            existing.setDescription(updated.getDescription());
            existing.setAssignedDate(updated.getAssignedDate());
            existing.setCompletedDate(updated.getCompletedDate());
            return repo.save(existing);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    public List<StudentProject> getAll() {
        return repo.findAll();
    }

    public List<StudentProject> getByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }
}
