package com.absence.services;

import com.absence.models.Attendance;
import com.absence.models.Employee;
import com.absence.models.LeaveSubmission;
import com.absence.payloads.EmailPayload;
import com.absence.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmailService emailService;

    @Override
    public void sendEmailSubmissionToSpv(Employee employee) {
        List<Employee> supervisors = employeeRepository
                .findSupervisorByDivisionId(employee.getJobTitle().getDivision().getDivisionId());

        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", employee.getEmployeeName());
        variables.put("employeeNumber", employee.getEmployeeNumber());
        variables.put("division", employee.getJobTitle().getDivision().getDivisionName());

        supervisors.forEach(supervisor -> {
            String emailBody = emailService.generateHtmlEmailBody("leave-notification.html", variables);
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setSubject("Leave Submission");
            emailPayload.setReceiver(supervisor.getEmployeeEmail());
            emailPayload.setEmailBody(emailBody);
            try {
                emailService.sendEmail(emailPayload);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void sendEmailSubmissionToHrd(Employee employee) {
        List<Employee> hrds = employeeRepository
                .findHRD("fcb90b90-b56b-4a98-b778-83848096cbc6");

        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", employee.getEmployeeName());
        variables.put("employeeNumber", employee.getEmployeeNumber());
        variables.put("division", employee.getJobTitle().getDivision().getDivisionName());

        hrds.forEach(hrd -> {
            String emailBody = emailService.generateHtmlEmailBody("leave-notification.html", variables);
            EmailPayload emailPayload = new EmailPayload();
            emailPayload.setSubject("Leave Submission");
            emailPayload.setReceiver(hrd.getEmployeeEmail());
            emailPayload.setEmailBody(emailBody);
            try {
                emailService.sendEmail(emailPayload);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void sendEmailApprove(LeaveSubmission leaveSubmission, String role) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("startDate", leaveSubmission.getStartDate());
        variables.put("endDate", leaveSubmission.getEndDate());
        variables.put("role", role);

        String emailBody = emailService.generateHtmlEmailBody("leave-notification-approved.html", variables);
        EmailPayload emailPayload = new EmailPayload();
        emailPayload.setSubject("Leave Submission Approved");
        emailPayload.setReceiver(leaveSubmission.getEmployee().getEmployeeEmail());
        emailPayload.setEmailBody(emailBody);
        emailService.sendEmail(emailPayload);
    }

    @Override
    public void sendEmailReject(LeaveSubmission leaveSubmission, String role) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("startDate", leaveSubmission.getStartDate());
        variables.put("endDate", leaveSubmission.getEndDate());
        variables.put("role", role);

        String emailBody = emailService.generateHtmlEmailBody("leave-notification-rejected.html", variables);
        EmailPayload emailPayload = new EmailPayload();
        emailPayload.setSubject("Leave Submission Approved");
        emailPayload.setReceiver(leaveSubmission.getEmployee().getEmployeeEmail());
        emailPayload.setEmailBody(emailBody);
        emailService.sendEmail(emailPayload);
    }
}
