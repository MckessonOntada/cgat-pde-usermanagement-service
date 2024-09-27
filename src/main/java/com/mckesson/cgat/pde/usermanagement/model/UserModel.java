package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name = "user_model")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@UuidGenerator
	private String id;

	
	private String firstName;

	private String middleName;

	
	private String lastName;

	
	private String suffix;

	private String addressFirst;

	private String addressSecond;

	
	private String city;

	
	private String state;

	
	private String zip;

	@Email
	private String emailId;


	private String phoneNumber;
	
	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive;

}
