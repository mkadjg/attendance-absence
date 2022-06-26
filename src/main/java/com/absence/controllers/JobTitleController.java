package com.absence.controllers;

import com.absence.dto.JobTitleRequestDto;
import com.absence.dto.JobTitleResponseDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.JobTitle;
import com.absence.repositories.DivisionRepository;
import com.absence.repositories.JobTitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-title")
public class JobTitleController {

    @Autowired
    JobTitleRepository jobTitleRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(jobTitleRepository.findAll()
                        .stream()
                        .map(this::toJobTitleResponseDto))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody JobTitleRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        JobTitle jobTitle = new JobTitle();
        jobTitle.setJobTitleName(dto.getJobTitleName());
        jobTitle.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
        jobTitle.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(jobTitleRepository.save(jobTitle))
                .message("Successfully create data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{jobTitleId}")
    public ResponseEntity<Object> update(@PathVariable("jobTitleId") String jobTitleId,
                                         @RequestBody JobTitleRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        JobTitle jobTitle = jobTitleRepository.findById(jobTitleId).orElse(null);
        if (jobTitle != null) {
            jobTitle.setJobTitleName(dto.getJobTitleName());
            jobTitle.setDivision(divisionRepository.findById(dto.getDivisionId()).orElse(null));
            jobTitle.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(jobTitleRepository.save(jobTitle))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @DeleteMapping("/delete/{jobTitleId}")
    public ResponseEntity<Object> delete(@PathVariable("jobTitleId") String jobTitleId) throws ResourceNotFoundException {
        JobTitle jobTitle = jobTitleRepository.findById(jobTitleId).orElse(null);
        if (jobTitle != null) {
            jobTitleRepository.delete(jobTitle);
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

    @GetMapping("/find-all/{divisionId}")
    public ResponseEntity<Object> findAll(@PathVariable String divisionId) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(jobTitleRepository.findAllByDivisionId(divisionId))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    private JobTitleResponseDto toJobTitleResponseDto(JobTitle jobTitle) {
        JobTitleResponseDto jobTitleResponseDto = new JobTitleResponseDto();
        jobTitleResponseDto.setJobTitleId(jobTitle.getJobTitleId());
        jobTitleResponseDto.setJobTitleName(jobTitle.getJobTitleName());
        jobTitleResponseDto.setDivisionId(jobTitle.getDivision().getDivisionId());
        jobTitleResponseDto.setDivisionName(jobTitle.getDivision().getDivisionName());
        return jobTitleResponseDto;
    }

}
