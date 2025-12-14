package com.institute.controller;

import com.institute.model.Faculty;
import com.institute.model.Student;
import com.institute.model.User;
import com.institute.repository.FacultyRepository;
import com.institute.repository.StudentRepository;
import com.institute.repository.UserRepository;
import com.institute.service.OtpService;
import com.institute.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UserRepository userRepository;


//    @PostMapping("/signup")
//    public String signup(@RequestBody User user) {
//        return userService.registerUser(user);
//    }

    // ---------------------- LOGIN -------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User userReq) {

        User user = userRepository.findByEmail(userReq.getEmail());

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User Not Found!"));
        }

        if (!user.getPassword().equals(userReq.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect Password!"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login Successful",
                "email", user.getEmail(),
                "name", user.getFullName() != null ? user.getFullName() : ""
        ));
    }




    // -----------------------------------------------------------
    //  LOGIN FOR COUNSELOR ONLY (EMAIL + PASSWORD)
    // -----------------------------------------------------------

   
//     LOGIN FOR COUNSELOR ONLY (EMAIL + PASSWORD)

    @PostMapping("/counselor-login")
    public ResponseEntity<?> counselorLogin(@RequestBody User user) {

        String result = userService.loginUser(user.getEmail(), user.getPassword());

        if (result.equals("Login Successful")) {
            return ResponseEntity.ok(Map.of(
                    "role", "counselor",
                    "email", user.getEmail(),
                    "message", "Login Successful"
            ));
        }

        return ResponseEntity.status(400).body(Map.of(
                "error", result
        ));
    }



    // -----------------------------------------------------------
    //  SEND EMAIL OTP FOR STUDENT + FACULTY
    // -----------------------------------------------------------



    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody Map<String, String> req) {

        String email = req.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("Email required");
        }

        email = email.trim().toLowerCase();

        // Check Student
        Student student = studentRepository.findByEmail(email);

        // Check Faculty
        Faculty faculty = facultyRepository.findByEmail(email);

        // Check Counselor (User table)
        User counselor = userRepository.findByEmail(email);

        if (student == null && faculty == null && counselor == null) {
            return ResponseEntity.status(404).body("Email not registered");
        }

        // Generate OTP
        String otp = otpService.generateOtp(email);
        System.out.println("OTP for " + email + " = " + otp);

        // Send Email
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Your Login OTP");
        msg.setText("Your OTP is: " + otp + "\nValid for 1 minute.");
        mailSender.send(msg);

        return ResponseEntity.ok("OTP sent!");
    }





    // -----------------------------------------------------------
    //  VERIFY OTP AND RETURN USER + ROLE
    // -----------------------------------------------------------

    
    // VERIFY OTP AND RETURN USER + ROLE
    

    @PostMapping("/verify-email-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String otp = req.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.status(400).body("Email & OTP required");
        }

        email = email.trim().toLowerCase();

        boolean valid = otpService.validateOtp(email, otp);

        if (!valid) {
            return ResponseEntity.status(400).body("INVALID OR EXPIRED OTP");
        }

        // Student Login
        Student student = studentRepository.findByEmail(email);
        if (student != null) {
            return ResponseEntity.ok(Map.of(
                    "role", "student",
                    "data", student
            ));
        }

        // Faculty Login
        Faculty faculty = facultyRepository.findByEmail(email);
        if (faculty != null) {
            return ResponseEntity.ok(Map.of(
                    "role", "faculty",
                    "data", faculty
            ));
        }

        // Counselor Login (User table)
        User counselor = userRepository.findByEmail(email);
        if (counselor != null) {
            return ResponseEntity.ok(Map.of(
                    "role", "counselor",
                    "data", counselor
            ));
        }

        return ResponseEntity.status(404).body("User not found");
    }
    // CHECK COUNSELOR EMAIL
    @GetMapping("/check/{email}")
    public ResponseEntity<?> checkCounselorEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        return ResponseEntity.ok(Map.of("exists", user != null));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String password = req.get("password");
        String fullName = req.get("fullName");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.status(409).body("Email already exists");
        }

        User u = new User(email, password, fullName);

        // OTP generate
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        u.setOtp(otp);
        u.setOtpExpiry(Instant.now().plusSeconds(300)); // 5 min expiry
        u.setActive(false);

        userRepository.save(u);


        System.out.println("SEND OTP TO ADMIN: " + otp + " (User: " + email + ")");

        return ResponseEntity.ok(Map.of(
                "message", "Signup pending admin approval",
                "userId", u.getId()
        ));
    }



    @PostMapping("/admin/verify-otp")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> req) {

        Long userId = Long.parseLong(req.get("userId"));
        String otp = req.get("otp");

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (user.isActive()) {
            return ResponseEntity.badRequest().body("User already active");
        }

        if (user.getOtp() == null || user.getOtpExpiry().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("OTP expired");
        }

        if (!user.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        // OTP correct → activate user
        user.setActive(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("User activated successfully");
    }


}
