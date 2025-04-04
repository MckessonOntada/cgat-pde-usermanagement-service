package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.type.NumericBooleanConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
public class Provider extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "PROVIDER_SEQUENCE";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private Long id;

	// @TenantId
	private Long practiceId;

	@Valid
	@Embedded
	private Name name;

	@NotBlank(message = "DEA Number is required", groups = BasicInfo.class)
	@Size(min = 9, max = 9, message = "DEA Number must be exactly 9 characters", groups = BasicInfo.class)
	@Column(name = "dea", length = 9, nullable = false)
	private String dea;

	@NotBlank(message = "NPI Number is required")
	@Size(min = 10, max = 10, message = "NPI must be exactly 10 digits")
	@Pattern(regexp = "\\d{10}", message = "NPI must contain only numeric digits")
	@Column(name = "npi", length = 10, nullable = false)
	private String npi;

	@NotEmpty(message = "Phone Number is required", groups = BasicInfo.class)
	@Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits", groups = BasicInfo.class)
	private String phone;

	@Valid
	@Embedded
	private Address address;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

}
