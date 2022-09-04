package com.absence.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailPayload {
    String subject;
    String receiver;
    String cc;
    String bcc;
    String emailBody;
}
