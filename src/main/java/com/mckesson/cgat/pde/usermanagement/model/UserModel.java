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

	private String firstName;

	private String middleName;

	private String lastName;

	private String suffix;

	private String address1;

	private String address2;

	private String city;

	private String state;

	private String zip;

	@Email
	private String email;

	private String phoneNumber;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive;

}
