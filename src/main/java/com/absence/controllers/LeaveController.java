package com.absence.controllers;

import com.absence.constants.AttendanceTypeConstant;
import com.absence.constants.SubmissionStatusConstants;
import com.absence.dto.LeaveRequestDto;
import com.absence.dto.LeaveResponseDto;
import com.absence.dto.RejectRequestDto;
import com.absence.dto.ResponseDto;
import com.absence.exceptions.ResourceNotFoundException;
import com.absence.models.*;
import com.absence.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    LeaveSubmissionRepository leaveSubmissionRepository;

    @Autowired
    SubmissionStatusRepository submissionStatusRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    SickRepository sickRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AttendanceTypeRepository attendanceTypeRepository;

    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    @Autowired
    EmployeeLeaveRepository employeeLeaveRepository;

    @GetMapping("/find-all-employee")
    public ResponseEntity<Object> findAllEmployee(@RequestParam("employeeId") String employeeId,
                                            @RequestParam("submissionStatusId") String submissionStatusId,
                                            @RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(Objects.equals(submissionStatusId, "all") ?
                        leaveSubmissionRepository.findByEmployeeAndYear(employeeId, year)
                                .stream()
                                .map(this::toLeaveResponseDto):
                        leaveSubmissionRepository.findByEmployeeAndYearAndSubmissionStatus(employeeId, year, submissionStatusId)
                                .stream()
                                .map(this::toLeaveResponseDto))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/find-all-supervisor")
    public ResponseEntity<Object> findAllSupervisor(@RequestParam("divisionId") String divisionId,
                                            @RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.findByDivisionAndYearForSupervisor(divisionId, year)
                            .stream()
                            .map(this::toLeaveResponseDto))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/find-all-hrd")
    public ResponseEntity<Object> findAllHrd(@RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.findByYearForHrd(year)
                                .stream()
                                .map(this::toLeaveResponseDto))
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/validate-total-days")
    public ResponseEntity<Object> validateTotalDays(@RequestParam("startDate") Date startDate,
                                                    @RequestParam("endDate") Date endDate) {
        int totalDaysOff = 0;
        Date actualDate = startDate;
        while (actualDate.compareTo(endDate) < 1) {
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

            totalDaysOff++;
            c.add(Calendar.DATE, 1);
            actualDate = c.getTime();
        }

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(totalDaysOff)
                .message("Successfully fetch data!")
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/submission")
    public ResponseEntity<Object> submission(@RequestBody LeaveRequestDto dto, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(dto.getEmployeeId()).orElse(null);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found!");
        }

        Date actualDate = dto.getStartDate();
        boolean isTaken = false;
        while (actualDate.compareTo(dto.getEndDate()) < 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(actualDate);

            Attendance leaveAttendance = attendanceRepository.findByAttendanceDateAndEmployeeId(actualDate, employee.getEmployeeId()).orElse(null);
            if (leaveAttendance != null) {
                isTaken = true;
                break;
            }

            c.add(Calendar.DATE, 1);
            actualDate = c.getTime();
        }

        if (isTaken) {
            ResponseDto responseDto = ResponseDto.builder()
                    .code(HttpStatus.BAD_REQUEST.toString())
                    .status("warning")
                    .data(null)
                    .message("Date Already Taken!")
                    .build();
            return ResponseEntity.ok(responseDto);
        }

        EmployeeLeave employeeLeave = employeeLeaveRepository.findByLeaveTypeAndEmployeeId(dto.getLeaveTypeId(),
                employee.getEmployeeId());

        employeeLeave.setAvailable((employeeLeave.getAvailable() == null ? 0 : employeeLeave.getAvailable()) - dto.getTotalDaysOff());
        employeeLeave.setUsed((employeeLeave.getUsed() == null ? 0 : employeeLeave.getUsed()) + dto.getTotalDaysOff());

        LeaveSubmission leaveSubmission = new LeaveSubmission();
        leaveSubmission.setDescriptionText(dto.getDescriptionText());
        leaveSubmission.setDescriptionHtml(dto.getDescriptionHtml());
        leaveSubmission.setStartDate(dto.getStartDate());
        leaveSubmission.setEndDate(dto.getEndDate());
        leaveSubmission.setEmployee(employee);
        leaveSubmission.setSubPartnerId(dto.getSubPartnerId());
        leaveSubmission.setTotalDaysOff(dto.getTotalDaysOff());
        leaveSubmission.setCreatedBy(userAuditId);
        leaveSubmission.setLeaveType(leaveTypeRepository.findById(dto.getLeaveTypeId()).orElse(null));
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.WAITING_APPROVAL_SPV));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully save leave submission data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/approve/supervisor/{leaveSubmissionId}")
    public ResponseEntity<Object> approveSpv(@PathVariable("leaveSubmissionId") String leaveSubmissionId, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setSupervisorId(userAuditId);
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.WAITING_APPROVAL_HRD));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully approve leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/reject/supervisor/{leaveSubmissionId}")
    public ResponseEntity<Object> rejectSpv(@RequestBody RejectRequestDto dto, @PathVariable("leaveSubmissionId") String leaveSubmissionId, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setSupervisorId(userAuditId);
        leaveSubmission.setReason(dto.getReason());
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.REJECT_BY_SPV));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully reject leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/approve/hrd/{leaveSubmissionId}")
    public ResponseEntity<Object> approveHrd(@PathVariable("leaveSubmissionId") String leaveSubmissionId, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setHrdId(userAuditId);
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.APPROVED));

        Sick sick = new Sick();
        sick.setDescriptionHtml(leaveSubmission.getDescriptionHtml());
        sick.setDescriptionText(leaveSubmission.getDescriptionText());
        sick.setStartDate(leaveSubmission.getStartDate());
        sick.setEndDate(leaveSubmission.getEndDate());
        sick.setEmployee(leaveSubmission.getEmployee());
        sick.setSubPartnerId(leaveSubmission.getSubPartnerId());
        sick.setCreatedBy(userAuditId);
        Sick result = sickRepository.save(sick);

        int totalDaysOff = 0;
        Date actualDate = leaveSubmission.getStartDate();
        while (actualDate.compareTo(leaveSubmission.getEndDate()) < 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(actualDate);
            int dayOfWeek =c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                continue;
            }

            Holiday holiday = holidayRepository.findByDate(actualDate).orElse(null);
            if (holiday != null) {
                continue;
            }

            totalDaysOff++;
            Attendance attendance = new Attendance();
            attendance.setAttendanceDate(actualDate);
            attendance.setSick(result);
            attendance.setCreatedBy(userAuditId);
            attendance.setAttendanceType(attendanceTypeRepository.findByAttendanceTypeName(AttendanceTypeConstant.SICK));
            attendanceRepository.save(attendance);

            c.add(Calendar.DATE, 1);
            actualDate = c.getTime();
        }

        leaveSubmission.setTotalDaysOff(totalDaysOff);
        sickRepository.save(sick);

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully approve leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/reject/hrd/{leaveSubmissionId}")
    public ResponseEntity<Object> rejectHrd(@RequestBody RejectRequestDto dto, @PathVariable("leaveSubmissionId") String leaveSubmissionId, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setHrdId(userAuditId);
        leaveSubmission.setReason(dto.getReason());
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.REJECT_BY_HRD));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully reject leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/cancel/{leaveSubmissionId}")
    public ResponseEntity<Object> cancel(@PathVariable("leaveSubmissionId") String leaveSubmissionId,
                                         @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.CANCELLED));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully cancel leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public LeaveResponseDto toLeaveResponseDto(LeaveSubmission leaveSubmission) {
        LeaveResponseDto leaveResponseDto = new LeaveResponseDto();
        leaveResponseDto.setLeaveSubmissionId(leaveSubmission.getLeaveSubmissionId());
        leaveResponseDto.setReason(leaveSubmission.getReason());
        leaveResponseDto.setDescriptionHtml(leaveSubmission.getDescriptionHtml());
        leaveResponseDto.setDescriptionText(leaveSubmission.getDescriptionText());
        leaveResponseDto.setStartDate(leaveSubmission.getStartDate());
        leaveResponseDto.setEndDate(leaveSubmission.getEndDate());
        leaveResponseDto.setSubPartnerId(leaveResponseDto.getSubPartnerId());
        leaveResponseDto.setSubPartnerName(Objects.requireNonNull(employeeRepository.findById(leaveSubmission.getSubPartnerId()).orElse(null)).getEmployeeName());
        leaveResponseDto.setSubmissionStatusId(leaveSubmission.getSubmissionStatus().getSubmissionStatusId());
        leaveResponseDto.setSubmissionStatusName(leaveSubmission.getSubmissionStatus().getSubmissionStatusName());
        return leaveResponseDto;
    }

}
