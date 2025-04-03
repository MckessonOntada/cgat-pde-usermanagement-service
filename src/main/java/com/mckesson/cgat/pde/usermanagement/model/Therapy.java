package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.mckesson.cgat.pde.usermanagement.utils.CustomUtils.determineDropshipIndicator;

/**
 * Copy below prompt to generate class using github copilot
 *
 * Generate java class with following csv
 *
 * Sequence,Fields,Length,Type,Description,Comments ,MPB = required/optional?,MSH =
 * required/optional? Sequence,Fields,Length,Type,Description,Comments ,MPB =
 * required/optional?,MSH = required/optional? 1,item_num,18,character,Material
 * Number,Y,Required,Required 2,ndc_id,18,character,NDC Number,Y,Required,Required
 * 3,strength_desc,10,character,Strength,Y,Optional,Optional
 * 4,dosage_desc,10,character,Dosage,Y,Optional,Optional
 * 5,selling_descr,40,character,Material Description,Y,Required,Required
 * 6,generic_name_FDB,30,character,NDC item description - from FDB,Y,Optional,Optional
 * 7,selling_unit_code,3,character,Selling Unit Code,Y,Optional,Required
 * 8,supplr_acct_nam_1,25,character,Vendor Name,Y,Optional,Optional
 * 9,upc_id,11,character,UPC Code,Y,Optional,Optional 10,rxda_code,1,character,RXDA
 * Code,Y,Optional,Optional 11,dropship_ind,1,character,Dropship Indicator,Y,Optional,
 * 12,item_activity,1,character,Item status = item active / inactive,Y,Optional,Optional
 * 13,sku_cd,2,character,base unit of measure (stocking unit),Y,Required,Required
 * 14,item_thera_catgy,6,character,THER-CATGY,Y,Optional,
 * 15,min_ordr_qty,3,character,SCAN-QTY,Y,Optional,Optional
 * 16,unit_size,1,character,DRUG-FORM,Y,Optional,
 * 17,item_adtl_dscr_txt,20,character,Additional Descriptor = FDB NDC,Y,Optional,
 * 18,color_txt,15,character,Color = FDB NDC,Y,Optional, 19,flavor_txt,15,character,Flavor
 * = FDB NDC,Y,Optional, 20,rte_dscr,10,character,"Route Description (Oral, Injection) =
 * FDB NDC",Y,, 21,pkg_siz_mult_qty,9,character,Matl Pack Size Multiplier (from FDB,Y,,
 * 22,shape_dscr,15,character,Shape = FDB NDC,Y,, 23,obc_cd_l3,3,character,Orange Book
 * Code = FDB NDC,Y,, 24,mfr_part_num,12,character,SYNONYM,Y,Optional,Optional
 * 25,mfr_splr_id,10,character,manufacturer number,Y,Required,Required
 * 26,mfr_phone_num,10,character,vendor telephone,Y,, 27,drug_sched_cd,1,character,DEA
 * code = FDB NDC,Y,, 28,gcn_seqno,6,character,Generic Code Number Sequence Number = FDB
 * NDC,Y,, 29,enh_thera_class_cd1,8,character,Therapeutic Class,Y,,
 * 30,tragr,4,character,Transportation Group,Y,Optional,Optional
 * 31,dscsa,1,character,DSCSA enabled,Y,Optional,optional 32,GTIN,14,character,ordering
 * UOM-GTIN,Y,, 33,GENERIC_NAME_CLS,30,character,Generic name,Y,,
 * 34,PRODUCT_FORM,30,character,Product Form,Y,, 35,FDB_PKG_SIZE,12,NUM,FDB_PKG_SIZE,Y,,
 * 36,FDB_CASE_PACK,7,NUM,FDB_CASE_PACK,Y,, 37,FDB_UOM,3,character,FDB_UOM,Y,,
 * 38,MANUFACTURER_NAME,35,character,Manufacturer Name,Y,, 39,HCPC_1,8,character,1
 * HCPC,Y,, 40,BILLING_UNIT_1,20,character,1 Billing Unit,Y,,
 * 41,MVGR1,3,character,Material group 1 (sales view),Y,Optional,optional
 * 42,LADGR,4,character,loading group,Y,Optional,optional 43,MATKL,9,character,material
 * group (material view),Y,Optional,optional 44,EXTWG,18,character,Ext. Material
 * Group,Y,optional,optional 45,NTGEW,"13(13,3)",NUM,Net Weight,Y,Optional,optional
 * 46,/STTPEC/SERTYPE,3,character,Serialization Type,Y,Optional,optional
 * 47,MSTAV,2,character,X-Distribution Channel Status,Y,optional,optional
 * 48,VMSTA,2,character,Dchain-spec. Status,Y,Optional,optional
 * 49,DWERK,4,character,Delivering Plant,Y,optional,optional 50,MTPOS,4,character,Item
 * Category Group,Y,Optional,optional 51,PRAT1,1,character,Exclude
 * Webstore,Y,Optional,Optional 52,PRAT2,1,character,Disp. Only
 * Webstore,Y,Optional,Optional 53,PRAT3,1,character,Addl. Info
 * Webstore,Y,Optional,Optional 54,XCHPF,1,character,Batch Management,Y,Optional,optional
 * 57,STOFF,18,character,Haz material number,Y,Optional,Optional
 * 58,MHDRZ,4,character(DEC),Min Remaining Shelf Life,Y,Optional,Optional
 * 59,IPRKZ,1,character,Period Indicator for Shelf Lif,Y,Optional,Optional
 * 60,MHDHB,4,character(DEC),Total shelf life,Y,Optional,Optional
 * 61,EKGRP,3,character,Purchasing Group,Y,Optional,optional
 * 63,MFRPN,40,character,Manufacturer Part Number,Y,Optional,Optional
 * 64,CGAT_IND,1,character,CGAT identifier,Y,Optional,optional 65,VKORG,4,character,sales
 * orgnization,Y,Required,required 66,CGAT_TYPE,2,character,CGAT type,Y,Optional,optional
 * 67,CGAT_PRODUCT_PRICING,1,character,Product level pricing,Y,Optional,optional
 * 66,MHDHB,4,character(DEC),Total shelf life,Y,, 67,EKGRP,3,character,Purchasing
 * Group,Y,Optional,optional 68,MMSTA,2,character,Plant Sp. Material
 * Status,Y,Optional,Optional 69,MFRPN,40,character,Manufacturer Part
 * Number,Y,Optional,Optional 70,CGAT_IND,1,character,CGAT identifier,Y,Optional,optional
 * 71,VKORG,4,character,sales orgnization,Y,Required,required
 * 72,CGAT_TYPE,2,character,CGAT type,Y,Optional,optional
 * 73,CGAT_PRODUCT_PRICING,1,character,Product level pricing,Y,Optional,optional
 *
 *
 * The class properties are generated like below
 *
 * @Meta(order=1, length = 10, name="EPKUNNR")
 * @Size(max = 35) private <Type> vistexCustomer; Type can be inferred by the Type column
 * and mapped to appropriate java type the Meta length value comes from Length column in
 * csv, order is the line number of csv and name is the Field Name column name of property
 * comes from Description column Size max value comes from Length column in csv if the
 * column is String
 * Add @NotBlank(group={MPBValidationGroup.class,NonMPBValidationGroup.class}) only when
 * MPB = required/optional? column is Required and MSH = required/optional? column is
 * Required if the column is String
 * else @NotNull(group={MPBValidationGroup.class,NonMPBValidationGroup.class}) when MPB =
 * required/optional? column is Required and MSH = required/optional? column is Required
 * Add @NotBlank(group={MPBValidationGroup.class}) only when MPB = required/optional?
 * column is Required and MSH = required/optional? column is optional if the column is
 * String else @NotNull(group={MPBValidationGroup.class}) when MPB = required/optional?
 * column is Required and MSH = required/optional? column is Optional
 * Add @NotBlank(group={NonMPBValidationGroup.class}) only when MPB = required/optional?
 * column is Optional and MSH = required/optional? column is Required if the column is
 * String else @NotNull(group={NonMPBValidationGroup.class}) when MPB = required/optional?
 * column is Optional and MSH = required/optional? column is Required For Number
 * generate @Digits For Date generate type as LocalDate
 *
 * Add @Column with name compatible with postgres DB column name and is derived from
 * description column Once The class is generated, Generate a create Table sql command for
 * the same compatible with postgres DB
 **/
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material", schema = "catalog")
public class Therapy implements Serializable {

