package com.absence.controllers;

import com.absence.dto.DivisionRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Division;
import com.absence.repositories.DivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/division")
public class DivisionController {

    @Autowired
    DivisionRepository divisionRepository;

    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(divisionRepository.findAll())
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody DivisionRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        Division division = new Division();
        division.setDivisionName(dto.getDivisionName());
        division.setDivisionDesc(dto.getDivisionDesc());
        division.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(divisionRepository.save(division))
                .message("Successfully create data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{divisionId}")
    public ResponseEntity<Object> update(@PathVariable("divisionId") String divisionId,
                                         @RequestBody DivisionRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Division division = divisionRepository.findById(divisionId).orElse(null);
        if (division != null) {
            division.setDivisionName(dto.getDivisionName());
            division.setDivisionDesc(dto.getDivisionDesc());
            division.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(divisionRepository.save(division))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @PutMapping("/delete/{divisionId}")
    public ResponseEntity<Object> delete(@PathVariable("divisionId") String divisionId) throws ResourceNotFoundException {
        Division division = divisionRepository.findById(divisionId).orElse(null);
        if (division != null) {
            divisionRepository.delete(division);
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
