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

	@Column(name = "is_prescription_details_required", nullable = false)
	private Boolean isPrescriptionDetailsRequired;

	@Column(name = "quantity")
	private Long quantity;

	@Column(name = "days_supply")
	private Long daysSupply;

	@Column(name = "refills")
	private Long refills;

	@Column(name = "instructions")
	private String instructions;

	@PrePersist
	@PreUpdate
	private void validatePrescriptionDetails() {
		if (Boolean.TRUE.equals(isPrescriptionDetailsRequired)) {
			if (quantity == null || daysSupply == null || refills == null || instructions == null) {
				throw new IllegalStateException("Prescription details are required but one or more fields are null");
			}
		}
		else {
			if (quantity != null || daysSupply != null || refills != null || instructions != null) {
				throw new IllegalStateException("Prescription details must be null when not required");
			}
		}
	}

}
