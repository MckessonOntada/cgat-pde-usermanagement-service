package com.mckesson.cgat.pde.usermanagement.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TherapyResponseDto {

	private Long id;

	public String tenantId;

	private String materialNumber;

	private String ndcNumber;

	private String strength;

	private String dosage;

	private String materialDescription;

	private String ndcItemDescription;

	private String sellingUnitCode;

	private String vendorName;

	private String upcCode;

	private String rxdaCode;

	private String dropshipIndicator;

	private String itemStatus;

	private String baseUnitOfMeasure;

	private String therCatgy;

	private String minOrdrQuantity;

	private String drugForm;

	private String additionalDescriptor;

	private String color;

	private String flavor;

	private String routeDescription;

	private String matlPackSizeMultiplier;

	private String shape;

	private String orangeBookCode;

	private String synonym;

	private String manufacturerNumber;

	private String vendorTelephone;

	private String deaCode;

	private String genericCodeNumberSequence;

	private String therapeuticClass;

	private String transportationGroup;

	private String dscsaEnabled;

	private String orderingUomGtin;

	private String genericName;

	private String productForm;

	private BigDecimal fdbPkgSize;

	private Integer fdbCasePack;

	private String fdbUom;

	private String manufacturerName;

	private String hcpc1;

	private String billingUnit1;

	private String materialGroup1;

	private String loadingGroup;

	private String materialGroup;

	private String extMaterialGroup;

	private BigDecimal netWeight;

	private String serializationType;

	private String xDistributionChannelStatus;

	private String dchainSpecStatus;

	private String deliveringPlant;

	private String itemCategoryGroup;

	private String excludeWebstore;

	private String dispOnlyWebstore;

	private String addlInfoWebstore;

	private String batchManagement;

	private String hazMaterialNumber;

	private Integer minRemainingShelfLife;

	private String periodIndicatorForShelfLife;

	private Integer totalShelfLife;

	private String purchasingGroup;

	private String manufacturerPartNumber;

	private String cgatIdentifier;

	private String salesOrg;

	private String cgatType;

	private String productLevelPricing;

}
