package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestDto {
    private String description;
    private Date startDate;
    private Date endDate;
    private String employeeId;
    private int totalDaysOff;
    private String subPartnerId;
}
