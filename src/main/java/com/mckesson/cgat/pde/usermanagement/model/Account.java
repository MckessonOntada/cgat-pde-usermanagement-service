package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;

import com.google.auto.value.AutoValue.Builder;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@SuppressWarnings("deprecation")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hilo_sequence_generator")
	@GenericGenerator(name = "hilo_sequence_generator", strategy = "sequence", parameters = {
			@Parameter(name = "sequence_name", value = "hilo_seqeunce"),
			@Parameter(name = "initial_value", value = "1"), @Parameter(name = "increment_size", value = "3"),
			@Parameter(name = "optimizer", value = "hilo") })
	private String id;

	@NotNull(message = "Name must not be null")
	@NotEmpty
	private String name;

	@NotNull(message = "Dc id must not be null")
	@NotEmpty
	private String dcId;

	@NotNull(message = "Dc name must not be null")
	@NotEmpty
	private String dcName;

	@NotEmpty
	@Convert(converter = NumericBooleanConverter.class)
	private Boolean isActive;

}
