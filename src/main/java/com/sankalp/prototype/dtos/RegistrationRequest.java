package com.sankalp.prototype.dtos;

import java.time.LocalDate;

public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String reference;
    private String emailId;
    private String cardType;
    private String cardNumber;
    private String mobileNo;
    private String positionApplied;
    private String qualification;
    private String specialization;
    private String collegeName;
    private Integer roleId;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getPositionApplied() { return positionApplied; }
    public void setPositionApplied(String positionApplied) { this.positionApplied = positionApplied; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
}
