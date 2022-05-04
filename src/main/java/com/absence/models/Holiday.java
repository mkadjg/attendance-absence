package com.absence.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "holiday")
@Entity
public class Holiday extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "holiday_id", nullable = false, unique = true)
    private String holidayId;

    @Column(name = "holiday_name")
    private String holidayName;

    @Column(name = "holiday_desc")
    private String holidayDesc;

    @Temporal(TemporalType.DATE)
    @Column(name = "holiday_date")
    private Date holidayDate;

}
