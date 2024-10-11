package com.mckesson.cgat.pde.usermanagement.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class HiloSequenceGenerator extends AuditColumns {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_hilo_generator")
	@GenericGenerator(name = "sequence_hilo_generator", strategy = "sequence",
			parameters = { @Parameter(name = "sequence_name", value = "hilo_seqeunce"),
					@Parameter(name = "initial_value", value = "1"), @Parameter(name = "increment_size", value = "1") })
	private Long id;

}
