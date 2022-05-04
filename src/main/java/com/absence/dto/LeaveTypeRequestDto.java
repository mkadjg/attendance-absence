package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTypeRequestDto {
    private String leaveTypeName;
    private String leaveTypeDesc;
    private Date leaveTypeDate;
    private int defaultValue;
}
