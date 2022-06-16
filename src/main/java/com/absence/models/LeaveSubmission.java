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
@Table(name = "leave_submission")
@Entity
public class LeaveSubmission extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "leave_submission_id", nullable = false, unique = true)
    private String leaveSubmissionId;

    @Type(type = "text")
    @Column(name = "description_html")
    private String descriptionHtml;

    @Type(type = "text")
    @Column(name = "description_text")
    private String descriptionText;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "total_days_off")
    private Integer totalDaysOff;

    @Column(name = "sub_partner_id")
    private String subPartnerId;

    @Column(name = "supervisor_id")
    private String supervisorId;

    @Column(name = "hrd_id")
    private String hrdId;

    @Type(type = "text")
    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_status_id", referencedColumnName = "submission_status_id")
    private SubmissionStatus submissionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", referencedColumnName = "leave_type_id")
    private LeaveType leaveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private Employee employee;

}
