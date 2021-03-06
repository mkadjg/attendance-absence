package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentRequestDto {
    private Date attendanceDate;
    private String taskHtml;
    private String taskText;
    private Date checkInTime;
    private Date checkOutTime;
    private String employeeId;
    private String projectId;
    private String location;
}
