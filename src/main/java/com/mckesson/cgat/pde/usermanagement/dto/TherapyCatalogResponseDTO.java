package com.mckesson.cgat.pde.usermanagement.dto;

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

	String getDropshipIndicator();

	String getStatus();

	String getBaseUnitOfMeasure();

	String getCgatIdentifier();

	String getSalesOrganization();

	String getCgatType();

	String getProductLevelPricing();

	String getMinimumOrderQuantity();

}
