package com.absence.controllers;

import com.absence.dto.ResponseDto;
import com.absence.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner")
public class PartnerController {

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/")
    public ResponseEntity<Object> partner(@RequestParam String employeeId, @RequestParam String divisionId) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeRepository.findAllPartner(employeeId, divisionId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
