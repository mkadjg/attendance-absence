package com.absence.dto;

import com.absence.models.Division;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDto {
    private String projectId;
    private String projectName;
    private String projectDesc;
    private String divisionId;
}
