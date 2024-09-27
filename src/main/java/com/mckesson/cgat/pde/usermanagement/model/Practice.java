package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;
import org.hibernate.type.NumericBooleanConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Where(clause = "IS_ACTIVE = 1")
public class Practice extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@UuidGenerator
	private UUID id;

	private String name;

	private String logoUrl;

	private String firstName;

	private String middleName;

	private String lastName;

	private String address1;

	private String address2;

	private String city;

	private String state;

	private String zip;

	private String email;

	private String phone;

	private String fax;

	@Convert(converter = NumericBooleanConverter.class)
	@JsonIgnore
	private Boolean isActive;
}
