package com.institute.controller;



import com.institute.model.Enquiry;
import com.institute.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiry")
@CrossOrigin("*")
public class EnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepo;

    // Add new enquiry
    @PostMapping("/add")
    public Enquiry addEnquiry(@RequestBody Enquiry e) {
        return enquiryRepo.save(e);
    }

    // Get all enquiries
    @GetMapping("/all")
    public List<Enquiry> getAll() {
        return enquiryRepo.findAll();
    }

    // Today enquiry count
    @GetMapping("/today-count")
    public int todayCount() {
        return enquiryRepo.countTodayEnquiries();
    }


    @GetMapping("/today")
    public List<Enquiry> todayEnquiries() {
        return enquiryRepo.findTodayEnquiries();
    }


    // ------------------------------------------
    // 🔍 CHECK IF EMAIL EXISTS
    // ------------------------------------------
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return enquiryRepo.existsByEmail(email);
    }

    // ------------------------------------------
    // 🔍 CHECK IF MOBILE EXISTS
    // ------------------------------------------
    @GetMapping("/check-mobile")
    public boolean checkMobile(@RequestParam String mobile) {
        return enquiryRepo.existsByMobile(mobile);
    }
}

