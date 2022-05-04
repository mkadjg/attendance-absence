package com.absence.controllers;

import com.absence.dto.LeaveTypeRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.LeaveType;
import com.absence.repositories.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-type")
public class LeaveTypeController {

    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveTypeRepository.findAll())
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody LeaveTypeRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        LeaveType leaveType = new LeaveType();
        leaveType.setLeaveTypeName(dto.getLeaveTypeName());
        leaveType.setLeaveTypeDesc(dto.getLeaveTypeDesc());
        leaveType.setDefaultValue(dto.getDefaultValue());
        leaveType.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveTypeRepository.save(leaveType))
                .message("Successfully create data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{leaveTypeId}")
    public ResponseEntity<Object> update(@PathVariable("leaveTypeId") String leaveTypeId,
                                         @RequestBody LeaveTypeRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId).orElse(null);
        if (leaveType != null) {
            leaveType.setLeaveTypeName(dto.getLeaveTypeName());
            leaveType.setLeaveTypeDesc(dto.getLeaveTypeDesc());
            leaveType.setDefaultValue(dto.getDefaultValue());
            leaveType.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(leaveTypeRepository.save(leaveType))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @PutMapping("/delete/{leaveTypeId}")
    public ResponseEntity<Object> delete(@PathVariable("leaveTypeId") String leaveTypeId) throws ResourceNotFoundException {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId).orElse(null);
        if (leaveType != null) {
            leaveTypeRepository.delete(leaveType);
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(null)
                    .message("Successfully delete data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

}
