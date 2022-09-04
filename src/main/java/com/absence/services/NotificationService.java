package com.absence.services;

import com.absence.models.Employee;
import com.absence.models.LeaveSubmission;

public interface NotificationService {
    void sendEmailSubmissionToSpv(Employee employee) throws Exception;
    void sendEmailSubmissionToHrd(Employee employee) throws Exception;
    void sendEmailApprove(LeaveSubmission leaveSubmission, String role) throws Exception;
    void sendEmailReject(LeaveSubmission leaveSubmission, String role) throws Exception;
}
