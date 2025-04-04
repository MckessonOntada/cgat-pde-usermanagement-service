package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
	@Column(name = "id")
	private Long id;

	@Column(name = "practice_id")
	private Long practiceId;

	@NotEmpty(message = "Location Name is required.")
	@Column(name = "name")
	private String name;

	@Valid
	@Embedded
	private Address address;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive = true;

	@Column(name = "account_id")
	private Long accountId;

}