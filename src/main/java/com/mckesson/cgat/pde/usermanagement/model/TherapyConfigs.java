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
@Table(name = "therapy_config", schema = "catalog")
public class TherapyConfigs implements Serializable {

	@Id
	@GeneratedValue(generator = "therapy_config_id_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "therapy_config_id_seq", sequenceName = "catalog.therapy_config_id_seq",
			allocationSize = 1)
	private Long id;

	@Size(max = 18)
	@Column(name = "ndc_number", nullable = false, unique = true)
	private String ndcNumber;

	@Column(name = "is_patient_information_required", nullable = false)
	private Boolean isPatientInformationRequired;

	@Enumerated(EnumType.STRING)
	@Column(name = "patient_identifier_requirement", nullable = false)
	private ConfigFieldRequirement patientIdentifierRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "mrn__requirement", nullable = false)
	private ConfigFieldRequirement mrnRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "coi_id_requirement", nullable = false)
	private ConfigFieldRequirement coiIdRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "po_number_requirement", nullable = false)
	private ConfigFieldRequirement poNumberRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "date_of_administration_requirement", nullable = false)
	private ConfigFieldRequirement dateOfAdministrationRequirement;

	@Enumerated(EnumType.STRING)
	@Column(name = "date_of_apheresis_requirement", nullable = false)
	private ConfigFieldRequirement dateOfApheresisRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "ship_to_po_requirement", nullable = false)
    private ConfigFieldRequirement shipToPORequirement;

    @Enumerated(EnumType.STRING)
	private ChannelType channelType;

	@Column(name = "min_quantity", nullable = false, columnDefinition = "SMALLINT DEFAULT 1")
	private Integer minQuantity = 1;

	@Column(name = "max_quantity", nullable = false, columnDefinition = "SMALLINT DEFAULT 99")
	private Integer maxQuantity = 99;

}
