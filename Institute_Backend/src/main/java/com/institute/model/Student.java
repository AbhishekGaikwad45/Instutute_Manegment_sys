package com.institute.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String studentId;   // Auto generated ID

    private String admissionDate;
    private String name;
    private String fatherName;
    private String addressLine1;
    private String addressLine2;
    private String nativePlace;
    private String state;
    private String mobile;
    private String parentContact;
    private String email;
    private String birthDate;   // LOGIN password/date etc.
    private String qualification;
    private String passOutYear;
    private String anyOtherCertification;
    private String courseEnrolledFor;
    private String totalFees;
    private String downPayment;
    private boolean active = true;

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getPassOutYear() { return passOutYear; }
    public void setPassOutYear(String passOutYear) { this.passOutYear = passOutYear; }

    public String getAnyOtherCertification() { return anyOtherCertification; }
    public void setAnyOtherCertification(String anyOtherCertification) { this.anyOtherCertification = anyOtherCertification; }

    public String getCourseEnrolledFor() { return courseEnrolledFor; }
    public void setCourseEnrolledFor(String courseEnrolledFor) { this.courseEnrolledFor = courseEnrolledFor; }

    public String getTotalFees() { return totalFees; }
    public void setTotalFees(String totalFees) { this.totalFees = totalFees; }

    public String getDownPayment() { return downPayment; }
    public void setDownPayment(String downPayment) { this.downPayment = downPayment; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
