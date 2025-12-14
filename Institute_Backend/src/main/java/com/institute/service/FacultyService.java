package com.institute.service;

import com.institute.model.Faculty;
import com.institute.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepo;


    public Faculty login(String email, String birthDate) {

        email = email.trim().toLowerCase();
        Faculty f = facultyRepo.findByEmail(email);


        if (f == null)
            throw new RuntimeException("Faculty not found!");

        if (!f.getBirthDate().equals(birthDate))
            throw new RuntimeException("Invalid Birth Date!");

        return f;
    }

    // ADD FACULTY
    public Faculty addFaculty(Faculty faculty) {

        if (facultyRepo.existsByEmail(faculty.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        if (facultyRepo.existsByMobile(faculty.getMobile())) {
            throw new RuntimeException("Mobile already exists!");
        }

        //  Get last faculty code
        String lastCode = facultyRepo.findLastFacultyCode();

        int nextNumber = 1; // default

        if (lastCode != null) {
            nextNumber = Integer.parseInt(lastCode.replace("FAC-", "")) + 1;
        }

        //  Generate new unique code
        String newCode = String.format("FAC-%03d", nextNumber);
        faculty.setFacultyCode(newCode);

        return facultyRepo.save(faculty);
    }


    // GET BY CODE
    public Faculty getFacultyByCode(String code) {
        return facultyRepo.findByFacultyCode(code);
    }

    // UPDATE FACULTY
    public Faculty updateFaculty(String code, Faculty data) {
        Faculty existing = facultyRepo.findByFacultyCode(code);

        if (existing == null)
            throw new RuntimeException("Faculty not found!");

        existing.setName(data.getName());
        existing.setEmail(data.getEmail());
        existing.setMobile(data.getMobile());

        return facultyRepo.save(existing);
    }

    // DELETE
    public String deleteByCode(String code) {
        Faculty fac = facultyRepo.findByFacultyCode(code);
        if (fac == null)
            throw new RuntimeException("Faculty not found!");

        facultyRepo.delete(fac);
        return "Deleted successfully";
    }

    // GET ALL
    public List<Faculty> getAll() {
        return facultyRepo.findAll();
    }

    public Faculty getFacultyByEmail(String email) {
        return facultyRepo.findByEmailIgnoreCase(email.trim());
    }


    public boolean getFacultyByMobile(String mobile) {
        return facultyRepo.existsByMobile(mobile);
    }


}
