package com.absence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leave_type")
@Entity
public class LeaveType extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "leave_type_id", nullable = false, unique = true)
    private String leaveTypeId;

    @Column(name = "leave_type_name")
    private String leaveTypeName;

    @Column(name = "leave_type_desc")
    private String leaveTypeDesc;

    @Column(name = "default_value")
    private int defaultValue;

}
