package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "on_behalf_aud", schema = "audit")
public class OnBehalfAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;

	private String userName;

	private String userEmail;

	private Long practiceId;

	private String impersonatedUserId;

	private String impersonatedUserName;

	private String impersonatedUserEmail;

	private LocalDateTime timestamp;

}
