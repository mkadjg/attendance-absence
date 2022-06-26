package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTitleRequestDto {
    private String jobTitleId;
    private String jobTitleName;
    private String divisionId;
}
