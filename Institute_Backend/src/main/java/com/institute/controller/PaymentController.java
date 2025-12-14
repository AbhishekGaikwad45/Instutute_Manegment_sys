package com.institute.controller;

import com.institute.model.Payment;
import com.institute.model.Student;
import com.institute.repository.PaymentRepository;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentRepository repo;

    @Autowired
    private StudentRepository studentRepo;



    @PostMapping("/add")
    public Payment addPayment(@RequestBody Payment p) {
        p.setPaidOn(LocalDate.now());
        return repo.save(p);
    }


}

