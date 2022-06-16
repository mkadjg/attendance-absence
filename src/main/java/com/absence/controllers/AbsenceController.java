package com.absence.controllers;

import com.absence.constants.AttendanceTypeConstant;
import com.absence.dto.PresentRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.dto.TimesheetResponseDto;
import com.absence.exceptions.InputValidationException;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.models.Holiday;
import com.absence.repositories.*;
import com.absence.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

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
    LeaveSubmissionRepository leaveSubmissionRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ReportService reportService;

    @Autowired
    HolidayRepository holidayRepository;

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
        newAttendance.setTaskHtml(dto.getTaskHtml());
        newAttendance.setTaskText(dto.getTaskText());
        newAttendance.setLocation(dto.getLocation());
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

    @GetMapping("/is-available/{employeeId}")
    public ResponseEntity<Object> isPresent(@PathVariable String employeeId) {

        Map<String, Object> result = new HashMap<>();

        Date actualDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(actualDate);
        int dayOfWeek =c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            result.put("status", "WEEKEND");
            result.put("message", "Sorry..., This form is not available in weekend! \\n Stay healthy and stay safe");
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(result)
                    .message("Successfully save present data!")
                    .build();
            return ResponseEntity.ok(responseDto);
        }

        Holiday holiday = holidayRepository.findByDate(actualDate).orElse(null);
        if (holiday != null) {
            result.put("status", "HOLIDAY");
            result.put("message", "Sorry..., This form is not available in holiday! \n Stay healthy and stay safe");
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(result)
                    .message("Successfully save present data!")
                    .build();
            return ResponseEntity.ok(responseDto);
        }

        Attendance attendance = attendanceRepository.findByAttendanceDateAndEmployeeId(new Date(), employeeId).orElse(null);
        if (attendance == null) {
            result.put("status", "AVAILABLE");
            result.put("message", "No message available!");
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.OK.toString())
                    .status("success")
                    .data(result)
                    .message("Successfully save present data!")
                    .build();
            return ResponseEntity.ok(responseDto);
        } else {
            if (attendance.getAttendanceType().getAttendanceTypeName().equals("Present")) {
                result.put("status", "ALREADY PRESENT");
                result.put("message", "Sorry..., This form is no more available, You are already submit \n Please check your present history");
                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(result)
                        .message("Successfully save present data!")
                        .build();
                return ResponseEntity.ok(responseDto);
            } else if (attendance.getAttendanceType().getAttendanceTypeName().equals("Sick")) {
                result.put("status", "SICK");
                result.put("message", "Sorry..., This form is not available, You are currently off, \n Get well soon!");
                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(result)
                        .message("Successfully save present data!")
                        .build();
                return ResponseEntity.ok(responseDto);
            } else {
                result.put("status", "LEAVE");
                result.put("message", "Sorry..., This form is not available, You are currently on leave, \n Happy holiday and stay safe!");
                ResponseDto responseDto = ResponseDto.builder()
                        .code(HttpStatus.OK.toString())
                        .status("success")
                        .data(result)
                        .message("Successfully save present data!")
                        .build();
                return ResponseEntity.ok(responseDto);
            }
        }
    }

    @PostMapping("/update/{attendanceId}")
    public ResponseEntity<Object> update(@RequestHeader("user-audit-id") String userAuditId,
                                         @PathVariable("attendanceId") String attendanceId,
                                         @RequestBody PresentRequestDto dto) throws ResourceNotFoundException {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
        if (attendance == null) {
            throw new ResourceNotFoundException("Absence data not found!");
        }

        attendance.setCheckInTime(dto.getCheckInTime());
        attendance.setCheckOutTime(dto.getCheckOutTime());
        attendance.setTaskHtml(dto.getTaskHtml());
        attendance.setTaskText(dto.getTaskText());
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
                .data(attendanceTypeId.equals("all") ? attendanceRepository.findHistoriesAttendance(startDate, endDate, employeeId) : attendanceRepository.findHistoriesAttendance(startDate, endDate, attendanceTypeId, employeeId))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/timesheet/excel")
    public void generate(@RequestParam("employeeId") String employeeId,
                         @RequestParam("month") int month,
                         @RequestParam("year") int year,
                         HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=\"timesheet.xlsx\"");
        OutputStream out = response.getOutputStream();
        reportService.exportExcelTimesheet(month, year, employeeId, out);
        out.flush();
        out.close();
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
