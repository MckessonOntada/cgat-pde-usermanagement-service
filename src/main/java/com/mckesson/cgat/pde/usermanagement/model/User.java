package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

//	@TenantId
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

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id")
	private Role role;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = false;

}
