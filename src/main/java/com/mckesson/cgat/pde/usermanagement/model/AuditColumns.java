package com.mckesson.cgat.pde.usermanagement.model;

import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

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
	private String createdBy;

	@Column(name = "CREATED_BY_IMPERSONATOR")
	@CreatedBy
	private String createdByImpersonator;

	@Column(name = "UPDATED_AT")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	@LastModifiedBy
	private String updatedBy;

	@Column(name = "UPDATED_BY_IMPERSONATOR")
	@CreatedBy
	private String updatedByImpersonator;

	@PrePersist
	public void onPersist() {
		LoggedInUserDetails loggedInUserDetails = UserSecurityUtils.getLoggedInUser();
		if (loggedInUserDetails != null) {
			this.createdBy = loggedInUserDetails.getId();
			this.updatedBy = loggedInUserDetails.getId();

			LoggedInUserDetails impersonatedByUser = loggedInUserDetails.getImpersonatedByUser();
			if (impersonatedByUser != null) {
				this.createdByImpersonator = impersonatedByUser.getId();
				this.updatedByImpersonator = impersonatedByUser.getId();
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

			LoggedInUserDetails impersonatedByUser = loggedInUserDetails.getImpersonatedByUser();
			if (impersonatedByUser != null) {
				this.updatedByImpersonator = impersonatedByUser.getId();
			}
		}
		this.updatedAt = Date.from(Instant.now());
	}

}
