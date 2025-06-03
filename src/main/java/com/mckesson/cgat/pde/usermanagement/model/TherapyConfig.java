package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_config", schema = "catalog")
public class TherapyConfig implements Serializable {

	@Id
	@GeneratedValue(generator = "material_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "material_config_id_seq", sequenceName = "catalog.material_config_id_seq",
			allocationSize = 1)
	private Long id;

	@Size(max = 18)
	@Column(name = "ndc_number", nullable = false, unique = true)
	private String ndcNumber;

	@Column(name = "is_coi_id_required", nullable = false)
	private Boolean isCoiIdRequired;

	@Column(name = "is_patient_information_required", nullable = false)
	private Boolean isPatientInformationRequired;

	@Enumerated(EnumType.STRING)
	private ChannelType channelType;

}
