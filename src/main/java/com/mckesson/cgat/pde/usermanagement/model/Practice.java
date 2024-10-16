package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Practice extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "practice_sequence")
	@SequenceGenerator(name = "practice_sequence", sequenceName = "practice_sequence", allocationSize = 1)
	private Long id;

	@NotNull(message = "Name cannot be null")
	@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	private String name;

	private String logoUrl;

	@Valid
	@Embedded
	private Name contactPerson;

	@Valid
	@Embedded
	private Address address;

	@Valid
	@Embedded
	private Contact contact;

	@Convert(converter = NumericBooleanConverter.class)
	@JsonIgnore
	@JsonProperty("isActive")
	private Boolean isActive;

}
