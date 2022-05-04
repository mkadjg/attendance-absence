package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {
    private String employeeNumber;
    private String employeeName;
    private String employeeAddress;
    private Date employeeBirthdate;
    private String employeeBirthplace;
    private String employeeEmail;
    private String employeePhoneNumber;
    private int employeeGender;
    private String divisionId;
    private boolean isSupervisor;
}
