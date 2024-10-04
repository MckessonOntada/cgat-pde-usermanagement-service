package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
public class UserModel extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "SEQ_USER_ID";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private String id;

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

	@NotNull(message = "address1 must not be null")
	@NotEmpty
	private String address1;

	private String address2;

	@NotNull(message = "City must not be null")
	@NotEmpty
	private String city;

	@NotNull(message = "State must not be null")
	@NotEmpty
	private String state;

	@NotNull(message = "Zip must not be null")
	@NotEmpty
	private String zip;

	@Email
	@NotNull(message = "email must not be null")
	@NotEmpty
	private String email;

	@NotNull(message = "Phone number must not be null")
	@NotEmpty
	private String phoneNumber;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive;

}
