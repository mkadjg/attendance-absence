package com.absence.controllers;

import com.absence.dto.EmployeeRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Employee;
import com.absence.repositories.DivisionRepository;
import com.absence.repositories.EmployeeRepository;
import com.absence.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @Autowired
    UsersRepository usersRepository;


    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeRepository.findAll())
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<Object> update(@PathVariable("employeeId") String employeeId,
                                         @RequestBody EmployeeRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            employee.setEmployeeName(dto.getEmployeeName());
            employee.setEmployeeAddress(dto.getEmployeeAddress());
            employee.setEmployeeNumber(dto.getEmployeeNumber());
            employee.setEmployeeBirthdate(dto.getEmployeeBirthdate());
            employee.setEmployeeBirthplace(dto.getEmployeeBirthplace());
            employee.setEmployeeEmail(dto.getEmployeeEmail());
            employee.setEmployeePhoneNumber(dto.getEmployeePhoneNumber());
            employee.setEmployeeGender(dto.getEmployeeGender());
            employee.setSupervisor(dto.isSupervisor());
            employee.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
            employee.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(employeeRepository.save(employee))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @PutMapping("/de-active/{employeeId}")
    public ResponseEntity<Object> deActive(@PathVariable("employeeId") String employeeId,
                                         @RequestBody EmployeeRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            employee.getUsers().setStatus(false);
            usersRepository.save(employee.getUsers());

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(employeeRepository.save(employee))
                    .message("Successfully de active employee!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }
}
