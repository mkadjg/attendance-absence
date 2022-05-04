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
@Table(name = "leave_detail")
@Entity
public class LeaveDetail extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "leave_detail_id", nullable = false, unique = true)
    private String leaveDetailId;

    @Type(type = "text")
    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "total_days_off")
    private Integer totalDaysOff;

    @ManyToOne
    @JoinColumn(name = "attendance_id", referencedColumnName = "attendance_id")
    private Attendance attendance;

    @ManyToOne
    @JoinColumn(name = "sub_partner_id", referencedColumnName = "employee_id")
    private Employee subPartner;

}
