package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	@Pattern(regexp = "^[A-Za-z]{2}\\d{7}$",
			message = "DEA Number must start with 2 letters followed by 7 digits (e.g., AB1234567)",
			groups = BasicInfo.class)
	@Column(name = "dea", length = 9, nullable = false)
	private String dea;

	@NotBlank(message = "NPI Number is required", groups = BasicInfo.class)
	@Size(min = 10, max = 10, message = "NPI must be exactly 10 digits", groups = BasicInfo.class)
	@Pattern(regexp = "\\d{10}", message = "NPI must contain only numeric digits", groups = BasicInfo.class)
	@Column(name = "npi", length = 10, nullable = false)
	private String npi;

	@NotBlank(message = "Phone Number is required", groups = BasicInfo.class)
	@Pattern(regexp = "^[1-9][0-9]{9}$",
			message = "Phone number must be exactly 10 digits, start with 1-9, and must not have any spaces or characters.",
			groups = BasicInfo.class)
	@NotBlank(message = "Phone number is required", groups = BasicInfo.class)
	private String phone;

	@Valid
	@Embedded
	private Address address;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "account_provider_mapping", joinColumns = @JoinColumn(name = "provider_id"),
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	@JsonIgnore
	private Set<Account> accounts;

	@NotBlank(message = "Please enter the TIN", groups = BasicInfo.class)
	@Size(min = 9, max = 9, message = "TIN must be exactly 9 digits", groups = BasicInfo.class)
	@Pattern(regexp = "\\d{9}", message = "TIN must only contain numeric values", groups = BasicInfo.class)
	@Column(name = "tin", length = 9, nullable = false)
	private String tin;

	@NotBlank(message = "Please enter the Medicaid Number", groups = BasicInfo.class)
	@Size(min = 6, max = 6, message = "Medicaid Number must be exactly 6 digits", groups = BasicInfo.class)
	@Pattern(regexp = "\\d{6}", message = "Medicaid Number must contain only numeric Values", groups = BasicInfo.class)
	@Column(name = "medicaid_number", length = 6, nullable = false)
	private String medicaidNumber;

	private String licenseState;

	@NotNull(message = "Please enter your Email ID", groups = BasicInfo.class)
	@Size(min = 1, max = 50, message = "Email ID should not exceed 50 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
			message = "Please enter a valid email address (e.g., name@example.com).", groups = BasicInfo.class)
	@Column(name = "email", nullable = false)
	private String email;

	@Size(min = 1, max = 20, message = "Fax number should not exceed 20 digits.", groups = BasicInfo.class)
	@Pattern(regexp = "\\d+", message = "Only numeric values are allowed in the Fax number.", groups = BasicInfo.class)
	@Column(name = "fax")
	private String fax;

}
