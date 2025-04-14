package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@MappedSuperclass
@Data
public class Auditable {

	@Column(name = "updated_at")
	@UpdateTimestamp
	private ZonedDateTime updatedAt;

	@Column(name = "created_by")
	private String createdBy = "SYSTEM";

	@Column(name = "created_at")
	@CreationTimestamp
	private ZonedDateTime createdAt;

	@Column(name = "updated_by")
	private String updatedBy = "SYSTEM";

}
