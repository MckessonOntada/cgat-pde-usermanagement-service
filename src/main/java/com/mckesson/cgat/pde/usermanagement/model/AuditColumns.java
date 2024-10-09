package com.mckesson.cgat.pde.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.jbat.auth.module.util.CurrentRequestContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditColumns implements Serializable {

    private static final long serialVersionUID = 8923669426693111099L;

    @Column(name = "CREATED_DATE")
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdDate;

    @Column(name = "CREATED_BY")
    @CreatedBy
//    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private String createdBy;

    @Column(name = "UPDATED_DATE")
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    @LastModifiedBy
//    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private String updatedBy;

//    @PrePersist
//    public void onPersist() {
//        CurrentRequestContext loginInfo = (CurrentRequestContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        this.createdBy = loginInfo.getUserId();
//        this.updatedBy = loginInfo.getUserId();
//        this.createdDate = Date.from(Instant.now());
//        this.updatedDate = Date.from(Instant.now());
//    }
//
//    @PreUpdate
//    public void onUpdate() {
//        if(this.updatedBy == null) {
//            CurrentRequestContext loginInfo = (CurrentRequestContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            this.updatedBy = loginInfo.getUserId();
//        }
//        this.updatedDate = Date.from(Instant.now());
//    }
}
