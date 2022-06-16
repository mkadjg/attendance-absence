package com.absence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SickResponseDto {
    private String sickId;
    private Date startDate;
    private Date endDate;
    private String descriptionText;
    private String descriptionHtml;
    private String subPartnerId;
    private String subPartnerName;
    private byte[] document;
}
