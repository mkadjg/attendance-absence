package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponseDto {
    private String leaveSubmissionId;
    private Date startDate;
    private Date endDate;
    private String employeeName;
    private String descriptionHtml;
    private String descriptionText;
    private String subPartnerId;
    private String subPartnerName;
    private String reason;
    private String submissionStatusId;
    private String submissionStatusName;
    private String leaveTypeId;
    private String leaveTypeName;
}
