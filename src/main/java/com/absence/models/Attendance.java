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
    @Column(name = "task_html")
    private String taskHtml;

    @Type(type = "text")
    @Column(name = "task_text")
    private String taskText;

    @Column(name = "location")
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_in_time")
    private Date checkInTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_out_time")
    private Date checkOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_type_id", referencedColumnName = "attendance_type_id")
    private AttendanceType attendanceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sick_id", referencedColumnName = "sick_id")
    private Sick sick;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_submission_id", referencedColumnName = "leave_submission_id")
    private LeaveSubmission leaveSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

}
