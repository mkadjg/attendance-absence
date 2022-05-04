package com.absence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_login_history")
@Entity
public class UserLoginHistory {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "user_login_history_id", nullable = false, unique = true)
    private String userLoginHistoryId;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "ip_public_address")
    private String ipPublicAddress;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private boolean status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_date")
    private Date loginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "logout_date")
    private Date logoutDate;

}
