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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "ACCOUNT_SEQUENCE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR_NAME)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	private Long practiceId;

	@NotNull(message = "Name must not be null")
	@NotEmpty
	private String name;

	@NotNull(message = "Dc id must not be null")
	@NotEmpty
	private String dcId;

	@NotNull(message = "Dc name must not be null")
	@NotEmpty
	private String dcName;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_account_mapping", joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	private Set<User> user;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = false;

}
