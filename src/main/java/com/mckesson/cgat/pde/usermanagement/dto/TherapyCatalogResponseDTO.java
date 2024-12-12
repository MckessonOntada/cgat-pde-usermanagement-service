package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class TherapyCatalogResponseDTO {

	private String id;

	private String therapyId;

	private String therapyDescription;

	private String ndc;

	private String udi;

	private String strength;

	private String dosage;

	private String sellingUnitCode;

	private String vendorName;

	private String upcCode;

	private String dropshipIndicator;

	private String status;

	private String baseUnitOfMeasure;

	private String cgatIdentifier;

	private String salesOrganization;

	private String cgatType;

	private String productLevelPricing;

	private String minimumOrderQuantity;

}
