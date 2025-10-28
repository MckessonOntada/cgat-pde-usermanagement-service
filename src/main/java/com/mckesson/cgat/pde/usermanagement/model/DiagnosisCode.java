package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "diagnosis_code", schema = "usermanagement")
public class DiagnosisCode extends AuditColumns implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diagnosis_code_sequence")
	@SequenceGenerator(name = "diagnosis_code_sequence", schema = "usermanagement",
			sequenceName = "diagnosis_code_sequence", allocationSize = 1)
	private Long id;

	@Size(max = 20)
	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "long_description")
	private String longDescription;

	@ManyToMany(mappedBy = "diagnosisCodes")
	private Set<SPTherapyConfigs> spTherapyConfigs;

}
