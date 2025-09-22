package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
@AuditOverride(forClass = AuditColumns.class)
public class User extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "USER_SEQUENCE";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	// @TenantId
	private Long practiceId;

	@Valid
	@Embedded
	private Name name;

	@Valid
	@Embedded
	private Contact contact;

	@Valid
	@Embedded
	private Address address;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "user_permission_mapping", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "permission")
	private Set<Permission> permissions;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_account_mapping", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	@JsonIgnore
	@Audited(targetAuditMode = NOT_AUDITED)
	private Set<Account> accounts;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@JsonIgnore
	@NotAudited
	private Set<UserPreference> preferences = new HashSet<>();

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id")
	@NotAudited
	private Role role;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@Enumerated(EnumType.STRING)
	@Column(name = "provisioned_in_okta_status")
	private UserProvisionedInOktaStatus provisionedInOktaStatus = UserProvisionedInOktaStatus.PENDING;

	@Column(name = "last_login")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime lastLogin;

	@Column(name = "is_phone_verified")
	@NotAudited
	private Boolean isPhoneVerified;

}
