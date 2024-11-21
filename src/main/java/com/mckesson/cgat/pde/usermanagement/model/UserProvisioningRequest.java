package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.type.NumericBooleanConverter;

import com.google.auto.value.AutoValue.Builder;

import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProvisioningRequest extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "USER_SEQUENCE";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	@NotNull(message = "PracticeId is required.")
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

	@Size(max = 5000, message = "Comment size max upto 5000 characters")
	private String comment;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = false;

	@Enumerated(EnumType.STRING)
	private UserProvisioningStaus status = UserProvisioningStaus.PENDING;

}
