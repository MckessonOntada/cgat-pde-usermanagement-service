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
@Table(name = "sp_therapy_config", schema = "catalog")
public class SPTherapyConfigs implements Serializable {

	@Id
	@GeneratedValue(generator = "sp_therapy_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sp_therapy_config_id_seq", sequenceName = "catalog.sp_therapy_config_id_seq",
			allocationSize = 1)
	private Long id;

	@Column(name = "sp_product_id", nullable = false, unique = true)
	private String spProductId;

	@Enumerated(EnumType.STRING)
	@Column(name = "patient_identifier_requirement")
	private ConfigFieldRequirement patientIdentifierRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "mrn__requirement")
	private ConfigFieldRequirement mrnRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "coi_id_requirement")
	private ConfigFieldRequirement coiIdRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "fulfillment_channel")
	private SPFullfilmentChannel fulfillmentChannel;

	@Column(name = "is_prescription_details_required")
	private Boolean isPrescriptionDetailsRequired;

	@Column(name = "quantity")
	private Long quantity;

	@Column(name = "days_supply")
	private Long daysSupply;

	@Column(name = "refills")
	private Long refills;

	@Column(name = "instructions")
	private String instructions;

	@Column(name = "consent_policy")
	private String consentPolicy;

	@PrePersist
	@PreUpdate
	private void validatePrescriptionDetails() {
		if (Boolean.TRUE.equals(isPrescriptionDetailsRequired)) {
			if (quantity == null || daysSupply == null || refills == null || instructions == null
					|| consentPolicy == null) {
				throw new IllegalStateException("Prescription details are required but one or more fields are null");
			}
		}
		else {
			if (quantity != null || daysSupply != null || refills != null || instructions != null
					|| consentPolicy != null) {
				throw new IllegalStateException("Prescription details must be null when not required");
			}
		}
	}

}
