package com.absence.controllers;

import com.absence.constants.AttendanceTypeConstant;
import com.absence.constants.SubmissionStatusConstants;
import com.absence.dto.LeaveRequestDto;
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
    LeaveDetailRepository leaveDetailRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AttendanceTypeRepository attendanceTypeRepository;

    @GetMapping("/find-all-employee")
    public ResponseEntity<Object> findAllEmployee(@RequestParam("employeeId") String employeeId,
                                            @RequestParam("submissionStatusId") String submissionStatusId,
                                            @RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(submissionStatusId == null ? leaveSubmissionRepository.findByEmployeeAndYear(employeeId, year) :
                        leaveSubmissionRepository.findByEmployeeAndYearAndSubmissionStatus(employeeId, year, submissionStatusId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/find-all-supervisor")
    public ResponseEntity<Object> findAllSupervisor(@RequestParam("submissionStatusId") String submissionStatusId,
                                            @RequestParam("divisionId") String divisionId,
                                            @RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(submissionStatusId == null ? leaveSubmissionRepository.findByDivisionAndYear(divisionId, year) :
                        leaveSubmissionRepository.findByEmployeeAndYearAndSubmissionStatus(divisionId, year, submissionStatusId))
                .message("Successfully fetch data!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/find-all-hrd")
    public ResponseEntity<Object> findAllHrd(@RequestParam("submissionStatusId") String submissionStatusId,
                                            @RequestParam("year") int year) {
        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(submissionStatusId == null ? leaveSubmissionRepository.findByYear(year) :
                        leaveSubmissionRepository.findByYearAndSubmissionStatus(year, submissionStatusId))
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

        LeaveSubmission leaveSubmission = new LeaveSubmission();
        leaveSubmission.setDescription(dto.getDescription());
        leaveSubmission.setStartDate(dto.getStartDate());
        leaveSubmission.setEndDate(dto.getEndDate());
        leaveSubmission.setEmployee(employee);
        leaveSubmission.setSubPartnerId(dto.getSubPartnerId());
        leaveSubmission.setCreatedBy(userAuditId);
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.WAITING_APPROVAL));

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
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.APPROVE_BY_SPV));

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

        LeaveDetail leaveDetail = new LeaveDetail();
        leaveDetail.setDescription(leaveSubmission.getDescription());
        leaveDetail.setStartDate(leaveSubmission.getStartDate());
        leaveDetail.setEndDate(leaveSubmission.getEndDate());
        leaveDetail.setEmployee(leaveSubmission.getEmployee());
        leaveDetail.setSubPartnerId(leaveSubmission.getSubPartnerId());
        leaveDetail.setCreatedBy(userAuditId);
        LeaveDetail result = leaveDetailRepository.save(leaveDetail);

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
            attendance.setLeaveDetail(result);
            attendance.setCreatedBy(userAuditId);
            attendance.setAttendanceType(attendanceTypeRepository.findByAttendanceTypeName(AttendanceTypeConstant.SICK));
            attendanceRepository.save(attendance);

            c.add(Calendar.DATE, 1);
            actualDate = c.getTime();
        }

        leaveSubmission.setTotalDaysOff(totalDaysOff);
        leaveDetailRepository.save(leaveDetail);

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

    @PostMapping("/cancel/{leaveSubmissionId}")
    public ResponseEntity<Object> cancel(@RequestBody RejectRequestDto dto, @PathVariable("leaveSubmissionId") String leaveSubmissionId, @RequestHeader("user-audit-id") String userAuditId) throws ResourceNotFoundException {
        LeaveSubmission leaveSubmission = leaveSubmissionRepository.findById(leaveSubmissionId).orElse(null);
        if (leaveSubmission == null) {
            throw new ResourceNotFoundException("Leave Submission not found!");
        }

        leaveSubmission.setUpdatedBy(userAuditId);
        leaveSubmission.setReason(dto.getReason());
        leaveSubmission.setSubmissionStatus(submissionStatusRepository.findBySubmissionStatusName(SubmissionStatusConstants.CANCELLED));

        ResponseDto responseDto = ResponseDto.builder()
                .code(HttpStatus.OK.toString())
                .status("success")
                .data(leaveSubmissionRepository.save(leaveSubmission))
                .message("Successfully cancel leave submission!")
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
