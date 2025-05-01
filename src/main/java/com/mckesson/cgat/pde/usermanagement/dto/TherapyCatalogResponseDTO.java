package com.mckesson.cgat.pde.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.determineDropshipIndicator;

public interface TherapyCatalogResponseDTO {

	String getId();

	String getTherapyId();

	String getTherapyDescription();

	String getNdc();

	String getUdi();

	String getStrength();

	String getDosage();

	String getSellingUnitCode();

	String getVendorName();

	String getUpcCode();

	default String getDropshipIndicator() {
		return determineDropshipIndicator(getItemCategoryGroup());
	}

	String getStatus();

	String getBaseUnitOfMeasure();

	String getCgatTherapyType();

	String getSalesOrganization();

	String getProductLevelPricing();

	String getMinimumOrderQuantity();

	@JsonIgnore
	String getItemCategoryGroup();

	String getChannelType();

}
