package com.absence.controllers;

import com.absence.dto.EmployeeRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Employee;
import com.absence.repositories.DivisionRepository;
import com.absence.repositories.EmployeeRepository;
import com.absence.repositories.JobTitleRepository;
import com.absence.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.sql.Blob;
import java.sql.SQLException;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    JobTitleRepository jobTitleRepository;

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

    @GetMapping("/find-by-user-id/{userId}")
    public ResponseEntity<Object> findByUserId(@PathVariable String userId) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeRepository.findByUserId(userId))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody EmployeeRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmployeeAddress(dto.getEmployeeAddress());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setEmployeeBirthdate(dto.getEmployeeBirthdate());
        employee.setEmployeeBirthplace(dto.getEmployeeBirthplace());
        employee.setEmployeeEmail(dto.getEmployeeEmail());
        employee.setEmployeePhoneNumber(dto.getEmployeePhoneNumber());
        employee.setEmployeeGender(dto.getEmployeeGender());
        employee.setIsSupervisor(dto.getIsSupervisor());
        employee.setUserId(dto.getUserId());
        employee.setJobTitle(jobTitleRepository.findById(dto.getJobTitleId()).orElse(null));
        employee.setUpdatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeRepository.save(employee))
                .message("Successfully update data!")
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
            employee.setIsSupervisor(dto.getIsSupervisor());
            employee.setJobTitle(jobTitleRepository.findById(dto.getJobTitleId()).orElse(null));
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

//    @PutMapping("/de-active/{employeeId}")
//    public ResponseEntity<Object> deActive(@PathVariable("employeeId") String employeeId,
//                                         @RequestBody EmployeeRequestDto dto,
//                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
//        Employee employee = employeeRepository.findById(employeeId).orElse(null);
//        if (employee != null) {
//            employee.getUsers().setStatus(false);
//            usersRepository.save(employee.getUsers());
//
//            ResponseDto responseDto = ResponseDto.builder()
//                    .code(HttpStatus.OK.toString())
//                    .status("success")
//                    .data(employeeRepository.save(employee))
//                    .message("Successfully de active employee!")
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//        } else {
//            throw new ResourceNotFoundException("Data not found!");
//        }
//    }

    @PostMapping("/upload-photo/{employeeId}")
    public ResponseEntity<Object> uploadPhoto(
            @RequestParam("photo") MultipartFile file,
            @PathVariable("employeeId") String employeeId,
            @RequestHeader("user-audit-id") String userAuditId) throws IOException, ResourceNotFoundException {
        byte[] fileByte = file.getBytes();
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }
        employee.setEmployeePhoto(fileByte);
        employee.setUpdatedBy(userAuditId);
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(employeeRepository.save(employee))
                .message("Successfully upload employee photo!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/partner")
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
