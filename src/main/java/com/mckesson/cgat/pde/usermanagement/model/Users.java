package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class Users extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "USER_SEQUENCE";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)

	private Long id;

	@NotNull(message = "First name must not be null")
	@NotEmpty
	private String firstName;

	private String middleName;

	@NotNull(message = "Last name must not be null")
	@NotEmpty
	private String lastName;

	@NotNull(message = "Suffix must not be null")
	@NotEmpty
	private String suffix;

	@Email
	@NotNull(message = "Email must not be null")
	@NotEmpty
	private String email;

	@NotNull(message = "Phone number must not be null")
	@NotEmpty
	private String phoneNumber;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = false;

	@Embedded
	private Address address;

	@ManyToMany
	@JoinTable(name = "user_account", // Name of the join table
			joinColumns = @JoinColumn(name = "user_id"), // Foreign key from the current
															// entity (User)
			inverseJoinColumns = @JoinColumn(name = "account_id") // Foreign key from the
																	// other entity
																	// (Account)
	)

	@Transient
	private Set<Account> account;

	// @Column(name = "is_syncwithchartabstraction")
	// private Integer isSyncWithChartAbstraction;

}
