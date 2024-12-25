package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
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
	private Date createdAt;

	@Column(name = "CREATED_BY")
	@CreatedBy
	@JsonIgnore
	private String createdBy;

	@Column(name = "CREATED_BY_IMPERSONATE")
	@CreatedBy
	@JsonIgnore
	private String createdByImpersonate;

	@Column(name = "UPDATED_AT")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	@LastModifiedBy
	@JsonIgnore
	private String updatedBy;

	@Column(name = "UPDATED_BY_IMPERSONATE")
	@CreatedBy
	@JsonIgnore
	private String updatedByImpersonate;

	@PrePersist
	public void onPersist() {
		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		if (loggedInUserDetails != null) {
			this.createdBy = loggedInUserDetails.getId();
			this.updatedBy = loggedInUserDetails.getId();

			LoggedInUserDetails impersonatedUser = loggedInUserDetails.getImpersonatedUser();
			if (impersonatedUser != null) {
				this.createdByImpersonate = impersonatedUser.getId();
				this.updatedByImpersonate = impersonatedUser.getId();
			}

		}

		this.createdAt = Date.from(Instant.now());
		this.updatedAt = Date.from(Instant.now());
	}

	@PreUpdate
	public void onUpdate() {
		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		if (loggedInUserDetails != null) {
			this.updatedBy = loggedInUserDetails.getId();
		}
		this.updatedAt = Date.from(Instant.now());
	}

}
