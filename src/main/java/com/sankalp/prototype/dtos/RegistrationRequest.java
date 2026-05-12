package com.sankalp.prototype.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
}