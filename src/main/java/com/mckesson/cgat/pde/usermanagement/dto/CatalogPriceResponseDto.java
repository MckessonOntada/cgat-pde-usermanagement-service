package com.mckesson.cgat.pde.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CatalogPriceResponseDto {

	private Long id;

	public String tenantId;

	private String soldToCustomer;

	private String soldToCustomerName;

	private String shipToCustomer;

	private String shipToCustomerName;

	private String parentCustomer;

	private String parentCustomerName;

	private String salesOrg;

	private String distChannel;

	private String division;

	private String sapMaterial;

	private String variant;

	private String variantDescription;

	private String materialIdentifierForCgat;

	private String ndc;

	private String udi;

	private String materialDescriptionInLanguageEn;

	private String sellUnit;

	private String contractNo;

	private String contractType;

	private String contractTypeDescription;

	private LocalDate contractValidFrom;

	private LocalDate contractValidTo;

	private String leadNumberMpb;

	private String buyingGroup;

	private String buyingGroupDescription;

	private String customerIdentifier;

	private LocalDate pricingDate;

	private BigDecimal netPrice;

	private BigDecimal regularPrice;

	private BigDecimal contractPrice;

	private BigDecimal wacPrice;

	private BigDecimal discounts;

	private String fixedPrice;

	private BigDecimal price340b;

	private String priceUnit;

	private String currency;

	private String inventoryType;

	private String minimumOrderQuantity;

	private boolean productLevelPricing;

}
