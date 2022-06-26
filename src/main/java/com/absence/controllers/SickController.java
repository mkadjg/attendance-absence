package com.absence.controllers;

import com.absence.constants.AttendanceTypeConstant;
import com.absence.dto.ResponseDto;
import com.absence.dto.SickRequestDto;
import com.absence.dto.SickResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.models.Holiday;
import com.absence.models.Sick;
import com.absence.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/attendance")
public class SickController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AttendanceTypeRepository attendanceTypeRepository;

    @Autowired
    SickRepository sickRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @PostMapping("/sick")
    public ResponseEntity<Object> sick(@RequestHeader("user-audit-id") String userAuditId, @RequestBody SickRequestDto dto) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(dto.getEmployeeId()).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Sick sick = new Sick();
        sick.setDescriptionText(dto.getDescriptionText());
        sick.setDescriptionHtml(dto.getDescriptionHtml());
        sick.setStartDate(dto.getStartDate());
        sick.setEndDate(dto.getEndDate());
        sick.setEmployee(employee);
        sick.setSubPartnerId(dto.getSubPartnerId());
        sick.setCreatedBy(userAuditId);
        long diffInMillis = Math.abs(dto.getEndDate().getTime() - dto.getStartDate().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        sick.setTotalDaysOff((int) diff);
        Sick result = sickRepository.save(sick);

        Date actualDate = dto.getStartDate();
        while (actualDate.compareTo(dto.getEndDate()) < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(actualDate);
            int dayOfWeek =c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                c.add(Calendar.DATE, 1);
                actualDate = c.getTime();
                continue;
            }

            Holiday holiday = holidayRepository.findByDate(actualDate).orElse(null);
            if (holiday != null) {
                c.add(Calendar.DATE, 1);
                actualDate = c.getTime();
                continue;
            }

            Attendance leaveAttendance = attendanceRepository.findByAttendanceDateAndEmployeeId(actualDate, employee.getEmployeeId()).orElse(null);
            if (leaveAttendance != null) {
                c.add(Calendar.DATE, 1);
                actualDate = c.getTime();
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setAttendanceDate(actualDate);
            attendance.setSick(result);
            attendance.setEmployee(employee);
            attendance.setCreatedBy(userAuditId);
            attendance.setAttendanceType(attendanceTypeRepository.findByAttendanceTypeName(AttendanceTypeConstant.SICK));
            attendanceRepository.save(attendance);

            c.add(Calendar.DATE, 1);
            actualDate = c.getTime();
        }

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(result)
                .message("Successfully save leave data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/upload-document/{sickId}")
    public ResponseEntity<Object> uploadDocument(@RequestHeader("user-audit-id") String userAuditId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @PathVariable("sickId") String sickId) throws IOException, ResourceNotFoundException {
        byte[] fileByte = file.getBytes();
        Sick sick = sickRepository.findById(sickId).orElse(null);
        if (sick == null) {
            throw new ResourceNotFoundException("Leave or submission not found!!");
        }
        sick.setDocument(fileByte);
        sick.setUpdatedBy(userAuditId);
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(sickRepository.save(sick))
                .message("Successfully upload document!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/sick-history")
    public ResponseEntity<Object> presentHistory(@RequestParam("employeeId") String employeeId,
                                                 @RequestParam("month") int month,
                                                 @RequestParam("year") int year) {
        Date startDate = getStartDate(month, year);
        Date endDate = getEndDate(month, year);
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(sickRepository.findSickHistories(startDate, endDate, employeeId)
                        .stream()
                        .map(this::toSickResponseDto))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    private SickResponseDto toSickResponseDto(Sick sick) {
        SickResponseDto sickResponseDto = new SickResponseDto();
        sickResponseDto.setSickId(sick.getSickId());
        sickResponseDto.setStartDate(sick.getStartDate());
        sickResponseDto.setEndDate(sick.getEndDate());
        sickResponseDto.setDescriptionHtml(sick.getDescriptionHtml());
        sickResponseDto.setDescriptionText(sick.getDescriptionText());
        sickResponseDto.setSubPartnerId(sick.getSubPartnerId());
        sickResponseDto.setSubPartnerName(Objects.requireNonNull(employeeRepository.findById(sick.getSubPartnerId()).orElse(null)).getEmployeeName());
        sickResponseDto.setDocument(sick.getDocument());
        return sickResponseDto;
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
