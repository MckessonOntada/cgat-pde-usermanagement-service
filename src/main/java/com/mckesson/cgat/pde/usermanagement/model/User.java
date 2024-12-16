package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_account_mapping", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	@JsonIgnore
	private Set<Account> accounts;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Set<UserPreference> preferences = new HashSet<>();

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id")
	private Role role;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@Enumerated(EnumType.STRING)
	@Column(name = "provisioned_in_okta_status")
	private UserProvisionedInOktaStatus provisionedInOktaStatus = UserProvisionedInOktaStatus.PENDING;

}
