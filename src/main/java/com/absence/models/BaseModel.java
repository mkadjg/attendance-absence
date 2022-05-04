package com.absence.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseModel implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    private String updatedBy;

    @PrePersist
    public void prePersist() { this.createdDate = new Date(); }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = new Date();
    }

}
