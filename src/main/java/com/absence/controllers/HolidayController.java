package com.absence.controllers;

import com.absence.dto.HolidayRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Holiday;
import com.absence.repositories.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/holiday")
public class HolidayController {

    @Autowired
    HolidayRepository holidayRepository;

    @GetMapping("/find-all")
    public ResponseEntity<Object> findAll() {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(holidayRepository.findAll())
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody HolidayRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) {
        Holiday holiday = new Holiday();
        holiday.setHolidayName(dto.getHolidayName());
        holiday.setHolidayDesc(dto.getHolidayDesc());
        holiday.setHolidayDate(dto.getHolidayDate());
        holiday.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(holidayRepository.save(holiday))
                .message("Successfully create data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{holidayId}")
    public ResponseEntity<Object> update(@PathVariable("holidayId") String holidayId,
                                         @RequestBody HolidayRequestDto dto,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Holiday holiday = holidayRepository.findById(holidayId).orElse(null);
        if (holiday != null) {
            holiday.setHolidayName(dto.getHolidayName());
            holiday.setHolidayDesc(dto.getHolidayDesc());
            holiday.setHolidayDate(dto.getHolidayDate());
            holiday.setUpdatedBy(userAuditId);

            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(holidayRepository.save(holiday))
                    .message("Successfully update data!")
                    .build();

            return ResponseEntity.ok(responseDto);
        } else {
            throw new ResourceNotFoundException("Data not found!");
        }
    }

    @PutMapping("/delete/{holidayId}")
    public ResponseEntity<Object> delete(@PathVariable("holidayId") String holidayId) throws ResourceNotFoundException {
        Holiday holiday = holidayRepository.findById(holidayId).orElse(null);
        if (holiday != null) {
            holidayRepository.delete(holiday);
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
