package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayRequestDto {
    private String holidayName;
    private String holidayDesc;
    private Date holidayDate;
}
