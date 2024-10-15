package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String SEQUENCE_GENERATOR_NAME = "ACCOUNT_SEQUENCE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR_NAME)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@NotNull(message = "Name must not be null")
	@NotEmpty
	private String name;

	@NotNull(message = "Dc id must not be null")
	@NotEmpty
	private String dcId;

	@NotNull(message = "Dc name must not be null")
	@NotEmpty
	private String dcName;
	
	private Long practiceId;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive=false;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_account_mapping", // Name of the join table
			joinColumns = @JoinColumn(name = "user_id"), // Foreign key from the current entity (User)
			inverseJoinColumns = @JoinColumn(name = "account_id") // Foreign key from the other entity (Account)
	)
	private Set<User> user;

}
