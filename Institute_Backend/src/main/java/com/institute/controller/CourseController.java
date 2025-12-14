package com.institute.controller;


import com.institute.model.Course;
import com.institute.service.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/course")
@CrossOrigin("*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ADD COURSE
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    // GET ALL COURSES
    @GetMapping("/all")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // UPDATE COURSE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updated) {

        Optional<Course> opt = courseService.getById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Course not found");
        }

        Course existing = opt.get();
        existing.setCourseName(updated.getCourseName());

        return ResponseEntity.ok(courseService.addCourse(existing));
    }

    // DELETE COURSE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {

        Optional<Course> opt = courseService.getById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Course not found");
        }

        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }

}

