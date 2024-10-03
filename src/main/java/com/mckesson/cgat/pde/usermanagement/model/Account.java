package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.type.NumericBooleanConverter;

import com.google.auto.value.AutoValue.Builder;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
public class Account extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String SEQUENCE_GENERATOR_NAME = "SEQ_ACCOUNT_ID";

	@Id
	@GeneratedValue(generator = SEQUENCE_GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME, allocationSize = 1)
	private String id;

	private String name;
	private String dcId;
	private String dcName;

	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive;

}
