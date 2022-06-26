package com.absence.controllers;

import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.models.EmployeeLeave;
import com.absence.repositories.AttendanceRepository;
import com.absence.repositories.EmployeeLeaveRepository;
import com.absence.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeLeaveRepository employeeLeaveRepository;

    @Autowired
    AttendanceRepository attendanceRepository;
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    @GetMapping("/leave-info/{employeeId}")
    public ResponseEntity<Object> leaveInfo(@PathVariable String employeeId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findByEmployeeId(employeeId);
        Map<String, Object> result = new HashMap<>();

        int available = 0;
        int used = 0;

        for (EmployeeLeave employeeLeave : employeeLeaves) {
            available+=employeeLeave.getAvailable();
            used+=employeeLeave.getUsed();
        }

        result.put("data", employeeLeaves);
        result.put("available", available);
        result.put("used", used);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(result)
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/people-off/{employeeId}")
    public ResponseEntity<Object> peopleOff(@PathVariable String employeeId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Date today = new Date();

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(attendanceRepository.findOffByDate(today))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/your-summary/{employeeId}")
    public ResponseEntity<Object> yourSummary(@PathVariable String employeeId,
                                              @RequestParam String type) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        List<Attendance> attendances;
        Date startDate;
        Date endDate;

        int year = new Date().toInstant().atZone(ZoneId.systemDefault()).getYear();
        int month = (new Date().toInstant().atZone(ZoneId.systemDefault()).getMonth().getValue() - 1);
        switch (type) {
            case MONTH: {
                startDate = getStartDate(month, year, Calendar.DAY_OF_MONTH);
                endDate = getEndDate(month, year, Calendar.DAY_OF_MONTH);
                attendances = attendanceRepository.findHistoriesAttendance(startDate, endDate, employeeId);
                break;
            }
            case YEAR: {
                startDate = getStartDate(0, year, Calendar.DAY_OF_YEAR);
                endDate = getEndDate(0, year, Calendar.DAY_OF_YEAR);
                attendances = attendanceRepository.findHistoriesAttendance(startDate, endDate, employeeId);
                break;
            }
            default: {
                Date today = new Date();
                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.SERVICE_UNAVAILABLE.toString())
                        .status("success")
                        .data(attendanceRepository.findOffByDate(today))
                        .message("Successfully fetch data!")
                        .build();
                return ResponseEntity.status(503).body(responseDto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        int hours = 0;
        int days = 0;
        int sicks = 0;
        int leaves = 0;

        for (Attendance attendance : attendances) {
            switch (attendance.getAttendanceType().getAttendanceTypeName()) {
                case "Present": {
                    long hourDiffTime = attendance.getCheckOutTime().getTime() - attendance.getCheckInTime().getTime();
                    long hourDiff = (hourDiffTime / (1000 * 60 * 60)) % 24;
                    hours+=hourDiff;
                    days++;
                    break;
                }
                case "Sick": {
                    sicks++;
                    break;
                }
                case "Leaves": {
                    leaves++;
                    break;
                }
                default: {}
            }
        }

        result.put("hours", hours);
        result.put("days", days);
        result.put("sicks", sicks);
        result.put("leaves", leaves);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(result)
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    private Date getStartDate(int month, int year, int type) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMinimum(type));
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date getEndDate(int month, int year, int type) {
        Calendar calendar = getCalendarForNow();
        calendar.set(year, month,
                calendar.getActualMaximum(type));
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
