package com.absence.controllers;

import com.absence.dto.ResponseDto;
import com.absence.models.EmployeeLeave;
import com.absence.repositories.EmployeeLeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leave")
public class EmployeeLeaveController {

    @Autowired
    EmployeeLeaveRepository employeeLeaveRepository;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Object> employeeLeave(@PathVariable String employeeId) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeLeaveRepository.findByEmployeeId(employeeId))
                .message("Successfully approve leave submission!")
                .build();
        return ResponseEntity.ok(responseDto);
    }
}
