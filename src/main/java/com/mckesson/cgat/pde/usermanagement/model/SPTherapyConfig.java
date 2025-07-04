package com.mckesson.cgat.pde.usermanagement.model;

import jakarta.persistence.*;
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
@Table(name = "sp_material_config", schema = "catalog")
public class SPTherapyConfig implements Serializable {

	@Id
	@GeneratedValue(generator = "sp_material_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sp_material_config_id_seq", sequenceName = "catalog.sp_material_config_id_seq",
			allocationSize = 1)
	private Long id;

	@Column(name = "sp_product_id", nullable = false, unique = true)
	private String spProductId;

	@Column(name = "is_coi_id_required", nullable = false)
	private Boolean isCoiIdRequired;

	@Column(name = "is_patient_identifier_required", nullable = false)
	private Boolean isPatientIdentifierRequired;

	@Column(name = "is_mrn_required", nullable = false)
	private Boolean isMRNRequired;

}
