package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_config", schema = "catalog")
public class TherapyConfig {

	@Id
	@Size(max = 18)
	@Column(name = "material_number", nullable = false, unique = true)
	private String materialNumber;

	@Column(name = "coi_id", nullable = false)
	private Boolean coiId;

	@Column(name = "patient_information", nullable = false)
	private Boolean patientInformation;

}
