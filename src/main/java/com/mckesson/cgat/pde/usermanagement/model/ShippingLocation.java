package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class ShippingLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "SHIPPING_ACCOUNT_NUMBER_SEQUENCE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_GENERATOR_NAME)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	@Column(name = "shipping_account_number")
	private Long shippingAccountNumber;

	@Column(name = "tenant_id")
	private Long practiceId;

	@NotEmpty(message = "Location Name is required.")
	@Size(min = 2, max = 100, message = "Location Name must be between 2 and 100 characters")
	@Column(name = "shipping_location_name")
	private String shippingLocationName;

	@NotEmpty(message = "Address is required.")
	@Column(name = "address_1")
	private String address1;

	@Column(name = "address_2")
	private String address2;

	@NotEmpty(message = "City is required.")
	@Column(name = "city")
	private String city;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@NotEmpty(message = "Zip is required.")
	@Column(name = "zip")
	private String zip;

	@NotEmpty(message = "State is required.")
	@Column(name = "state")
	private String state;

	@Column(name = "account_id")
	private Long accountId;

}