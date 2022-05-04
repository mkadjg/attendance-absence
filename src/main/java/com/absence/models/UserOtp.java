package com.absence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_otp")
@Entity
public class UserOtp extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "user_otp_id", nullable = false, unique = true)
    private UUID userOtpId = UUID.randomUUID();

    @Column(name = "otp_number")
    private String otpNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "otp_date")
    private Date otpDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until")
    private Date validUntil;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Users users;

}
