package com.institute.service;


import com.institute.model.Course;
import com.institute.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepo;

    public Course addCourse(Course c) {
        return courseRepo.save(c);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
    public Optional<Course> getById(Long id) {
        return courseRepo.findById(id);
    }

    public void deleteCourse(Long id) {
        courseRepo.deleteById(id);
    }

}