	@Id
	@GeneratedValue(generator = "material_id_seq")
	@SequenceGenerator(name = "material_id_seq", sequenceName = "material_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "tenant_id")
	public String tenantId;

	@Size(max = 50)
	@Column(name = "material_number")
	private String materialNumber;

	@Size(max = 50)
	@Column(name = "site_name")
	private String siteName;

	@Size(max = 20)
	@Column(name = "channel_type")
	@Enumerated(EnumType.STRING)
	private ChannelType channelType;

	@Size(max = 18)
	@Column(name = "ndc_number")
	private String ndcNumber;

	@Size(max = 10)
	@Column(name = "strength")
	private String strength;

	@Size(max = 10)
	@Column(name = "dosage")
	private String dosage;

	@Size(max = 100)
	@Column(name = "material_description")
	private String materialDescription;

	@Size(max = 30)
	@Column(name = "ndc_item_description")
	private String ndcItemDescription;

	@Size(max = 3)
	@Column(name = "selling_unit_code")
	private String sellingUnitCode;

	@Size(max = 25)
	@Column(name = "vendor_name")
	private String vendorName;

	@Column(name = "upc_code")
	private String upcCode;

	@Size(max = 1)
	@Column(name = "rxda_code")
	private String rxdaCode;

	@Transient
	private String dropshipIndicator;

	@Size(max = 1)
	@Column(name = "item_status")
	private String itemStatus;

	@Size(max = 2)
	@Column(name = "base_unit_of_measure")
	private String baseUnitOfMeasure;

	@Size(max = 6)
	@Column(name = "ther_catgy")
	private String therCatgy;

	@Size(max = 3)
	@Column(name = "scan_qty")
	private String minOrdrQuantity;

	@Size(max = 1)
	@Column(name = "drug_form")
	private String drugForm;

	@Size(max = 20)
	@Column(name = "additional_descriptor")
	private String additionalDescriptor;

	@Size(max = 15)
	@Column(name = "color")
	private String color;

	@Size(max = 15)
	@Column(name = "flavor")
	private String flavor;

	@Size(max = 10)
	@Column(name = "route_description")
	private String routeDescription;

	@Size(max = 9)
	@Column(name = "matl_pack_size_multiplier")
	private String matlPackSizeMultiplier;

	@Size(max = 15)
	@Column(name = "shape")
	private String shape;

	@Size(max = 3)
	@Column(name = "orange_book_code")
	private String orangeBookCode;

	@Size(max = 12)
	@Column(name = "synonym")
	private String synonym;

	@Size(max = 10)
	@Column(name = "manufacturer_number")
	private String manufacturerNumber;

	@Size(max = 10)
	@Column(name = "vendor_telephone")
	private String vendorTelephone;

	@Size(max = 1)
	@Column(name = "dea_code")
	private String deaCode;

	@Size(max = 6)
	@Column(name = "generic_code_number_sequence")
	private String genericCodeNumberSequence;

	@Size(max = 8)
	@Column(name = "therapeutic_class")
	private String therapeuticClass;

	@Size(max = 4)
	@Column(name = "transportation_group")
	private String transportationGroup;

	@Size(max = 1)
	@Column(name = "dscsa_enabled")
	private String dscsaEnabled;

	@Size(max = 14)
	@Column(name = "ordering_uom_gtin")
	private String orderingUomGtin;

	@Size(max = 30)
	@Column(name = "generic_name")
	private String genericName;

	@Size(max = 30)
	@Column(name = "product_form")
	private String productForm;

	@Digits(integer = 9, fraction = 3)
	@Column(name = "fdb_pkg_size")
	private BigDecimal fdbPkgSize;

	@Digits(integer = 7, fraction = 0)
	@Column(name = "fdb_case_pack")
	private Integer fdbCasePack;

	@Size(max = 3)
	@Column(name = "fdb_uom")
	private String fdbUom;

	@Size(max = 35)
	@Column(name = "manufacturer_name")
	private String manufacturerName;

	@Size(max = 8)
	@Column(name = "hcpc_1")
	private String hcpc1;

	@Size(max = 20)
	@Column(name = "billing_unit_1")
	private String billingUnit1;

	@Size(max = 3)
	@Column(name = "material_group_1")
	private String materialGroup1;

	@Size(max = 4)
	@Column(name = "loading_group")
	private String loadingGroup;

	@Size(max = 9)
	@Column(name = "material_group")
	private String materialGroup;

	@Size(max = 18)
	@Column(name = "ext_material_group")
	private String extMaterialGroup;

	@Digits(integer = 10, fraction = 3)
	@Column(name = "net_weight")
	private BigDecimal netWeight;

	@Size(max = 3)
	@Column(name = "serialization_type")
	private String serializationType;

	@Size(max = 2)
	@Column(name = "x_distribution_channel_status")
	private String xDistributionChannelStatus;

	@Size(max = 2)
	@Column(name = "dchain_spec_status")
	private String dchainSpecStatus;

	@Size(max = 4)
	@Column(name = "delivering_plant")
	private String deliveringPlant;

	@Size(max = 4)
	@Column(name = "item_category_group")
	private String itemCategoryGroup;

	@Size(max = 1)
	@Column(name = "exclude_webstore")
	private String excludeWebstore;

	@Size(max = 1)
	@Column(name = "disp_only_webstore")
	private String dispOnlyWebstore;

	@Size(max = 1)
	@Column(name = "addl_info_webstore")
	private String addlInfoWebstore;

	@Size(max = 1)
	@Column(name = "batch_management")
	private String batchManagement;

	@Size(max = 18)
	@Column(name = "haz_material_number")
	private String hazMaterialNumber;

	@Digits(integer = 4, fraction = 0)
	@Column(name = "min_remaining_shelf_life")
	private Integer minRemainingShelfLife;

	@Size(max = 1)
	@Column(name = "period_indicator_for_shelf_life")
	private String periodIndicatorForShelfLife;

	@Digits(integer = 4, fraction = 0)
	@Column(name = "total_shelf_life")
	private Integer totalShelfLife;

	@Size(max = 3)
	@Column(name = "purchasing_group")
	private String purchasingGroup;

	@Size(max = 40)
	@Column(name = "manufacturer_part_number")
	private String manufacturerPartNumber;

	@Size(max = 10)
	@Column(name = "cgat_therapy_type")
	private String cgatIdentifier;

	@Size(max = 4)
	@Column(name = "sales_organization")
	private String salesOrg;

	@Size(max = 1)
	@Column(name = "product_level_pricing")
	private String productLevelPricing;

	@PostLoad
	public void postLoad() {
		this.dropshipIndicator = determineDropshipIndicator(this.itemCategoryGroup);
	}

}