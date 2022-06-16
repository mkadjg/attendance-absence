package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimesheetResponseDto {
    private String attendanceId;
    private Date attendanceDate;
    private String taskHtml;
    private String taskText;
    private String location;
    private Date checkInTime;
    private Date checkOutTime;
    private String projectId;
    private String projectName;
    private String attendanceTypeId;
    private String attendanceTypeName;
    private String descriptionHtml;
    private String descriptionName;
}
