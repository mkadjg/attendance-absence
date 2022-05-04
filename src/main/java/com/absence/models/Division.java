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
@Table(name = "division")
@Entity
public class Division extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "division_id", nullable = false, unique = true)
    private String divisionId;

    @Column(name = "division_name")
    private String divisionName;

    @Column(name = "division_desc")
    private String divisionDesc;

}
