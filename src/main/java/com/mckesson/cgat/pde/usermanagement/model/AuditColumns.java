package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditColumns implements Serializable {

	private static final long serialVersionUID = 8923669426693111099L;

	@Column(name = "CREATED_AT")
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdAt;

	@Column(name = "CREATED_BY")
	@CreatedBy
	// @Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private String createdBy;

	@Column(name = "UPDATED_AT")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	@LastModifiedBy
	// @Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private String updatedBy;

	@PrePersist
	public void onPersist() {
		/*
		 * CurrentRequestContext loginInfo = (CurrentRequestContext)
		 * SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		 */
		this.createdBy = null;// loginInfo.getUserId();
		this.updatedBy = null;// loginInfo.getUserId();
		this.createdAt = Date.from(Instant.now());
		this.updatedAt = Date.from(Instant.now());
	}

	@PreUpdate
	public void onUpdate() {
		if (this.updatedBy == null) {
			// CurrentRequestContext loginInfo = (CurrentRequestContext)
			// SecurityContextHolder.getContext()
			// .getAuthentication().getPrincipal();
			this.updatedBy = null;// loginInfo.getUserId();
		}
		this.updatedAt = Date.from(Instant.now());
	}

}
