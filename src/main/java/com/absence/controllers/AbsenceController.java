package com.absence.controllers;

import com.absence.constants.AttendanceTypeConstant;
import com.absence.dto.PresentRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.InputValidationException;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.models.Sick;
import com.absence.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RestController
@RequestMapping("/attendance")
public class AbsenceController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AttendanceTypeRepository attendanceTypeRepository;

    @Autowired
    SickRepository sickRepository;

    @Autowired
    ProjectRepository projectRepository;

    @PostMapping("/present")
    public ResponseEntity<Object> present(@RequestHeader("user-audit-id") String userAuditId,
            @RequestBody PresentRequestDto dto) throws ResourceNotFoundException, InputValidationException {
        Employee employee = employeeRepository.findById(dto.getEmployeeId()).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        if (dto.getAttendanceDate().compareTo(new Date()) > 0) {
            throw new InputValidationException("Absence date must be today or less!");
        }

        Attendance attendance = attendanceRepository.findByAttendanceDateAndEmployeeId(dto.getAttendanceDate(), dto.getEmployeeId()).orElse(null);
        if (attendance != null) {
            throw new InputValidationException("You are already present!");
        }

        Attendance newAttendance = new Attendance();
        newAttendance.setAttendanceDate(dto.getAttendanceDate());
        newAttendance.setCheckInTime(dto.getCheckInTime());
        newAttendance.setCheckOutTime(dto.getCheckOutTime());
        newAttendance.setTask(dto.getTask());
        newAttendance.setEmployee(employee);
        newAttendance.setProject(projectRepository.findById(dto.getProjectId()).orElse(null));
        newAttendance.setAttendanceType(attendanceTypeRepository.findByAttendanceTypeName(AttendanceTypeConstant.PRESENT));
        newAttendance.setCreatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(attendanceRepository.save(newAttendance))
                .message("Successfully save present data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/update/{attendanceId}")
    public ResponseEntity<Object> update(@RequestHeader("user-audit-id") String userAuditId, @PathVariable("attendanceId") String attendanceId, @RequestBody PresentRequestDto dto) throws ResourceNotFoundException {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
        if (attendance == null) {
            throw new ResourceNotFoundException("Absence data not found!");
        }

        attendance.setCheckInTime(dto.getCheckInTime());
        attendance.setCheckOutTime(dto.getCheckOutTime());
        attendance.setTask(dto.getTask());
        attendance.setUpdatedBy(userAuditId);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(attendanceRepository.save(attendance))
                .message("Successfully update absence data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/present-history")
    public ResponseEntity<Object> presentHistory(@RequestParam("employeeId") String employeeId,
                                            @RequestParam("month") int month,
                                            @RequestParam("year") int year) {
        Date startDate = getStartDate(month, year);
        Date endDate = getEndDate(month, year);
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(attendanceRepository.findPresentHistories(startDate, endDate, employeeId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/timesheet")
    public ResponseEntity<Object> timesheet(@RequestParam("employeeId") String employeeId,
                                            @RequestParam("attendanceTypeId") String attendanceTypeId,
                                            @RequestParam("month") int month,
                                            @RequestParam("year") int year) {
        Date startDate = getStartDate(month, year);
        Date endDate = getEndDate(month, year);
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(attendanceTypeId == null ? attendanceRepository.findHistoriesAttendance(startDate, endDate, employeeId) : attendanceRepository.findHistoriesAttendance(startDate, endDate, attendanceTypeId, employeeId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    private Date getStartDate(int month, int year) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date getEndDate(int month, int year) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        return calendar.getTime();
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

}
