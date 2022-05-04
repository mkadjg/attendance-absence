package com.absence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance")
@Entity
public class Attendance extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "attendance_id", nullable = false, unique = true)
    private String attendanceId;

    @Temporal(TemporalType.DATE)
    @Column(name = "attendance_date")
    private Date attendanceDate;

    @Type(type = "text")
    @Column(name = "task")
    private String task;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_in_time")
    private Date checkInTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_out_time")
    private Date checkOutTime;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "attendance_type_id", referencedColumnName = "attendance_type_id")
    private AttendanceType attendanceType;

}
